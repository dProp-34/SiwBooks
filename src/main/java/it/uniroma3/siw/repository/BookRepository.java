package it.uniroma3.siw.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import it.uniroma3.siw.model.Book;

public interface BookRepository extends CrudRepository<Book, Long> {

	public Iterable<Book> findByReleaseYear(int year);

	public Book findByTitleAndReleaseYear(String title, int year);

	@Query("SELECT b FROM Book b WHERE b NOT IN " +
			"(SELECT b2 FROM Author a JOIN a.books b2 WHERE a.id = :authorId)")
	public Iterable<Book> findBooksNotInAuthor(@Param("authorId") Long authorId);

}
