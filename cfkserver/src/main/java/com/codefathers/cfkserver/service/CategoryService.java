package com.codefathers.cfkserver.service;

import com.codefathers.cfkserver.exceptions.model.CategoryNotFoundException;
import com.codefathers.cfkserver.model.entities.product.Category;
import com.codefathers.cfkserver.model.repositories.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class CategoryService {
    @Autowired
    private CategoryRepository categoryRepository;

    public Category findCategoryById(int id) throws CategoryNotFoundException {
        Optional<Category> category = categoryRepository.findById(id);
        if (category.isPresent()){
            return category.get();
        }else {
            throw new CategoryNotFoundException("No Category With Id(" + id + ")");
        }
    }
}
