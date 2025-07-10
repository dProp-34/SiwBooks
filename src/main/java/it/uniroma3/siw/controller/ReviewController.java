package it.uniroma3.siw.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import it.uniroma3.siw.model.Book;
import it.uniroma3.siw.model.Credentials;
import it.uniroma3.siw.model.Review;
import it.uniroma3.siw.model.User;
import it.uniroma3.siw.service.BookService;
import it.uniroma3.siw.service.ReviewService;
import jakarta.validation.Valid;

@Controller
public class ReviewController {

	@Autowired
	private ReviewService reviewService;
	@Autowired
	private BookService bookService;

	// Aggiungi una nuova recensione
	@GetMapping("/books/{id}/addReview")
	@PreAuthorize("hasRole('USER')")
	public String showReviewForm(@PathVariable Long id, Model model) {
		Book book = bookService.findById(id);
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		User user = ((Credentials) auth.getPrincipal()).getUser();

		if (reviewService.findByReviewerAndReviewedBook(user, book) != null) {
			return "redirect:/books/" + id + "?alreadyReviewed";
		}
		model.addAttribute("review", new Review());
		model.addAttribute("currBook", book);
		return "reviewForm";
	}

	@PostMapping("/books/{id}/addReview")
	@PreAuthorize("hasRole('USER')")
	public String submitReview(@PathVariable Long id, @Valid @ModelAttribute Review review,
			BindingResult result, Model model) {
		Book book = bookService.findById(id);
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		User user = ((Credentials) auth.getPrincipal()).getUser();

		if (result.hasErrors()) {
			model.addAttribute("currBook", book);
			return "reviewForm";
		}
		reviewService.addReviewToBook(review, book, user);
		return "redirect:/books/" + id;
	}

	// Cancella una recensione esistente
	@PreAuthorize("hasRole('ADMIN')")
	@PostMapping("/admin/reviews/{id}/delete")
	public String deleteReviewById(@PathVariable Long id) {
		reviewService.deleteReviewById(id);
		return "redirect:/books";
	}

	// Mostra dettagli recensione
	@GetMapping("/admin/reviews/{id}")
	public String showReview(@PathVariable Long id, Model model) {
		Review review = reviewService.findById(id);
		model.addAttribute("review", review);
		return "admin/reviewDetails";
	}

}
