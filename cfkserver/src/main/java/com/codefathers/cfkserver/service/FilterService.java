package com.codefathers.cfkserver.service;

import com.codefathers.cfkserver.exceptions.model.category.CategoryNotFoundException;
import com.codefathers.cfkserver.exceptions.model.filters.InvalidFilterException;
import com.codefathers.cfkserver.model.entities.product.Product;
import com.codefathers.cfkserver.model.entities.product.SellPackage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;

@Service
public class FilterService {
    @Autowired private CategoryService categoryService;

    public List<Product> updateFilterList(int categoryId, HashMap<String, String> filters, int[] priceRange, boolean offMode, boolean AvaiOnly)
            throws  InvalidFilterException, CategoryNotFoundException {
        List<Product> allProductsInCategory = categoryService.getAllProductsInThisCategory(categoryId);
        ArrayList<String> validFeatures = categoryService.getAllSpecialFeaturesFromCategory(categoryId);
        validFeatures.addAll(categoryService.getPublicFeatures());
        Set<String> filterSet = filters.keySet();
        ArrayList<String> filter = new ArrayList<>(filterSet);
        checkIfFiltersAreAvailable(filter,validFeatures);
        List<Product> products = new CopyOnWriteArrayList<>(matchProductsToFilters(allProductsInCategory, filters, priceRange));
        if (AvaiOnly) {
            for (Product product : products) {
                if (!product.isAvailable()) products.remove(product);
            }
        }
        return products;
    }

    public List<Product> filterList(List<Product> list,HashMap<String,String> filters,int[] priceRange){
        List<Product> toReturn = new ArrayList<>();
        for (Product product : list) {
            if (doesMatchTheFilters(product,filters,priceRange))toReturn.add(product);
        }
        return toReturn;
    }

    private boolean doesMatchTheFilters(Product product, HashMap<String,String> filters, int[] priceRange){
        if (!thisProductIsInPriceRange(priceRange[0],priceRange[1],product.getLeastPrice())) return false;
        HashMap<String,String> features = new HashMap<>(product.getPublicFeatures());
        features.putAll(product.getSpecialFeatures());
        for (String filter : filters.keySet()) {
            if (!features.containsKey(filter))return false;
            if (!features.get(filter).equals(filters.get(filter)))return false;
        }
        return true;
    }

    private  ArrayList<Product> matchProductsToFilters(List<Product> products,HashMap<String,String> filters,int[] priceRange){
        ArrayList<Product> filteredProducts = new ArrayList<>();
        for (Product product : products) {
            if (doesMatchTheFilters(product, filters, priceRange))
                filteredProducts.add(product);
        }
        return filteredProducts;
    }

    private void checkIfFiltersAreAvailable(ArrayList<String> filters,ArrayList<String> features)
            throws InvalidFilterException {
        for (String filter : filters) {
            if (!features.contains(filter)) throw new InvalidFilterException("Filter (" + filter + ") is Invalid");
        }
    }

    private boolean thisProductIsInPriceRange(int lower, int high, int leastPrice){
        if (high == 0) return true;
        return (leastPrice >= lower && leastPrice <= high);
    }

    public List<SellPackage> filterSellPackages(int catId, List<SellPackage> list, HashMap<String, String> filters, int[] priceRange, boolean avaiOnly) {
        List<SellPackage> sellPackages = new ArrayList<>();
        list.forEach(sellPackage -> {
            if (thisProductIsInPriceRange(priceRange[0], priceRange[1], sellPackage.getPrice())) {
                if (sellPackage.getProduct().getCategory().getId() == catId) {
                    if (doesMatchTheFilters(sellPackage.getProduct(), filters,new int[]{0,0})) sellPackages.add(sellPackage);
                }
            }
        });
        if (avaiOnly) {
            List<SellPackage> packages = new CopyOnWriteArrayList<>(sellPackages);
            for (SellPackage aPackage : packages) {

            }
            return packages;
        } else {
            return sellPackages;
        }
    }
}
