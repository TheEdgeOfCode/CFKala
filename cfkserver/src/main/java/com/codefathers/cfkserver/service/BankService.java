package com.codefathers.cfkserver.service;

import com.codefathers.cfkserver.bank.BankAPI;
import com.codefathers.cfkserver.exceptions.model.bank.account.InvalidUsernameException;
import com.codefathers.cfkserver.exceptions.model.bank.account.PasswordsDoNotMatchException;
import com.codefathers.cfkserver.model.dtos.bank.CreateBankAccountDTO;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class BankService {
    private BankAPI bankAPI = BankAPI.getInstance();

    public String createAccount(CreateBankAccountDTO dto) throws IOException, PasswordsDoNotMatchException, InvalidUsernameException {
        String message = "create_account" + dto.getFirstName() + dto.getLastName() +
                dto.getUsername() + dto.getPassword() + dto.getRePassword();
        bankAPI.sendMessage(message);
        String response = bankAPI.getMessage();
        if (response.startsWith("passwords")){
            throw new PasswordsDoNotMatchException(response);
        } else if (response.startsWith("username")){
            throw new InvalidUsernameException(response);
        } else {
            return response;
        }
    }

}
