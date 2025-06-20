package it.uniroma3.siw.service;

import java.io.IOException;
import java.time.LocalDate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import it.uniroma3.siw.model.Author;
import it.uniroma3.siw.model.Book;
import it.uniroma3.siw.model.Image;
import it.uniroma3.siw.repository.AuthorRepository;
import it.uniroma3.siw.repository.BookRepository;

@Service
public class AuthorService {

	@Autowired
	private AuthorRepository authorRepository;
	@Autowired
	private BookRepository bookRepository;

	public Author findById(Long id) {
		return authorRepository.findById(id).get();
	}

	public Iterable<Author> findAll() {
		return authorRepository.findAll();
	}

	public Author save(Author author) {
		return authorRepository.save(author);
	}

	public void deleteAuthorById(Long id) {
		authorRepository.deleteById(id);
	}

	public Iterable<Author> findAuthorsNotInBook(@Param("bookId") Long bookId) {
		return authorRepository.findAuthorsNotInBook(bookId);
	}

	public Iterable<Author> findByDateOfBirth(LocalDate year) {
		return authorRepository.findByDateOfBirth(year);
	}

	public boolean existsBySurnameAndDateOfBirth(String title, LocalDate year) {
		return authorRepository.existsBySurnameAndDateOfBirth(title, year);
	}

	public void addBookToAuthor(Long bookId, Long authorId) {
		Book book = bookRepository.findById(bookId).orElseThrow();
		Author author = authorRepository.findById(authorId).orElseThrow();

		// Aggiorna entrambi i lati
		book.getAuthors().add(author);
		author.getBooks().add(book);

		// Salva entrambi
		bookRepository.save(book);
		authorRepository.save(author);
	}

	public void deleteBookFromAuthor(Long bookId, Long authorId) {
		Book book = bookRepository.findById(bookId).orElseThrow();
		Author author = authorRepository.findById(authorId).orElseThrow();

		book.getAuthors().remove(author);
		author.getBooks().remove(book);

		bookRepository.save(book);
		authorRepository.save(author);
	}

	public void saveAuthorPicture(Long authorId, MultipartFile imageFile) throws IOException {
		Author author = authorRepository.findById(authorId)
				.orElseThrow(() -> new IllegalArgumentException("Autore non trovato"));
		Image img = new Image();
		img.setName(imageFile.getOriginalFilename());
		img.setContentType(imageFile.getContentType());
		img.setBytes(imageFile.getBytes());

		author.setPicture(img);
		authorRepository.save(author);
	}

}
