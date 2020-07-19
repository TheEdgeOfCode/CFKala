package com.codefathers.cfkserver.controller;

import com.codefathers.cfkserver.exceptions.model.category.CategoryNotFoundException;
import com.codefathers.cfkserver.exceptions.model.category.RepeatedFeatureException;
import com.codefathers.cfkserver.exceptions.model.category.RepeatedNameInParentCategoryException;
import com.codefathers.cfkserver.exceptions.token.ExpiredTokenException;
import com.codefathers.cfkserver.exceptions.token.InvalidTokenException;
import com.codefathers.cfkserver.model.dtos.category.CategoryPM;
import com.codefathers.cfkserver.model.dtos.category.CreateDTO;
import com.codefathers.cfkserver.model.entities.request.edit.CategoryEditAttribute;
import com.codefathers.cfkserver.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.util.ArrayList;
import java.util.List;

import static com.codefathers.cfkserver.utils.ErrorUtil.sendError;
import static com.codefathers.cfkserver.utils.TokenUtil.checkToken;

@RestController
public class CategoryController {
    @Autowired
    private CategoryService categoryService;

    @PostMapping("/category/edit")
    private void editCategory(@RequestBody CategoryEditAttribute attribute,
                              HttpServletRequest request, HttpServletResponse response){
        try {
            if (checkToken(response,request)){
                try {
                    categoryService.editCategory(attribute.getSourceId(),attribute);
                } catch (RepeatedNameInParentCategoryException | RepeatedFeatureException | CategoryNotFoundException e) {
                    sendError(response, HttpStatus.BAD_REQUEST,e.getMessage());
                }
            }
        } catch (ExpiredTokenException | InvalidTokenException e) {
            sendError(response, HttpStatus.UNAUTHORIZED,e.getMessage());
        }
    }

    @PostMapping("/category/remove")
    private void remove(@RequestBody Integer id,
                        HttpServletRequest request, HttpServletResponse response){
        try {
            if (checkToken(response,request)){
                try {
                    categoryService.removeCategory(id);
                } catch (Exception e) {
                    sendError(response, HttpStatus.BAD_REQUEST,e.getMessage());
                }
            }
        } catch (ExpiredTokenException | InvalidTokenException e) {
            sendError(response, HttpStatus.UNAUTHORIZED,e.getMessage());
        }
    }

    @PostMapping("/category/add")
    private void add(@RequestBody CreateDTO dto,
                        HttpServletRequest request, HttpServletResponse response){
        try {
            if (checkToken(response,request)){
                try {
                    categoryService.createCategory(dto.getName(),dto.getParent(),dto.getFeature());
                } catch (Exception e) {
                    sendError(response, HttpStatus.BAD_REQUEST,e.getMessage());
                }
            }
        } catch (ExpiredTokenException | InvalidTokenException e) {
            sendError(response, HttpStatus.UNAUTHORIZED,e.getMessage());
        }
    }

    @GetMapping()
    @RequestMapping("/category/get_special/{id}")
    private ArrayList<String> getSpecialFeatures(HttpServletResponse response, @PathVariable Integer id){
        try {
            return categoryService.getAllSpecialFeaturesFromCategory(id);
        } catch (CategoryNotFoundException e) {
            sendError(response,HttpStatus.BAD_REQUEST,e.getMessage());
            return null;
        }
    }

    @GetMapping("/category/get_public")
    public List<String> getPublicFeatures() {
        return categoryService.getPublicFeatures();
    }

    @GetMapping("/category/get_all")
    private ArrayList<CategoryPM> getAllCategories(){
        return categoryService.getAllCategories();
    }
}
