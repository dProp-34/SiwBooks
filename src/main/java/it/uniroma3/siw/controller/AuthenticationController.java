package it.uniroma3.siw.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import it.uniroma3.siw.model.Credentials;
import it.uniroma3.siw.model.User;
import it.uniroma3.siw.service.CredentialsService;
import it.uniroma3.siw.service.UserService;
import jakarta.validation.Valid;

@Controller
public class AuthenticationController {

	@Autowired
	private CredentialsService credentialsService;
	@Autowired
	private UserService userService;

	@GetMapping("/")
	public String index() {
		return "index";
	}

	@GetMapping("/login")
	public String showLoginForm(Model model) {
		return "/formLogin.html";
	}

	@GetMapping("/register")
	public String showRegisterForm(Model model) {
		model.addAttribute("user", new User());
		model.addAttribute("credentials", new Credentials());
		return "/formRegistrati.html";
	}

	@PostMapping("/register")
	public String registerUser(@Valid @ModelAttribute User user, BindingResult userBindingResult,
			@Valid @ModelAttribute Credentials credentials, BindingResult credentialsBindingResult, Model model) {
		if (!userBindingResult.hasErrors() && !credentialsBindingResult.hasErrors()) {
			userService.saveUser(user);
			credentials.setUser(user);
			credentialsService.saveCredentials(credentials);

			// Se user e credentials hanno entrambi contenuti validi, allora memorizzali
			model.addAttribute("registrationSuccess", true);
			return "/formLogin.html";
		}
		// Ritorna al form con gli errori e i modelli già popolati
		model.addAttribute("user", user);
		model.addAttribute("credentials", credentials);
		return "/formRegistrati.html";
	}

}
