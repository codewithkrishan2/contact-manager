package com.contact.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.contact.entities.User;
import com.contact.helper.Message;
import com.contact.repository.UserRepository;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

@Controller
public class HomeController {

	@Autowired
	private UserRepository userRepository;
	
	@RequestMapping("/")
	public String home(Model model) {
		model.addAttribute("title", "Home - Smart Contact Manager");
		return "home";
	}
	
	@RequestMapping("/about")
	public String about(Model model) {
		model.addAttribute("title", "About - Smart Contact Manager");
		return "about";
	}
	
	@RequestMapping("/signup")
	public String signup(Model model) {
		model.addAttribute("title", "SignUp - Smart Contact Manager");
		model.addAttribute("user", new User());
		return "signup";
	}
	
	//This handler is for registering the user
	@PostMapping("/doRegister")
	public String registerUser(@Valid
			@ModelAttribute("user")User user, BindingResult result,
			@RequestParam(value = "agreement", defaultValue = "false")boolean agreement,
			Model model, 
			 HttpSession session) {
		try {
			if (!agreement) {
				System.out.println("You have not agreed T&C");
				throw new Exception("You have not agreed T&C");
			}
			
			if (result.hasErrors()) {
				System.out.println("ERROR: "+result.toString());
				model.addAttribute("user", user);
				return "signup";
			}
			
			
			user.setRole("ROLE_USER");
			user.setEnable(true);
			user.setImageUrl("default.jpg");
			
			User resultSave = this.userRepository.save(user);

			model.addAttribute("user", new User());	
			session.setAttribute("message", new Message("Successfull Registered","alert-success"));
			return "redirect:/signup";
			
		} catch (Exception e) {
			e.printStackTrace();
			model.addAttribute("user", user);
			session.setAttribute("message", new Message("Something went Wrong!"+e.getMessage(),"alert-danger"));
			return "signup";
		} 
	}
}
