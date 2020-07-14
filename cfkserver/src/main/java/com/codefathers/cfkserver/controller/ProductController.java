package com.codefathers.cfkserver.controller;

import com.codefathers.cfkserver.model.dtos.product.FilterSortDto;
import com.codefathers.cfkserver.model.dtos.product.MiniProductDto;
import com.codefathers.cfkserver.model.dtos.product.MiniProductListDto;
import com.codefathers.cfkserver.model.dtos.product.SellPackageDto;
import com.codefathers.cfkserver.model.entities.product.Product;
import com.codefathers.cfkserver.service.FilterService;
import com.codefathers.cfkserver.service.ProductService;
import com.codefathers.cfkserver.service.Sorter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;

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

    private MiniProductDto dtoFromProduct(Product product) {
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
}
