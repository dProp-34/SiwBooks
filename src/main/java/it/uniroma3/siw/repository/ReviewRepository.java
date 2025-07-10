package it.uniroma3.siw.repository;

import org.springframework.data.repository.CrudRepository;
import it.uniroma3.siw.model.Book;
import it.uniroma3.siw.model.Review;
import it.uniroma3.siw.model.User;

public interface ReviewRepository extends CrudRepository<Review, Long> {

	public Iterable<Review> findByVote(int year);

	public Review findByReviewerAndReviewedBook(User reviewer, Book reviewedBook);

	/*
	 * @Query("SELECT b FROM Book b WHERE b NOT IN " +
	 * "(SELECT b2 FROM Author a JOIN a.books b2 WHERE a.id = :authorId)")
	 * public Iterable<Book> findBooksNotInAuthor(@Param("authorId") Long authorId);
	 */

}
