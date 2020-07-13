package com.codefathers.cfkserver.service;

import com.codefathers.cfkserver.exceptions.model.off.NoSuchAOffException;
import com.codefathers.cfkserver.exceptions.model.product.NoSuchAProductException;
import com.codefathers.cfkserver.exceptions.model.product.NoSuchSellerException;
import com.codefathers.cfkserver.exceptions.model.request.NoSuchARequestException;
import com.codefathers.cfkserver.exceptions.model.user.NoSuchAPackageException;
import com.codefathers.cfkserver.model.entities.contents.Advertise;
import com.codefathers.cfkserver.model.entities.offs.Off;
import com.codefathers.cfkserver.model.entities.offs.OffStatus;
import com.codefathers.cfkserver.model.entities.product.*;
import com.codefathers.cfkserver.model.entities.request.Request;
import com.codefathers.cfkserver.model.entities.request.RequestType;
import com.codefathers.cfkserver.model.entities.request.edit.OffChangeAttributes;
import com.codefathers.cfkserver.model.entities.request.edit.ProductEditAttribute;
import com.codefathers.cfkserver.model.entities.user.Seller;
import com.codefathers.cfkserver.model.repositories.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class RequestService {
    @Autowired
    private RequestRepository requestRepository;
    @Autowired
    private SellerRepository sellerRepository;
    @Autowired
    private AdvertiseRepository advertiseRepository;
    @Autowired
    private MessageService messageService;
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private ProductService productService;
    @Autowired
    private CategoryService categoryService;
    @Autowired
    private SellPackageRepository sellPackageRepository;
    @Autowired
    private OffRepository offRepository;
    @Autowired
    private OffService offService;

    public Request createRequest(Object requestObject, RequestType type,String userRequested,String requset){
        Request request = new Request(userRequested,type,requset,requestObject);
        requestRepository.save(request);
        return request;
    }

    public Request findRequestById(int id)
            throws NoSuchARequestException {
        Optional<Request> requestOptional = requestRepository.findById(id);
        if (requestOptional.isPresent()){
            return requestOptional.get();
        }else throw new NoSuchARequestException("No Request With Id (" + id + ")");

    }

    public void accept(int requestId)
            throws NoSuchARequestException, NoSuchAProductException {
        Request request = findRequestById(requestId);
        RequestType type = request.getRequestType();
        switch (type){
            case CREATE_PRODUCT:
                acceptCreateProduct(request);
                break;
            case EDIT_PRODUCT:
                acceptEditProduct(request);
                break;
            case CREATE_OFF:
                acceptCreateOff(request);
                break;
            case EDIT_OFF:
                acceptEditOff(request);
                break;
            case ASSIGN_COMMENT:
                acceptAssignComment(request);
                break;
            case REGISTER_SELLER:
                acceptSeller(request);
                break;
            case ADVERTISE:
                acceptAdvertise(request);
        }
        request.setDone(true);
        request.setAccepted(true);
        requestRepository.save(request);
    }

    private void acceptAdvertise(Request request) {
        String username = request.getUserHasRequested();
        Optional<Seller> sellerOptional = sellerRepository.findById(request.getUserHasRequested());
        if (sellerOptional.isPresent()) {
            Seller seller = sellerOptional.get();
            if (seller.getBalance() > 20) {
                seller.setBalance(seller.getBalance() - 20);
                Advertise ad = request.getAdvertise();
                ad.setActive(true);
                ad.setCreated(new Date());
                advertiseRepository.save(ad);
                sellerRepository.save(seller);
            } else {
               messageService.sendMessage(seller, "Ad Request Rejected",
                        "You Don't Have Enough Money to Create An Advertisement for your product.\n" +
                                "Refill Your Account Ant Try Late.\n" +
                                "\n" +
                                new Date() + "" +
                                "CFKala Manager");
            }
        }
    }

    private void acceptCreateProduct(Request request){
        Product product = request.getProduct();
        product.setProductStatus(ProductStatus.VERIFIED);
        productRepository.save(product);
        categoryService.addProductToCategory(product,product.getCategory());
        Seller seller;
        Optional<Seller> sellerOptional = sellerRepository.findById(request.getUserHasRequested());
        if (sellerOptional.isPresent()){
            seller = sellerOptional.get();
            SellPackage sellPackage = product.getPackages().get(0);
            seller.getPackages().add(sellPackage);
            sellerRepository.save(seller);
        }
    }

    private void acceptEditProduct(Request request) throws NoSuchAProductException {
        ProductEditAttribute editAttribute = request.getProductEditAttribute();
        Product product = productService.findById(editAttribute.getSourceId());
        product.setProductStatus(ProductStatus.VERIFIED);
        if (editAttribute.getName() != null) {
            product.setName(editAttribute.getName());
            productRepository.save(product);
        }
        if (editAttribute.getPublicFeatures() != null) {
            editPublicFeatureProduct(product,editAttribute);
        }
        if (editAttribute.getSpecialFeatures() != null) {
            editSpecialFeatureProduct(product,editAttribute);
        }
        if (editAttribute.getNewCategoryId() != 0){
            try {
                categoryService.editProductCategory(product.getId(),product.getCategory().getId(),editAttribute.getNewCategoryId());
            } catch (Exception ignore) {
            }
        }
        if (editAttribute.getNewPrice() != 0) {
            try {
                productService.changePrice(product, editAttribute.getNewPrice(), request.getUserHasRequested());
            } catch (NoSuchSellerException ignore) {
            }
        }
        if (editAttribute.getNewStock() != 0) {
            try {
                productService.changeStock(product, editAttribute.getNewStock(), request.getUserHasRequested());
            } catch (NoSuchSellerException ignore) {
            }
        }
    }

    private void editPublicFeatureProduct(Product product,ProductEditAttribute editAttribute){
        Map<String, String> publicFeatures = editAttribute.getPublicFeatures();
        Map<String, String> features = product.getPublicFeatures();
        publicFeatures.forEach((key, value) -> {
            if (features.containsKey(key)) {
                features.replace(key, value);
            }
        });
        productRepository.save(product);
    }

    private void editSpecialFeatureProduct(Product product,ProductEditAttribute editAttribute){
        Map<String, String> specialFeatures = editAttribute.getSpecialFeatures();
        Map<String, String> features = product.getPublicFeatures();
        specialFeatures.forEach((key, value) -> {
            if (features.containsKey(key)) {
                features.replace(key, value);
            }
        });
        productRepository.save(product);
    }

    private void acceptCreateOff(Request request){
        Off off = request.getOff();
        off.setOffStatus(OffStatus.ACCEPTED);
        offRepository.save(off);
        Seller seller = off.getSeller();
        List<Off> offs = seller.getOffs();
        offs.add(off);
        seller.setOffs(offs);
        sellerRepository.save(seller);
    }

    private void acceptEditOff(Request request){
        OffChangeAttributes changeAttributes = request.getOffEdit();
        try {
            Off off = offService.findOffById(changeAttributes.getSourceId());
            if (changeAttributes.getStart() != null){
                off.setStartTime(changeAttributes.getStart());
            }
            if (changeAttributes.getEnd() != null){
                off.setEndTime(changeAttributes.getEnd());
            }
            if (changeAttributes.getPercentage() != 0){
                off.setOffPercentage(changeAttributes.getPercentage());
            }
            if (changeAttributes.getProductIdToAdd() != 0){
                addProductToOff(off,changeAttributes);
            }
            if (changeAttributes.getProductIdToRemove() != 0){
                removeProductFromOff(off, changeAttributes);
            }
            off.setOffStatus(OffStatus.ACCEPTED);
            offRepository.save(off);
        } catch (NoSuchAOffException ignore) {}
    }

    private void addProductToOff(Off off,OffChangeAttributes changeAttributes){
        try {
            Product product = productService.findById(changeAttributes.getProductIdToAdd());
            off.getProducts().add(product);
            productRepository.save(product);
            Seller seller = off.getSeller();
            try {
                SellPackage sellPackage = seller.findPackageByProductId(product.getId());
                sellPackage.setOnOff(true);
                sellPackage.setOff(off);
                sellPackageRepository.save(sellPackage);
            } catch (NoSuchAPackageException e) {
                e.printStackTrace();
            }
            sellerRepository.save(seller);
        } catch (NoSuchAProductException ignore) {}
    }

    private void removeProductFromOff(Off off, OffChangeAttributes changeAttributes) {
        try {
            Product product = productService.findById(changeAttributes.getProductIdToRemove());
            SellPackage sellPackage = off.getSeller().findPackageByProductId(product.getId());
            sellPackage.setOff(null);
            sellPackage.setOnOff(false);
            sellPackageRepository.save(sellPackage);
            off.getProducts().remove(product);
        } catch (NoSuchAProductException | NoSuchAPackageException ignore) {}
    }


    private void acceptAssignComment(Request request) throws NoSuchAProductException {
        Comment comment = request.getComment();
        comment.setStatus(CommentStatus.VERIFIED);
        productService.assignAComment(comment.getProduct().getId(), comment);
    }

    private void acceptSeller(Request request) {
        Seller seller = request.getSeller();
        seller.setVerified(true);
        sellerRepository.save(seller);
    }

    public void decline(int requestId) throws NoSuchARequestException {
        Request request = findRequestById(requestId);
        request.setDone(true);
        request.setAccepted(false);
        requestRepository.save(request);
    }

    public List<Request> getRequestsForManager() {
        return requestRepository.findAllByDoneIsFalse();
    }

    void save(Request request) {
        requestRepository.save(request);
    }
}
