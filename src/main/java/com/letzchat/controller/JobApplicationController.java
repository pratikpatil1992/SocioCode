package com.letzchat.controller;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import com.letzchat.dao.JobApplicationDAO;
import com.letzchat.model.Job;
import com.letzchat.model.JobApplication;

@RestController
public class JobApplicationController 
{
	
	private static Logger log = LoggerFactory.getLogger(JobController.class);	
	@Autowired
	private JobApplicationDAO jobApplicationDAO;
	@Autowired
	private JobApplication jobApplication;
	
	@PostMapping("/apply/{id}/{userId}")
	public JobApplication createJobApplication(@PathVariable("id") String id,@PathVariable("userId") String userId)
	{
		log.debug("Calling createJobApplication method ");
		//before creating job application, check whether the id exist in the db or not
		
		jobApplication = jobApplicationDAO.get(id);
		if( jobApplication ==null)
		{
			log.debug("JobApplication does not exist...trying to create new JobApplication");
			//id does not exist in the db
			jobApplication=new JobApplication();
			jobApplication.setJob_id(id);
			jobApplication.setUser_id(userId);
			jobApplicationDAO.save(jobApplication);
			jobApplication.setErrorCode("200");
			jobApplication.setErrorMessage("Thank you for applying.");
			
		}
		else
		{
			log.debug("Please choose another id as it exists");
			//id already exists in db.
			jobApplication.setErrorCode("800");
			jobApplication.setErrorMessage("Please choose another id as it exists");
			
		}
		log.debug("Ending of the apply method ");
		return jobApplication;
		
	}
	
	@GetMapping("/appliedjobs/{userId}")
	public ResponseEntity<List<JobApplication>> getAppliedJobs(@PathVariable("userId") String userId)
	{
		log.debug("inside getAllJobs");
		List<JobApplication> appliedList =  jobApplicationDAO.appliedlist(userId);
		//ResponseEntity:  we can send the data + HTTP status codes + error message
		// like 200 - success
		// 404 - page not found
		return new ResponseEntity<List<JobApplication>>(appliedList, HttpStatus.OK);
	}
}
