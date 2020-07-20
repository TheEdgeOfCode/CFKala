package com.codefathers.cfkserver.controller;

import com.codefathers.cfkserver.exceptions.token.ExpiredTokenException;
import com.codefathers.cfkserver.exceptions.token.InvalidTokenException;
import com.codefathers.cfkserver.model.entities.support.Message;
import com.codefathers.cfkserver.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.math.BigInteger;
import java.util.ArrayList;

import static com.codefathers.cfkserver.utils.ErrorUtil.sendError;
import static com.codefathers.cfkserver.utils.TokenUtil.*;

@RestController
public class SupportController {
    @Autowired
    private UserService userService;
    @Autowired
    private SimpMessagingTemplate simpMessagingTemplate;

    private ArrayList<String> onlineSupports = new ArrayList<>();

    @MessageMapping("/chat/{receiver}")
    public void sendMessage(@DestinationVariable String receiver, Message message){
        //check if receiver exist
        simpMessagingTemplate.convertAndSend("/topic/messages/" + receiver,message);
    }

    @PostMapping("/support/online")
    public void becomeOnline(HttpServletRequest request, HttpServletResponse response){
        try {
            checkToken(response, request);
            String username = getUsernameFromToken(request);
            if (username != null) {
                onlineSupports.add(username);
            }
        } catch (ExpiredTokenException | InvalidTokenException e) {
            sendError(response, HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    @PostMapping("/support/offline")
    private void becomeOffline(HttpServletRequest request, HttpServletResponse response){
        try {
            checkToken(response, request);
            String username = getUsernameFromToken(request);
            if (username != null) {
                onlineSupports.remove(username);
            }
        } catch (ExpiredTokenException | InvalidTokenException e) {
            sendError(response, HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    @GetMapping("/support/get/all")
    public ResponseEntity<?> getAllOnlineSupports(){
        return ResponseEntity.ok(onlineSupports);
    }

    @GetMapping("/support/guest/get_account")
    public ResponseEntity<?> getGuestSupport(){
        long time = System.nanoTime();
        String guestToken = (new BigInteger(Long.toString(time))).toString(36);
        return ResponseEntity.ok("guest_"+guestToken);
    }
}
