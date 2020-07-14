package com.codefathers.cfkserver.controller;

import antlr.Token;
import com.codefathers.cfkserver.exceptions.model.product.NoSuchSellerException;
import com.codefathers.cfkserver.model.dtos.customer.CartDTO;
import com.codefathers.cfkserver.model.dtos.customer.InCartDTO;
import com.codefathers.cfkserver.model.dtos.product.MiniProductDto;
import com.codefathers.cfkserver.model.dtos.product.SellPackageDto;
import com.codefathers.cfkserver.model.entities.offs.Off;
import com.codefathers.cfkserver.model.entities.product.Product;
import com.codefathers.cfkserver.model.entities.user.Cart;
import com.codefathers.cfkserver.model.entities.user.Customer;
import com.codefathers.cfkserver.model.entities.user.Seller;
import com.codefathers.cfkserver.model.entities.user.SubCart;
import com.codefathers.cfkserver.service.CustomerService;
import com.codefathers.cfkserver.utils.ErrorUtil;
import com.codefathers.cfkserver.utils.TokenUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;

import static com.codefathers.cfkserver.controller.ProductController.dtoFromProduct;


@RestController
public class CustomerController {
    @Autowired
    private CustomerService customerService;

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
