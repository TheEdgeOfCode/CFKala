package com.codefathers.cfkserver.controller;

import com.codefathers.cfkserver.exceptions.model.product.NoSuchAProductException;
import com.codefathers.cfkserver.exceptions.model.product.NoSuchSellerException;
import com.codefathers.cfkserver.exceptions.model.user.NoSuchAPackageException;
import com.codefathers.cfkserver.exceptions.token.ExpiredTokenException;
import com.codefathers.cfkserver.exceptions.token.InvalidTokenException;
import com.codefathers.cfkserver.model.dtos.log.SellLogDTO;
import com.codefathers.cfkserver.model.dtos.log.SellLogListDTO;
import com.codefathers.cfkserver.model.dtos.off.OffDTO;
import com.codefathers.cfkserver.model.dtos.off.OffListDTO;
import com.codefathers.cfkserver.model.dtos.product.*;
import com.codefathers.cfkserver.model.dtos.user.CompanyDto;
import com.codefathers.cfkserver.model.entities.logs.SellLog;
import com.codefathers.cfkserver.model.entities.offs.Off;
import com.codefathers.cfkserver.model.entities.product.Company;
import com.codefathers.cfkserver.model.entities.product.Product;
import com.codefathers.cfkserver.model.entities.user.Seller;
import com.codefathers.cfkserver.service.FilterService;
import com.codefathers.cfkserver.service.ProductService;
import com.codefathers.cfkserver.service.SellerService;
import com.codefathers.cfkserver.service.Sorter;
import com.codefathers.cfkserver.utils.TokenUtil;
import org.apache.tomcat.util.http.fileupload.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import static com.codefathers.cfkserver.controller.ProductController.dtoFromProduct;
import static com.codefathers.cfkserver.utils.ErrorUtil.sendError;
import static com.codefathers.cfkserver.utils.TokenUtil.checkToken;
import static com.codefathers.cfkserver.utils.TokenUtil.getUsernameFromToken;

@RestController
public class SellerController {
    @Autowired
    private ProductService productService;
    @Autowired
    private SellerService sellerService;
    @Autowired
    private Sorter sorter;

    @GetMapping("seller/view_company")
    public ResponseEntity<?> viewCompanyInfo(HttpServletRequest request, HttpServletResponse response){
        try {
            if (checkToken(response, request)) {
                try {
                    Company company = sellerService.viewCompanyInformation(getUsernameFromToken(request));
                    CompanyDto dto = new CompanyDto(company.getName(), company.getGroup(), company.getId(), company.getPhone());
                    return ResponseEntity.ok(dto);
                } catch (NoSuchSellerException e) {
                    sendError(response, HttpStatus.BAD_REQUEST, e.getMessage());
                }
            }
        } catch (ExpiredTokenException | InvalidTokenException e) {
            sendError(response, HttpStatus.UNAUTHORIZED, e.getMessage());
        }
        return null;
    }

    @GetMapping("seller/sellLog")
    public ResponseEntity<?> viewSalesHistory(HttpServletRequest request, HttpServletResponse response) {
        try {
            if (checkToken(response, request)) {
                try {
                    List<SellLog> sellLogs = sellerService.viewSalesHistory(getUsernameFromToken(request));
                    ArrayList<SellLogDTO> sellLogDTOs = new ArrayList<>();
                    for (SellLog sellLog : sellLogs) {
                        sellLogDTOs.add(new SellLogDTO(sellLog.getLogId(),
                                sellLog.getProduct().getSourceId(),
                                sellLog.getMoneyGotten(),
                                sellLog.getDiscount(),
                                sellLog.getDate(),
                                sellLog.getBuyer().getUsername(),
                                sellLog.getDeliveryStatus()));
                    }
                    return ResponseEntity.ok(new SellLogListDTO(sellLogDTOs));
                } catch (NoSuchSellerException e) {
                    sendError(response, HttpStatus.BAD_REQUEST, e.getMessage());
                }
            }
        } catch (ExpiredTokenException | InvalidTokenException e) {
            sendError(response, HttpStatus.UNAUTHORIZED, e.getMessage());
        }
        return null;
    }

    @PostMapping("seller/become_seller")
    public void becomeSellerOfExistingProduct(@RequestBody AddSellerToProductDTO dto,
                                                           HttpServletRequest request, HttpServletResponse response){
        try {
            if (checkToken(response, request)) {
                try {
                    Product product = productService.findById(dto.getProductId());
                    Seller seller = sellerService.findSellerByUsername(getUsernameFromToken(request));
                    productService.addASellerToProduct(product, seller, dto.getAmount(), dto.getPrice());
                } catch (NoSuchSellerException | NoSuchAProductException e) {
                    sendError(response, HttpStatus.BAD_REQUEST, e.getMessage());
                }
            }
        } catch (ExpiredTokenException | InvalidTokenException e) {
            sendError(response, HttpStatus.UNAUTHORIZED, e.getMessage());
        }
    }

    @GetMapping("seller/balance")
    public ResponseEntity<?> viewBalance(HttpServletRequest request, HttpServletResponse response) {
        try {
            if (checkToken(response, request)) {
                try {
                    Seller seller = sellerService.findSellerByUsername(getUsernameFromToken(request));
                    return ResponseEntity.ok(seller.getBalance());
                } catch (NoSuchSellerException e) {
                    sendError(response, HttpStatus.BAD_REQUEST, e.getMessage());
                }
            }
        } catch (ExpiredTokenException | InvalidTokenException e) {
            sendError(response, HttpStatus.UNAUTHORIZED, e.getMessage());
        }
        return null;
    }

    @GetMapping("seller/products")
    public ResponseEntity<?> manageProducts(HttpServletRequest request, HttpServletResponse response) {
        try {
            if (checkToken(response, request)) {
                try {
                    List<Product> sellerProducts = sellerService.viewProducts(getUsernameFromToken(request));
                    //List<Product> sortedSellerProducts = sortManager.sort(sellerProducts,sort.getSortType());
                    //if (!sort.isAscending()) Collections.reverse(sortedSellerProducts);
                    ArrayList<MiniProductDto> miniProductDTOs = new ArrayList<>();
                    for (Product sellerProduct : sellerProducts) {
                        miniProductDTOs.add(dtoFromProduct(sellerProduct));
                    }
                    return ResponseEntity.ok(new MiniProductListDto(miniProductDTOs));
                } catch (NoSuchSellerException e) {
                    sendError(response, HttpStatus.BAD_REQUEST, e.getMessage());
                }
            }
        } catch (ExpiredTokenException | InvalidTokenException e) {
            sendError(response, HttpStatus.UNAUTHORIZED, e.getMessage());
        }
        return null;
    }

    /*public List<MiniProductPM> manageProducts(String sellerUserName, SortPackage sortPackage, FilterPackage filterPackage)
            throws UserNotAvailableException {
        List<Product> sortedSellerProducts = sortManager.sort(sellerManager.viewProducts(sellerUserName), sortPackage.getSortType());
        int[] priceRange = new int[2];
        priceRange[0] = filterPackage.getDownPriceLimit();
        priceRange[1] = filterPackage.getUpPriceLimit();
        List<Product> filteredSellerProducts = FilterManager.filterList(sortedSellerProducts, filterPackage.getActiveFilters(), priceRange);
        ArrayList<MiniProductPM> miniProductPMs = new ArrayList<>();
        //System.err.println(filteredSellerProducts);
        filteredSellerProducts.forEach(product -> miniProductPMs.add(createMiniProductPM(product)));
        return miniProductPMs;
    }*/

    /*public List<MicroProductDto> getProductsForSeller(HttpServletRequest request, HttpServletResponse response) {
        try {
            if (checkToken(response, request)) {
                List<Product> sellerProducts = sellerService.viewProducts(TokenUtil.getUsernameFromToken(request));
                List<MicroProductDto> list = new ArrayList<>();
                if (sellerProducts != null)
                    sellerProducts.forEach(product -> list.add(new MicroProductDto(product.getName(), product.getId())));
                return list;
            }
            else
                return null;
        } catch (Exception e) {
            sendError(response, HttpStatus.BAD_REQUEST, e.getMessage());
            return null;
        }
    }*/

    @DeleteMapping("seller/remove_product")
    public void removeProduct(@RequestBody int productId, HttpServletRequest request, HttpServletResponse response) {
        try {
            if (checkToken(response, request)) {
                try {
                    sellerService.deleteProductForSeller(getUsernameFromToken(request), productId);
                } catch (NoSuchSellerException | NoSuchAPackageException e) {
                    sendError(response, HttpStatus.BAD_REQUEST, e.getMessage());
                }
            }
        } catch (ExpiredTokenException | InvalidTokenException e) {
            sendError(response, HttpStatus.UNAUTHORIZED, e.getMessage());
        }
    }

    @GetMapping("seller/offs")
    public OffListDTO viewAllOffs(@RequestBody FilterSortDto sortPackage, HttpServletRequest request, HttpServletResponse response) {
        try {
            if (checkToken(response, request)) {
                try {
                    Seller seller = sellerService.findSellerByUsername(getUsernameFromToken(request));
                    List<Off> offs = seller.getOffs();
                    sorter.sortOff(offs);
                    if (!sortPackage.isAscending()) Collections.reverse(offs);
                    List<OffDTO> offPMs = new ArrayList<>();
                    for (Off off : offs) {
                        offPMs.add(new OffDTO(
                                off.getOffId(),
                                addProductToOffDTO(off),
                                off.getSeller().getUsername(),
                                off.getStartTime(),
                                off.getEndTime(),
                                off.getOffPercentage(),
                                off.getOffStatus().toString()));
                    }
                    return new OffListDTO(new ArrayList<>(offPMs));
                } catch (NoSuchSellerException e) {
                    sendError(response, HttpStatus.BAD_REQUEST, e.getMessage());
                }
            }
        } catch (ExpiredTokenException | InvalidTokenException e) {
            sendError(response, HttpStatus.UNAUTHORIZED, e.getMessage());
        }
        return null;
    }

    private ArrayList<MiniProductDto> addProductToOffDTO(Off off) {
        ArrayList<MiniProductDto> products = new ArrayList<>();
        for (Product product : off.getProducts()) {
            products.add(dtoFromProduct(product));
        }
        return products;
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
