package it.uniroma3.siw.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import it.uniroma3.siw.controller.validator.ReviewValidator;
import it.uniroma3.siw.model.Book;
import it.uniroma3.siw.model.Review;
import it.uniroma3.siw.model.User;
import it.uniroma3.siw.service.BookService;
import it.uniroma3.siw.service.CredentialsService;
import it.uniroma3.siw.service.ReviewService;
import jakarta.validation.Valid;

@Controller
public class ReviewController {

	@Autowired
	private ReviewService reviewService;
	@Autowired
	private ReviewValidator reviewValidator;
	@Autowired
	private CredentialsService credentialsService;
	@Autowired
	private BookService bookService;

	// Aggiungi una nuova recensione
	@PreAuthorize("hasRole('USER')")
	@GetMapping("/books/{id}/newReviewForm")
	public String newReviewForm(@PathVariable Long id, Model model) {
		Book book = bookService.findById(id);
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		User currentUser = credentialsService.getCredentials(auth.getName()).getUser();

		if (reviewService.findByReviewerAndReviewedBook(currentUser, book) != null) {
			return "redirect:/books/" + id + "?alreadyReviewed";
		}
		model.addAttribute("currReview", new Review());
		model.addAttribute("currBook", book);
		return "newReviewForm";
	}

	@Transactional
	@PreAuthorize("hasRole('USER')")
	@PostMapping("/books/{id}/reviews")
	public String saveReview(@Valid @ModelAttribute("currReview") Review review, @PathVariable Long id,
			BindingResult bindingResult, Model model) {
		reviewValidator.validate(review, bindingResult);
		Book book = bookService.findById(id);
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		User user = credentialsService.getCredentials(auth.getName()).getUser();

		if (bindingResult.hasErrors()) {
			model.addAttribute("currBook", book);
			return "newReviewForm";
		}
		reviewService.addReviewToBook(review, book, user);
		model.addAttribute("currBook", book);
		model.addAttribute("currUser", user);
		return "redirect:/books/" + id;
	}
	/*
	 * Mostra dettagli recensione
	 * 
	 * @GetMapping("/reviews/{id}")
	 * public String showReview(@PathVariable Long id, Model model) {
	 * Review review = reviewService.findById(id);
	 * model.addAttribute("currReview", review);
	 * return "currReview";
	 * }
	 */

	// Cancella una recensione esistente
	@PreAuthorize("hasRole('ADMIN')")
	@PostMapping("/admin/books/{bId}/reviews/{rId}/delete")
	public String deleteReviewById(@PathVariable Long bId, @PathVariable Long rId) {
		reviewService.deleteReviewById(rId);
		return "redirect:/books";
	}

	// Modifica una recensione
	@PreAuthorize("hasRole('USER')")
	@GetMapping("/books/{bId}/reviews/{rId}/edit")
	public String editReview(@PathVariable Long bId, @PathVariable Long rId, Model model) {
		Review review = reviewService.findById(rId);
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		User currentUser = credentialsService.getCredentials(auth.getName()).getUser();

		if (!review.getReviewer().equals(currentUser)) {
			return "redirect:/books/" + bId + "?alreadyReviewed";
		}
		model.addAttribute("currReview", review);
		return "editCurrReview";
	}

	@Transactional
	@PreAuthorize("hasRole('USER')")
	@PostMapping("/books/{bId}/reviews/{rId}/edit")
	public String updateReview(@PathVariable Long bId, @PathVariable Long rId,
			@ModelAttribute("currReview") Review updatedReview, BindingResult result, Model model) {
		updatedReview.setId(rId);
		reviewValidator.validate(updatedReview, result);
		if (result.hasErrors())
			return "editCurrReview";

		Review existingReview = reviewService.findById(rId);
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		User currentUser = credentialsService.getCredentials(auth.getName()).getUser();

		if (!existingReview.getReviewer().equals(currentUser)) {
			return "redirect:/books/" + bId + "?alreadyReviewed";
		}
		existingReview.setTitle(updatedReview.getTitle());
		existingReview.setText(updatedReview.getText());
		existingReview.setVote(updatedReview.getVote());

		reviewService.save(existingReview);
		model.addAttribute("currBook", bookService.findById(bId));
		model.addAttribute("currUser", currentUser);
		return "redirect:/books/" + bId;
	}

}
