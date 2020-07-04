package com.codefathers.cfkserver.service;

import com.codefathers.cfkserver.exceptions.model.company.NoSuchACompanyException;
import com.codefathers.cfkserver.model.entities.product.Company;
import com.codefathers.cfkserver.model.repositories.CompanyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class CompanyService {
    @Autowired
    private CompanyRepository companyRepository;

    public Company getCompanyById(int id) throws NoSuchACompanyException {
        Optional<Company> company = companyRepository.findById(id);
        if (company.isPresent()){
            return company.get();
        }else {
            throw new NoSuchACompanyException("No Company With Id(" + id + ") is Available");
        }
    }
}
