package com.codefathers.anonymous_bank.model.service;

import com.codefathers.anonymous_bank.model.dtos.ReceiptDTO;
import com.codefathers.anonymous_bank.model.entity.Receipt;
import com.codefathers.anonymous_bank.model.entity.ReceiptType;
import com.codefathers.anonymous_bank.model.exceptions.receipt.EqualSourceDestException;
import com.codefathers.anonymous_bank.model.exceptions.receipt.InvalidAccountIdException;
import com.codefathers.anonymous_bank.model.exceptions.receipt.InvalidDestAccountException;
import com.codefathers.anonymous_bank.model.exceptions.receipt.InvalidSourceAccountException;
import com.codefathers.anonymous_bank.model.repositories.AccountRepository;
import com.codefathers.anonymous_bank.model.repositories.ReceiptRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ReceiptService {
    @Autowired
    private ReceiptRepository receiptRepository;
    @Autowired private AccountRepository accountRepository;

    public Integer createReceipt(ReceiptDTO dto)
            throws EqualSourceDestException, InvalidAccountIdException, InvalidSourceAccountException, InvalidDestAccountException {
        checkValidInput(dto);
        ReceiptType type = dto.getType();
        if ((type.equals(ReceiptType.MOVE) || type.equals(ReceiptType.WITHDRAW))
                && !accountRepository.existsAccountById(dto.getDest())){
            throw new InvalidSourceAccountException("destination account id is invalid");
        }else if ((type.equals(ReceiptType.MOVE) || type.equals(ReceiptType.DEPOSIT))
                && !accountRepository.existsAccountById(dto.getSource())){
            throw new InvalidDestAccountException("source account id is invalid");
        }else {
            Receipt receipt = new Receipt(type,dto.getSource(),dto.getDest(),dto.getMoney());
            receiptRepository.save(receipt);
            return receipt.getId();
        }
    }

    private void checkValidInput(ReceiptDTO dto)
            throws InvalidAccountIdException, EqualSourceDestException {
        ReceiptType type = dto.getType();
        if ((type.equals(ReceiptType.MOVE) && (dto.getSource() < 0 || dto.getDest() <0))
                ||(type.equals(ReceiptType.DEPOSIT) && dto.getSource() < 0)
                ||(type.equals(ReceiptType.WITHDRAW) && dto.getDest() < 0)){
            throw new InvalidAccountIdException("invalid account id");
        }else if ((type.equals(ReceiptType.MOVE) && (dto.getSource() == dto.getDest()))){
            throw new EqualSourceDestException("equal source and dest account");
        }
    }
}
