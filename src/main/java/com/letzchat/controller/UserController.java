package com.letzchat.controller;


import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.letzchat.dao.FriendDAO;
import com.letzchat.dao.UserDAO;
import com.letzchat.model.User;

@RestController
public class UserController {

	private static Logger log = LoggerFactory.getLogger(UserController.class);

	@Autowired 
	private User user;
	
	@Autowired
	private UserDAO userDAO;
	
	@Autowired
	FriendDAO friendDAO;
	
	@Autowired
	HttpSession session;
		
		@PostMapping("/searchpeople/")
		public ResponseEntity<List<User>> searchUsers(@RequestBody String name)
		{
			
			List<User> userList =  userDAO.searchlist(name);
			return new ResponseEntity<List<User>>(userList, HttpStatus.OK);
		}
		
		//http://localhost:8080/CollaborationResetService/user/niit
		@GetMapping("/user/{id}")
		public ResponseEntity<User> getUserByID(@PathVariable("id") String id)
		{
			log.debug("Starting of the method getUserByID");
			log.info("Trying to get userdetails of the id " + id);
			user = userDAO.getUser(id);
			
			if(user==null)
			{
				user = new User();
				user.setErrorCode("404");
				user.setErrorMessage("User does not exist with the id :" + id);
			}
			else
			{
				user.setErrorCode("200");
				user.setErrorMessage("success");
			}
			
			log.info("**************** Name of the user is " + user.getName());
			log.debug("**************Ending of the method getUserByID");
		  return	new ResponseEntity<User>(user , HttpStatus.OK);
		}
		
		@PostMapping("/validate")
		public ResponseEntity<User> validateCredentials(@RequestBody User user)
		{
			user = userDAO.validate(user.getEmail(), user.getPassword());
			log.debug("user"+user);
			
			if (user == null) {
				user = new User(); // Do wee need to create new user?
				user.setErrorCode("404");
				user.setErrorMessage("Invalid Credentials.  Please enter valid credentials");
				log.debug("->->->->InValid Credentials");

			 }
			 else
			 {
				 
						user.setErrorCode("200");
						user.setErrorMessage("You have successfully logged in.");
						user.setIsOnline('Y');
						userDAO.update(user);
						log.debug("->->->->Valid Credentials");
						session.setAttribute("loggedInUserID", user.getId());
						session.setAttribute("loggedInUserRole", user.getRole());
					
						log.debug("You are loggin with the role : " +session.getAttribute("loggedInUserRole"));
			 }

					return new ResponseEntity<User>(user, HttpStatus.OK);
			
		}
		
		@GetMapping("/logout")
		public ResponseEntity<User> logout(HttpSession session)
		{
		log.debug("->->->->calling method logout");
		String loggedInUserID = (String) session.getAttribute("loggedInUserID");
		
		 user = userDAO.get(loggedInUserID);
		 user.setLastSeenTime(new Date(System.currentTimeMillis()));
		 user.setIsOnline('N');
		 userDAO.update(user);

		session.invalidate();

		user.setErrorCode("200");
		user.setErrorMessage("You have successfully logged in");
		return new ResponseEntity<User>(user, HttpStatus.OK);
		}

		@PostMapping("/createuser")
		public User createUser(@RequestBody User newUser)
		{
			log.debug("Calling createUser method ");
			//before creating user, check whether the id exist in the db or not
			
			user = userDAO.get(newUser.getId());
			if( user ==null)
			{
				log.debug("User does not exist...trying to create new user");
				//id does not exist in the db
				userDAO.save(newUser);
				//NLP - NullPointerException
				//Whenever you call any method/variable on null object - you will get NLP
				newUser.setErrorCode("200");
				newUser.setErrorMessage("Thank you for registration.");
				
			}
			else
			{
				log.debug("Please choose another id as it exists");
				//id already exist in db.
				newUser.setErrorCode("800");
				newUser.setErrorMessage("Please choose another id as it is exists");
				
			}
			log.debug("Endig of the  createUser method ");
			return newUser;
			
		}

		@DeleteMapping("deleteuser/{id}")
		public User deleteUser(@PathVariable("id") String id)
		{
			
			//whether record exist with this id or not
			
		    if(	userDAO.get(id)  ==null)
		    {
		    	user.setErrorCode("404");
		    	user.setErrorMessage("Could not delete.  User does not exist with this id " + id);
		    }
		    else
		    {
		    	  if (userDAO.delete(id) )
		    	  {
		    		  user.setErrorCode("200");
		  	    	  user.setErrorMessage("Successfully deleted");
		    	  }
		    	  else
		    	  {
		    	    	user.setErrorCode("404");
		    	    	user.setErrorMessage("Could not delete. Please contact administrator");
		    	  }
		    	
		     }
		     return user;
		}	
		
		@PostMapping("/setimage/{id}")
		public User user(@PathVariable("id") String id, @RequestParam("file") MultipartFile[] file, HttpServletRequest request)
		{
			User user=userDAO.get(id);
			System.out.println(file[0]);
			userDAO.storeFile(file[0], request);
			user.setImagepath(file[0].getOriginalFilename());
			if (userDAO.update(user))
	    	{
				user.setErrorCode("200");
	  	    	user.setErrorMessage("Successfully uploaded");
	    	}
	    	else
	    	{
	    		user.setErrorCode("404");
	    	    user.setErrorMessage("Could not upload");
	    	}
			return user;
		}
			
}