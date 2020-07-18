package com.codefathers.cfkserver.controller;

import com.codefathers.cfkserver.exceptions.token.ExpiredTokenException;
import com.codefathers.cfkserver.exceptions.token.InvalidTokenException;
import com.codefathers.cfkserver.model.dtos.user.MessagePM;
import com.codefathers.cfkserver.model.entities.user.Message;
import com.codefathers.cfkserver.model.entities.user.User;
import com.codefathers.cfkserver.service.MessageService;
import com.codefathers.cfkserver.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.util.ArrayList;
import java.util.List;

import static com.codefathers.cfkserver.utils.ErrorUtil.sendError;
import static com.codefathers.cfkserver.utils.TokenUtil.checkToken;
import static com.codefathers.cfkserver.utils.TokenUtil.getUsernameFromToken;

@RestController
public class MessageController {
    @Autowired
    private MessageService messageService;
    @Autowired
    private UserService userService;

    @RequestMapping("/messages/open/{id}")
    @PostMapping
    private void openMessage(@PathVariable Integer id, HttpServletResponse response, HttpServletRequest request){
        try {
            checkToken(response,request);
            messageService.setMessageAsRead(id);
        } catch (ExpiredTokenException | InvalidTokenException e) {
            sendError(response, HttpStatus.UNAUTHORIZED,e.getMessage());
        }
    }

    @PostMapping("/messages/get_all")
    private ResponseEntity<?> getAllMessages(HttpServletResponse response, HttpServletRequest request){
        try {
            checkToken(response, request);
            User user = userService.getUserByUsername(getUsernameFromToken(request));
            List<Message> allMessagesOfThisUser = messageService.getAllMesagesOfThisUser(user);
            ArrayList<MessagePM> toReturn = new ArrayList<>();
            for (Message message : allMessagesOfThisUser) {
                toReturn.add(new MessagePM(message.getId(),message.getSubject(),message.getMessage(),message.isRead(),message.getDate()));
            }
            return ResponseEntity.ok(toReturn);
        } catch (ExpiredTokenException | InvalidTokenException e) {
            sendError(response, HttpStatus.UNAUTHORIZED,e.getMessage());
            return null;
        } catch (Exception e) {
            sendError(response, HttpStatus.BAD_REQUEST,e.getMessage());
            return null;
        }
    }
}
