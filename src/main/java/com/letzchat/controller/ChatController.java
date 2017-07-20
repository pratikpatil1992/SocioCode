package com.letzchat.controller;

import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.letzchat.model.Chat;
import com.letzchat.model.Message;
import com.letzchat.model.OutputMessage;
import com.letzchat.model.User;

@RestController
public class ChatController 
{
	
	private static Logger log = LoggerFactory.getLogger(FriendController.class);
	
	@PostMapping("/sendmessage/{friendId}")
	public Chat sendmessage(@PathVariable ("friendId") String friendId,@RequestBody User user)
	{
		log.debug("inside sendmessage");
		Chat chat = new Chat();
		return chat;
	}
	
	@MessageMapping("/chat")   ///sendMessage
	@SendTo("/topic/message")        //receiveMessage
	public OutputMessage sendMessage(Message message) {
		log.debug("Calling the method sendMessage");
	  
		//  logger.debug(" Message ID : ",message.getId());
	    return new OutputMessage(message, new Date()); 
	}
	  

}
