package it.uniroma3.siw.controller.validator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import it.uniroma3.siw.model.Review;
import it.uniroma3.siw.service.ReviewService;

@Component
public class ReviewValidator implements Validator {

	@Autowired
	private ReviewService reviewService;

	@Override
	public void validate(Object o, Errors errors) {
		Review review = (Review) o;
		Review existing = reviewService.findByReviewerAndReviewedBook(review.getReviewer(), review.getReviewedBook());
		// Se ne esiste uno, ed è diverso da quello che stiamo validando (per update)
		if (existing != null && (review.getId() == null || !existing.getId().equals(review.getId())))
			errors.reject("review.duplicate");
	}

	@Override
	public boolean supports(Class<?> aClass) {
		return Review.class.equals(aClass);
	}

}
