package it.uniroma3.siw.repository;

import java.time.LocalDate;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import it.uniroma3.siw.model.Author;

public interface AuthorRepository extends CrudRepository<Author, Long> {

	public Iterable<Author> findByDateOfBirth(LocalDate year);

	public Author findBySurnameAndDateOfBirth(String title, LocalDate year);

	@Query("SELECT a FROM Author a WHERE a NOT IN " +
			"(SELECT a2 FROM Book b JOIN b.authors a2 WHERE b.id = :bookId)")
	public Iterable<Author> findAuthorsNotInBook(@Param("bookId") Long bookId);

}
