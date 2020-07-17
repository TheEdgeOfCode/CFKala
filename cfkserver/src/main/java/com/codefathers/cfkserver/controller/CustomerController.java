package com.codefathers.cfkserver.controller;

import com.codefathers.cfkserver.exceptions.model.feedback.NotABuyer;
import com.codefathers.cfkserver.exceptions.model.log.NoSuchALogException;
import com.codefathers.cfkserver.exceptions.model.product.NoSuchAProductException;
import com.codefathers.cfkserver.exceptions.model.product.NoSuchSellerException;
import com.codefathers.cfkserver.exceptions.model.user.NoSuchACustomerException;
import com.codefathers.cfkserver.model.dtos.customer.*;
import com.codefathers.cfkserver.model.dtos.discount.DisCodeUserDTO;
import com.codefathers.cfkserver.model.dtos.discount.DisCodeUserListDTO;
import com.codefathers.cfkserver.model.dtos.product.MiniProductDto;
import com.codefathers.cfkserver.model.entities.logs.PurchaseLog;
import com.codefathers.cfkserver.model.entities.maps.DiscountcodeIntegerMap;
import com.codefathers.cfkserver.model.entities.maps.SoldProductSellerMap;
import com.codefathers.cfkserver.model.entities.offs.DiscountCode;
import com.codefathers.cfkserver.model.entities.offs.Off;
import com.codefathers.cfkserver.model.entities.product.Product;
import com.codefathers.cfkserver.model.entities.product.SellPackage;
import com.codefathers.cfkserver.model.entities.user.*;
import com.codefathers.cfkserver.service.*;
import com.codefathers.cfkserver.utils.ErrorUtil;
import com.codefathers.cfkserver.utils.TokenUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import static com.codefathers.cfkserver.controller.ProductController.dtoFromProduct;


@RestController
public class CustomerController {

    @Autowired
    private CustomerService customerService;

    @Autowired
    private CartService cartService;

    @Autowired
    private DiscountService discountService;

    @Autowired
    private LogService logService;

    @Autowired
    private ProductService productService;

    @Autowired
    private FeedbackService feedbackService;

    @PostMapping("/customer/show_cart")
    private ResponseEntity<?> showCart(HttpServletRequest request, HttpServletResponse response) {
        try {
            if (TokenUtil.checkToken(response, request)) {
                String username = TokenUtil.getUsernameFromToken(request);
                Customer customer = customerService.getCustomerByUsername(username);
                Cart cart = customer.getCart();
                List<InCartDTO> inCartDTOS = new ArrayList<>();
                for (SubCart subCart : cart.getSubCarts()) {
                    InCartDTO inCartDTO = createInCartDTOFrom(subCart);
                    inCartDTOS.add(inCartDTO);
                }
                CartDTO cartDTO = new CartDTO(
                        inCartDTOS,
                        cart.getTotalPrice()
                );
                return ResponseEntity.ok(cartDTO);
            } else {
                return null;
            }
        } catch (Exception e) {
            ErrorUtil.sendError(response, HttpStatus.BAD_REQUEST, e.getMessage());
            return null;
        }
    }

    @PostMapping("/customer/change_amount")
    private ResponseEntity<?> changeAmount(HttpServletRequest request, HttpServletResponse response, @RequestBody String info) {
        try {
            if (TokenUtil.checkToken(response, request)) {
                String username = TokenUtil.getUsernameFromToken(request);
                Customer customer = customerService.getCustomerByUsername(username);
                int productId = Integer.parseInt(info.split(",")[0]);
                int change = Integer.parseInt(info.split(",")[1]);
                Cart cart = customer.getCart();
                String sellerUsername = cartService.getSubCartByProductId(cart, productId).getSeller().getUsername();
                cartService.changeProductAmountInCart(cart, productId, sellerUsername, change);
                return ResponseEntity.ok(ResponseEntity.status(200));
            } else {
                return null;
            }
        } catch (Exception | NoSuchAProductException e) {
            ErrorUtil.sendError(response, HttpStatus.BAD_REQUEST, e.getMessage());
            return null;
        }
    }

    @PostMapping("/customer/show_products")
    private ResponseEntity<?> showProducts(HttpServletRequest request, HttpServletResponse response) {
        try {
            if (TokenUtil.checkToken(response, request)) {
                String username = TokenUtil.getUsernameFromToken(request);
                List<InCartDTO> inCartDTOS = new ArrayList<>();
                Customer customer = customerService.getCustomerByUsername(username);
                Cart cart = customer.getCart();
                for (SubCart subCart : cart.getSubCarts()) {
                    inCartDTOS.add(createInCartDTOFrom(subCart));
                }
                return ResponseEntity.ok(new InCartArrayListDTO(new ArrayList<>(inCartDTOS)));
            }
            else {
                return null;
            }
        } catch (Exception e) {
            ErrorUtil.sendError(response, HttpStatus.BAD_REQUEST, e.getMessage());
            return null;
        }
    }

    @PostMapping("/customer/delete_product_from_cart")
    private ResponseEntity<?> deleteProductFromCart(HttpServletRequest request, HttpServletResponse response, @RequestBody String info) {
        try {
            if (TokenUtil.checkToken(response, request)) {
                String username = TokenUtil.getUsernameFromToken(request);
                Customer customer = customerService.getCustomerByUsername(username);
                Cart cart = customer.getCart();
                int productId = Integer.parseInt(info);
                cartService.deleteProductFromCart(cart, productId);
                return ResponseEntity.ok(ResponseEntity.status(200));
            }
            else
                return null;
        } catch (Exception e) {
                ErrorUtil.sendError(response, HttpStatus.BAD_REQUEST, e.getMessage());
                return null;
        }
    }

    @PostMapping("/customer/purchase")
    private ResponseEntity<?> purchase(HttpServletRequest request, HttpServletResponse response, @RequestBody PurchaseDTO purchaseDTO) {
        try {
            if (TokenUtil.checkToken(response, request)) {
                 CustomerInformation customerInformation = new CustomerInformation(
                    purchaseDTO.getAddress(),
                    purchaseDTO.getZipCode(),
                    purchaseDTO.getCardNumber(),
                    purchaseDTO.getCardPassword()
            );
                 DiscountCode discountCode = (purchaseDTO.getDisCodeId().isBlank() ? null
                         : discountService.findByCode(purchaseDTO.getDisCodeId()));
                 String username = TokenUtil.getUsernameFromToken(request);
                 customerService.purchase(
                         username,
                         customerInformation,
                         discountCode);
                 return ResponseEntity.ok(ResponseEntity.status(200));
            }
            else
                return null;
        } catch (Exception | NoSuchAProductException e) {
            ErrorUtil.sendError(response, HttpStatus.BAD_REQUEST, e.getMessage());
            return null;
        }
    }

    @PostMapping("/customer/purchase/show_total_price")
    private ResponseEntity<?> showPurchaseTotalPrice(HttpServletRequest request, HttpServletResponse response, @RequestBody String disCode) {
        try {
            if (TokenUtil.checkToken(response, request)) {
                String username = TokenUtil.getUsernameFromToken(request);
                Customer customer = customerService.getCustomerByUsername(username);
                DiscountCode discountCode = discountService.findByCode(disCode);
                Long totalPrice = customerService.getTotalPrice(discountCode, customer);
                return ResponseEntity.ok(totalPrice);
            }
            else
                return null;
        } catch (Exception e) {
            ErrorUtil.sendError(response, HttpStatus.BAD_REQUEST, e.getMessage());
            return null;
        }
    }

    @PostMapping("/customer/show_orders")
    private ResponseEntity<?> showOrders(HttpServletRequest request, HttpServletResponse response) {
        try {
            if (TokenUtil.checkToken(response, request)) {
                String username = TokenUtil.getUsernameFromToken(request);
                Customer customer = customerService.getCustomerByUsername(username);
                List<PurchaseLog> purchaseLogs = customer.getPurchaseLogs();
                List<OrderLogDTO> orderLogDTOS = new ArrayList<>();
                for (PurchaseLog purchaseLog : purchaseLogs) {
                    orderLogDTOS.add(createOrder(purchaseLog.getLogId()));
                }
                return ResponseEntity.ok(new OrderLogListDTO(new ArrayList<>(orderLogDTOS)));
            }
            else
                return null;
        } catch (Exception | NoSuchAProductException e) {
                ErrorUtil.sendError(response, HttpStatus.BAD_REQUEST, e.getMessage());
                return null;
        }
    }

    @PostMapping("/customer/add_view")
    private ResponseEntity<?> addViewDigest(HttpServletRequest request, HttpServletResponse response, @RequestBody String productId) {
        try {
            if (TokenUtil.checkToken(response, request)) {
                productService.addView(Integer.parseInt(productId));
                return ResponseEntity.ok(ResponseEntity.status(200));
            } else {
                return null;
            }
        } catch (NoSuchAProductException e) {
            ErrorUtil.sendError(response, HttpStatus.BAD_REQUEST, e.getMessage());
            return null;
        }
    }

    @PostMapping("/customer/show_discounts")
    private ResponseEntity<?> showDisCodes(HttpServletRequest request, HttpServletResponse response) {
        try {
            if (TokenUtil.checkToken(response, request)) {
                String username = TokenUtil.getUsernameFromToken(request);
                Customer customer = customerService.getCustomerByUsername(username);
                List<DiscountcodeIntegerMap> disCodes = customer.getDiscountCodes();
                List<DisCodeUserDTO> disCodeUserDTOS = new ArrayList<>();
                for (DiscountcodeIntegerMap disCode : disCodes) {
                    disCodeUserDTOS.add(createDisCodeDTOFrom(disCode));
                }
                return ResponseEntity.ok(new DisCodeUserListDTO(new ArrayList<>(disCodeUserDTOS)));
            } else {
                return null;
            }
        } catch (Exception e) {
            ErrorUtil.sendError(response, HttpStatus.BAD_REQUEST, e.getMessage());
            return null;
        }
    }

    @PostMapping("/customer/assign_score")
    private ResponseEntity<?> assignAScore(HttpServletRequest request, HttpServletResponse response, @RequestBody String info) {
        try {
            if (TokenUtil.checkToken(response, request)) {
                String username = TokenUtil.getUsernameFromToken(request);
                int productId = Integer.parseInt(info.split(",")[0]);
                int score = Integer.parseInt(info.split(",")[1]);
                feedbackService.createScore(username, productId, score);
                return ResponseEntity.ok(ResponseEntity.status(200));
            } else {
                return null;
            }
        } catch (NoSuchACustomerException | NoSuchAProductException | NotABuyer e) {
            ErrorUtil.sendError(response, HttpStatus.BAD_REQUEST, e.getMessage());
            return null;
        }
    }

    private DisCodeUserDTO createDisCodeDTOFrom(DiscountcodeIntegerMap disCode) {
        DiscountCode discountCode = disCode.getDiscountCode();
        return new DisCodeUserDTO(
            discountCode.getCode(),
            discountCode.getStartTime(),
            discountCode.getEndTime(),
            discountCode.getOffPercentage(),
            discountCode.getMaxDiscount(),
            disCode.getInteger()
        );
    }

    private OrderLogDTO createOrder(int id) throws NoSuchALogException, NoSuchAProductException {
        PurchaseLog purchaseLog = (PurchaseLog) logService.getLogById(id);
        ArrayList<OrderProductDTO> orderProductDTOS = createOrderProductDTOS(purchaseLog);
        float realPrice = (float) purchaseLog.getPricePaid() / (1 + (float) purchaseLog.getDiscount() / 100);
        return new OrderLogDTO(
                purchaseLog.getLogId(),
                new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(purchaseLog.getDate()),
                orderProductDTOS,
                purchaseLog.getDeliveryStatus().toString(),
                (long)realPrice,
                purchaseLog.getPricePaid(),
                purchaseLog.getDiscount()
        );
    }

    private ArrayList<OrderProductDTO> createOrderProductDTOS(PurchaseLog purchaseLog) throws NoSuchAProductException {
        List<SoldProductSellerMap> soldProductSellerMaps = purchaseLog.getProductsAndItsSellers();
        ArrayList<OrderProductDTO> orderProductDTOS = new ArrayList<>();
        for (SoldProductSellerMap soldProductSellerMap : soldProductSellerMaps) {
            int id = soldProductSellerMap.getSoldProduct().getSourceId();
            Product product = productService.findById(id);
            orderProductDTOS.add(createOrderProductDTOFrom(product, soldProductSellerMap.getSeller()));
        }
        return orderProductDTOS;
    }

    private OrderProductDTO createOrderProductDTOFrom(Product product, String sellerId) {
        OrderProductDTO orderProductDTO = null;
        for (SellPackage aPackage : product.getPackages()) {
            if (aPackage.getSeller().getUsername().equals(sellerId)) {
                orderProductDTO = new OrderProductDTO(
                        product.getId(),
                        product.getName(),
                        sellerId,
                        aPackage.getPrice()
                );
            }
        }
        return orderProductDTO;
    }

    private InCartDTO createInCartDTOFrom(SubCart subCart) throws NoSuchSellerException {
        MiniProductDto miniProductDto = dtoFromProduct(subCart.getProduct());
        int price = findPriceForSpecialSeller(subCart.getSeller(), subCart.getProduct());
        int offPrice = findOffPriceFor(subCart, price);
        return new InCartDTO(
                miniProductDto,
                miniProductDto.getName(),
                subCart.getSeller().getUsername(),
                price,
                offPrice,
                subCart.getAmount(),
                subCart.getAmount() * (offPrice == 0 ? price : offPrice)
        );
    }

    private int findOffPriceFor(SubCart subCart, int price) throws NoSuchSellerException {
        Off off = subCart.getProduct().findPackageBySeller(subCart.getSeller().getUsername()).getOff();
        if (off != null) {
            return price * (100 - off.getOffPercentage()) / 100;
        }
        return 0;
    }

    private int findPriceForSpecialSeller(Seller seller, Product product) throws NoSuchSellerException {
        return product.findPackageBySeller(seller).getPrice();
    }
}
