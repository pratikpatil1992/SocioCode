package com.letzchat.controller;

import java.util.Date;
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
import com.letzchat.dao.BlogDAO;
import com.letzchat.model.Blog;
import com.letzchat.model.User;
@RestController
public class BlogController {
	private static Logger log = LoggerFactory.getLogger(BlogController.class);	
	@Autowired
	private BlogDAO blogDAO;
	@Autowired
	private Blog blog;		
	@GetMapping("/blogs")
	public ResponseEntity<List<Blog>> getAllBlogs()
	{
		List<Blog> blogList =  blogDAO.list();	
		//ResponseEntity:  we can send the data + HTTP status codes + error message
		// like 200 - success
		// 404 - page not found
		return   new ResponseEntity<List<Blog>>(blogList, HttpStatus.OK);
	}

	@GetMapping("/blog/{id}")
	public ResponseEntity<Blog> getBlogByID(@PathVariable("id") String id)
	{
		log.debug("Starting of the method getBlogByID");
		log.info("Getting details of the id " + id);
		blog = blogDAO.get(id);
		
		if(blog==null)
		{
			blog = new Blog();
			blog.setErrorCode("404");
			blog.setErrorMessage("Blog with id "+id+" does not exist");
		}
		else
		{
			blog.setErrorCode("200");
			blog.setErrorMessage("Success");
		}
		
		log.info("Id of the Blog is " + blog.getId());
		log.debug("Ending of the method getBlogByID");
	  return	new ResponseEntity<Blog>(blog , HttpStatus.OK);
	}
	
	@PostMapping("/createblog/")
	public Blog createBlog(@RequestBody Blog newBlog)
	{
		log.debug("Calling createblog method ");
		System.out.println("inside createblog");
		blog = blogDAO.get(newBlog.getId());
		if( blog ==null)
		{
			log.debug("Blog does not exist. Trying to create new blog");
			//id does not exist
			
			blogDAO.save(newBlog);
			//NLP - NullPointerException
			newBlog.setErrorCode("200");
			newBlog.setErrorMessage("Thank you for writing a blog.");
		}
		else
		{
			log.debug("Please choose another id as this one already exists.");
			//id already exists in DB.
			newBlog.setErrorCode("800");
			newBlog.setErrorMessage("Please choose another id as this one already exists.");
			
		}
		log.debug("Ending of the createBlog method ");
		return newBlog;
	}
	
	@PostMapping("/updateBlog/")
	public Blog updateBlog(@RequestBody Blog updateBlog)
	{
		
		//check whether the id exists or not
		
		blog=  blogDAO.get(updateBlog.getId());
		
		
		if(blog!=null)
		{
			blogDAO.update(updateBlog);
			updateBlog.setErrorCode("200");
			updateBlog.setErrorMessage("Successfully updated the details of the blog");
		}
		else
		{
			updateBlog.setErrorCode("800");
			updateBlog.setErrorMessage("Could not update. Blog with id "+updateBlog.getId()+" does not exist");
		}
		
		return updateBlog;
		
	}
	
	@DeleteMapping("/deleteblog/{id}")
	public ResponseEntity<Blog>deleteBlog(@PathVariable("id") String id)
	{
		
		//whether record exists with this id or not
		log.debug("DeleteBlog Method Start");
		blog=blogDAO.get(id);
		
	    if(	blogDAO.get(id)==null)
	    {
	    	blog.setErrorCode("404");
	    	blog.setErrorMessage("Could not delete.  Blog does not exist with this id " + id);
	    }
	    else
	    {
	    	blogDAO.delete(id) ;
	    	blog.setErrorCode("200");
	  	    blog.setErrorMessage("Successfully deleted");
	    }
	    	  	     
	    log.debug("DeleteBlog Method Ending");
	    return new ResponseEntity<Blog>(blog, HttpStatus.OK);
	}

	@GetMapping( "/acceptblog/{id}")
	public ResponseEntity<Blog> accept(@PathVariable("id") String id) {
		log.debug("Starting of the method Blogaccept");

		blog = updateStatus(id, 'A', "");
		log.debug("Ending of the method accept");
		return new ResponseEntity<Blog>(blog, HttpStatus.OK);

	}
	@GetMapping( "/rejectblog/{id}/{reason}")
	public ResponseEntity<Blog> reject(@PathVariable("id") String id, @PathVariable("reason") String reason) {
		log.debug("Starting of the method reject");

		blog = updateStatus(id, 'R', reason);
		log.debug("Ending of the method reject");
		return new ResponseEntity<Blog>(blog, HttpStatus.OK);

	}
	
	private Blog updateStatus(String id, char status, String reason) {
		log.debug("Starting of the method updateStatus");

		log.debug("status: "+status);
		blog = blogDAO.get(id);

		if (blog == null)
		{
			blog = new Blog();
			blog.setErrorCode("404");
			blog.setErrorMessage("Could not update the status to " + status);
		} 
		else 
		{

			blogDAO.update(blog);
			
			blog.setErrorCode("200");
			blog.setErrorMessage("Updated the status Successfully");
		}
		log.debug("Ending of the method updateStatus");
		return blog;

	}
	
}