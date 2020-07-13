package com.codefathers.cfkserver.service;

import com.codefathers.cfkserver.model.entities.user.Message;
import com.codefathers.cfkserver.model.entities.user.User;
import com.codefathers.cfkserver.model.repositories.MessageRepository;
import com.codefathers.cfkserver.model.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class MessageService {
    @Autowired private MessageRepository messageRepository;
    @Autowired private UserRepository userRepository;

    public void sendMessage(User user, String subject, String text){
        Message message = new Message();
        message.setDate(new Date());
        message.setSubject(subject);
        message.setRead(false);
        message.setMessage(text);
        messageRepository.save(message);
        user.getMessages().add(message);
        userRepository.save(user);
    }

    public List<Message> getAllMesagesOfThisUser(User user){
        return user.getMessages();
    }

    public void setMessageAsRead(int id){
        Optional<Message> optionalMessage = messageRepository.findById(id);
        if (optionalMessage.isPresent()) {
            Message message = optionalMessage.get();
            message.setRead(true);
            messageRepository.save(message);
        }
    }
}
