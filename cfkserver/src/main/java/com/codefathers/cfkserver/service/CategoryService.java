package com.codefathers.cfkserver.service;

import com.codefathers.cfkserver.exceptions.model.CategoryNotFoundException;
import com.codefathers.cfkserver.exceptions.model.category.RepeatedNameInParentCategoryException;
import com.codefathers.cfkserver.model.entities.product.Category;
import com.codefathers.cfkserver.model.repositories.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Service
public class CategoryService {
    @Autowired private CategoryRepository categoryRepository;

    private static final ArrayList<String> publicFeatures = new ArrayList<>(Arrays.asList("Dimension", "Weigh", "Color"));

    public Category findCategoryById(int id) throws CategoryNotFoundException {
        Optional<Category> category = categoryRepository.findById(id);
        if (category.isPresent()){
            return category.get();
        }else {
            throw new CategoryNotFoundException("No Category With Id(" + id + ")");
        }
    }

    public void createCategory(String name, int parentId, List<String> features)
            throws RepeatedNameInParentCategoryException, CategoryNotFoundException {
        Category parent;
        if (parentId != 0)
            parent = findCategoryById(parentId);
        else
            parent = null;
        checkIfThisNameIsValidForThisParent(name,parent);

        Category toCreate = new Category(name,parent);
        toCreate.setSpecialFeatures(features);
        addToBase(toCreate,parent);
        categoryRepository.save(toCreate);
    }

    private void checkIfThisNameIsValidForThisParent(String name,Category parent)
            throws RepeatedNameInParentCategoryException {
        List<Category> subCategories;
        if (parent != null)
            subCategories = parent.getSubCategories();
        else
            subCategories = getBaseCats();
        for (Category category : subCategories) {
            if (category.getName().equals(name))
                throw new RepeatedNameInParentCategoryException(name);
        }
    }

    private List<Category> getBaseCats() {
        return categoryRepository.findAllByParentIsNull();
    }

    private void addToBase(Category cat, Category parent) {
        List<Category> subCategories;
        if (parent != null)
            subCategories = parent.getSubCategories();
        else
            subCategories = getBaseCats();
        subCategories.add(cat);
        if (parent != null){
            parent.setSubCategories(subCategories);
            categoryRepository.save(parent);
        }
    }

    void saveCategory(Category category){
        categoryRepository.save(category);
    }


}
