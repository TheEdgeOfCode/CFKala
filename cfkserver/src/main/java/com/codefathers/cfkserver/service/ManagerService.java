package com.codefathers.cfkserver.service;

import com.codefathers.cfkserver.model.dtos.user.UserFullDTO;
import com.codefathers.cfkserver.model.entities.maps.UserIntegerMap;
import com.codefathers.cfkserver.model.entities.offs.DiscountCode;
import com.codefathers.cfkserver.model.entities.offs.Off;
import com.codefathers.cfkserver.model.entities.product.Product;
import com.codefathers.cfkserver.model.entities.product.SellPackage;
import com.codefathers.cfkserver.model.entities.request.Request;
import com.codefathers.cfkserver.model.entities.user.*;
import com.codefathers.cfkserver.model.repositories.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CopyOnWriteArrayList;

@Service
public class ManagerService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ManagerRepository managerRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private UserIntegerMapRepository userIntegerMapRepository;

    @Autowired
    private DiscountRepository discountRepository;

    @Autowired
    private SellerRepository sellerRepository;

    @Autowired
    private OffRepository offRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private SellPackageRepository sellPackageRepository;

    @Autowired
    private OffService offService;

    @Autowired
    private RequestRepository requestRepository;

    @Autowired
    private SubCartRepository subCartRepository;

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private Sorter sorter;

    public List<User> getAllUsers() {
        List<User> users =  userRepository.findAllBy();
        sorter.sortUser(users);
        return users;
    }

    public void deleteUser(String username) {
        Optional<User> user = userRepository.findById(username);
        if (user.isPresent() && user.get() instanceof Manager) {
            deleteManager(username);
        } else if (user.isPresent() && user.get() instanceof Customer) {
            deleteCustomer(username);
        } else if (user.isPresent() && user.get() instanceof Seller) {
            deleteSeller(username);
        }
    }

    private void deleteSeller(String username) {
        Optional<Seller> seller = sellerRepository.findById(username);
        if (seller.isPresent()) {
            deletePackages(seller.get());
            deleteAllOffs(seller.get());
            seller.get().setOffs(new ArrayList<>());
            deleteAllRequests(seller.get());
            seller.get().setRequests(new ArrayList<>());
            deleteAllSubCarts(seller.get());
            seller.get().setCart(null);
            seller.get().setCompany(null);
            sellerRepository.delete(seller.get());
        }
    }

    private void deleteAllSubCarts(Seller seller) {
        List<SubCart> subCarts = subCartRepository.findAllBySeller(seller);
        subCarts.forEach(subCart -> {
            Cart cart = subCart.getCart();
            cart.getSubCarts().remove(subCart);
            cartRepository.save(cart);
            subCartRepository.delete(subCart);
        });
    }

    private void deleteAllRequests(Seller seller) {
        List<Request> requests = requestRepository.findAllBySeller(seller);
        requests.forEach(request -> {
            request.setSeller(null);
            requestRepository.save(request);
        });
    }

    private void deleteAllOffs(Seller seller) {
        List<Off> offs = new CopyOnWriteArrayList<>(seller.getOffs());
        for (Off off : offs) {
            offService.deleteOff(off);
        }
    }

    private void deletePackages(Seller seller) {
        List<SellPackage> sellPackages = new CopyOnWriteArrayList<>(seller.getPackages());
        for (SellPackage sellPackage : sellPackages) {
            deletePackage(sellPackage, seller);
        }
    }

    private void deletePackage(SellPackage sellPackage, Seller seller) {
        if (sellPackage.isOnOff()) {
            Off off = sellPackage.getOff();
            off.getProducts().remove(sellPackage.getProduct());
            offRepository.save(off);
            sellPackage.setOff(null);
        }
        List<SellPackage> packages = seller.getPackages();
        packages.remove(sellPackage);
        seller.setPackages(new ArrayList<>(packages));
        Product product = sellPackage.getProduct();
        List<SellPackage> sellPackages = product.getPackages();
        sellPackages.remove(sellPackage);
        product.setPackages(new ArrayList<>(sellPackages));
        sellPackage.setProduct(null);
        sellPackage.setSeller(null);
        productRepository.save(product);
        sellerRepository.save(seller);
        sellPackageRepository.delete(sellPackage);
    }

    private void deleteCustomer(String username) {
        Optional<Customer> customer = customerRepository.findById(username);
        if (customer.isPresent()) {
            deleteAllUserIntegerMaps(customer.get());
            customerRepository.delete(customer.get());
        }
    }

    private void deleteAllUserIntegerMaps(User customer) {
        UserIntegerMap userIntegerMap = userIntegerMapRepository.findByUser(customer);
        DiscountCode discountCode = userIntegerMap.getDiscountCode();
        discountCode.getUsers().remove(userIntegerMap);
        discountRepository.save(discountCode);
        userIntegerMapRepository.delete(userIntegerMap);
    }

    private void deleteManager(String username) {
        Optional<Manager> manager = managerRepository.findById(username);
        manager.ifPresent(value -> managerRepository.delete(value));
    }

    public Boolean isFirstManager(){
        return !managerRepository.existsAllBy();
    }

}
