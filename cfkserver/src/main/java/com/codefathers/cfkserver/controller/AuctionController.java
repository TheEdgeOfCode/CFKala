package com.codefathers.cfkserver.controller;

import com.codefathers.cfkserver.exceptions.model.bank.account.InvalidUsernameException;
import com.codefathers.cfkserver.exceptions.model.bank.receipt.InvalidReceiptIdException;
import com.codefathers.cfkserver.exceptions.model.off.InvalidTimesException;
import com.codefathers.cfkserver.exceptions.model.product.NoSuchAProductException;
import com.codefathers.cfkserver.exceptions.model.product.NoSuchSellerException;
import com.codefathers.cfkserver.exceptions.model.product.ProductNotAvailableException;
import com.codefathers.cfkserver.exceptions.token.ExpiredTokenException;
import com.codefathers.cfkserver.exceptions.token.InvalidTokenException;
import com.codefathers.cfkserver.model.dtos.auction.CreateAuctionDTO;
import com.codefathers.cfkserver.model.dtos.auction.MiniAuctionDTO;
import com.codefathers.cfkserver.model.dtos.auction.MiniAuctionListDTO;
import com.codefathers.cfkserver.model.dtos.bank.TransactionDTO;
import com.codefathers.cfkserver.model.dtos.bank.TransactionListDTO;
import com.codefathers.cfkserver.model.entities.offs.Auction;
import com.codefathers.cfkserver.model.entities.user.Seller;
import com.codefathers.cfkserver.service.AuctionService;
import com.codefathers.cfkserver.service.SellerService;
import com.codefathers.cfkserver.utils.TokenUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static com.codefathers.cfkserver.utils.ErrorUtil.sendError;
import static com.codefathers.cfkserver.utils.TokenUtil.checkToken;
import static com.codefathers.cfkserver.utils.TokenUtil.getUsernameFromToken;
import static org.springframework.http.HttpStatus.BAD_REQUEST;

@RestController
public class AuctionController {

    @Autowired
    private AuctionService auctionService;

    @Autowired
    private SellerService sellerService;

    @PostMapping("/auction/create_auction")
    private void createAuction(HttpServletRequest request, HttpServletResponse response, @RequestBody CreateAuctionDTO dto) {
        try {
            if (checkToken(response, request)) {
                try {
                    Seller seller = sellerService.findSellerByUsername(getUsernameFromToken(request));
                    auctionService.createAuction(seller, dto);
                } catch (Exception e) {
                    sendError(response, BAD_REQUEST, e.getMessage());
                }
            }
        } catch (ExpiredTokenException | InvalidTokenException e) {
            sendError(response, HttpStatus.UNAUTHORIZED, e.getMessage());
        }
    }

    @GetMapping("/auction/get_auctions")
    private ResponseEntity<?> getAllAuctions(HttpServletRequest request, HttpServletResponse response) {
        try {
            if (checkToken(response, request)) {
                List<MiniAuctionDTO> dtos = new ArrayList<>();
                List<Auction> auctions = auctionService.getAllAuctions();
                for (Auction auction : auctions) {
                    dtos.add(new MiniAuctionDTO(
                            auction.getSellPackage().getProduct().getName(),
                            auction.getSellPackage().getProduct().getId(),
                            auction.getCurrentPrice(),
                            auction.getStartTime(),
                            auction.getEndTime(),
                            auction.getSellPackage().getSeller().getUsername()
                    ));
                }
                return ResponseEntity.ok(new MiniAuctionListDTO(new ArrayList<>(dtos)));
            }
        } catch (ExpiredTokenException | InvalidTokenException e) {
            sendError(response, HttpStatus.UNAUTHORIZED, e.getMessage());
        }
        return null;
    }
}
