package com.codefathers.cfkserver.service;

import com.codefathers.cfkserver.model.dtos.product.CreateProductDTO;
import com.codefathers.cfkserver.model.entities.product.Company;
import com.codefathers.cfkserver.model.entities.product.Product;
import com.codefathers.cfkserver.model.entities.product.ProductStatus;
import com.codefathers.cfkserver.model.entities.user.Seller;
import com.codefathers.cfkserver.model.repositories.CompanyRepository;
import com.codefathers.cfkserver.model.repositories.ProductRepository;
import com.codefathers.cfkserver.model.repositories.SellerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ProductService {
    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private SellerRepository sellerRepository;

    @Autowired
    private CompanyRepository companyRepository;

    public List<Product> getAllActiveProduct(){
        return productRepository.findAllByProductStatusEquals(ProductStatus.VERIFIED);
    }

    public int createProduct(CreateProductDTO dto){
        return 1;
    }
}
