package com.codefathers.cfkserver.service;

import com.codefathers.cfkserver.model.dtos.edit.TollMinimumBalanceEditAttribute;
import com.codefathers.cfkserver.utils.BankUtil;
import com.codefathers.cfkserver.exceptions.model.bank.account.InvalidUsernameException;
import com.codefathers.cfkserver.exceptions.model.bank.account.PasswordsDoNotMatchException;
import com.codefathers.cfkserver.exceptions.model.bank.receipt.*;
import com.codefathers.cfkserver.exceptions.token.ExpiredTokenException;
import com.codefathers.cfkserver.exceptions.token.InvalidTokenException;
import com.codefathers.cfkserver.model.dtos.bank.*;
import com.google.gson.Gson;
import org.springframework.stereotype.Service;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

@Service
public class BankService {
    private BankUtil bankUtil = BankUtil.getInstance();

    //TODO: Check Invalid Input and DataBase Error
    public String createAccount(CreateBankAccountDTO dto) throws IOException, PasswordsDoNotMatchException, InvalidUsernameException {
        String message = "create_account " + dto.getFirstName() + " " + dto.getLastName() + " " +
                dto.getUsername() + " " + dto.getPassword() + " " + dto.getRePassword();
        bankUtil.sendMessage(message);
        String response = bankUtil.getMessage();

        if (response.startsWith("passwords")){
            throw new PasswordsDoNotMatchException(response);
        } else if (response.startsWith("username")){
            throw new InvalidUsernameException(response);
        } else {
            return response;
        }
    }

    public String getToken(TokenRequestDTO dto) throws IOException, InvalidUsernameException {
        String message = "get_token " + dto.getUsername() + " " + dto.getPassword();
        bankUtil.sendMessage(message);
        String response = bankUtil.getMessage();

        if (response.startsWith("invalid")){
            throw new InvalidUsernameException(response);
        } else {
            return response;
        }
    }

    public int createReceipt(CreateReceiptDTO dto) throws IOException, InvalidRecieptTypeException, InvalidMoneyException,
            InvalidParameterPassedException, InvalidTokenException, ExpiredTokenException, InvalidSourceAccountException,
            InvalidDestAccountException, EqualSourceDestException, InvalidAccountIdException, InvalidDescriptionExcxeption,
            InvalidUsernameException {
        String message = "create_receipt " + dto.getToken() + " " + ReceiptType.from(dto.getType()) + " " +
                dto.getMoney() + " " + dto.getSource() + " " + dto.getDest() + " " + dto.getDescription();
        bankUtil.sendMessage(message);
        String response = bankUtil.getMessage();
        System.out.println(response);

        if (response.startsWith("invalid receipt")){
            throw new InvalidRecieptTypeException(response);
        } else if (response.startsWith("invalid money")){
            throw new InvalidMoneyException(response);
        } else if (response.startsWith("invalid parameters")){
            throw new InvalidParameterPassedException(response);
        } else if (response.startsWith("token is")){
            throw new InvalidTokenException(response);
        } else if (response.startsWith("token expired")){
            //throw new ExpiredTokenException(response);
            String token = getToken(new TokenRequestDTO(dto.getUsername(), dto.getPassword()));
            dto.setToken(token);
            return createReceipt(dto);
        } else if (response.startsWith("source")){
            throw new InvalidSourceAccountException(response);
        } else if (response.startsWith("dest")){
            throw new InvalidDestAccountException(response);
        } else if (response.startsWith("equal")){
            throw new EqualSourceDestException(response);
        } else if (response.startsWith("invalid account")){
            throw new InvalidAccountIdException(response);
        } else if (response.startsWith("your input")){
            throw new InvalidDescriptionExcxeption(response);
        } else {
            return Integer.parseInt(response);
        }
    }

    public List<TransactionDTO> getTransactions(NeededForTransactionDTO dto) throws IOException, InvalidTokenException,
            ExpiredTokenException, InvalidReceiptIdException, InvalidUsernameException {
        String message = "get_transactions " + dto.getToken() + " " + dto.getType().getValue();
        bankUtil.sendMessage(message);
        String response = bankUtil.getMessage();

        if (response.startsWith("token is")){
            throw new InvalidTokenException(response);
        } else if (response.startsWith("token expired")){
            //throw new ExpiredTokenException(response);
            String token = getToken(new TokenRequestDTO(dto.getUsername(), dto.getPassword()));
            dto.setToken(token);
            return getTransactions(dto);
        } else if (response.startsWith("invalid")){
            throw new InvalidReceiptIdException(response);
        } else {
            List<String> stringTransactions = Arrays.asList(response.split("/*"));
            List<TransactionDTO> dtos = new ArrayList<>();
            for (String string : stringTransactions) {
                dtos.add(new Gson().fromJson(string, TransactionDTO.class));
            }
            return dtos;
        }
    }

    public void pay(int receiptId) throws IOException, InvalidReceiptIdException, PaidReceiptException,
            NotEnoughMoneyAtSourceException, InvalidAccountIdException {
        String message = "pay " + receiptId;
        bankUtil.sendMessage(message);
        String response = bankUtil.getMessage();

        if (response.startsWith("invalid receipt")){
            throw new InvalidReceiptIdException(response);
        } else if (response.startsWith("receipt")){
            throw new PaidReceiptException(response);
        } else if (response.startsWith("source")){
            throw new NotEnoughMoneyAtSourceException(response);
        } else if (response.startsWith("invalid account")){
            throw new InvalidAccountIdException(response);
        }
    }

    public long getBalance(BalanceDTO dto) throws IOException, ExpiredTokenException, InvalidTokenException,
            InvalidUsernameException {
        String message = "get_balance " + dto.getToken();
        bankUtil.sendMessage(message);
        String response = bankUtil.getMessage();

        if (response.startsWith("token is")){
            throw new InvalidTokenException(response);
        } else if (response.startsWith("token expired")){
            //throw new ExpiredTokenException(response);
            String newToken = getToken(new TokenRequestDTO(dto.getUsername(), dto.getPassword()));
            dto.setToken(newToken);
            return getBalance(dto);
        } else {
            return Long.parseLong(response);
        }
    }

    public String getInfo(String request){
        File file = new File("application_info.txt");
        Scanner scanner;
        try {
            scanner = new Scanner(file);
            while (scanner.hasNextLine()) {
                String fileLine = scanner.nextLine();
                if (fileLine.startsWith(request)) {
                    // AccountId = 123
                    return fileLine.substring(fileLine.indexOf('=') + 2);
                }
            }
        } catch (FileNotFoundException e) {
            System.out.println("D:\\Educational\\University\\Semester '98-99' (2)\\AP\\Project\\Phase 3\\CFKala\\cfkserver\\src\\main\\resources\\application_info.txt");
        }
        return null;
    }

    public void editTollMinimumBalanceInfo(TollMinimumBalanceEditAttribute attribute) {
        String accountId = getInfo("AccountId");
        String toll = getInfo("Toll");
        String minimum = getInfo("Minimum Balance");
        try {
            PrintWriter printWriter = new PrintWriter(
                    new FileWriter("src/main/resources/application_info.txt", false
                    ));
            printWriter.write("AccountId = " + accountId + "\n");
            if (attribute.getNewToll() != null)
                printWriter.write("Toll = " + attribute.getNewToll() + "\n");
            else printWriter.write("Toll = " + toll + "\n");
            if (attribute.getNewMinimumBalance() != null)
                printWriter.write("Minimum Balance = " + attribute.getNewMinimumBalance());
            else printWriter.write("Minimum Balance = " + minimum);
            printWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
