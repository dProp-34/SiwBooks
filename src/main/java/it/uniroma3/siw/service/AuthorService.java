package it.uniroma3.siw.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import it.uniroma3.siw.model.Author;
import it.uniroma3.siw.model.Book;
import it.uniroma3.siw.repositoy.AuthorRepository;
import it.uniroma3.siw.repositoy.BookRepository;
import jakarta.persistence.EntityNotFoundException;

@Service
public class AuthorService {
	@Autowired
	private AuthorRepository authorRepository;
	@Autowired
	private BookRepository bookRepository;

	public Author getAuthorById(Long id) {
		return authorRepository.findById(id)
				.orElseThrow(() -> new EntityNotFoundException("Author not found"));
	}

	public Author saveAuthor(Author author) {
		return authorRepository.save(author);
	}

	public Iterable<Author> getAllAuthors() {
		return authorRepository.findAll();
	}

	public void deleteAuthorById(Long id) {
		authorRepository.deleteById(id);
	}

	public void addBookToAuthor(Long authorId, Long bookId) {
		Book book = bookRepository.findById(bookId)
				.orElseThrow(() -> new EntityNotFoundException("Book not found"));
		Author author = authorRepository.findById(authorId)
				.orElseThrow(() -> new EntityNotFoundException("Author not found"));
		if (!author.getBooks().contains(book)) {
			author.getBooks().add(book); // Per mantenere l'associazione bidirezionale
			book.getAuthors().add(author);
			authorRepository.save(author);
			bookRepository.save(book);
		}
	}

	public void deleteBookFromAuthor(Long authorId, Long bookId) {
		Book book = bookRepository.findById(bookId)
				.orElseThrow(() -> new EntityNotFoundException("Book not found"));
		Author author = authorRepository.findById(authorId)
				.orElseThrow(() -> new EntityNotFoundException("Author not found"));
		if (author.getBooks().remove(book)) {
			book.getAuthors().remove(author);
			authorRepository.save(author);
			bookRepository.save(book);
		}
	}

	public void deleteBookFromAuthor(Long authorId) {
		Author author = authorRepository.findById(authorId)
				.orElseThrow(() -> new EntityNotFoundException("Book not found"));
		for (Book b : author.getBooks()) {
			b.getAuthors().remove(author);
			bookRepository.save(b);
		}
		author.getBooks().clear();
		authorRepository.save(author);
	}

}
