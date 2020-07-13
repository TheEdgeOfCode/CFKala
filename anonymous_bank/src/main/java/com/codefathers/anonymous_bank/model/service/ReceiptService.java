package com.codefathers.anonymous_bank.model.service;

import com.codefathers.anonymous_bank.model.dtos.ReceiptDTO;
import com.codefathers.anonymous_bank.model.dtos.TransactionDto;
import com.codefathers.anonymous_bank.model.entity.Account;
import com.codefathers.anonymous_bank.model.entity.Receipt;
import com.codefathers.anonymous_bank.model.entity.ReceiptType;
import com.codefathers.anonymous_bank.model.exceptions.receipt.*;
import com.codefathers.anonymous_bank.model.repositories.AccountRepository;
import com.codefathers.anonymous_bank.model.repositories.ReceiptRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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

    public List<TransactionDto> getTransactions(String request,String username) throws InvalidParameterPassedException {
        if (request.startsWith("get_transaction")){
            String mode = request.substring(16);
            int account = getAccountFromUsername(username);
            List<Receipt> receipts;
            if (mode.equals("+")){
                receipts = receiptRepository.findAllByDestAccountIs(account);
            } else if (mode.equals("-")){
                receipts = receiptRepository.findAllBySourceAccountIs(account);
            } else if (mode.equals("*")){
                receipts = receiptRepository.findAllByDestAccountIsOrSourceAccountIs(account,account);
            } else if (mode.matches("\\d{1,9}")){
                receipts = new ArrayList<>();
                int id = Integer.parseInt(mode);
                Optional<Receipt> receiptOptional = receiptRepository.findById(id);
                if (receiptOptional.isPresent()){
                    Receipt receipt = receiptOptional.get();
                    if (receipt.getDestAccount() == account || receipt.getSourceAccount() == account ){
                        receipts.add(receipt);
                    } else {
                        throw new InvalidParameterPassedException("invalid receipt id");
                    }
                }else {
                    throw new InvalidParameterPassedException("invalid receipt id");
                }
            } else {
                throw new InvalidParameterPassedException("Invalid parameter");
            }
            return createDtoListFromReceipts(receipts);
        }else {
            throw new InvalidParameterPassedException("Invalid parameter");
        }
    }

    private int getAccountFromUsername(String username) {
        return accountRepository.findByUsername(username).map(Account::getId).orElse(0);
    }

    private List<TransactionDto> createDtoListFromReceipts(List<Receipt> receipts) {
        List<TransactionDto> list = new ArrayList<>();
        receipts.forEach(receipt -> list.add(new TransactionDto(receipt.getId(),receipt.getType(),receipt.getMoney(),receipt.getSourceAccount(),
                receipt.getDestAccount(),receipt.getDescription(),receipt.isPaid())));
        return list;
    }
}
