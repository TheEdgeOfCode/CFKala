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

    public List<Product> getAllActiveProduct(){
        return productRepository.findAllByProductStatusEquals(ProductStatus.VERIFIED);
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
        Optional<Product> productOptional = productRepository.findById(attribute.getSourceId());
        attributeRepository.save(attribute);
        if (productOptional.isPresent()){
            Product product = productOptional.get();
            checkIfEditorIsASeller(editor,product);
            product.setProductStatus(ProductStatus.UNDER_EDIT);
            Request request = requestService.createRequest(attribute,RequestType.EDIT_PRODUCT,editor,requestStr);
            Seller seller = sellerService.findSellerByUsername(editor);
            seller.addRequest(request);
            sellerService.saveSeller(seller);
        } else {
            throw new NoSuchAProductException();
        }
    }

    private void checkIfEditorIsASeller(String username,Product product) throws EditorIsNotSellerException {
        if (!product.hasSeller(username))throw new EditorIsNotSellerException();
    }
}
