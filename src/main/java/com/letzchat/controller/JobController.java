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

import com.letzchat.dao.JobDAO;
import com.letzchat.model.Job;

@RestController
public class JobController {
	private static Logger log = LoggerFactory.getLogger(JobController.class);	
	@Autowired
	private JobDAO jobDAO;
	@Autowired
	private Job job;
	
	@GetMapping("/jobs")
	public ResponseEntity<List<Job>> getAllJobs()
	{
		log.debug("inside getAllJobs");
		List<Job> jobList =  jobDAO.list();	
		//ResponseEntity:  we can send the data + HTTP status codes + error message
		// like 200 - success
		// 404 - page not found
		return   new ResponseEntity<List<Job>>(jobList, HttpStatus.OK);
	}

	@GetMapping("/job/{id}")
	public ResponseEntity<Job> getJobByID(@PathVariable("id") String id)
	{
		log.debug("Starting of the method getJobByID");
		log.info("Getting details of the id " + id);
		job = jobDAO.get(id);
		
		if(job==null)
		{
			job = new Job();
			job.setErrorCode("404");
			job.setErrorMessage("Job with id "+id+" does not exist");
		}
		else
		{
			job.setErrorCode("200");
			job.setErrorMessage("Success");
		}
		
		log.info("Id of the Job is " + job.getId());
		log.debug("Ending of the method getJobByID");
	    return	new ResponseEntity<Job>(job , HttpStatus.OK);
	}
	
	@PostMapping("/createjob/")
	public Job createJob(@RequestBody Job newJob)
	{
		log.debug("Calling createjob method ");
		//before creating user, check whether the id exist in the db or not
		
		job = jobDAO.get(newJob.getId());
		if( job ==null)
		{
			log.debug("User does not exist. Trying to create new user");
			//id does not exist
			
			job.setDate_Time(new Date());
			jobDAO.save(newJob);
			//NLP - NullPointerException
			newJob.setErrorCode("200");
			newJob.setErrorMessage("Thank you for writing a job.");
		}
		else
		{
			log.debug("Please choose another id as this one already exists.");
			//id already exists in DB.
			newJob.setErrorCode("800");
			newJob.setErrorMessage("Please choose another id as this one already exists.");
			
		}
		log.debug("Ending of the createJob method ");
		return newJob;
	}
	
	@PostMapping("/updateJob/")
	public Job updateJob(@RequestBody Job updateJob)
	{
		//check whether the id exists or not
		job=  jobDAO.get(updateJob.getId());
		if(job!=null)
		{
			jobDAO.update(updateJob);
			updateJob.setErrorCode("200");
			updateJob.setErrorMessage("Successfully updated the details of the job");
		}
		else
		{
			updateJob.setErrorCode("800");
			updateJob.setErrorMessage("Could not update. Job with id "+updateJob.getId()+" does not exist");
		}
		return updateJob;
		
	}
	
	@DeleteMapping("/deletejob/{id}")
	public ResponseEntity<Job>deleteJob(@PathVariable("id") String id)
	{
		
		//whether record exists with this id or not
		log.debug("DeleteJob Method Start");
		job=jobDAO.get(id);
		
	    if(	jobDAO.get(id)==null)
	    {
	    	job.setErrorCode("404");
	    	job.setErrorMessage("Could not delete.  Job does not exist with this id " + id);
	    }
	    else
	    {
	    	jobDAO.delete(id) ;
	    	job.setErrorCode("200");
	  	    job.setErrorMessage("Successfully deleted");
	    }
	    	  	     
	    log.debug("DeleteJob Method Ending");
	    return new ResponseEntity<Job>(job, HttpStatus.OK);
	}

	@GetMapping( "/acceptjob/{id}")
	public ResponseEntity<Job> accept(@PathVariable("id") String id) {
		log.debug("Starting of the method Jobaccept");

		job = updateStatus(id, 'A', "");
		log.debug("Ending of the method accept");
		return new ResponseEntity<Job>(job, HttpStatus.OK);

	}
	@GetMapping( "/rejectjob/{id}/{reason}")
	public ResponseEntity<Job> reject(@PathVariable("id") String id, @PathVariable("reason") String reason) {
		log.debug("Starting of the method reject");

		job = updateStatus(id, 'R', reason);
		log.debug("Ending of the method reject");
		return new ResponseEntity<Job>(job, HttpStatus.OK);

	}
	
	private Job updateStatus(String id, char status, String reason) {
		log.debug("Starting of the method updateStatus");

		log.debug("status: "+status);
		job = jobDAO.get(id);

		if (job == null)
		{
			job = new Job();
			job.setErrorCode("404");
			job.setErrorMessage("Could not update the status to " + status);
		} 
		else 
		{
			jobDAO.update(job);
			
			job.setErrorCode("200");
			job.setErrorMessage("Updated the status Successfully");
		}
		log.debug("Ending of the method updateStatus");
		return job;

	}
	
	@PostMapping("/searchjobs/")
	public ResponseEntity<List<Job>> searchjobs(@RequestBody String title)
	{
		
		List<Job> jobList =  jobDAO.searchlist(title);
		return new ResponseEntity<List<Job>>(jobList, HttpStatus.OK);
	}
	
	
}