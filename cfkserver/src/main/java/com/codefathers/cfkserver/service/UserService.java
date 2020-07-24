package com.codefathers.cfkserver.service;

import com.codefathers.cfkserver.exceptions.model.bank.account.InvalidUsernameException;
import com.codefathers.cfkserver.exceptions.model.bank.account.PasswordsDoNotMatchException;
import com.codefathers.cfkserver.exceptions.model.bank.receipt.*;
import com.codefathers.cfkserver.exceptions.model.company.NoSuchACompanyException;
import com.codefathers.cfkserver.exceptions.model.product.NoSuchSellerException;
import com.codefathers.cfkserver.exceptions.model.user.*;
import com.codefathers.cfkserver.exceptions.token.ExpiredTokenException;
import com.codefathers.cfkserver.exceptions.token.InvalidTokenException;
import com.codefathers.cfkserver.model.dtos.bank.CreateBankAccountDTO;
import com.codefathers.cfkserver.model.dtos.bank.CreateReceiptDTO;
import com.codefathers.cfkserver.model.dtos.bank.ReceiptType;
import com.codefathers.cfkserver.model.dtos.bank.TokenRequestDTO;
import com.codefathers.cfkserver.model.dtos.user.*;
import com.codefathers.cfkserver.model.entities.request.Request;
import com.codefathers.cfkserver.model.entities.request.RequestType;
import com.codefathers.cfkserver.model.entities.request.edit.UserEditAttributes;
import com.codefathers.cfkserver.model.entities.user.*;
import com.codefathers.cfkserver.model.repositories.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.*;
import java.util.Optional;
import java.util.Scanner;

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
    @Autowired
    private ManagerService managerService;
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

    public void createSeller(SellerDTO sellerDTO) throws NoSuchACompanyException, UserAlreadyExistsException,
            InvalidUsernameException, IOException, PasswordsDoNotMatchException, NotEnoughMoneyAtSourceException,
            InvalidMoneyException, InvalidTokenException, InvalidSourceAccountException, InvalidAccountIdException,
            InvalidDestAccountException, PaidReceiptException, InvalidDescriptionExcxeption, InvalidParameterPassedException,
            InvalidRecieptTypeException, InvalidReceiptIdException, ExpiredTokenException, EqualSourceDestException {
        checkUsername(sellerDTO.getUsername());
        /*String accountId =  bankService.createAccount(new CreateBankAccountDTO(
                sellerDTO.getFirstName(),
                sellerDTO.getLastName(),
                sellerDTO.getUsername(),
                sellerDTO.getPassword(),
                sellerDTO.getPassword())
        );*/
        Seller seller = new Seller(
                sellerDTO.getUsername(),
                sellerDTO.getPassword(),
                sellerDTO.getFirstName(),
                sellerDTO.getLastName(),
                sellerDTO.getEmail(),
                sellerDTO.getPhoneNumber(),
                new Cart(),
                companyService.getCompanyById(sellerDTO.getCompanyID()),
                0,
                "456123123513"
        );
        //chargeBankAccount(sellerDTO.getUsername(), sellerDTO.getPassword(), sellerDTO.getBalance(), accountId);
        sellerRepository.save(seller);
        String requestStr = String.format("%s has requested to create a seller with email %s", sellerDTO.getUsername(), sellerDTO.getEmail());
        Request request = requestService.createRequest(seller, RequestType.REGISTER_SELLER, sellerDTO.getUsername(), requestStr);
        seller.addRequest(request);
        sellerRepository.save(seller);
    }

    public void createCustomer(CustomerDTO customerDTO) throws UserAlreadyExistsException, InvalidUsernameException,
            IOException, PasswordsDoNotMatchException, NotEnoughMoneyAtSourceException, InvalidMoneyException,
            InvalidTokenException, InvalidSourceAccountException, InvalidAccountIdException,
            InvalidDestAccountException, PaidReceiptException, InvalidDescriptionExcxeption,
            InvalidParameterPassedException, InvalidRecieptTypeException, InvalidReceiptIdException,
            ExpiredTokenException, EqualSourceDestException {
        checkUsername(customerDTO.getUsername());
        /*String accountId =  bankService.createAccount(new CreateBankAccountDTO(
                customerDTO.getFirstName(),
                customerDTO.getLastName(),
                customerDTO.getUsername(),
                customerDTO.getPassword(),
                customerDTO.getPassword())
        );*/
        Customer customer = new Customer(
                customerDTO.getUsername(),
                customerDTO.getPassword(),
                customerDTO.getFirstName(),
                customerDTO.getLastName(),
                customerDTO.getEmail(),
                customerDTO.getPhoneNumber(),
                new Cart(),
                0,
                "456313"
        );
        //chargeBankAccount(customerDTO.getUsername(), customerDTO.getPassword(), customerDTO.getBalance(), accountId);
        customerRepository.save(customer);
    }

    private void chargeBankAccount(String username, String password, long balance, String accountId) throws IOException,
            InvalidUsernameException, InvalidDestAccountException, InvalidTokenException,
            InvalidSourceAccountException, InvalidAccountIdException, InvalidMoneyException,
            InvalidDescriptionExcxeption, InvalidParameterPassedException, InvalidRecieptTypeException,
            ExpiredTokenException, EqualSourceDestException, NotEnoughMoneyAtSourceException,
            PaidReceiptException, InvalidReceiptIdException {
        String token = bankService.getToken(new TokenRequestDTO(username, password));
        int receiptID = bankService.createReceipt(
                new CreateReceiptDTO(
                    username,
                    password,
                    token,
                    ReceiptType.DEPOSIT,
                    balance,
                    "-1",
                    accountId,
                    "")
                );
        bankService.pay(receiptID);
    }

    public void createManager(ManagerDTO managerDTO) throws UserAlreadyExistsException, InvalidUsernameException,
            IOException, PasswordsDoNotMatchException {
        checkUsername(managerDTO.getUsername());
        String accountId = "asdfasdff";

        /*if (managerService.isFirstManager()) {
            accountId = bankService.createAccount(new CreateBankAccountDTO(
                    managerDTO.getFirstName(),
                    managerDTO.getLastName(),
                    managerDTO.getUsername(),
                    managerDTO.getPassword(),
                    managerDTO.getPassword())
            );
            saveToFile(accountId);
        } else {
            accountId = bankService.getInfo("AccountId");
        }*/

        Manager manager = new Manager(
                managerDTO.getUsername(),
                managerDTO.getPassword(),
                managerDTO.getFirstName(),
                managerDTO.getLastName(),
                managerDTO.getEmail(),
                managerDTO.getPhoneNumber(),
                new Cart(),
                accountId
        );
        managerRepository.save(manager);
    }

    private void saveToFile(String accountId) {
        File file = new File("cfkserver/src/main/resources/application_info.txt");
        Scanner scanner;
        StringBuilder info = new StringBuilder("");
        try {
            scanner = new Scanner(file);
            while (scanner.hasNextLine()) {
                String fileLine = scanner.nextLine();
                if (fileLine.startsWith("AccountId")) {
                    String changed = fileLine.replaceFirst(fileLine.substring(fileLine.indexOf('=') + 2), accountId);
                    info.append(changed);
                } else {
                    info.append(fileLine).append("\n");
                }
            }
        } catch (FileNotFoundException e) {
            System.out.println("File Not Found!!!");
        }
        try {
            FileWriter writer = new FileWriter(file, false);
            writer.write(String.valueOf(info));
            writer.close();
        } catch (IOException e) {
            System.out.println("Could Not Write to File.");
        }
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

    public String getPassByUsername(String username) throws UserNotFoundException {
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
                MOVE,
                dto.getMoney(),
                bankService.getInfo("AccountId"),
                user.getAccountId(),
                "Move"
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
