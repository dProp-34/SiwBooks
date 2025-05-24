package it.uniroma3.siw.controller;

import java.beans.PropertyEditorSupport;
import java.time.Year;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import it.uniroma3.siw.model.Author;
import it.uniroma3.siw.model.Book;
import it.uniroma3.siw.service.AuthorService;
import it.uniroma3.siw.service.BookService;
import jakarta.validation.Valid;

// Il Controller riceve la richiesta HTTP, ed in base a questa interagisce con
// il Service e decide che risposta dare al server. Ogni entità ha un
// Controller, che offre tutte le operazioni che vengono esposte per quella
// determinata entità. Ogni metodo della classe risponde ad una richiesta HTTP
// come GET o POST, mappata con l'uso di annotazioni come @GetMapping
@Controller
public class BookController {
	@Autowired
	BookService bookService;
	@Autowired
	AuthorService authorService;

	@InitBinder
	public void initBinder(WebDataBinder binder) {
		binder.registerCustomEditor(Year.class, new PropertyEditorSupport() {
			@Override
			public void setAsText(String text) {
				setValue(Year.parse(text));
			}
		});
	}

	@GetMapping("/")
	public String index(Model model) {
		return "index.html";
	}

	// Dopo che l'utente ha compilato la form, il nuovo libro viene aggiunto al
	// sistema e viene mostrato all'utente
	@PostMapping("/book")
	public String createNewBook(@Valid @ModelAttribute Book book, BindingResult bindingResult, Model model) {
		if (bindingResult.hasErrors()) {
			return "newBookForm.html";
		} else {
			Book nuovoBook = this.bookService.saveBook(book);
			model.addAttribute("currBook", nuovoBook);
			return "redirect:/book.html";
		}
	}

	// Mostra la lista di tutti i libri
	@GetMapping("/book")
	public String getAllBooks(Model model) {
		model.addAttribute("allBooks", this.bookService.getAllBooks());
		return "allBooks.html";
	}

	// Se l'utente clicca il link ad un libro, viene mostrata la pagina di quel
	// libro
	@GetMapping("/book/{id}")
	public String getBookById(@PathVariable Long id, Model model) {
		/*
		 * When rendering the view (book.html) I want to make this object
		 * (getBookById(id)) available inside the page, under the name "currBook".
		 * This way, inside book.html all of the book’s properties can be accessed:
		 * <h1 th:text="${currBook.title}">Default Title</h1>
		 */
		model.addAttribute("currBook", this.bookService.getBookById(id));
		return "book.html";
	}

	// Quando l'utente clicca su 'Inserisci nuovo libro' viene indirizzato qui
	@GetMapping("/newBookForm")
	public String newBookForm(Model model) {
		model.addAttribute("currBook", new Book());
		/*
		 * List<Year> years = new ArrayList<>();
		 * for (int y = 1900; y <= Year.now().getValue(); y++) {
		 * years.add(Year.of(y));
		 * }
		 * model.addAttribute("years", years);
		 */
		return "newBookForm.html";
	}

	// Quando l'utente clicca su 'Gestisci' viene mostrata una lista di tutti i
	// libri simile a quella di prima, ma accanto ad ogni libro sono anche presenti
	// le opzioni 'modifica' e 'cancella'
	@GetMapping("/editBook")
	public String editAllBooks(Model model) {
		model.addAttribute("allBooks", this.bookService.getAllBooks());
		return "editAllBooks.html";
	}

	// Dopo aver cliccato su 'Gestisci', e successivamente sul link ad un libro,
	// viene mostrata una pagina corrispondente a quel libro simile a quella di
	// prima, con alcune aggiunte:
	// 1. accanto ad ogni autore è presente l'opzione 'cancella' 2. alla fine della
	// lista degli autori è presente un'opzione 'aggiungi', che permette di
	// aggiungere alla lista di autori di quel libro un altro autore già registrato
	// nel sistema
	@GetMapping("/editBook/{id}")
	public String editBookById(@PathVariable Long id, Model model) {
		model.addAttribute("currBook", this.bookService.getBookById(id));
		return "editBook.html";
	}

	@PostMapping("/editBook/{id}/delete")
	public String deleteBookById(@PathVariable Long id, Model model) {
		this.bookService.deleteBookById(id);
		model.addAttribute("allBooks", this.bookService.getAllBooks());
		return "redirect:/editAllBooks.html";
	}

	@PostMapping("/editBook/{bookId}/addAuthor/{authorId}")
	public String addAuthorToBook(@PathVariable Long bookId, @PathVariable Long authorId, Model model) {
		this.bookService.addAuthorToBook(bookId, authorId);
		return "redirect:/editBook.html";
	}

	@PostMapping("/editBook/{bookId}/deleteAuthor/{authorId}")
	public String deleteAuthorFromBook(@PathVariable Long bookId, @PathVariable Long authorId, Model model) {
		bookService.deleteAuthorFromBook(bookId, authorId);
		Book nuovoBook = this.bookService.getBookById(bookId);
		model.addAttribute("currBook", nuovoBook);
		return "redirect:/editBook.html";
	}

	@GetMapping("/editBook/{bookId}/selectAuthor")
	public String showAuthorSelection(@PathVariable Long bookId, Model model) {
		Book book = bookService.getBookById(bookId);
		List<Author> allAuthors = (List<Author>) authorService.getAllAuthors();

		// Filtra solo gli autori non già associati al libro
		List<Author> availableAuthors = allAuthors.stream()
				.filter(author -> !book.getAuthors().contains(author))
				.toList();

		model.addAttribute("bookId", bookId);
		model.addAttribute("availableAuthors", availableAuthors);
		return "selectAuthor.html";
	}

}
