package it.uniroma3.siw.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Service;
import it.uniroma3.siw.model.Author;
import it.uniroma3.siw.model.Book;
import it.uniroma3.siw.repository.AuthorRepository;
import it.uniroma3.siw.repository.BookRepository;

@Service
public class BookService {

	@Autowired
	private BookRepository bookRepository;
	@Autowired
	private AuthorRepository authorRepository;

	public Book findById(Long id) {
		return bookRepository.findById(id).get();
	}

	public Iterable<Book> findAll() {
		return bookRepository.findAll();
	}

	public Book save(Book book) {
		return bookRepository.save(book);
	}

	public void deleteBookById(Long id) {
		bookRepository.deleteById(id);
	}

	public Iterable<Book> findBooksNotInAuthor(@Param("authorId") Long authorId) {
		return bookRepository.findBooksNotInAuthor(authorId);
	}

	public Iterable<Book> findByReleaseYear(int year) {
		return bookRepository.findByReleaseYear(year);
	}

	public boolean existsByTitleAndReleaseYear(String title, int year) {
		return bookRepository.existsByTitleAndReleaseYear(title, year);
	}

	public void addAuthorToBook(Long authorId, Long bookId) {
		Author author = authorRepository.findById(authorId).orElseThrow();
		Book book = bookRepository.findById(bookId).orElseThrow();

		author.getBooks().add(book);
		book.getAuthors().add(author);

		authorRepository.save(author);
		bookRepository.save(book);
	}

	public void deleteAuthorFromBook(Long authorId, Long bookId) {
		Author author = authorRepository.findById(authorId).orElseThrow();
		Book book = bookRepository.findById(bookId).orElseThrow();

		author.getBooks().remove(book);
		book.getAuthors().remove(author);

		authorRepository.save(author);
		bookRepository.save(book);
	}

}
