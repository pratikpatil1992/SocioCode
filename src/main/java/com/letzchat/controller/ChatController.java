package com.letzchat.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.letzchat.model.Chat;
import com.letzchat.model.User;

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

}
