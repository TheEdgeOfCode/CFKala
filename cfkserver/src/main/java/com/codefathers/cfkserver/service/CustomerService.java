package com.codefathers.cfkserver.service;

import com.codefathers.cfkserver.exceptions.model.bank.account.InvalidUsernameException;
import com.codefathers.cfkserver.exceptions.model.bank.receipt.*;
import com.codefathers.cfkserver.exceptions.model.cart.NotEnoughAmountOfProductException;
import com.codefathers.cfkserver.exceptions.model.discount.NoMoreDiscount;
import com.codefathers.cfkserver.exceptions.model.discount.NoSuchADiscountCodeException;
import com.codefathers.cfkserver.exceptions.model.discount.UserNotExistedInDiscountCodeException;
import com.codefathers.cfkserver.exceptions.model.product.NoSuchAProductException;
import com.codefathers.cfkserver.exceptions.model.product.NoSuchSellerException;
import com.codefathers.cfkserver.exceptions.model.user.NoSuchACustomerException;
import com.codefathers.cfkserver.exceptions.model.user.NoSuchAPackageException;
import com.codefathers.cfkserver.exceptions.model.user.NotEnoughMoneyException;
import com.codefathers.cfkserver.exceptions.model.user.UserNotFoundException;
import com.codefathers.cfkserver.exceptions.token.ExpiredTokenException;
import com.codefathers.cfkserver.exceptions.token.InvalidTokenException;
import com.codefathers.cfkserver.model.dtos.bank.BalanceDTO;
import com.codefathers.cfkserver.model.dtos.bank.CreateReceiptDTO;
import com.codefathers.cfkserver.model.dtos.bank.ReceiptType;
import com.codefathers.cfkserver.model.entities.logs.PurchaseLog;
import com.codefathers.cfkserver.model.entities.maps.DiscountcodeIntegerMap;
import com.codefathers.cfkserver.model.entities.offs.DiscountCode;
import com.codefathers.cfkserver.model.entities.product.SellPackage;
import com.codefathers.cfkserver.model.entities.user.*;
import com.codefathers.cfkserver.model.repositories.CustomerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
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
    @Autowired
    private LogService logService;
    @Autowired
    private CartService cartService;
    @Autowired
    private BankService bankService;

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
            NoSuchAPackageException, NoSuchACustomerException, NotEnoughMoneyException,
            InvalidTokenException, ExpiredTokenException, InvalidUsernameException, IOException,
            NotEnoughMoneyAtSourceException, InvalidDestAccountException,
            InvalidSourceAccountException, InvalidAccountIdException, InvalidMoneyException,
            PaidReceiptException, InvalidDescriptionExcxeption, InvalidParameterPassedException,
            InvalidRecieptTypeException, InvalidReceiptIdException, EqualSourceDestException {
        Customer customer = getCustomerByUsername(username);

        Cart cart = customer.getCart();
        checkIfThereIsEnoughAmount(customer);
        purchaseForCustomer(customer, customerInformation, discountCode);
        sellerService.getMoneyFromSale(cart);

        if (discountCode != null) {
            try {
                discountService.useADiscount(customer, discountCode.getCode());
                logService.createPurchaseLog(cart, discountCode.getOffPercentage(), customer);
            } catch (NoSuchADiscountCodeException | UserNotExistedInDiscountCodeException | NoMoreDiscount e) {
                logService.createPurchaseLog(cart, 0, customer);
                e.printStackTrace();
            }
        } else {
            logService.createPurchaseLog(cart, 0, customer);
        }
        String name = customer.getUsername();
        cart.getSubCarts().forEach(subCart -> {
            try {
                logService.createSellLog(subCart, name);
                productService.addBought(subCart.getProduct().getId(), subCart.getAmount());
            } catch (NoSuchAPackageException | NoSuchAProductException | UserNotFoundException e) {
                e.printStackTrace();
            }
        });
        productChangeInPurchase(customer);
        save(customer);
        cartService.emptyCart(cart);
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
            throws NoSuchAPackageException, NotEnoughMoneyException, InvalidTokenException,
            ExpiredTokenException, InvalidUsernameException, IOException,
            InvalidRecieptTypeException, InvalidSourceAccountException,
            EqualSourceDestException, InvalidDescriptionExcxeption,
            InvalidMoneyException, InvalidDestAccountException, InvalidParameterPassedException,
            InvalidAccountIdException, NotEnoughMoneyAtSourceException,
            PaidReceiptException, InvalidReceiptIdException {
        long totalPrice = getTotalPrice(discountCode, customer);

        //long difference = totalPrice - customer.getBalance();

        BalanceDTO dto = new BalanceDTO(customer.getUsername(), customer.getPassword(), customerInformation.getToken());
        long difference = totalPrice - bankService.getBalance(dto);

        checkIfCustomerHasEnoughMoney(difference);

        //customer.setBalance(customer.getBalance() - totalPrice);

        int receiptId = makeReceiptIdForCustomer(customer, customerInformation, totalPrice);

        bankService.pay(receiptId);

        customer.getCustomerInformation().add(customerInformation);
    }

    private int makeReceiptIdForCustomer(Customer customer, CustomerInformation customerInformation, long totalPrice) throws IOException, InvalidRecieptTypeException, InvalidMoneyException, InvalidParameterPassedException, InvalidTokenException, ExpiredTokenException, InvalidSourceAccountException, InvalidDestAccountException, EqualSourceDestException, InvalidAccountIdException, InvalidDescriptionExcxeption, InvalidUsernameException {
        return bankService.createReceipt(new CreateReceiptDTO(customer.getUsername(),
                customer.getPassword(),
                customerInformation.getToken(),
                ReceiptType.WITHDRAW,
                totalPrice,
                Integer.parseInt(customer.getAccountId()),
                -1,
                "Purchase"));
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

    int findPrice(SubCart subCart) throws NoSuchAPackageException {
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
