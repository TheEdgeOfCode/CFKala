package com.codefathers.cfkserver.service;

import com.codefathers.cfkserver.exceptions.model.category.CategoryNotFoundException;
import com.codefathers.cfkserver.exceptions.model.category.NoSuchAProductInCategoryException;
import com.codefathers.cfkserver.exceptions.model.category.RepeatedFeatureException;
import com.codefathers.cfkserver.exceptions.model.category.RepeatedNameInParentCategoryException;
import com.codefathers.cfkserver.exceptions.model.product.NoSuchAProductException;
import com.codefathers.cfkserver.model.dtos.product.MicroProductDto;
import com.codefathers.cfkserver.model.entities.product.Category;
import com.codefathers.cfkserver.model.entities.product.Product;
import com.codefathers.cfkserver.model.entities.product.ProductStatus;
import com.codefathers.cfkserver.model.entities.request.edit.CategoryEditAttribute;
import com.codefathers.cfkserver.model.repositories.CategoryRepository;
import com.codefathers.cfkserver.model.repositories.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;

@Service
public class CategoryService {
    @Autowired private CategoryRepository categoryRepository;
    @Autowired private ProductRepository productRepository;
    @Autowired private ProductService productService;
    @Autowired private MessageService messageService;

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

    public ArrayList<MicroProductDto> allProductsInACategoryList(int id) throws  CategoryNotFoundException {
        ArrayList<MicroProductDto> list = new ArrayList<>();
        Category category = findCategoryById(id);
        for (Product product : category.getAllProducts()) {
            if (product.getProductStatus().equals(ProductStatus.VERIFIED))
                list.add(new MicroProductDto(product.getName(), product.getId()));
        }
        return list;
    }

    public void addProductToCategory(Product product,Category toBeAddedTo) {
        List<Product> productsIn = toBeAddedTo.getAllProducts();
        productsIn.add(product);
        toBeAddedTo.setAllProducts(productsIn);
        categoryRepository.save(toBeAddedTo);
    }

    public ArrayList<String> getAllSpecialFeaturesFromCategory(int categoryId)
            throws CategoryNotFoundException {
        if (categoryId == 0) return new ArrayList<>();
        Category category = findCategoryById(categoryId);
        return getAllSpecialFeatures(category);
    }

    private ArrayList<String> getAllSpecialFeatures(Category category) {
        ArrayList<String> features = new ArrayList<>();
        if (category.getParent() != null){
            Category parent = category.getParent();
            features = new ArrayList<>(getAllSpecialFeatures(parent));
        }
        features.addAll(category.getSpecialFeatures());
        return features;
    }

    public void editProductCategory(int productId,int oldCategoryId,int newCategoryId)
            throws NoSuchAProductException, NoSuchAProductInCategoryException, CategoryNotFoundException {
        Category oldCategory = findCategoryById(oldCategoryId);
        productService.findById(productId);
        checkIfThisProductExistInThisCategory(productId,oldCategory);
        Product product = productService.findById(productId);
        List<Product> products = oldCategory.getAllProducts();
        products.remove(product);
        oldCategory.setAllProducts(products);
        Category newCategory = findCategoryById(newCategoryId);
        products = newCategory.getAllProducts();
        products.add(product);
        newCategory.setAllProducts(products);
        product.setCategory(newCategory);
        setNewFeaturesToProduct(product,newCategory.getSpecialFeatures());
        categoryRepository.save(newCategory);
        categoryRepository.save(oldCategory);
        productRepository.save(product);
    }

    private void setNewFeaturesToProduct(Product product,List<String> newFeatures) {
        HashMap<String,String> features = new HashMap<>();
        for (String feature : newFeatures) {
            features.put(feature,"");
        }
        product.setSpecialFeatures(features);
        product.setProductStatus(ProductStatus.UNDER_EDIT);
    }

    private void checkIfThisProductExistInThisCategory(int productId,Category category)
            throws NoSuchAProductInCategoryException {
        for (Product product : category.getAllProducts()) {
            if (product.getId() == productId)return;
        }
        throw new NoSuchAProductInCategoryException("Product (" + productId +
                ") Doesn't Exist In Category (" + category.getName()+")");
    }

    public void editCategory(int categoryId, CategoryEditAttribute editAttribute)
            throws  RepeatedNameInParentCategoryException, RepeatedFeatureException, CategoryNotFoundException {
        Category category = findCategoryById(categoryId);

        String newName = editAttribute.getName();
        String addFeature = editAttribute.getAddFeature();
        String removeFeature = editAttribute.getRemoveFeature();
        int newParentId = editAttribute.getNewParentId();

        if (newName != null){
            editName(newName, categoryId);
        }
        if (addFeature != null){
            addFeatureToCategory(category, addFeature);
        }
        if (removeFeature != null){
            removeFeatureInCategory(category, removeFeature);
        }
        if (newParentId != 0) {
            moveCategoryToAnotherParent(newParentId,categoryId);
        }

        categoryRepository.save(category);
    }

    public void editName(String name,int categoryId)
            throws RepeatedNameInParentCategoryException, CategoryNotFoundException {
        Category category = findCategoryById(categoryId);
        Category parent = category.getParent();
        checkIfThisNameIsValidForThisParent(name,parent);
        category.setName(name);
        categoryRepository.save(category);
    }

    public void moveCategoryToAnotherParent(int newParentId,int categoryId)
            throws RepeatedNameInParentCategoryException, CategoryNotFoundException {
        Category category = findCategoryById(categoryId);
        Category currentParent = category.getParent();
        Category  newParent = findCategoryById(newParentId);
        checkIfThisNameIsValidForThisParent(category.getName(),newParent);
        category.setParent(newParent);

        List<Category> subcategories = newParent.getSubCategories();
        subcategories.add(category);
        newParent.setSubCategories(subcategories);

        subcategories = currentParent.getSubCategories();
        subcategories.remove(category);
        currentParent.setSubCategories(subcategories);
        categoryRepository.save(currentParent);
        categoryRepository.save(newParent);
        categoryRepository.save(category);
    }

    public void addFeatureToCategory(Category category, String newFeature)
            throws RepeatedFeatureException {
        checkIfThisFeatureExistInThisCategory(category,newFeature);
        List<String> features = category.getSpecialFeatures();
        features.add(newFeature);
        category.setSpecialFeatures(features);
        addNewFeatureToProducts(newFeature,category.getAllProducts());
        categoryRepository.save(category);
    }

    private void checkIfThisFeatureExistInThisCategory(Category category, String feature)
            throws RepeatedFeatureException {
        for (String specialFeature : category.getSpecialFeatures()) {
            if (specialFeature.equals(feature)) throw new RepeatedFeatureException();
        }
    }

    public void removeFeatureInCategory(Category category, String removeFeature) {
        List<String> features = category.getSpecialFeatures();
        features.remove(removeFeature);
        category.setSpecialFeatures(features);
        removeAFeatureFromProducts(removeFeature,category.getAllProducts());
        categoryRepository.save(category);
    }

    private void addNewFeatureToProducts(String newFeature,List<Product> products){
        for (Product product : products) {
            Map<String,String> specialFeatures = product.getSpecialFeatures();
            specialFeatures.put(newFeature,"");
            product.setSpecialFeatures(specialFeatures);
            productRepository.save(product);
            String name = product.getName();
            product.getPackages().forEach(sellPackage -> messageService.sendMessage(sellPackage.getSeller(), "Feature Added",
                    String.format("Edit Your Product \"%s\", Some Features Added to Category", name)));
        }
    }

    private void removeAFeatureFromProducts(String removeFeature, List<Product> products){
        for (Product product : products) {
            Map<String,String> specialFeatures = product.getSpecialFeatures();
            specialFeatures.remove(removeFeature);
            product.setSpecialFeatures(specialFeatures);
        }
    }

    List<Product> getAllProductsInThisCategory(int categoryId)
            throws CategoryNotFoundException {
        if (categoryId == 0) return productService.getAllActiveProduct();
        Category category = findCategoryById(categoryId);
        return getAllProductsInThisCategory(category);
    }

    private List<Product> getAllProductsInThisCategory(Category category){
        List<Product> products = new ArrayList<>(category.getAllProducts());
        List<Category> subcategories = category.getSubCategories();
        if (!subcategories.isEmpty()){
            for (Category subcategory : subcategories) {
                products.addAll(getAllProductsInThisCategory(subcategory));
            }
        }
        return products;
    }

    public void removeCategory(int categoryId) throws CategoryNotFoundException {
        Category category = findCategoryById(categoryId);
        removeCategory(category);
    }

    private void removeCategory(Category category) {
        if (!category.getSubCategories().isEmpty())
            for (Category subCategory : category.getSubCategories()) {
                removeCategory(subCategory);
            }
        removeAllProductsIn(category);
        category.setAllProducts(null);
        Category parent = category.getParent();
        if (parent != null) {
            parent.getSubCategories().remove(category);
            categoryRepository.save(parent);
        }
        category.setParent(null);
        categoryRepository.delete(category);
    }

    private void removeAllProductsIn(Category category){
        List<Product> list = new CopyOnWriteArrayList<>(category.getAllProducts());
        for (Product product : list) {
            productService.deleteProduct(product);
        }
    }

    public ArrayList<String> getPublicFeatures() {
        return publicFeatures;
    }
}
