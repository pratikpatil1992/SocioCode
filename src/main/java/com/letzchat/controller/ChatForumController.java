package com.letzchat.controller;

import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.web.bind.annotation.RestController;

import com.letzchat.model.Message;
import com.letzchat.model.OutputMessage;

@RestController
public class ChatForumController {
	
	private static final Logger logger = LoggerFactory.getLogger(ChatForumController.class);
	  
	  @MessageMapping("/chat_forum")   ///sendMessage
	  @SendTo("/topic/message")        //receiveMessage
	  public OutputMessage sendMessage(Message message) {
		  logger.debug("Calling the method sendMessage");
	  
		//  logger.debug(" Message ID : ",message.getId());
	    return new OutputMessage(message, new Date()); 
	  }
	  
	

}







