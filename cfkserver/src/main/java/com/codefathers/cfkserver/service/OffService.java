package com.codefathers.cfkserver.service;

import com.codefathers.cfkserver.exceptions.model.off.InvalidTimesException;
import com.codefathers.cfkserver.exceptions.model.off.NoSuchAOffException;
import com.codefathers.cfkserver.exceptions.model.off.ThisOffDoesNotBelongsToYouException;
import com.codefathers.cfkserver.exceptions.model.product.NoSuchSellerException;
import com.codefathers.cfkserver.model.entities.offs.Off;
import com.codefathers.cfkserver.model.entities.offs.OffStatus;
import com.codefathers.cfkserver.model.entities.product.Product;
import com.codefathers.cfkserver.model.entities.product.SellPackage;
import com.codefathers.cfkserver.model.entities.request.Request;
import com.codefathers.cfkserver.model.entities.request.RequestType;
import com.codefathers.cfkserver.model.entities.request.edit.OffChangeAttributes;
import com.codefathers.cfkserver.model.entities.user.Seller;
import com.codefathers.cfkserver.model.repositories.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class OffService {
    @Autowired private OffRepository offRepository;
    @Autowired private RequestService requestService;
    @Autowired private SellerRepository sellerRepository;
    @Autowired private OffEditAttributeRepository attributeRepository;
    @Autowired private SellPackageRepository sellPackageRepository;
    @Autowired private RequestRepository requestRepository;

    public void createOff(Seller seller, Date[] dates, int percentage) throws InvalidTimesException {
        if (!dates[0].before(dates[1])) throw new InvalidTimesException();
        Off off = new Off();
        off.setSeller(seller);
        off.setOffPercentage(percentage);
        off.setOffStatus(OffStatus.CREATION);
        off.setStartTime(dates[0]);
        off.setEndTime(dates[1]);
        offRepository.save(off);
        String strRequest = String.format("%s requested to create an off with percentage %d",seller.getUsername(),percentage);
        Request request = requestService.createRequest(off, RequestType.CREATE_OFF,seller.getUsername(),strRequest);
        seller.addRequest(request);
        sellerRepository.save(seller);
    }

    public Off findOffById(int id) throws NoSuchAOffException {
        Optional<Off> off = offRepository.findById(id);
        if (off.isEmpty()) throw new NoSuchAOffException("No Such an Off (" + id + ")");
        return off.get();
    }

    public void editOff(OffChangeAttributes changeAttributes, String editor) throws NoSuchAOffException, ThisOffDoesNotBelongsToYouException {
        Off off = findOffById(changeAttributes.getSourceId());
        checkIfThisSellerCreatedTheOff(off,editor);
        off.setOffStatus(OffStatus.EDIT);
        String strRequest = String.format("%s requested to edit %s", editor, changeAttributes);
        Request request = requestService.createRequest(changeAttributes, RequestType.EDIT_OFF,strRequest,editor);
        attributeRepository.save(changeAttributes);
        Optional<Seller> sellerOptional = sellerRepository.findById(editor);
        if (sellerOptional.isPresent()) {
            Seller seller = sellerOptional.get();
            seller.addRequest(request);
            sellerRepository.save(seller);
        }
    }

    public void deleteOff(int id,String remover) throws NoSuchAOffException, ThisOffDoesNotBelongsToYouException {
        Off off = findOffById(id);
        checkIfThisSellerCreatedTheOff(off,remover);
        deleteOff(off);
    }

    private void deleteOff(Product product, Seller remover) {
        try {
            SellPackage sellPackage = product.findPackageBySeller(remover);
            sellPackage.setOnOff(false);
            sellPackage.setOff(null);
            sellPackageRepository.save(sellPackage);
        } catch (NoSuchSellerException e) {
            e.printStackTrace();
        }
    }

    void deleteOff(Off off) {
        Seller seller = off.getSeller();
        off.getProducts().forEach(product -> deleteOff(product, seller));
        seller.getOffs().remove(off);
        sellerRepository.save(seller);
        deleteRequestsContainOff(off);
        off.setSeller(null);
        off.setProducts(null);
        offRepository.delete(off);
    }

    private void deleteRequestsContainOff(Off off) {
        List<Request> requests = requestRepository.findAllByOff(off);
        requests.forEach(request -> {
            request.setOff(null);
            requestService.save(request);
        });
    }

    private void checkIfThisSellerCreatedTheOff(Off off,String viewer) throws ThisOffDoesNotBelongsToYouException {
        if (!off.getSeller().getUsername().equals(viewer))
            throw new ThisOffDoesNotBelongsToYouException("This Off Does Not Belongs To You Exception");
    }
}
