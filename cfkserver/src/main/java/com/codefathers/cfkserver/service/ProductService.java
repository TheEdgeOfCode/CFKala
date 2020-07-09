package com.codefathers.cfkserver.service;

import com.codefathers.cfkserver.exceptions.model.CategoryNotFoundException;
import com.codefathers.cfkserver.exceptions.model.company.NoSuchACompanyException;
import com.codefathers.cfkserver.exceptions.model.product.EditorIsNotSellerException;
import com.codefathers.cfkserver.exceptions.model.product.NoSuchAProductException;
import com.codefathers.cfkserver.exceptions.model.product.NoSuchSellerException;
import com.codefathers.cfkserver.model.dtos.product.CreateProductDTO;
import com.codefathers.cfkserver.model.entities.product.*;
import com.codefathers.cfkserver.model.entities.request.Request;
import com.codefathers.cfkserver.model.entities.request.RequestType;
import com.codefathers.cfkserver.model.entities.request.edit.ProductEditAttribute;
import com.codefathers.cfkserver.model.entities.user.Seller;
import com.codefathers.cfkserver.model.repositories.ProductEditAttributeRepository;
import com.codefathers.cfkserver.model.repositories.ProductRepository;
import com.codefathers.cfkserver.model.repositories.SellPackageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ProductService {
    @Autowired private ProductRepository productRepository;
    @Autowired private CompanyService companyService;
    @Autowired private SellerService sellerService;
    @Autowired private CategoryService categoryService;
    @Autowired private RequestService requestService;
    @Autowired private ProductEditAttributeRepository attributeRepository;
    @Autowired private SellPackageRepository sellPackageRepository;

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
}
