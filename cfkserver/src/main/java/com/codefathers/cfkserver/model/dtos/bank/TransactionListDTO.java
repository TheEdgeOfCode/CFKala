package com.codefathers.cfkserver.model.dtos.bank;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data @AllArgsConstructor
public class TransactionListDTO {
    private List<TransactionDTO> dtos;
}
