package com.contact.controller;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.Principal;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.contact.entities.Contact;
import com.contact.entities.User;
import com.contact.helper.Message;
import com.contact.repository.ContactRepository;
import com.contact.repository.UserRepository;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/user")
public class UserController {
	
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private ContactRepository contactRepository;
	
	
	//Method for adding common data to UI Page
	@ModelAttribute
	public void addCommanData(Model model, Principal principal) {
		String username = principal.getName();
		System.out.println(username);
		//get the user using username(ie Email)
		User user =  userRepository.getUserByUsername(username);
		System.out.println("User:"+user);
		model.addAttribute("user", user);
	}
	
	
	//Dashboard Home
	@GetMapping("/index")
	public String dashboard(Model model, Principal principal) {
		model.addAttribute("title", "User Dashboard");

		return "normal/user-dashboard";
	}
	
	
	//Open Add form Handler
	@GetMapping("/add-contact")
	public String openAddContactForm(Model model) {
		model.addAttribute("title", "Add Contact");
		model.addAttribute("contact", new Contact());
		return "normal/add-contact";
				
	}
	
	//Processing add-contact-form
	
	@PostMapping("/process-contact")
	public String processContact(@ModelAttribute Contact contact,
			@RequestParam("image1")MultipartFile file,
			BindingResult bindingResult, 
			Principal principal, HttpSession session2) {
		try {
			
			String name = principal.getName();
			User user = userRepository.getUserByUsername(name);
			
			contact.setUser(user);
			
			//Processing and uploading file
			if (file.isEmpty()) {
				//
				contact.setImage("default.png");
				System.out.println("Image is not uploaded by user");
			}
			else {
				//put the file into the folder and update the name to contact in database
				contact.setImage(file.getOriginalFilename());
				
				File filepath = new ClassPathResource("static/img").getFile();
				Path path = Paths.get(filepath.getAbsolutePath()+File.separatorChar+file.getOriginalFilename());
				Files.copy(file.getInputStream(),path,StandardCopyOption.REPLACE_EXISTING);
				System.out.println("Image is Uploaded");
			}
			user.getContacts().add(contact);
			userRepository.save(user);

			
			//Message Success will be shown on ui
			session2.setAttribute("message", new Message("Your Contact is added!!, Add more", "success"));
			
		} catch (Exception e) {
			// TODO: handle exception
			System.out.println("Error: "+ e.getMessage());
			e.printStackTrace();
			//Error Message will be shown on ui
			session2.setAttribute("message", new Message("Something went Wrong, !Try Again...", "danger"));
		}
		return "normal/add-contact";

	}
	
	//Show Contact Handler
	//per page =5[n](contacts)
	//Current Page = 0[page]
	@GetMapping("/show-contacts/{page}")
	public String showContact(@PathVariable("page")Integer page, Model model,Principal principal) {
		model.addAttribute("title", "Show User Contacts");
		
		/*
		//sending the list of contacts to UI by Principal principal(1st Method)
		String username = principal.getName();
		User user= userRepository.getUserByUsername(username);
		List<Contact> contacts = user.getContacts();
		*/
		
		//sending the list of contacts to UI by contactRepository(2nd Method)
		String username = principal.getName();
		User user= userRepository.getUserByUsername(username);
		
		Pageable pageable = PageRequest.of(page, 8);
		Page<Contact> contacts = contactRepository.findContactsByUser(user.getId(), pageable);
		
		model.addAttribute("contacts", contacts);
		model.addAttribute("currentPage", page);
		model.addAttribute("totalPages", contacts.getTotalPages());
		
		return "normal/show-contacts";
	}
	
	//showing particular contact details
	
	@GetMapping("/{cId}/contact")
	public String showParticular(@PathVariable("cId")Integer cId, Model model,Principal principal ) {
		Optional<Contact> contactOptional = contactRepository.findById(cId);
		Contact contact = contactOptional.get();
		
		String userName = principal.getName();
		User user = userRepository.getUserByUsername(userName);
		if(user.getId()==contact.getUser().getId()) {
			model.addAttribute("contact", contact);
			model.addAttribute("title",contact.getName());
		}
		return "normal/contact-detail";
	}
	
	
	//Delete Contact Handler
	
	@GetMapping("/delete/{cId}")
	public String deleteContact(@PathVariable("cId")Integer cId, Model model,Principal principal , HttpSession session) {
		
		Optional<Contact> contactOptional = contactRepository.findById(cId);
		Contact contact = contactOptional.get();
		
		contact.setUser(null);

		contactRepository.delete(contact);
		session.setAttribute("message", new Message("Contact Deleted Successfully...", "success"));
		return "redirect:/user/show-contacts/0";
	}
	
	//open Update form handler
	@PostMapping("/update-contact/{cid}")
	public String updateForm(@PathVariable("cid")Integer cid,
			Model model) {
		model.addAttribute("title", "Update Contact");
		Contact contact = contactRepository.findById(cid).get();
		model.addAttribute("contact", contact);
		return "normal/update-contact";
	}
	
	//update contact handler
	
	@PostMapping("/process-update")
	public String updateHandler(@ModelAttribute Contact contact ,
			Model model,
			@RequestParam("image1") MultipartFile file,
			HttpSession session, Principal principal) {
		try {
			
			//old contact details
			Contact oldContactDetails = contactRepository.findById(contact.getCId()).get();
			
			//Checking that file is uploaded or not
			if(!file.isEmpty()) {
				
				//Deleting Old Photo
				
				File deleteFile = new ClassPathResource("static/img").getFile();
				File file2 = new File(deleteFile, oldContactDetails.getImage());
				file2.delete();
				//* uploading new photo
				File filepath = new ClassPathResource("static/img").getFile();
				Path path = Paths.get(filepath.getAbsolutePath()+File.separatorChar+file.getOriginalFilename());
				Files.copy(file.getInputStream(),path,StandardCopyOption.REPLACE_EXISTING);
				contact.setImage(file.getOriginalFilename());
			}
			else {
				
				contact.setImage(oldContactDetails.getImage());
			}
			User user = userRepository.getUserByUsername(principal.getName());
			contact.setUser(user);
			contactRepository.save(contact);
			
			session.setAttribute("message", new Message( "Your Contact is updated", "success"));
		} catch (Exception e) {
			e.printStackTrace();
		}

		return "redirect:/user/"+contact.getCId()+"/contact";
	}
}
