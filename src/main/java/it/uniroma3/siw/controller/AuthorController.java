package it.uniroma3.siw.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import it.uniroma3.siw.model.Author;
import it.uniroma3.siw.model.Book;
import it.uniroma3.siw.service.AuthorService;
import it.uniroma3.siw.service.BookService;
import jakarta.validation.Valid;

@Controller
public class AuthorController {

	@Autowired
	AuthorService authorService;
	@Autowired
	BookService bookService;

	/* Mostra la lista di tutti gli autori */
	@GetMapping("/author")
	public String getAllAuthors(Model model) {
		model.addAttribute("allAuthors", this.authorService.getAllAuthors());
		return "allAuthors.html";
	}

	/* Mostra il dettaglio di un singolo autore */
	@GetMapping("/author/{id}")
	public String getAuthorById(@PathVariable Long id, Model model) {
		model.addAttribute("currAuthor", this.authorService.getAuthorById(id));
		return "author.html";
	}

	/* Form per inserire un nuovo autore */
	@GetMapping("/newAuthorForm")
	public String newAuthorForm(Model model) {
		model.addAttribute("currAuthor", new Author());
		return "newAuthorForm.html";
	}

	/* Aggiunge un autore al sistema */
	@PostMapping("/author")
	public String createNewAuthor(@Valid @ModelAttribute("currAuthor") Author author,
			BindingResult bindingResult,
			Model model) {
		if (bindingResult.hasErrors()) {
			return "newAuthorForm.html";
		}
		Author savedAuthor = this.authorService.saveAuthor(author);
		return "redirect:/author/" + savedAuthor.getAuthorId();
	}

	/* Pagina per la gestione di tutti gli autori */
	@GetMapping("/editAuthor")
	public String editAllAuthors(Model model) {
		model.addAttribute("allAuthors", this.authorService.getAllAuthors());
		return "editAllAuthors.html";
	}

	/* Pagina di modifica di un singolo autore */
	@GetMapping("/editAuthor/{id}")
	public String editAuthorById(@PathVariable Long id, Model model) {
		model.addAttribute("currAuthor", this.authorService.getAuthorById(id));
		return "editAuthor.html";
	}

	/* Cancella un autore */
	@PostMapping("/editAuthor/{id}/delete")
	public String deleteAuthorById(@PathVariable Long id, Model model) {
		this.authorService.deleteAuthorById(id);
		return "redirect:/editAuthor";
	}

	/* Aggiunge un libro già esistente all'autore */
	@PostMapping("/editAuthor/{authorId}/addBook/{bookId}")
	public String addBookToAuthor(@PathVariable Long authorId,
			@PathVariable Long bookId) {
		this.authorService.addBookToAuthor(authorId, bookId);
		return "redirect:/editAuthor/" + authorId;
	}

	/* Rimuove un libro dall'autore */
	@PostMapping("/editAuthor/{authorId}/removeBook/{bookId}")
	public String removeBookFromAuthor(@PathVariable Long authorId,
			@PathVariable Long bookId) {
		this.authorService.deleteBookFromAuthor(authorId, bookId);
		return "redirect:/editAuthor/" + authorId;
	}

	/* Pagina di selezione libro per aggiungerlo a un autore */
	@GetMapping("/editAuthor/{authorId}/selectBook")
	public String showBookSelection(@PathVariable Long authorId, Model model) {
		Author author = this.authorService.getAuthorById(authorId);
		List<Book> allBooks = (List<Book>) this.bookService.getAllBooks();

		// Filtra solo i libri non già associati all'autore
		List<Book> availableBooks = allBooks.stream()
				.filter(book -> !author.getBooks().contains(book))
				.toList();

		model.addAttribute("authorId", authorId);
		model.addAttribute("availableBooks", availableBooks);
		return "selectBook.html";
	}
}
