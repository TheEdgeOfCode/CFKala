package com.codefathers.cfkserver.service;

import com.codefathers.cfkserver.exceptions.model.auction.AuctionNotFoundException;
import com.codefathers.cfkserver.exceptions.model.off.InvalidTimesException;
import com.codefathers.cfkserver.exceptions.model.product.NoSuchAProductException;
import com.codefathers.cfkserver.exceptions.model.product.NoSuchSellerException;
import com.codefathers.cfkserver.exceptions.model.product.ProductNotAvailableException;
import com.codefathers.cfkserver.model.dtos.auction.AuctionLogDTO;
import com.codefathers.cfkserver.model.dtos.auction.CreateAuctionDTO;
import com.codefathers.cfkserver.model.entities.offs.Auction;
import com.codefathers.cfkserver.model.entities.product.Product;
import com.codefathers.cfkserver.model.entities.product.SellPackage;
import com.codefathers.cfkserver.model.entities.user.Seller;
import com.codefathers.cfkserver.model.repositories.AuctionRepository;
import com.google.gson.Gson;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Optional;

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

    public void startConnection(){
        new AuctionServer(9090).startConnection();
    }
}

@Data
class AuctionServer{
    private int port;
    private ArrayList<AuctionClient> clients;

    public AuctionServer(int port){
        this.port = port;
        this.clients = new ArrayList<>();
    }

    public void startConnection(){
        Thread thread = getServerThread();
        thread.setDaemon(true);
        thread.start();
    }

    private Thread getServerThread(){
        return new Thread(() -> {
            try {
                ServerSocket serverSocket = new ServerSocket(port);

                while (true) {
                    Socket socket = serverSocket.accept();
                    socket.setTcpNoDelay(true);
                    AuctionClient client = new AuctionClient(socket, this);
                    clients.add(client);

                    Thread thread = new Thread(client);
                    thread.start();
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        });
    }

    public void broadcast(String message) {
        for (AuctionClient client : this.clients) {
            client.send(message);
        }
    }

    public boolean hasClients() {
        return !clients.isEmpty();
    }
}

class AuctionClient extends Thread {
    @Autowired private AuctionService auctionService;

    private Socket socket;
    private AuctionServer server;
    private DataInputStream input;
    private DataOutputStream output;

    public AuctionClient(Socket socket, AuctionServer server) {
        this.socket = socket;
        this.server = server;
    }

    @Override
    public void run() {
        try(DataOutputStream out = new DataOutputStream(socket.getOutputStream());
            DataInputStream input = new DataInputStream(socket.getInputStream())) {
            this.input = input;
            this.output = out;

            //printUsers();

            while (true) {
                String message = input.readUTF();
                if (message.startsWith("{\"expression\"")){
                    AuctionLogDTO dto = new Gson().fromJson(message, AuctionLogDTO.class);
                    long price = Long.parseLong(dto.getPrice());
                    try {
                        auctionService.addPrice(dto.getAuctionId(), price, dto.getUsername());
                    } catch (AuctionNotFoundException e) {
                        e.printStackTrace();
                    }
                }
                server.broadcast(message);
            }

        } catch (IOException ex) {
            ex.printStackTrace();
        }  finally {
            try {
                socket.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }


    void printUsers() {
        if (server.hasClients()) {
            send(new Gson().toJson(server.getClients()));
        } else {
            send("No Other Users Are in the Auction Now");
        }
    }

    public void send(String message) {
        try {
            output.writeUTF(message);
            output.flush();
        } catch (IOException ex) {
            ex.printStackTrace();
        }

    }
}
