package it.uniroma3.siw.repositoy;

import org.springframework.data.repository.CrudRepository;
import it.uniroma3.siw.model.Book;

public interface BookRepository extends CrudRepository<Book, Long> {
	/*
	 * @Modifying * @Transactional
	 * 
	 * @Query(value = "UPDATE book SET director_id = :authorId WHERE id = :bookId ",
	 * nativeQuery = true)
	 * void addDirectorToBook(@Param("bookId") Long bookId, @Param("authorId") Long
	 * authorId);
	 * You can add custom queries here if needed later, but not required for
	 * add/remove
	 */
}
