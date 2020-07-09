package com.codefathers.cfkserver.service;

import com.codefathers.cfkserver.model.entities.maps.DiscountcodeIntegerMap;
import com.codefathers.cfkserver.model.entities.offs.DiscountCode;
import com.codefathers.cfkserver.model.entities.offs.Off;
import com.codefathers.cfkserver.model.entities.product.Category;
import com.codefathers.cfkserver.model.entities.product.Product;
import com.codefathers.cfkserver.model.entities.product.SellPackage;
import com.codefathers.cfkserver.model.entities.user.User;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Component
public class Sorter {
    public List<Product> sort(List<Product> toSortList, SortType sortType) {
        switch (sortType) {
            case NAME:
                sortByName(toSortList);
                break;
            case TIME:
                sortByTime(toSortList);
                break;
            case DEFAULT:
            case VIEW:
                sortByView(toSortList);
                break;
            case MORE_PRICE:
                sortByMorePrice(toSortList);
                break;
            case LESS_PRICE:
                sortByLessPrice(toSortList);
                break;
            case BOUGHT_AMOUNT:
                sortByBoughtAmount(toSortList);
                break;
            case SCORE:
                sortByScore(toSortList);
                break;
        }
        return toSortList;
    }

    public List<User> sortUser(List<User> toSortList) {
        toSortList.sort(Comparator.comparing(User::getUsername));
        return toSortList;
    }

    public void sortCategories(List<Category> list){
        for (Category category : list) {
            if (!category.getSubCategories().isEmpty()){
                sortCategories(category.getSubCategories());
            }
        }
        list.sort(Comparator.comparing(Category::getName));
    }

    public void sortDiscountCodes(List<DiscountCode> list, SortType sortType){
        switch (sortType){
            case NAME:sortDiscountByCode(list);break;
            case DEFAULT:
            case TIME:sortDiscountByStartTime(list);break;
        }
    }

    public void sortDiscountIntegers(List<DiscountcodeIntegerMap> list, SortType sortType){
        switch (sortType){
            case DEFAULT: sortDiscountIntegersByAmount(list);break;
            case NAME: sortDiscountIntegersByName(list);break;
        }
    }

    private void sortDiscountIntegersByAmount(List<DiscountcodeIntegerMap> list){
        list.sort(Comparator.comparingInt(DiscountcodeIntegerMap::getInteger));
    }

    private void sortDiscountIntegersByName(List<DiscountcodeIntegerMap> list){
        list.sort(Comparator.comparing(discountCodeIntegerMap -> discountCodeIntegerMap.getDiscountCode().getCode()));
    }

    private void sortDiscountByStartTime(List<DiscountCode> list){
        list.sort(Comparator.comparing(DiscountCode::getStartTime));
    }

    private void sortDiscountByCode(List<DiscountCode> list){
        list.sort(Comparator.comparing(DiscountCode::getCode));
    }

    public void sortOff(List<Off> list){
        list.sort(Comparator.comparingInt(Off::getOffPercentage));
    }

    private void sortByName(List<Product> products) {
        products.sort(Comparator.comparing(Product::getName));
    }

    private void sortByView(List<Product> products) {
        products.sort((firstProduct, secondProduct) -> secondProduct.getView() - firstProduct.getView());
    }

    private void sortByBoughtAmount(List<Product> products) {
        products.sort((firstProduct, secondProduct) -> secondProduct.getBoughtAmount() - firstProduct.getBoughtAmount());
    }

    private void sortByTime(List<Product> products) {
        products.sort((firstProduct, secondProduct) -> secondProduct.getDateAdded().compareTo(firstProduct.getDateAdded()));
    }

    private void sortByMorePrice(List<Product> products) {
        products.sort((firstProduct, secondProduct) -> secondProduct.getLeastPrice() - firstProduct.getLeastPrice());
    }

    private void sortByLessPrice(List<Product> products) {
        products.sort(Comparator.comparingInt(Product::getLeastPrice));
    }

    private void sortByScore(List<Product> products) {
        products.sort((firstProduct, secondProduct) -> Double.toString(secondProduct.getTotalScore()).
                compareTo(Double.toString(firstProduct.getLeastPrice())));
    }

    public void sortSellPackage(List<SellPackage> list, SortType type) {
        switch (type) {
            default:
            case VIEW:
                sortPackagesByView(list);
            case TIME:
                sortPackagesByTime(list);
            case NAME:
                sortPackagesByName(list);
            case SCORE:
                sortPackagesByScore(list);
            case MORE_PRICE:
                sortPackagesByPrice(list);
            case BOUGHT_AMOUNT:
                sortPackagesByBought(list);
        }
    }

    private void sortPackagesByBought(List<SellPackage> list) {
        list.sort(Comparator.comparing(o -> o.getProduct().getBoughtAmount()));
    }

    private void sortPackagesByPrice(List<SellPackage> list) {
        list.sort(Comparator.comparing(SellPackage::getPrice));
    }

    private void sortPackagesByScore(List<SellPackage> list) {
        list.sort(Comparator.comparing(o -> o.getProduct().getTotalScore()));
    }

    private void sortPackagesByName(List<SellPackage> list) {
        list.sort(Comparator.comparing(o -> o.getProduct().getName()));
    }

    private void sortPackagesByTime(List<SellPackage> list) {
        list.sort(Comparator.comparing(o -> o.getProduct().getDateAdded()));
    }

    private void sortPackagesByView(List<SellPackage> list) {
        list.sort(Comparator.comparingInt(sellPackage -> sellPackage.getProduct().getView()));
    }

    public enum SortType {
        NAME,
        VIEW,
        MORE_PRICE,
        LESS_PRICE,
        BOUGHT_AMOUNT,
        TIME,
        SCORE,
        DEFAULT;

        public static SortType valueOF(String string) {
            switch (string) {
                case "Price":
                    return MORE_PRICE;
                case "Date Added":
                    return TIME;
                case "View":
                    return VIEW;
                case "Bought":
                    return BOUGHT_AMOUNT;
                case "Name":
                    return NAME;
                case "Score":
                    return SCORE;
                default:
                    return DEFAULT;
            }
        }
    }
}
