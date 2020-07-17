package com.codefathers.cfkserver.controller;

import com.codefathers.cfkserver.exceptions.model.product.NoSuchAProductException;
import com.codefathers.cfkserver.exceptions.model.product.NoSuchSellerException;
import com.codefathers.cfkserver.model.dtos.log.SellLogDTO;
import com.codefathers.cfkserver.model.dtos.log.SellLogListDTO;
import com.codefathers.cfkserver.model.dtos.product.AddSellerToProductDTO;
import com.codefathers.cfkserver.model.dtos.product.MicroProductDto;
import com.codefathers.cfkserver.model.dtos.product.MiniProductDto;
import com.codefathers.cfkserver.model.dtos.product.MiniProductListDto;
import com.codefathers.cfkserver.model.dtos.user.CompanyDto;
import com.codefathers.cfkserver.model.dtos.user.UserFullDTO;
import com.codefathers.cfkserver.model.entities.logs.SellLog;
import com.codefathers.cfkserver.model.entities.product.Company;
import com.codefathers.cfkserver.model.entities.product.Product;
import com.codefathers.cfkserver.model.entities.user.Seller;
import com.codefathers.cfkserver.model.entities.user.User;
import com.codefathers.cfkserver.service.CompanyService;
import com.codefathers.cfkserver.service.ProductService;
import com.codefathers.cfkserver.service.SellerService;
import com.codefathers.cfkserver.utils.JwtUtil;
import com.codefathers.cfkserver.utils.TokenUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.util.ArrayList;
import java.util.List;

import static com.codefathers.cfkserver.utils.ErrorUtil.sendError;

@RestController
public class SellerController {
    @Autowired
    private ProductService productService;
    @Autowired
    private SellerService sellerService;

    @GetMapping("seller/view_company")
    public ResponseEntity<?> viewCompanyInfo(HttpServletRequest request, HttpServletResponse response){
        try {
            if (TokenUtil.checkToken(response, request)) {
                Company company = sellerService.viewCompanyInformation(TokenUtil.getUsernameFromToken(request));
                CompanyDto dto = new CompanyDto(company.getName(), company.getGroup(), company.getId(), company.getPhone());
                return ResponseEntity.ok(dto);
            }
            else
                return null;
        } catch (Exception e) {
            sendError(response, HttpStatus.BAD_REQUEST, e.getMessage());
            return null;
        }
    }

    @GetMapping("seller/sellLog")
    public ResponseEntity<?> viewSalesHistory(HttpServletRequest request, HttpServletResponse response) {
        try {
            if (TokenUtil.checkToken(response, request)) {
                List<SellLog> sellLogs = sellerService.viewSalesHistory(TokenUtil.getUsernameFromToken(request));
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
            }
            else
                return null;
        } catch (Exception e) {
            sendError(response, HttpStatus.BAD_REQUEST, e.getMessage());
            return null;
        }
    }

    @PostMapping("seller/become_seller")
    public ResponseEntity<?> becomeSellerOfExistingProduct(@RequestBody AddSellerToProductDTO dto,
                                                           HttpServletRequest request, HttpServletResponse response){
        try {
            if (TokenUtil.checkToken(response, request)) {
                Product product = productService.findById(dto.getProductId());
                Seller seller = sellerService.findSellerByUsername(TokenUtil.getUsernameFromToken(request));
                productService.addASellerToProduct(product, seller, dto.getAmount(), dto.getPrice());
                return ResponseEntity.ok(HttpStatus.valueOf(200));
            }
            else
                return null;
        } catch (Exception | NoSuchAProductException e) {
            sendError(response, HttpStatus.BAD_REQUEST, e.getMessage());
            return null;
        }
    }

    @GetMapping("seller/balance")
    public ResponseEntity<?> viewBalance(HttpServletRequest request, HttpServletResponse response) {
        try {
            if (TokenUtil.checkToken(response, request)) {
                Seller seller = sellerService.findSellerByUsername(TokenUtil.getUsernameFromToken(request));
                return ResponseEntity.ok(seller.getBalance());
            }
            else
                return null;
        } catch (Exception e) {
            sendError(response, HttpStatus.BAD_REQUEST, e.getMessage());
            return null;
        }
    }

    @GetMapping("seller/products")
    public ResponseEntity<?> manageProducts(HttpServletRequest request, HttpServletResponse response) {
        try {
            if (TokenUtil.checkToken(response, request)) {
                List<Product> sellerProducts = sellerService.viewProducts(TokenUtil.getUsernameFromToken(request));
                //List<Product> sortedSellerProducts = sortManager.sort(sellerProducts,sort.getSortType());
                //if (!sort.isAscending()) Collections.reverse(sortedSellerProducts);
                ArrayList<MiniProductDto> miniProductDTOs = new ArrayList<>();
                for (Product sellerProduct : sellerProducts) {
                    miniProductDTOs.add(ProductController.dtoFromProduct(sellerProduct));
                }
                return ResponseEntity.ok(new MiniProductListDto(miniProductDTOs));
            }
            else
                return null;
        } catch (Exception e) {
            sendError(response, HttpStatus.BAD_REQUEST, e.getMessage());
            return null;
        }
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

    public ResponseEntity<?> getProductsForSeller(HttpServletRequest request, HttpServletResponse response) {
        try {
            if (TokenUtil.checkToken(response, request)) {
                List<Product> sellerProducts = sellerService.viewProducts(TokenUtil.getUsernameFromToken(request));
                List<MicroProductDto> list = new ArrayList<>();
                if (sellerProducts != null)
                    sellerProducts.forEach(product -> list.add(new MicroProductDto(product.getName(), product.getId())));
                return ResponseEntity.ok(list);
            }
            else
                return null;
        } catch (Exception e) {
            sendError(response, HttpStatus.BAD_REQUEST, e.getMessage());
            return null;
        }
    }
}
