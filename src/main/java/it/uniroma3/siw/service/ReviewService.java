package it.uniroma3.siw.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import it.uniroma3.siw.model.Book;
import it.uniroma3.siw.model.Review;
import it.uniroma3.siw.model.User;
import it.uniroma3.siw.repository.ReviewRepository;

@Service
public class ReviewService {

	@Autowired
	private ReviewRepository reviewRepository;

	public Review findById(Long id) {
		return reviewRepository.findById(id).get();
	}

	public Iterable<Review> findAll() {
		return reviewRepository.findAll();
	}

	public Review save(Review review) {
		return reviewRepository.save(review);
	}

	public void deleteReviewById(Long id) {
		reviewRepository.deleteById(id);
	}

	public Iterable<Review> findByVote(int year) {
		return reviewRepository.findByVote(year);
	}

	public Review findByReviewerAndReviewedBook(User reviewer, Book reviewedBook) {
		return reviewRepository.findByReviewerAndReviewedBook(reviewer, reviewedBook);
	}

	public void addReviewToBook(Review review, Book book, User reviewer) {
		// Verifica se l'utente ha già recensito questo libro
		if (findByReviewerAndReviewedBook(reviewer, book) != null)
			throw new IllegalStateException("L'utente ha già recensito questo libro.");

		book.addReview(review);
		review.setReviewedBook(book);
		review.setReviewer(reviewer);
		reviewRepository.save(review);
	}

}
