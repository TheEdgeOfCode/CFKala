package com.codefathers.cfkserver.service;

import com.codefathers.cfkserver.exceptions.model.company.NoSuchACompanyException;
import com.codefathers.cfkserver.exceptions.model.user.NotVerifiedSeller;
import com.codefathers.cfkserver.exceptions.model.user.UserNotFoundException;
import com.codefathers.cfkserver.exceptions.model.user.WrongPasswordException;
import com.codefathers.cfkserver.model.dtos.user.CustomerDTO;
import com.codefathers.cfkserver.model.dtos.user.ManagerDTO;
import com.codefathers.cfkserver.model.dtos.user.SellerDTO;
import com.codefathers.cfkserver.model.entities.request.RequestType;
import com.codefathers.cfkserver.model.entities.request.edit.UserEditAttributes;
import com.codefathers.cfkserver.model.entities.user.*;
import com.codefathers.cfkserver.model.repositories.CustomerRepository;
import com.codefathers.cfkserver.model.repositories.ManagerRepository;
import com.codefathers.cfkserver.model.repositories.SellerRepository;
import com.codefathers.cfkserver.model.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

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

    public User getUserByUsername(String username) throws UserNotFoundException {
        Optional<User> optionalUser = userRepository.findById(username);
        if (optionalUser.isPresent()){
            return optionalUser.get();
        } else {
            throw new UserNotFoundException();
        }
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

    public String login(String username, String password) throws UserNotFoundException, NotVerifiedSeller, WrongPasswordException {
        User user = getUserByUsername(username);

        if (isCorrectPassword(user, password)) {
            Optional<Customer> customer = customerRepository.findById(username);
            if (customer.isPresent()) return "Customer";
            Optional<Seller> seller = sellerRepository.findById(username);
            if (seller.isPresent()) if (seller.get().getVerified())
                return "Seller";
            else
                throw new NotVerifiedSeller();
            Optional<Manager> manager = managerRepository.findById(username);
            if (manager.isPresent()) {
                return "Manager";
            }
        } else {
            throw new WrongPasswordException(username);
        }
        return "";
    }

    public User viewPersonalInfo(String username) throws UserNotFoundException {
        return getUserByUsername(username);
    }

    public void changeInfo(String username, UserEditAttributes editAttributes) throws UserNotFoundException {
        User user = getUserByUsername(username);
        String newFirstName = editAttributes.getNewFirstName();
        String newLastName = editAttributes.getNewLastName();
        String newPassword = editAttributes.getNewPassword();
        String newEmail = editAttributes.getNewEmail();
        String newPhone = editAttributes.getNewPhone();

        if (newFirstName != null) {
            user.setFirstName(newFirstName);
        }
        if (newLastName != null) {
            user.setLastName(newLastName);
        }
        if (newPassword != null) {
            user.setPassword(newPassword);
        }
        if (newEmail != null) {
            user.setEmail(newEmail);
        }
        if (newPhone != null) {
            user.setPhoneNumber(newPhone);
        }

        userRepository.save(user);
    }

    public void logout(String username) {
        //TODO: What TODO?
    }

    private boolean isCorrectPassword(User user, String password) {
        return user.getPassword().equals(password);
    }
}
