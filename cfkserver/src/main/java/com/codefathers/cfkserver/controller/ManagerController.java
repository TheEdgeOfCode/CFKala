package com.codefathers.cfkserver.controller;

import com.codefathers.cfkserver.exceptions.model.product.NoSuchAProductException;
import com.codefathers.cfkserver.model.dtos.product.FilterSortDto;
import com.codefathers.cfkserver.model.dtos.product.MiniProductDto;
import com.codefathers.cfkserver.model.dtos.product.MiniProductListDto;
import com.codefathers.cfkserver.model.dtos.user.RequestDTO;
import com.codefathers.cfkserver.model.dtos.user.RequestsListDTO;
import com.codefathers.cfkserver.model.dtos.user.UserFullDTO;
import com.codefathers.cfkserver.model.dtos.user.UserFullListDTO;
import com.codefathers.cfkserver.model.entities.product.Product;
import com.codefathers.cfkserver.model.entities.request.Request;
import com.codefathers.cfkserver.model.entities.user.User;
import com.codefathers.cfkserver.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.File;
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

    @PostMapping("/manager/show_users")
    private ResponseEntity<?> showUsers(HttpServletRequest request, HttpServletResponse response) {
        try {
            if (checkToken(response, request)) {
                List<User> allUsers = managerService.getAllUsers();
                List<UserFullDTO> userDTOS = new ArrayList<>();
                allUsers.forEach(user -> userDTOS.add(createUserFullDTO(user)));
                return ResponseEntity.ok(new UserFullListDTO(new ArrayList<>(userDTOS)));
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

    @GetMapping("/manager/show_products")
    private ResponseEntity<?> showProducts_Manager(HttpServletRequest request, HttpServletResponse response, @RequestBody FilterSortDto filterSortDto) {
        try {
            if (checkToken(response, request)) {
                int[] priceRange = {filterSortDto.getDownPriceLimit(), filterSortDto.getUpPriceLimit()};
                List<Product> products = sorter.sort(filterService.updateFilterList(
                        filterSortDto.getCategoryId(), filterSortDto.getActiveFilters(), priceRange,
                        false, filterSortDto.isAvailableOnly()
                        )
                        , filterSortDto.getSortType());
                List<MiniProductDto> toReturn = dtosFromList(products);
                return ResponseEntity.ok(new MiniProductListDto(new ArrayList<>(toReturn)));
            } else {
                return null;
            }
        } catch (Exception e) {
            sendError(response, HttpStatus.BAD_REQUEST, e.getMessage());
            return null;
        }
    }

    @PostMapping("/manager/remove_product")
    private ResponseEntity<?> removeProduct_Manager(HttpServletRequest request, HttpServletResponse response, @RequestBody String info) {
        try {
            if (checkToken(response, request)) {
                try {
                    productService.deleteProduct(Integer.parseInt(info));
                    return ResponseEntity.ok(ResponseEntity.status(200));
                } catch (NoSuchAProductException e) {
                    sendError(response, HttpStatus.BAD_REQUEST, e.getMessage());
                    return null;
                }
            } else {
                return null;
            }
        } catch (Exception e) {
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

    // TODO : Please check the path.I don't know the exact path!!
    private void deleteProfilePhoto(String username) {
        File file = new File("src\\main\\resources\\db\\images\\users\\" + username + ".jpg");
        if (file.exists()) {
            file.delete();
        }
    }

    private UserFullDTO createUserFullDTO(User user) {
        return new UserFullDTO(user.getUsername(),
                user.getFirstName(),
                user.getLastName(), user.getEmail(), user.getPhoneNumber(),
                user.getClass().getName().split("\\.")[2]);
    }

}
