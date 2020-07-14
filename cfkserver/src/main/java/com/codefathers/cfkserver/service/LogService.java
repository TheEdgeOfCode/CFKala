package com.codefathers.cfkserver.service;

import com.codefathers.cfkserver.exceptions.model.log.NoSuchALogException;
import com.codefathers.cfkserver.exceptions.model.user.NoSuchAPackageException;
import com.codefathers.cfkserver.exceptions.model.user.UserNotFoundException;
import com.codefathers.cfkserver.model.entities.logs.DeliveryStatus;
import com.codefathers.cfkserver.model.entities.logs.Log;
import com.codefathers.cfkserver.model.entities.logs.PurchaseLog;
import com.codefathers.cfkserver.model.entities.logs.SellLog;
import com.codefathers.cfkserver.model.entities.maps.SoldProductSellerMap;
import com.codefathers.cfkserver.model.entities.product.Product;
import com.codefathers.cfkserver.model.entities.product.SellPackage;
import com.codefathers.cfkserver.model.entities.product.SoldProduct;
import com.codefathers.cfkserver.model.entities.user.Cart;
import com.codefathers.cfkserver.model.entities.user.Customer;
import com.codefathers.cfkserver.model.entities.user.SubCart;
import com.codefathers.cfkserver.model.entities.user.User;
import com.codefathers.cfkserver.model.repositories.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class LogService {
    @Autowired
    private PurchaseLogRepository purchaseLogRepository;
    @Autowired
    private SellLogRepository sellLogRepository;
    @Autowired
    private UserService userService;
    @Autowired
    private SoldProductRepository soldProductRepository;
    @Autowired
    private SellerService sellerService;
    @Autowired
    private CartRepository cartRepository;
    @Autowired
    private CustomerService customerService;
    @Autowired
    private LogRepository logRepository;

    public void createSellLog(SubCart subCart, String buyerId) throws NoSuchAPackageException, UserNotFoundException {
        Product product = subCart.getProduct();
        int moneyGotten = customerService.findPrice(subCart);
        User user = userService.getUserByUsername(buyerId);
        SoldProduct soldProduct = new SoldProduct();
        soldProduct.setSourceId(product.getId());
        soldProduct.setSoldPrice(moneyGotten);
        int discount = getOffPercent(subCart);
        SellLog log = new SellLog(soldProduct, moneyGotten, discount, user, new Date(), DeliveryStatus.DEPENDING);
        soldProductRepository.save(soldProduct);
        sellerService.addASellLog(log, subCart.getSeller());
    }

    public void emptyCart(Cart cart) {
        cart.setSubCarts(new ArrayList<>());
        cartRepository.save(cart);
    }

    private int getOffPercent(SubCart subCart) {
        try {
            SellPackage sellPackage = subCart.getSeller().findPackageByProductId(subCart.getProduct().getId());
            if (sellPackage.isOnOff())
                return sellPackage.getOff().getOffPercentage();
            else return 0;
        } catch (NoSuchAPackageException e) {
            return 0;
        }
    }

    public void createPurchaseLog(Cart cart, int discount, Customer customer) {
        List<SoldProductSellerMap> map = new ArrayList<>();
        int pricePaid = 0;
        for (SubCart subCart : cart.getSubCarts()) {
            SoldProduct soldProduct = new SoldProduct();
            soldProduct.setSourceId(subCart.getProduct().getId());
            try {
                int price = customerService.findPrice(subCart);
                soldProduct.setSoldPrice(price);
                pricePaid += price * subCart.getAmount();
                SoldProductSellerMap toAdd = new SoldProductSellerMap();
                toAdd.setSeller(subCart.getSeller().getUsername());
                toAdd.setSoldProduct(soldProduct);
                map.add(toAdd);
            } catch (NoSuchAPackageException e) {
                e.printStackTrace();
            }
        }
        PurchaseLog log = new PurchaseLog(new Date(), DeliveryStatus.DEPENDING, map, pricePaid, discount);
        purchaseLogRepository.save(log);

       customerService.addPurchaseLog(log, customer);
    }

    public Log getLogById(int id) throws NoSuchALogException {
        Optional<Log> log = logRepository.findById(id);
        if (log.isPresent()) {
            return log.get();
        }
        else throw new NoSuchALogException(Integer.toString(id));
    }
}
