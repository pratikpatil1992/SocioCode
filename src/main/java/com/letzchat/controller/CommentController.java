package com.letzchat.controller;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.letzchat.dao.CommentDAO;
import com.letzchat.model.Comment;

@RestController
public class CommentController 
{
	private static Logger log = LoggerFactory.getLogger(CommentController.class);
	
	@Autowired CommentDAO commentDAO;
	
	@Autowired Comment comment;
	
	@GetMapping("/comments/{blogId}")
	public ResponseEntity<List<Comment>> getAllComments(@PathVariable("blogId") String blogId)
	{
		log.debug("inside getAllFriends");
		List<Comment> commentList =  commentDAO.getComments(blogId);	
		return new ResponseEntity<List<Comment>>(commentList, HttpStatus.OK);
	}
	
	@PostMapping("/postcomment/{blogId}/{userId}")
	public Comment postcomment(@PathVariable ("blogId") String blogId, @PathVariable ("userId") String userId,@RequestBody Comment newComment)
	{
		log.debug("inside postcomment");
		comment=new Comment();
		comment.setContent(newComment.getContent());
		comment.setUser_id(userId);
		comment.setBlog_id(blogId);
		commentDAO.save(comment);
		comment.setErrorCode("200");
		comment.setErrorMessage("Thank you for commenting.");
			
		return comment;
	}
	
	@DeleteMapping("/deletecomment/{id}")
	public boolean deletecomment(@PathVariable ("id") String id)
	{
		log.debug("delete comment rest controller");
		comment=commentDAO.get(id);
		if(commentDAO.delete(id))
			return true;
		else
			return false;
	}
	
}


