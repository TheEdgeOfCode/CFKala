package com.codefathers.cfkserver.controller;

import com.codefathers.cfkserver.exceptions.model.product.NoSuchSellerException;
import com.codefathers.cfkserver.model.dtos.log.SellLogDTO;
import com.codefathers.cfkserver.model.dtos.log.SellLogListDTO;
import com.codefathers.cfkserver.model.dtos.user.CompanyDto;
import com.codefathers.cfkserver.model.dtos.user.UserFullDTO;
import com.codefathers.cfkserver.model.entities.logs.SellLog;
import com.codefathers.cfkserver.model.entities.product.Company;
import com.codefathers.cfkserver.model.entities.user.User;
import com.codefathers.cfkserver.service.CompanyService;
import com.codefathers.cfkserver.service.SellerService;
import com.codefathers.cfkserver.utils.JwtUtil;
import com.codefathers.cfkserver.utils.TokenUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.util.ArrayList;
import java.util.List;

import static com.codefathers.cfkserver.utils.ErrorUtil.sendError;

@RestController
public class SellerController {
    @Autowired
    private JwtUtil jwtUtil;
    @Autowired
    private SellerService sellerService;

    @GetMapping("seller/view_company")
    public ResponseEntity<?> viewCompanyInfo(HttpServletRequest request, HttpServletResponse response){
        try {
            if (TokenUtil.checkToken(response, request)) {
                Company company = sellerService.viewCompanyInformation(TokenUtil.getUsernameFromToken(request));
                CompanyDto dto = new CompanyDto(company.getName(), company.getGroup(), company.getId(), company.getPhone());
                return ResponseEntity.ok(dto);
            }
            else
                return null;
        } catch (Exception e) {
            sendError(response, HttpStatus.BAD_REQUEST, e.getMessage());
            return null;
        }
    }

    @GetMapping("seller/sellLog")
    public ResponseEntity<?> viewSalesHistory(HttpServletRequest request, HttpServletResponse response) {
        try {
            if (TokenUtil.checkToken(response, request)) {
                List<SellLog> sellLogs = sellerService.viewSalesHistory(TokenUtil.getUsernameFromToken(request));
                ArrayList<SellLogDTO> sellLogDTOs = new ArrayList<>();
                for (SellLog sellLog : sellLogs) {
                    sellLogDTOs.add(new SellLogDTO(sellLog.getLogId(),
                            sellLog.getProduct().getSourceId(),
                            sellLog.getMoneyGotten(),
                            sellLog.getDiscount(),
                            sellLog.getDate(),
                            sellLog.getBuyer().getUsername(),
                            sellLog.getDeliveryStatus()));
                }
                return ResponseEntity.ok(new SellLogListDTO(sellLogDTOs));
            }
            else
                return null;
        } catch (Exception e) {
            sendError(response, HttpStatus.BAD_REQUEST, e.getMessage());
            return null;
        }
    }
}
