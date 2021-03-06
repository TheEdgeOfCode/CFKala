package com.codefathers.cfkserver.controller;

import com.codefathers.cfkserver.exceptions.model.category.CategoryNotFoundException;
import com.codefathers.cfkserver.exceptions.model.filters.InvalidFilterException;
import com.codefathers.cfkserver.exceptions.model.log.NoSuchALogException;
import com.codefathers.cfkserver.exceptions.model.product.NoSuchAProductException;
import com.codefathers.cfkserver.exceptions.token.ExpiredTokenException;
import com.codefathers.cfkserver.exceptions.token.InvalidTokenException;
import com.codefathers.cfkserver.model.dtos.customer.OrderLogDTO;
import com.codefathers.cfkserver.model.dtos.customer.OrderLogListDTO;
import com.codefathers.cfkserver.model.dtos.customer.OrderProductDTO;
import com.codefathers.cfkserver.model.dtos.customer.PurchaseDTO;
import com.codefathers.cfkserver.model.dtos.log.PurchaseLogDTO;
import com.codefathers.cfkserver.model.dtos.log.PurchaseLogDTOList;
import com.codefathers.cfkserver.model.dtos.product.FilterSortDto;
import com.codefathers.cfkserver.model.dtos.product.MiniProductDto;
import com.codefathers.cfkserver.model.dtos.product.MiniProductListDto;
import com.codefathers.cfkserver.model.dtos.user.RequestDTO;
import com.codefathers.cfkserver.model.dtos.user.RequestsListDTO;
import com.codefathers.cfkserver.model.dtos.user.UserFullDTO;
import com.codefathers.cfkserver.model.dtos.user.UserFullListDTO;
import com.codefathers.cfkserver.model.entities.logs.PurchaseLog;
import com.codefathers.cfkserver.model.entities.product.Product;
import com.codefathers.cfkserver.model.entities.request.Request;
import com.codefathers.cfkserver.model.entities.user.User;
import com.codefathers.cfkserver.service.*;
import com.codefathers.cfkserver.service.file.StorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import static com.codefathers.cfkserver.controller.ProductController.dtoFromProduct;
import static com.codefathers.cfkserver.utils.ErrorUtil.sendError;
import static com.codefathers.cfkserver.utils.TokenUtil.checkToken;

@RestController
public class ManagerController {

    @Autowired
    private ManagerService managerService;

    @Autowired
    private Sorter sorter;

    @Autowired
    private FilterService filterService;

    @Autowired
    private ProductService productService;

    @Autowired
    private RequestService requestService;

    @Autowired
    private StorageService storageService;

    @Autowired
    private LogService logService;

    @GetMapping("/manager/show_users")
    private UserFullListDTO showUsers(HttpServletRequest request, HttpServletResponse response) {
        try {
            if (checkToken(response, request)) {
                List<User> allUsers = managerService.getAllUsers();
                List<UserFullDTO> userDTOS = new ArrayList<>();
                allUsers.forEach(user -> userDTOS.add(createUserFullDTO(user)));
                return new UserFullListDTO(new ArrayList<>(userDTOS));
            } else {
                return null;
            }
        } catch (Exception e) {
            sendError(response, HttpStatus.BAD_REQUEST, e.getMessage());
            return null;
        }
    }

    @PostMapping("/manager/delete_user")
    private ResponseEntity<?> deleteUser(HttpServletRequest request, HttpServletResponse response, @RequestBody String info) {
        try {
            if (checkToken(response, request)) {
                managerService.deleteUser(info);
                deleteProfilePhoto(info);
                return ResponseEntity.ok(ResponseEntity.status(200));
            } else {
                return null;
            }
        } catch (Exception e) {
            sendError(response, HttpStatus.BAD_REQUEST, e.getMessage());
            return null;
        }
    }

    @PostMapping("/manager/show_products")
    private ResponseEntity<?> showProducts_Manager(HttpServletRequest request, HttpServletResponse response, @RequestBody FilterSortDto filterSortDto) {
        try {
            checkToken(response, request);
            int[] priceRange = {filterSortDto.getDownPriceLimit(), filterSortDto.getUpPriceLimit()};
            List<Product> products = sorter.sort(filterService.updateFilterList(
                    filterSortDto.getCategoryId(), filterSortDto.getActiveFilters(), priceRange,
                    false, filterSortDto.isAvailableOnly()
                    )
                    , filterSortDto.getSortType());
            List<MiniProductDto> toReturn = dtosFromList(products);
            return ResponseEntity.ok(new MiniProductListDto(new ArrayList<>(toReturn)));
        } catch (InvalidTokenException | ExpiredTokenException e) {
            sendError(response, HttpStatus.UNAUTHORIZED, e.getMessage());
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            sendError(response, HttpStatus.BAD_REQUEST, e.getMessage());
            return null;
        }
    }

    @PostMapping()
    @RequestMapping("/manager/remove_product/{id}")
    private ResponseEntity<?> removeProduct_Manager(HttpServletRequest request, HttpServletResponse response, @PathVariable String id) {
        try {
            checkToken(response, request);
            try {
                productService.deleteProduct(Integer.parseInt(id));
                return ResponseEntity.ok(ResponseEntity.status(200));
            } catch (NoSuchAProductException e) {
                e.printStackTrace();
                sendError(response, HttpStatus.BAD_REQUEST, e.getMessage());
                return null;
            }
        } catch (ExpiredTokenException | InvalidTokenException e) {
            sendError(response, HttpStatus.UNAUTHORIZED, e.getMessage());
            return null;
        }
    }

    @GetMapping("/manager/show_requests")
    private ResponseEntity<?> showRequests(HttpServletRequest request, HttpServletResponse response) {
        try {
            if (checkToken(response, request)) {
                List<Request> allRequests = requestService.getRequestsForManager();
                List<RequestDTO> requestDTOS = new ArrayList<>();
                for (Request request_manager : allRequests) {
                    requestDTOS.add(createRequestDTO(request_manager));
                }
                return ResponseEntity.ok(new RequestsListDTO(new ArrayList<>(requestDTOS)));
            } else {
                return null;
            }
        } catch (Exception e) {
            sendError(response, HttpStatus.UNAUTHORIZED, e.getMessage());
            return null;
        }
    }

    @PostMapping("/manager/accept_request")
    private void acceptRequest(HttpServletRequest request, HttpServletResponse response, @RequestBody String info) {
        try {
            if (checkToken(response, request)) {
                try {
                    requestService.accept(Integer.parseInt(info));
                } catch (NoSuchAProductException e) {
                    sendError(response, HttpStatus.BAD_REQUEST, e.getMessage());
                }
            }
        } catch (Exception e) {
            sendError(response, HttpStatus.UNAUTHORIZED, e.getMessage());
        }
    }

    @PostMapping("/manager/decline_request")
    private void declineRequest(HttpServletRequest request, HttpServletResponse response, @RequestBody String info) {
        try {
            if (checkToken(response, request)) {
                requestService.decline(Integer.parseInt(info));
            }
        } catch (Exception e) {
            sendError(response, HttpStatus.UNAUTHORIZED, e.getMessage());
        }
    }

    @GetMapping("/manager/show_all_logs")
    private ResponseEntity<?> showAllLogs(HttpServletRequest request, HttpServletResponse response) {
        try {
            if (checkToken(response, request)) {
                List<PurchaseLog> purchaseLogs = logService.getAllPurchaseLogs();
                List<PurchaseLogDTO> purchaseLogDTOS = new ArrayList<>();
                for (PurchaseLog purchaseLog : purchaseLogs) {
                    purchaseLogDTOS.add(createPurchaseDTO(purchaseLog));
                }
                return ResponseEntity.ok(new PurchaseLogDTOList(new ArrayList<>(purchaseLogDTOS)));
            } else {
                return null;
            }
        } catch (Exception e) {
            sendError(response, HttpStatus.UNAUTHORIZED, e.getMessage());
            return null;
        }
    }

    private PurchaseLogDTO createPurchaseDTO(PurchaseLog log) throws NoSuchALogException {
        return new PurchaseLogDTO(
                log.getLogId(),
                new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(log.getDate()),
                log.getPricePaid(),
                log.getDeliveryStatus()
        );
    }

    @PostMapping("/manager/change_log_status")
    private void changeLogStatus(HttpServletRequest request, HttpServletResponse response, @RequestBody int logId) {
        try {
            if (checkToken(response, request)) {
                logService.changeLogStatus(logId);
            }
        } catch (Exception e) {
            sendError(response, HttpStatus.UNAUTHORIZED, e.getMessage());
        }
    }

    private RequestDTO createRequestDTO(Request request) {
        return new RequestDTO(
                request.getUserHasRequested(),
                request.getRequestId(),
                request.getRequestType().toString(),
                request.getRequest()
        );
    }

    private List<MiniProductDto> dtosFromList(List<Product> products) {
        List<MiniProductDto> toReturn = new ArrayList<>();
        products.forEach(product -> toReturn.add(dtoFromProduct(product)));
        return toReturn;
    }

    private void deleteProfilePhoto(String username) {
        storageService.deleteProfile(username);
    }

    private UserFullDTO createUserFullDTO(User user) {
        return new UserFullDTO(user.getUsername(),
                user.getPassword(),
                user.getFirstName(),
                user.getLastName(), user.getEmail(), user.getPhoneNumber(),
                user.getClass().getName().split("\\.")[2],
                user.getAccountId());
    }

    @GetMapping("manager/is_first")
    private ResponseEntity<?> isFirstManager(){
        Boolean answer = managerService.isFirstManager();
        return ResponseEntity.ok(answer);
    }
}
