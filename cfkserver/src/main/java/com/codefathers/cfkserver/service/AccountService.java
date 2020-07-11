package com.codefathers.cfkserver.service;

import com.codefathers.cfkserver.exceptions.model.company.NoSuchACompanyException;
import com.codefathers.cfkserver.model.dtos.product.user.CustomerDTO;
import com.codefathers.cfkserver.model.dtos.product.user.ManagerDTO;
import com.codefathers.cfkserver.model.dtos.product.user.SellerDTO;
import com.codefathers.cfkserver.model.entities.request.Request;
import com.codefathers.cfkserver.model.entities.request.RequestType;
import com.codefathers.cfkserver.model.entities.user.*;
import com.codefathers.cfkserver.model.repositories.CustomerRepository;
import com.codefathers.cfkserver.model.repositories.ManagerRepository;
import com.codefathers.cfkserver.model.repositories.SellerRepository;
import com.codefathers.cfkserver.model.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AccountService {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private SellerRepository sellerRepository;
    @Autowired
    private CustomerRepository customerRepository;
    @Autowired
    private ManagerRepository managerRepository;
    @Autowired
    private RequestService requestService;
    @Autowired
    private CompanyService companyService;

    public User getUserByUsername(String username){
        return userRepository.getByUsername(username);
    }

    public void createSeller(SellerDTO sellerDTO) throws NoSuchACompanyException {
        Seller seller = new Seller(
                sellerDTO.getUsername(),
                sellerDTO.getPassword(),
                sellerDTO.getFirstName(),
                sellerDTO.getLastName(),
                sellerDTO.getEmail(),
                sellerDTO.getPhoneNumber(),
                new Cart(),
                companyService.getCompanyById(sellerDTO.getCompany().getId()),
                sellerDTO.getBalance()
        );
        String requestStr = String.format("%s has requested to create a seller with email %s", sellerDTO.getUsername(), sellerDTO.getEmail());
        requestService.createRequest(seller, RequestType.REGISTER_SELLER, sellerDTO.getUsername(), requestStr);
        sellerRepository.save(seller);
        //TODO: Handle Request on Seller
        /*Request request = new Request(sellerDTO.getUsername(), RequestType.REGISTER_SELLER, requestStr, seller);
        seller.addRequest(request);*/
    }

    public void createCustomer(CustomerDTO customerDTO){
        Customer customer = new Customer(
                customerDTO.getUsername(),
                customerDTO.getPassword(),
                customerDTO.getFirstName(),
                customerDTO.getLastName(),
                customerDTO.getEmail(),
                customerDTO.getPhoneNumber(),
                new Cart(),
                customerDTO.getBalance()
        );
        customerRepository.save(customer);
    }

    public void createManager(ManagerDTO managerDTO){
        Manager manager = new Manager(
                managerDTO.getUsername(),
                managerDTO.getPassword(),
                managerDTO.getFirstName(),
                managerDTO.getLastName(),
                managerDTO.getEmail(),
                managerDTO.getPhoneNumber(),
                new Cart()
        );
        managerRepository.save(manager);
    }


}
