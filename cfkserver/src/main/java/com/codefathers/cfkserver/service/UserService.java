package com.codefathers.cfkserver.service;

import com.codefathers.cfkserver.exceptions.model.company.NoSuchACompanyException;
import com.codefathers.cfkserver.exceptions.model.user.NotVerifiedSeller;
import com.codefathers.cfkserver.exceptions.model.user.UserAlreadyExistsException;
import com.codefathers.cfkserver.exceptions.model.user.UserNotFoundException;
import com.codefathers.cfkserver.exceptions.model.user.WrongPasswordException;
import com.codefathers.cfkserver.model.dtos.user.CustomerDTO;
import com.codefathers.cfkserver.model.dtos.user.ManagerDTO;
import com.codefathers.cfkserver.model.dtos.user.SellerDTO;
import com.codefathers.cfkserver.model.dtos.user.UserDTO;
import com.codefathers.cfkserver.model.entities.request.Request;
import com.codefathers.cfkserver.model.entities.request.RequestType;
import com.codefathers.cfkserver.model.entities.request.edit.UserEditAttributes;
import com.codefathers.cfkserver.model.entities.user.*;
import com.codefathers.cfkserver.model.repositories.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.*;
import java.util.Optional;

@Service
public class UserService {

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
    @Autowired
    private SupportRepository supportRepository;

    public User getUserByUsername(String username) throws UserNotFoundException {
        Optional<User> optionalUser = userRepository.findById(username);
        if (optionalUser.isPresent()){
            return optionalUser.get();
        } else {
            throw new UserNotFoundException("User not found");
        }
    }

    public void createSeller(SellerDTO sellerDTO) throws NoSuchACompanyException, UserAlreadyExistsException {
        checkUsername(sellerDTO.getUsername());
        Seller seller = new Seller(
                sellerDTO.getUsername(),
                sellerDTO.getPassword(),
                sellerDTO.getFirstName(),
                sellerDTO.getLastName(),
                sellerDTO.getEmail(),
                sellerDTO.getPhoneNumber(),
                new Cart(),
                companyService.getCompanyById(sellerDTO.getCompanyID()),
                sellerDTO.getBalance()
        );
        sellerRepository.save(seller);
        String requestStr = String.format("%s has requested to create a seller with email %s", sellerDTO.getUsername(), sellerDTO.getEmail());
        Request request = requestService.createRequest(seller, RequestType.REGISTER_SELLER, sellerDTO.getUsername(), requestStr);
        seller.addRequest(request);
        sellerRepository.save(seller);
    }

    public void createCustomer(CustomerDTO customerDTO) throws UserAlreadyExistsException {
        checkUsername(customerDTO.getUsername());
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

    public void createManager(ManagerDTO managerDTO) throws UserAlreadyExistsException {
        checkUsername(managerDTO.getUsername());
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

    public void createSupport(UserDTO userDTO) throws UserAlreadyExistsException {
        checkUsername(userDTO.getUsername());
        Support support = new Support(userDTO.getUsername(),userDTO.getPassword(),userDTO.getFirstName(),
                userDTO.getLastName(),userDTO.getEmail(),userDTO.getPhoneNumber(),null);
        supportRepository.save(support);
    }

    private void checkUsername(String username) throws UserAlreadyExistsException {
        if(userRepository.findById(username).isPresent()){
            throw new UserAlreadyExistsException();
        }
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
                throw new NotVerifiedSeller("Your not verified yet");

            Optional<Manager> manager = managerRepository.findById(username);
            if (manager.isPresent()) {
                return "Manager";
            }

            Optional<Support> support = supportRepository.findById(username);
            if (support.isPresent()){
                return "Support";
            }
        } else {
            throw new WrongPasswordException("Wrong Password");
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
