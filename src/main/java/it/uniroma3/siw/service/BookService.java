package it.uniroma3.siw.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import it.uniroma3.siw.model.Author;
import it.uniroma3.siw.model.Book;
import it.uniroma3.siw.repositoy.AuthorRepository;
import it.uniroma3.siw.repositoy.BookRepository;
import jakarta.persistence.EntityNotFoundException;

@Service
public class BookService {
	@Autowired // Per iniettare automaticamente le variabili da cui dipende questa classe
	private BookRepository bookRepository;
	@Autowired
	private AuthorRepository authorRepository;

	public Book getBookById(Long id) {
		return bookRepository.findById(id)
				.orElseThrow(() -> new EntityNotFoundException("Book not found"));
	}

	public Book saveBook(Book book) {
		return bookRepository.save(book);
	}

	public Iterable<Book> getAllBooks() {
		return bookRepository.findAll();
	}

	public void deleteBookById(Long id) {
		bookRepository.deleteById(id);
	}

	public void addAuthorToBook(Long bookId, Long authorId) {
		Book book = bookRepository.findById(bookId)
				.orElseThrow(() -> new EntityNotFoundException("Book not found"));
		Author author = authorRepository.findById(authorId)
				.orElseThrow(() -> new EntityNotFoundException("Author not found"));
		if (!book.getAuthors().contains(author)) {
			book.getAuthors().add(author);
			author.getBooks().add(book); // Per mantenere l'associazione bidirezionale
			bookRepository.save(book);
			authorRepository.save(author);
		}
	}

	public void deleteAuthorFromBook(Long bookId, Long authorId) {
		Book book = bookRepository.findById(bookId)
				.orElseThrow(() -> new EntityNotFoundException("Book not found"));
		Author author = authorRepository.findById(authorId)
				.orElseThrow(() -> new EntityNotFoundException("Author not found"));
		if (book.getAuthors().remove(author)) {
			author.getBooks().remove(book);
			bookRepository.save(book);
			authorRepository.save(author);
		}
	}

	public void deleteAuthorFromBook(Long bookId) {
		Book book = bookRepository.findById(bookId)
				.orElseThrow(() -> new EntityNotFoundException("Book not found"));
		for (Author a : book.getAuthors()) {
			a.getBooks().remove(book);
			authorRepository.save(a);
		}
		book.getAuthors().clear();
		bookRepository.save(book);
	}

}
