package com.codefathers.cfkserver.service;

import com.codefathers.cfkserver.exceptions.model.discount.NoMoreDiscount;
import com.codefathers.cfkserver.exceptions.model.discount.NoSuchADiscountCodeException;
import com.codefathers.cfkserver.exceptions.model.discount.UserNotExistedInDiscountCodeException;
import com.codefathers.cfkserver.exceptions.model.product.NoSuchAProductException;
import com.codefathers.cfkserver.exceptions.model.product.NoSuchSellerException;
import com.codefathers.cfkserver.exceptions.model.user.NoSuchACustomerException;
import com.codefathers.cfkserver.exceptions.model.user.NoSuchAPackageException;
import com.codefathers.cfkserver.exceptions.model.user.NotEnoughMoneyException;
import com.codefathers.cfkserver.model.entities.logs.PurchaseLog;
import com.codefathers.cfkserver.model.entities.maps.DiscountcodeIntegerMap;
import com.codefathers.cfkserver.model.entities.offs.DiscountCode;
import com.codefathers.cfkserver.model.entities.product.SellPackage;
import com.codefathers.cfkserver.model.entities.user.*;
import com.codefathers.cfkserver.model.repositories.CustomerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CustomerService {
    @Autowired
    private CustomerRepository customerRepository;
    @Autowired
    private DiscountService discountService;
    @Autowired
    private SellerService sellerService;
    @Autowired
    private ProductService productService;

    public Customer getCustomerByUsername(String username) throws NoSuchACustomerException {
        Optional<Customer> customer = customerRepository.findById(username);
        if (customer.isPresent())
            return customer.get();
        else throw new NoSuchACustomerException();
    }

    public List<PurchaseLog> viewOrders(String username) throws NoSuchACustomerException {
        Customer customer = getCustomerByUsername(username);
        return customer.getPurchaseLogs();
    }

    public List<DiscountcodeIntegerMap> viewDiscountCodes(String username) throws NoSuchACustomerException {
        Customer customer = getCustomerByUsername(username);
        return customer.getDiscountCodes();
    }

    public void purchase(String username, CustomerInformation customerInformation, DiscountCode discountCode)
            throws NotEnoughAmountOfProductException, NoSuchAProductException, NoSuchSellerException,
            NoSuchAPackageException, NoSuchACustomerException {
        Customer customer = getCustomerByUsername(username);

        Cart cart = customer.getCart();
        checkIfThereIsEnoughAmount(customer);
        purchaseForCustomer(customer, customerInformation, discountCode);
        sellerService.getMoneyFromSale(cart);

        if (discountCode != null) {
            try {
                discountService.useADiscount(customer, discountCode.getCode());
                csclManager.createPurchaseLog(cart, discountCode.getOffPercentage(), customer);
            } catch (NoSuchADiscountCodeException | UserNotExistedInDiscountCodeException | NoMoreDiscount e) {
                csclManager.createPurchaseLog(cart, 0, customer);
                e.printStackTrace();
            }
        } else {
            csclManager.createPurchaseLog(cart, 0, customer);
        }
        String name = customer.getUsername();
        cart.getSubCarts().forEach(subCart -> {
            try {
                csclManager.createSellLog(subCart, name);
                ProductManager.getInstance().addBought(subCart.getProduct().getId(), subCart.getAmount());
            } catch (NoSuchAPackageException | NoSuchAProductException e) {
                e.printStackTrace();
            }
        });
        productChangeInPurchase(customer);
        DBManager.save(customer);
        csclManager.emptyCart(cart);
    }

    public void checkIfThereIsEnoughAmount(Customer customer) throws NotEnoughAmountOfProductException, NoSuchAProductException, NoSuchSellerException {
        Cart cart = customer.getCart();

        for (SubCart subCart : cart.getSubCarts()) {
            cartService.checkIfThereIsEnoughAmountOfProduct(
                    subCart.getProduct().getId(),
                    subCart.getSeller().getUsername(),
                    subCart.getAmount()
            );
        }
    }

    public void purchaseForCustomer(Customer customer, CustomerInformation customerInformation, DiscountCode discountCode)
            throws NoSuchAPackageException, NotEnoughMoneyException {
        long totalPrice = getTotalPrice(discountCode, customer);

        long difference = totalPrice - customer.getBalance();

        checkIfCustomerHasEnoughMoney(difference);

        customer.setBalance(customer.getBalance() - totalPrice);
        customer.getCustomerInformation().add(customerInformation);
    }

    public void productChangeInPurchase(Customer customer) throws NoSuchSellerException, NoSuchAProductException {
        Cart cart = customer.getCart();

        for (SubCart subCart : cart.getSubCarts()) {
            productService.changeAmountOfStock(
                    subCart.getProduct().getId(),
                    subCart.getSeller().getUsername(),
                    -subCart.getAmount()
            );
        }
    }

    public long getTotalPrice(DiscountCode discountCode, Customer customer) throws NoSuchAPackageException {
        Cart cart = customer.getCart();
        long totalPrice = 0;

        for (SubCart subCart : cart.getSubCarts()) {
            totalPrice = findPrice(subCart);
        }

        if (discountCode != null) {
            double discount = (double) cart.getTotalPrice() * discountCode.getOffPercentage() / 100;
            if (discount > discountCode.getMaxDiscount()) {
                totalPrice = cart.getTotalPrice() - discountCode.getMaxDiscount();
            } else {
                totalPrice = cart.getTotalPrice() - (int) discount;
            }
        }
        return totalPrice;
    }

    long findPrice(SubCart subCart) throws NoSuchAPackageException {
        SellPackage sellPackage = subCart.getSeller().findPackageByProductId(subCart.getProduct().getId());
        return sellPackage.isOnOff() ? sellPackage.getPrice() * (100 - sellPackage.getOff().getOffPercentage()) / 100 : sellPackage.getPrice();
    }

    public void checkIfCustomerHasEnoughMoney(long difference) throws NotEnoughMoneyException {
        if (difference > 0) {
            throw new NotEnoughMoneyException(difference);
        }
    }

    public void addPurchaseLog(PurchaseLog log, Customer customer) {
        customer.getPurchaseLogs().add(log);
        customerRepository.save(customer);
    }

    public void save(Customer customer) {
        customerRepository.save(customer);
    }
}
