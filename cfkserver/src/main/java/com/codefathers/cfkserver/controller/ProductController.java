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
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

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
    public void addProduct(@RequestBody CreateProductDTO dto, HttpServletRequest request, HttpServletResponse response) {
        //TODO: Check if this works properly!!! (Two method for this...)
        try {
            if (checkToken(response,request)){
                try {
                    productService.createProduct(dto);
                } catch (Exception e) {
                    sendError(response, HttpStatus.BAD_REQUEST,e.getMessage());
                }
            }
        } catch (ExpiredTokenException | InvalidTokenException e) {
            sendError(response, HttpStatus.UNAUTHORIZED,e.getMessage());
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

    @PostMapping("product/save_image")
    public void saveImagesForProduct(@RequestBody SaveImageDTO dto, HttpServletRequest request, HttpServletResponse response) {
        try {
            if (checkToken(response, request)) {
                int id = dto.getProductId();
                File directory = new File("src/main/resources/db/images/products/" + id);
                if (!directory.exists()) directory.mkdirs();
                saveMainImage(id, dto.getMainImage());
                dto.getFiles().forEach(file -> saveImageForProduct(id, file));
            }
        } catch (ExpiredTokenException | InvalidTokenException e) {
            sendError(response, HttpStatus.UNAUTHORIZED, e.getMessage());
        }
    }

    private void saveMainImage(int id, InputStream data) {
        File image = new File("src/main/resources/db/images/products/" + id + "/main.jpg");
        if (!image.exists()) {
            try {
                image.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        try {
            saveDataToFile(data, image);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void saveDataToFile(InputStream data, File file) throws IOException {
        byte[] buffer = new byte[data.available()];
        data.read(buffer);
        OutputStream outStream = new FileOutputStream(file);
        outStream.write(buffer);
        outStream.close();
    }

    private void saveImageForProduct(int id, InputStream data) {
        File directory = new File("src/main/resources/db/images/products/" + id + "/other");
        directory.mkdirs();
        File image = new File("src/main/resources/db/images/products/" + id + "/other/" + generateUniqueFileName() + ".jpg");
        try {
            if (image.createNewFile()) {
                saveDataToFile(data, image);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @PutMapping("product/update_pic")
    public void updateProductPicture(@RequestBody SaveImageDTO dto, HttpServletRequest request, HttpServletResponse response) {
        try {
            if (checkToken(response, request)) {
                File directory = new File("src/main/resources/db/images/products/" + dto.getProductId());
                try {
                    FileUtils.cleanDirectory(directory);
                    saveImagesForProduct(dto, request, response);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } catch (ExpiredTokenException | InvalidTokenException e) {
            sendError(response, HttpStatus.UNAUTHORIZED, e.getMessage());
        }
    }

    public void addVideo(int id, InputStream data) {
        File video = new File("src/main/resources/db/videos/products/" + id + ".mp4");
        try {
            video.createNewFile();
            saveDataToFile(data, video);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String generateUniqueFileName() {
        Random random = new Random();
        return String.format("%s%s", System.currentTimeMillis(), random.nextInt(100000));
    }
}
