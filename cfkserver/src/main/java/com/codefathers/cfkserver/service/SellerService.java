package com.codefathers.cfkserver.service;

import com.codefathers.cfkserver.exceptions.model.product.NoSuchAProductException;
import com.codefathers.cfkserver.exceptions.model.product.NoSuchSellerException;
import com.codefathers.cfkserver.exceptions.model.user.NoSuchAPackageException;
import com.codefathers.cfkserver.model.entities.logs.SellLog;
import com.codefathers.cfkserver.model.entities.offs.Off;
import com.codefathers.cfkserver.model.entities.product.Company;
import com.codefathers.cfkserver.model.entities.product.Product;
import com.codefathers.cfkserver.model.entities.product.SellPackage;
import com.codefathers.cfkserver.model.entities.user.Cart;
import com.codefathers.cfkserver.model.entities.user.Seller;
import com.codefathers.cfkserver.model.entities.user.SubCart;
import com.codefathers.cfkserver.model.repositories.*;
import org.hibernate.Session;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class SellerService {
    @Autowired
    private SellerRepository sellerRepository;
    @Autowired
    private OffRepository offRepository;
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private SellPackageRepository sellPackageRepository;
    @Autowired
    private CartRepository cartRepository;
    @Autowired
    private SubCartRepository subCartRepository;
    @Autowired
    private ProductService productService;
    @Autowired
    private CustomerService customerService;

    public Seller findSellerByUsername(String username) throws NoSuchSellerException {
        Optional<Seller> seller = sellerRepository.findById(username);
        if (seller.isPresent()){
            return seller.get();
        }else {
            throw new NoSuchSellerException("Seller ("+ username + ") Doesn't Exist");
        }
    }

    public Company viewCompanyInformation(String username) throws NoSuchSellerException {
        Seller seller = findSellerByUsername(username);
        return seller.getCompany();
    }

    public List<SellLog> viewSalesHistory(String username) throws NoSuchSellerException {
        Seller seller = findSellerByUsername(username);
        return seller.getSellLogs();
    }

    public List<Product> viewProducts(String username) throws NoSuchSellerException {
        Seller seller = findSellerByUsername(username);
        List<Product> toReturn = new ArrayList<>();
        if (seller.getPackages().size() != 0)
            seller.getPackages().forEach(sellPackage -> toReturn.add(sellPackage.getProduct()));
        return toReturn;
    }

    public void deleteProductForSeller(String username, int productId) throws NoSuchAPackageException, NoSuchSellerException {
        Seller seller = findSellerByUsername(username);
        SellPackage sellPackage = seller.findPackageByProductId(productId);
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
        deleteAllSubCarts(seller, product);
        sellPackage.setProduct(null);
        sellPackage.setSeller(null);
        productRepository.save(product);
        saveSeller(seller);
        sellPackageRepository.delete(sellPackage);
    }

    private void deleteAllSubCarts(Seller seller, Product product) {
        List<SubCart> subCarts = getAllSubCarts(seller, product);
        subCarts.forEach(subCart -> {
            Cart cart = subCart.getCart();
            cart.getSubCarts().remove(subCart);
            cartRepository.save(cart);
            subCartRepository.delete(subCart);
        });
    }

    private List<SubCart> getAllSubCarts(Seller seller, Product product) {
        //TODO:
        /*Session session = HibernateUtil.getSession();
        CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();
        CriteriaQuery<SubCart> criteriaQuery = criteriaBuilder.createQuery(SubCart.class);
        Root<SubCart> root = criteriaQuery.from(SubCart.class);
        criteriaQuery.select(root);
        Predicate[] predicates = {
                criteriaBuilder.equal(root.get("seller").as(Seller.class), seller),
                criteriaBuilder.equal(root.get("product").as(Product.class), product)
        };
        criteriaQuery.where(
                predicates
        );
        Query<SubCart> query = session.createQuery(criteriaQuery);
        return query.getResultList();*/
    }

    public List<Seller> viewSellersOfProduct(int productId)
            throws NoSuchAProductException {
        Product product = productService.findById(productId);
        List<Seller> sellers = new ArrayList<>();
        product.getPackages().forEach(sellPackage -> sellers.add(sellPackage.getSeller()));
        return sellers;
    }

    public void getMoneyFromSale(Cart cart) throws NoSuchAPackageException {
        Seller seller;
        int off;
        long price;
        long balance;

        for (SubCart subCart : cart.getSubCarts()) {
            seller = subCart.getSeller();
            price = customerService.findPrice(subCart);
            off = getOff(seller, subCart);
            balance = (long) (seller.getBalance() + price * subCart.getAmount() * (double) (100 - off) / 100);
            seller.setBalance(balance);
            saveSeller(seller);
        }
    }

    private int getOff(Seller seller, SubCart subCart) {
        for (SellPackage product : subCart.getProduct().getPackages()) {
            if (product.getSeller().equals(seller)) {
                if (product.isOnOff()) {
                    return product.getOff().getOffPercentage();
                }
            }
        }
        return 0;
    }

    public void addASellLog(SellLog sellLog, Seller seller) {
        seller.getSellLogs().add(sellLog);
        saveSeller(seller);
    }

    public void saveSeller(Seller seller){
        sellerRepository.save(seller);
    }
}
