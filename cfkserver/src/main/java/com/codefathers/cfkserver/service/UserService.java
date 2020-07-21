package com.codefathers.cfkserver.service;

import com.codefathers.cfkserver.exceptions.model.bank.account.InvalidUsernameException;
import com.codefathers.cfkserver.exceptions.model.bank.receipt.*;
import com.codefathers.cfkserver.exceptions.model.company.NoSuchACompanyException;
import com.codefathers.cfkserver.exceptions.model.product.NoSuchSellerException;
import com.codefathers.cfkserver.exceptions.model.user.*;
import com.codefathers.cfkserver.exceptions.token.ExpiredTokenException;
import com.codefathers.cfkserver.exceptions.token.InvalidTokenException;
import com.codefathers.cfkserver.model.dtos.bank.CreateReceiptDTO;
import com.codefathers.cfkserver.model.dtos.user.*;
import com.codefathers.cfkserver.model.entities.request.Request;
import com.codefathers.cfkserver.model.entities.request.RequestType;
import com.codefathers.cfkserver.model.entities.request.edit.UserEditAttributes;
import com.codefathers.cfkserver.model.entities.user.*;
import com.codefathers.cfkserver.model.repositories.CustomerRepository;
import com.codefathers.cfkserver.model.repositories.ManagerRepository;
import com.codefathers.cfkserver.model.repositories.SellerRepository;
import com.codefathers.cfkserver.model.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.*;
import java.util.Optional;

import static com.codefathers.cfkserver.model.dtos.bank.ReceiptType.DEPOSIT;
import static com.codefathers.cfkserver.model.dtos.bank.ReceiptType.MOVE;
import static com.codefathers.cfkserver.model.entities.user.Role.CUSTOMER;

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
    private BankService bankService;
    @Autowired
    private CustomerService customerService;
    @Autowired
    private SellerService sellerService;

    public User getUserByUsername(String username) throws UserNotFoundException {
        Optional<User> optionalUser = userRepository.findById(username);
        if (optionalUser.isPresent()){
            return optionalUser.get();
        } else {
            throw new UserNotFoundException();
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

    String getPassByUsername(String username) throws UserNotFoundException {
        User user = getUserByUsername(username);
        return user.getPassword();
    }

    public void chargeWallet(ChargeWalletDTO dto, String username) throws UserNotFoundException,
            IOException, InvalidDestAccountException, InvalidTokenException,
            InvalidSourceAccountException, InvalidAccountIdException, InvalidMoneyException, InvalidDescriptionExcxeption,
            InvalidParameterPassedException, InvalidRecieptTypeException, InvalidUsernameException, ExpiredTokenException,
            EqualSourceDestException, NotEnoughMoneyAtSourceException, PaidReceiptException, InvalidReceiptIdException,
            NoSuchACustomerException, NoSuchSellerException {
        User user = getUserByUsername(username);
        int receiptId = bankService.createReceipt(
                new CreateReceiptDTO(
                        username,
                        getPassByUsername(username),
                        dto.getToken(),
                        MOVE,
                        dto.getMoney(),
                        user.getAccountId(),
                        bankService.getInfo("AccountId"),
                        "Charge"
                ));
        bankService.pay(receiptId);
        if (dto.getRole().equals(CUSTOMER)) {
            Customer customer = customerService.getCustomerByUsername(username);
            customer.setBalance(customer.getBalance() + dto.getMoney());
        } else {
            Seller seller = sellerService.findSellerByUsername(username);
            seller.setBalance(seller.getBalance() + dto.getMoney());
        }
    }

    public int takeMoneyIntoAccount(TakeMoneyDTO dto, String username) throws UserNotFoundException, IOException, InvalidDestAccountException, InvalidTokenException, InvalidSourceAccountException, InvalidAccountIdException, InvalidMoneyException, InvalidDescriptionExcxeption, InvalidParameterPassedException, InvalidRecieptTypeException, InvalidUsernameException, ExpiredTokenException, EqualSourceDestException, NotEnoughMoneyAtSourceException, PaidReceiptException, InvalidReceiptIdException, NoSuchACustomerException, NoSuchSellerException {
        User user = getUserByUsername(username);
        int receiptId = bankService.createReceipt(new CreateReceiptDTO(
                username,
                getPassByUsername(username),
                dto.getToken(),
                DEPOSIT,
                dto.getMoney(),
                Integer.toString(-1),
                user.getAccountId(),
                "Deposit"
        ));
        bankService.pay(receiptId);
        if (dto.getRole().equals(CUSTOMER)) {
            Customer customer = customerService.getCustomerByUsername(username);
            customer.setBalance(customer.getBalance() - dto.getMoney());
        } else {
            Seller seller = sellerService.findSellerByUsername(username);
            seller.setBalance(seller.getBalance() - dto.getMoney());
        }
        return receiptId;
    }
}
