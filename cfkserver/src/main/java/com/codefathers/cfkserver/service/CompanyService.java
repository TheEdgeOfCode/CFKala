package com.codefathers.cfkserver.service;

import com.codefathers.cfkserver.exceptions.model.company.NoSuchACompanyException;
import com.codefathers.cfkserver.exceptions.model.product.NoSuchAProductException;
import com.codefathers.cfkserver.model.entities.product.Company;
import com.codefathers.cfkserver.model.entities.product.Product;
import com.codefathers.cfkserver.model.repositories.CompanyRepository;
import com.codefathers.cfkserver.model.repositories.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class CompanyService {
    @Autowired
    private CompanyRepository companyRepository;
    @Autowired
    private ProductService productService;
    @Autowired
    private ProductRepository productRepository;

    public Company getCompanyById(int id) throws NoSuchACompanyException {
        Optional<Company> company = companyRepository.findById(id);
        if (company.isPresent()){
            return company.get();
        }else {
            throw new NoSuchACompanyException("No Company With Id(" + id + ") is Available");
        }
    }

    public void editCompanyName(int id, String newName)
            throws NoSuchACompanyException {
        Company company = getCompanyById(id);
        company.setName(newName);
        companyRepository.save(company);
    }

    public void editCompanyGroup(int id, String newGroup)
            throws NoSuchACompanyException {
        Company company = getCompanyById(id);
        company.setGroup(newGroup);
        companyRepository.save(company);
    }

    public void addProductToCompany(int productId, int companyId)
            throws NoSuchACompanyException, NoSuchAProductException {
        Company company = getCompanyById(companyId);
        Product product = productService.findById(productId);
        company.getProductsIn().add(product);
        product.setCompanyClass(company);
        companyRepository.save(company);
        productRepository.save(product);
    }

    public void removeProductFromCompany(int productId, int companyId)
            throws NoSuchACompanyException, NoSuchAProductException {
        Company company = getCompanyById(companyId);
        company.getProductsIn().remove(productService.findById(productId));
        companyRepository.save(company);
    }

    public void removeProductFromCompany(Product product) {
        Company company = product.getCompanyClass();
        company.getProductsIn().remove(product);
        companyRepository.save(company);
    }


}
