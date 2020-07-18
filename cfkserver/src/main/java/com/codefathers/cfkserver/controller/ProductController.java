package com.codefathers.cfkserver.controller;

import com.codefathers.cfkserver.exceptions.model.product.NoSuchAProductException;
import com.codefathers.cfkserver.exceptions.token.ExpiredTokenException;
import com.codefathers.cfkserver.exceptions.token.InvalidTokenException;
import com.codefathers.cfkserver.model.dtos.product.*;
import com.codefathers.cfkserver.model.entities.product.Product;
import com.codefathers.cfkserver.model.entities.request.edit.ProductEditAttribute;
import com.codefathers.cfkserver.service.FilterService;
import com.codefathers.cfkserver.service.ProductService;
import com.codefathers.cfkserver.service.Sorter;
import org.apache.tomcat.util.http.fileupload.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.*;

import static com.codefathers.cfkserver.utils.ErrorUtil.sendError;
import static com.codefathers.cfkserver.utils.TokenUtil.checkToken;
import static com.codefathers.cfkserver.utils.TokenUtil.getUsernameFromToken;

@RestController
public class ProductController {
    @Autowired
    private ProductService productService;
    @Autowired
    private FilterService filterService;
    @Autowired
    private Sorter sorter;

    @PostMapping("/product/get_all_products")
    private ResponseEntity<?> getAllProducts(@RequestBody FilterSortDto filterSortDto) {
        try {
            int[] priceRange = {filterSortDto.getDownPriceLimit(), filterSortDto.getUpPriceLimit()};
            List<Product> products = sorter.sort(filterService.updateFilterList(
                    filterSortDto.getCategoryId(), filterSortDto.getActiveFilters(), priceRange,
                    false, filterSortDto.isAvailableOnly()
                    )
                    , filterSortDto.getSortType());
            List<MiniProductDto> toReturn = dtosFromList(products);
            return ResponseEntity.ok(new MiniProductListDto(new ArrayList<>(toReturn)));
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage(), e);
        }
    }

    private List<MiniProductDto> dtosFromList(List<Product> products) {
        List<MiniProductDto> toReturn = new ArrayList<>();
        products.forEach(product -> toReturn.add(dtoFromProduct(product)));
        return toReturn;
    }

    static MiniProductDto dtoFromProduct(Product product) {
        List<SellPackageDto> sellPackages = new ArrayList<>();
        product.getPackages().forEach(sellPackage -> {
            int offPercent = sellPackage.isOnOff() ? sellPackage.getOff().getOffPercentage() : 0;
            sellPackages.add(new SellPackageDto(offPercent,
                    sellPackage.getPrice(),
                    sellPackage.getStock(),
                    sellPackage.getSeller().getUsername(),
                    sellPackage.isAvailable()));
        });
        return new MiniProductDto(product.getName(), product.getId(),
                product.getCategory().getName(), sellPackages, product.getCompanyClass().getName(),
                product.getTotalScore(), product.getDescription(), product.isAvailable());
    }

    @PostMapping("/products/create")
    public ResponseEntity<?> addProduct(@RequestBody CreateProductDTO dto, HttpServletRequest request, HttpServletResponse response) {
        //TODO: Check if this works properly!!! (Two method for this...)
        try {
            if (checkToken(response,request)){
                try {
                    int id = productService.createProduct(dto);
                    return ResponseEntity.ok(id);
                } catch (Exception e) {
                    sendError(response, HttpStatus.BAD_REQUEST,e.getMessage());
                    return null;
                }
            }
            return null;
        } catch (ExpiredTokenException | InvalidTokenException e) {
            sendError(response, HttpStatus.UNAUTHORIZED,e.getMessage());
            return null;
        }
    }

    @PostMapping("/products/edit")
    public void editProduct(@RequestBody ProductEditAttribute editAttribute,
                            HttpServletRequest request, HttpServletResponse response) {
        try {
            if (checkToken(response,request)){
                try {
                    productService.editProduct(getUsernameFromToken(request), editAttribute);
                } catch (Exception | NoSuchAProductException e) {
                    sendError(response, HttpStatus.BAD_REQUEST,e.getMessage());
                }
            }
        } catch (ExpiredTokenException | InvalidTokenException e) {
            sendError(response, HttpStatus.UNAUTHORIZED,e.getMessage());
        }
    }

    @GetMapping
    @RequestMapping("/product/similar/{name}")
    public ResponseEntity<?> similarProducts(@PathVariable String name){
        return ResponseEntity.ok(productService.findProductsByName(name));
    }

    @GetMapping
    @RequestMapping("/products/full/{id}")
    private ResponseEntity<?> getFullProduct(@PathVariable Integer id, HttpServletResponse response){
        try {
            Product product = productService.findById(id);
            return ResponseEntity.ok(createFullProduct(product));
        } catch (NoSuchAProductException e) {
            sendError(response, HttpStatus.BAD_REQUEST,e.getMessage());
            return null;
        }
    }

    private FullProductPM createFullProduct(Product product) {
        MiniProductDto miniProductDto = dtoFromProduct(product);
        Map<String,String> features = new HashMap<>();
        features.putAll(product.getPublicFeatures());
        features.putAll(product.getSpecialFeatures());
        return new FullProductPM(miniProductDto,features);
    }
}
