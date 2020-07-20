package com.codefathers.cfkserver.service;

import com.codefathers.cfkserver.exceptions.model.auction.AuctionNotFoundException;
import com.codefathers.cfkserver.exceptions.model.off.InvalidTimesException;
import com.codefathers.cfkserver.exceptions.model.product.NoSuchAProductException;
import com.codefathers.cfkserver.exceptions.model.product.NoSuchSellerException;
import com.codefathers.cfkserver.exceptions.model.product.ProductNotAvailableException;
import com.codefathers.cfkserver.model.dtos.auction.CreateAuctionDTO;
import com.codefathers.cfkserver.model.entities.offs.Auction;
import com.codefathers.cfkserver.model.entities.product.Product;
import com.codefathers.cfkserver.model.entities.product.SellPackage;
import com.codefathers.cfkserver.model.entities.user.Seller;
import com.codefathers.cfkserver.model.repositories.AuctionRepository;
import lombok.Data;
import org.apache.catalina.Server;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Optional;
import java.util.function.Consumer;

@Service
public class AuctionService {
    @Autowired private AuctionRepository auctionRepository;
    @Autowired private ProductService productService;

    public Auction getAuctionById(int id) throws AuctionNotFoundException {
        Optional<Auction> auction = auctionRepository.findById(id);
        if (auction.isPresent()){
            return auction.get();
        } else {
            throw new AuctionNotFoundException("Auction Was Not Found");
        }
    }

    public void createAuction(Seller seller, CreateAuctionDTO dto) throws InvalidTimesException, NoSuchAProductException,
            ProductNotAvailableException, NoSuchSellerException {
        if (!dto.getStart().before(dto.getEnd())) throw new InvalidTimesException();
        Auction auction = new Auction();
        auction.setStartTime(dto.getStart());
        auction.setEndTime(dto.getEnd());

        Product product = productService.findById(dto.getProductId());
        if (!product.isAvailable()) throw new ProductNotAvailableException("You don't have this product in stock");
        SellPackage sellPackage = product.findPackageBySeller(seller);

        auction.setSellPackage(sellPackage);
        auctionRepository.save(auction);
    }

    public void addPrice(int auctionId, long price, String username) throws AuctionNotFoundException {
        Auction auction = getAuctionById(auctionId);
        auction.setCurrentPrice(price);
        auction.setMostPriceUser(username);
    }
}

@Data
class AuctionServer{
    private Consumer<Serializable> onReceiveCallBack;
    private AuctionThread auctionThread = new AuctionThread();
    private int port;

    public AuctionServer(int port, Consumer<Serializable> onReceiveCallBack){
        this.onReceiveCallBack = onReceiveCallBack;
        this.port = port;
        this.auctionThread.setDaemon(true);
    }

    public void startConnection(){
        auctionThread.start();
    }

    public void send(Serializable data) throws IOException {
        auctionThread.out.writeObject(data);
    }

    public void closeConnection() throws IOException {
        auctionThread.socket.close();
    }

    private class AuctionThread extends Thread{
        private ObjectOutputStream out;
        private Socket socket;

        @Override
        public void run() {
            try(ServerSocket server = new ServerSocket(port);
                Socket socket = server.accept();
                ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
                ObjectInputStream in = new ObjectInputStream(socket.getInputStream())) {

                this.out = out;
                this.socket = socket;
                socket.setTcpNoDelay(true);

                while (true){
                    Serializable data = (Serializable) in.readObject();
                    onReceiveCallBack.accept(data);
                }

            } catch(Exception e){
                onReceiveCallBack.accept("Connection Lost");
            }
        }
    }
}
