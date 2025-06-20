package it.uniroma3.siw.controller.rest;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import it.uniroma3.siw.model.Author;
import it.uniroma3.siw.model.Book;
import it.uniroma3.siw.service.AuthorService;
import it.uniroma3.siw.service.BookService;
import jakarta.validation.Valid;

@RestController
public class BookRestController {

	@Autowired
	private BookService bookService;
	@Autowired
	private AuthorService authorService;

	@GetMapping("/rest/books")
	public List<Book> findAllBooks() {
		List<Book> books = new ArrayList<>();
		for (Book m : this.bookService.findAll())
			books.add(m);
		return books;
	}

	@GetMapping("/rest/books/{id}")
	public Book findBookById(@PathVariable Long id) {
		return this.bookService.findById(id);
	}

	@PostMapping("/admin/rest/books")
	public Book saveBook(@Valid @RequestBody Book book) {
		this.bookService.save(book);
		return book;
	}

	@PutMapping("/admin/rest/books/{bookId}/authors/{authorId}")
	public Book addAuthorToBook(@PathVariable Long bookId, @PathVariable Long authorId) {
		Author director = this.authorService.findById(authorId);
		Book book = this.bookService.findById(bookId);
		book.getAuthors().add(director);
		this.bookService.save(book);
		return book;
	}

	@DeleteMapping("/admin/rest/books/{bookId}/authors/{authorId}")
	public Book removeAuthorFromBook(@PathVariable Long bookId, @PathVariable Long authorId) {
		Book book = this.bookService.findById(bookId);
		Author actor = this.authorService.findById(authorId);
		Set<Author> bookAuthors = book.getAuthors();
		bookAuthors.remove(actor);
		this.bookService.save(book);
		return book;
	}

}
