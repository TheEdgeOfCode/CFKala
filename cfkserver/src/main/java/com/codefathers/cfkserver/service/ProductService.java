package com.codefathers.cfkserver.service;

import com.codefathers.cfkserver.exceptions.model.category.CategoryNotFoundException;
import com.codefathers.cfkserver.exceptions.model.company.NoSuchACompanyException;
import com.codefathers.cfkserver.exceptions.model.product.EditorIsNotSellerException;
import com.codefathers.cfkserver.exceptions.model.product.NoSuchAProductException;
import com.codefathers.cfkserver.exceptions.model.product.NoSuchSellerException;
import com.codefathers.cfkserver.model.dtos.product.CreateProductDTO;
import com.codefathers.cfkserver.model.entities.offs.Off;
import com.codefathers.cfkserver.model.entities.product.*;
import com.codefathers.cfkserver.model.entities.request.Request;
import com.codefathers.cfkserver.model.entities.request.RequestType;
import com.codefathers.cfkserver.model.entities.request.edit.ProductEditAttribute;
import com.codefathers.cfkserver.model.entities.user.Cart;
import com.codefathers.cfkserver.model.entities.user.Seller;
import com.codefathers.cfkserver.model.entities.user.SubCart;
import com.codefathers.cfkserver.model.repositories.*;
import org.apache.tomcat.util.http.fileupload.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CopyOnWriteArrayList;

@Service
public class ProductService {
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private CompanyService companyService;
    @Autowired
    private SellerService sellerService;
    @Autowired
    private CategoryService categoryService;
    @Autowired
    private RequestService requestService;
    @Autowired
    private ProductEditAttributeRepository attributeRepository;
    @Autowired
    private SellPackageRepository sellPackageRepository;
    @Autowired
    private CommentRepository commentRepository;
    @Autowired
    private SubCartRepository subCartRepository;
    @Autowired
    private CartRepository cartRepository;
    @Autowired
    private RequestRepository requestRepository;
    @Autowired
    private OffRepository offRepository;

    public List<Product> getAllActiveProduct(){
        return productRepository.findAllByProductStatusEquals(ProductStatus.VERIFIED);
    }

    public Product findById(int id) throws NoSuchAProductException {
        Optional<Product> productOptional = productRepository.findById(id);
        if (productOptional.isPresent()) {
            return productOptional.get();
        }else {
            throw new NoSuchAProductException("There Isn't Any Product With ID (" + id + ")");
        }
    }

    public void createProduct(CreateProductDTO dto)
            throws NoSuchACompanyException, NoSuchSellerException, CategoryNotFoundException {
        Product product = createProductFromDto(dto);
        productRepository.save(product);
        String username = dto.getSellerName();
        String request = String.format("User \"%20s\" Requested to Create Product\" %30s\"",
                username, product.getName());
        requestService.createRequest(product, RequestType.CREATE_PRODUCT,request,username);
    }

    private Product createProductFromDto(CreateProductDTO dto)
            throws NoSuchACompanyException, NoSuchSellerException, CategoryNotFoundException {
        Company company = companyService.getCompanyById(dto.getCompanyId());
        Seller seller = sellerService.findSellerByUsername(dto.getSellerName());
        Category category = categoryService.findCategoryById(dto.getCategoryId());
        SellPackage sellPackage = new SellPackage(null,seller,dto.getPrice(),dto.getAmount());
        Product product = new Product(dto.getProductName(),company,category,dto.getPublicFeatures(),
                dto.getSpecialFeature(),dto.getDescription(),sellPackage);
        sellPackage.setProduct(product);
        return product;
    }

    public void editProduct(String editor, ProductEditAttribute attribute)
            throws EditorIsNotSellerException, NoSuchSellerException, NoSuchAProductException {
        String requestStr = String.format("%s has requested to edit %s",editor,attribute);
        attributeRepository.save(attribute);
        Product product = findById(attribute.getSourceId());
        checkIfEditorIsASeller(editor,product);
        product.setProductStatus(ProductStatus.UNDER_EDIT);
        Request request = requestService.createRequest(attribute,RequestType.EDIT_PRODUCT,editor,requestStr);
        Seller seller = sellerService.findSellerByUsername(editor);
        seller.addRequest(request);
        sellerService.saveSeller(seller);
    }

    private void checkIfEditorIsASeller(String username,Product product) throws EditorIsNotSellerException {
        if (!product.hasSeller(username))throw new EditorIsNotSellerException();
    }

    public void changeAmountOfStock(int productId, String sellerId, int amount)
            throws NoSuchSellerException, NoSuchAProductException {
        Product product = findById(productId);
        SellPackage sellPackage = product.findPackageBySeller(sellerId);
        int stock = sellPackage.getStock();
        stock += amount;
        if (stock < 0) stock = 0;
        sellPackage.setStock(stock);
        sellPackageRepository.save(sellPackage);
    }

    public List<Product> findProductsByName(String name){
        return productRepository.findAllByNameContains(name);
    }

    public void addView(int productId) throws NoSuchAProductException {
        Product product = findById(productId);
        product.setView(product.getView()+1);
        productRepository.save(product);
    }

    public void addBought(int productId, int amount) throws NoSuchAProductException {
        Product product = findById(productId);
        product.setBoughtAmount(product.getBoughtAmount() + amount);
        productRepository.save(product);
    }

    public void assignAComment(int productId, Comment comment) throws NoSuchAProductException {
        Product product = findById(productId);
        List<Comment> comments = product.getAllComments();
        comments.add(comment);
        product.setAllComments(comments);
        productRepository.save(product);
        commentRepository.save(comment);
    }

    public void assignAScore(int productId, Score score) throws NoSuchAProductException {
        Product product = findById(productId);
        List<Score> scores = product.getAllScores();
        int amount = scores.size();
        scores.add(score);
        product.setAllScores(scores);
        product.setTotalScore((product.getTotalScore()*amount + score.getScore())/(amount+1));
        productRepository.save(product);
    }

    public Comment[] getAllComment(int productId) throws NoSuchAProductException {
        Product product = findById(productId);
        List<Comment> comments = new CopyOnWriteArrayList<>(product.getAllComments());
        for (Comment comment : comments) {
            if (!comment.getStatus().equals(CommentStatus.VERIFIED)) comments.remove(comment);
        }
        Comment[] toReturn = new Comment[comments.size()];
        comments.toArray(toReturn);
        return toReturn;
    }

    public void deleteProduct(int productId) throws NoSuchAProductException {
        Product product = findById(productId);
        deleteProduct(product);
    }

    public void deleteProduct(Product product) {
        List<SellPackage> packages = product.getPackages();
        product.setPackages(new ArrayList<>());
        productRepository.save(product);
        packages.forEach(this::deleteSellPackage);
        deleteAllRequestRelatedToProduct(product);
        Category category = product.getCategory();
        category.getAllProducts().remove(product);
        categoryService.saveCategory(category);
        product.setCategory(null);
        product.setCompanyClass(null);
        deletePictures(product.getId());
    }

    private void deletePictures(int id) {
        File directory = new File("src/main/resources/db/images/products/" + id);
        try {
            FileUtils.deleteDirectory(directory);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void deleteAllRequestRelatedToProduct(Product product) {
        List<Request> requests = requestRepository.findAllByProduct(product);
        requests.forEach(request -> {
            request.setProduct(null);
            requestService.save(request);
        });
    }

    private void deleteSellPackage(SellPackage sellPackage) {
        Seller seller = sellPackage.getSeller();
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
        deleteAllSubCarts(seller, product);
        sellPackage.setProduct(null);
        sellPackage.setSeller(null);
        productRepository.save(product);
        sellerService.saveSeller(seller);
        sellPackageRepository.delete(sellPackage);
    }

    private void deleteAllSubCarts(Seller seller, Product product) {
        List<SubCart> subCarts = subCartRepository.findAllByProductAndSeller(product,seller);
        subCarts.forEach(subCart -> {
            Cart cart = subCart.getCart();
            cart.getSubCarts().remove(subCart);
            cartRepository.save(cart);
            subCartRepository.delete(subCart);
        });
    }

    public void changePrice(Product product, int newPrice, String username) throws NoSuchSellerException {
        SellPackage sellPackage = product.findPackageBySeller(username);
        if (newPrice > 0) {
            sellPackage.setPrice(newPrice);
            sellPackageRepository.save(sellPackage);
        }
        if (newPrice < product.getLeastPrice()) {
            product.setLeastPrice(newPrice);
        }
        productRepository.save(product);
    }

    public void changeStock(Product product, int newStock, String username) throws NoSuchSellerException {
        SellPackage sellPackage = product.findPackageBySeller(username);
        if (newStock > 0) {
            sellPackage.setStock(newStock);
            sellPackageRepository.save(sellPackage);
        }
    }

    public void addASellerToProduct(Product product,Seller seller,int amount,int price) {
        SellPackage sellPackage = new SellPackage(product, seller, price, amount, null, false, price != 0);
        sellPackageRepository.save(sellPackage);
        int currentLeast = product.getLeastPrice();
        if (currentLeast > price) {
            product.setLeastPrice(price);
        }
        product.getPackages().add(sellPackage);
        seller.getPackages().add(sellPackage);
        sellerService.saveSeller(seller);
        productRepository.save(product);
    }

    public Seller bestSellerOf(Product product){
        Seller seller = null;
        int pricy = 2000000000;
        for (SellPackage aPackage : product.getPackages()) {
            int price = aPackage.getPrice();
            if (price < pricy){
                seller = aPackage.getSeller();
                pricy = price;
            }
        }
        return seller;
    }

    public List<Product> getAllOffFromActiveProducts(){
        return productRepository.findAllByOnOffTrue();
    }
}
