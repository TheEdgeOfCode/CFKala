package com.codefathers.cfkserver.controller;

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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;


@RestController
public class CustomerController {
    @Autowired
    private CustomerService customerService;

    @PostMapping("/customer/show_cart")
    private ResponseEntity<?> showCart(String username) {
        try {
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
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage(), e);
        }
    }

    private InCartDTO createInCartDTOFrom(SubCart subCart) throws NoSuchSellerException {
        MiniProductDto miniProductDto = createMiniProductDTOFrom(subCart.getProduct());
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

    private MiniProductDto createMiniProductDTOFrom(Product product) {
        List<SellPackageDto> sellPackageDtos = new ArrayList<>();
        product.getPackages().forEach(sellPackage -> {
            int offPercent = sellPackage.isOnOff()? sellPackage.getOff().getOffPercentage() : 0;
            sellPackageDtos.add(new SellPackageDto(
                    offPercent,
                    sellPackage.getPrice(),
                    sellPackage.getStock(),
                    sellPackage.getSeller().getUsername(),
                    sellPackage.isAvailable()));
        });
        return new MiniProductDto(
                product.getName(),
                product.getId(),
                product.getCategory().getName(),
                sellPackageDtos,
                product.getCompanyClass().getName(),
                product.getTotalScore(),
                product.getDescription(),
                product.isAvailable());
    }

}
