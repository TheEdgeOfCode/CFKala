package com.codefathers.cfkserver.controller;

import com.codefathers.cfkserver.model.entities.support.Message;
import com.codefathers.cfkserver.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SupportController {
    @Autowired
    private UserService userService;
    @Autowired
    private SimpMessagingTemplate simpMessagingTemplate;

    @MessageMapping("/chat/{receiver}")
    public void sendMessage(@DestinationVariable String receiver, Message message){
        //check if receiver exist
        simpMessagingTemplate.convertAndSend("/topic/messages/" + receiver,message);
    }
}
