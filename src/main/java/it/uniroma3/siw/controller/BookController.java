package it.uniroma3.siw.controller;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import it.uniroma3.siw.controller.validator.BookValidator;
import it.uniroma3.siw.model.Book;
import it.uniroma3.siw.model.Credentials;
import it.uniroma3.siw.model.Image;
import it.uniroma3.siw.model.User;
import it.uniroma3.siw.service.AuthorService;
import it.uniroma3.siw.service.BookService;
import it.uniroma3.siw.service.CredentialsService;
import it.uniroma3.siw.service.ImageService;
import jakarta.validation.Valid;

// Il Controller riceve la richiesta HTTP, ed in base a questa interagisce con
// il Service e decide che risposta dare al server. Ogni entità ha un
// Controller, che offre tutte le operazioni che vengono esposte per quella
// determinata entità. Ogni metodo della classe risponde ad una richiesta HTTP
// come GET o POST, mappata con l'uso di annotazioni come @GetMapping
@Controller
public class BookController {

	@Autowired
	private BookService bookService;
	@Autowired
	private BookValidator bookValidator;
	@Autowired
	private AuthorService authorService;
	@Autowired
	private ImageService imageService;
	@Autowired
	private CredentialsService credentialsService;

	/*
	 * Se l'utente clicca su 'Mostra Libri', viene mostrata la lista di tutti i
	 * libri
	 */
	@GetMapping("/books")
	public String findAll(Model model) {
		model.addAttribute("allBooks", this.bookService.findAll());
		return "allBooks";
	}

	/* Se l'utente clicca su un libro, viene mostrata la pagina di quel libro */
	@Transactional
	@GetMapping("/books/{id}")
	public String findById(@PathVariable Long id, Model model) {
		/*
		 * When rendering the view (book.html) I want to make this object
		 * (findById(id)) available inside the page, under the name "currBook".
		 * This way, inside book.html all of the book’s properties can be accessed:
		 * <h1 th:text="${currBook.title}">Default Title</h1>
		 */
		Book book = this.bookService.findById(id);
		book.getImages().size(); // forza l'inizializzazione
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		Credentials currCredentials = credentialsService.getCredentials(auth.getName());
		User currUser = currCredentials != null ? currCredentials.getUser() : null;

		model.addAttribute("currUser", currUser);
		model.addAttribute("currBook", book);
		return "currBook";
	}

	/*
	 * Se l'amministratore clicca su 'Crea nuovo libro'
	 * viene mostrato questo form
	 */
	@GetMapping("/admin/newBookForm")
	public String newBookForm(Model model) {
		model.addAttribute("currBook", new Book());
		return "admin/newBookForm";
	}

	/*
	 * Dopo che l'utente ha compilato la form, il nuovo libro viene aggiunto al
	 * sistema e viene mostrato all'utente
	 */
	@Transactional
	@PostMapping("/admin/books")
	public String saveBook(@Valid @ModelAttribute("currBook") Book book, BindingResult bindingResult,
			@RequestParam MultipartFile[] imageFiles, Model model) {
		bookValidator.validate(book, bindingResult);
		if (bindingResult.hasErrors())
			return "admin/newBookForm";
		Book savedBook = this.bookService.save(book);
		for (MultipartFile imageFile : imageFiles)
			if (!imageFile.isEmpty())
				imageService.saveImageToBook(savedBook.getId(), imageFile);
		model.addAttribute("currBook", savedBook);
		return "redirect:/admin/editBooks";
	}

	/*
	 * Se l'amministratore clicca su 'Gestisci Libri' viene mostrata una lista di
	 * tutti i libri simile a prima, ma accanto ad ogni libro sono anche presenti le
	 * opzioni 'modifica' e 'cancella'
	 */
	@GetMapping("/admin/editBooks")
	public String editAllBooks(Model model) {
		model.addAttribute("allBooks", this.bookService.findAll());
		return "admin/editAllBooks";
	}

	/*
	 * Dopo aver cliccato su 'Gestisci' e successivamente su un libro,
	 * viene mostrata la pagina corrispondente a quel libro simile a prima,
	 * ma con alcune aggiunte:
	 * 1. accanto ad ogni autore è presente l'opzione 'cancella'
	 * 2. alla fine della lista degli autori è presente un'opzione 'aggiungi', che
	 * permette di aggiungere alla lista di autori di quel libro un altro autore già
	 * registrato nel sistema
	 */
	@GetMapping("/admin/editBooks/{id}")
	public String editBookById(@PathVariable Long id, Model model) {
		model.addAttribute("currBook", this.bookService.findById(id));
		return "admin/editCurrBook";
	}

	/* Cancella un libro */
	@PostMapping("/admin/editBooks/{id}/delete")
	public String deleteBookById(@PathVariable Long id, Model model) {
		this.bookService.deleteBookById(id);
		model.addAttribute("allBooks", this.bookService.findAll());
		return "redirect:/admin/editBooks";
	}

	/* Rimuove un autore dal libro */
	@PostMapping("/admin/editBooks/{bId}/deleteAuthors/{aId}")
	public String deleteAuthorFromBook(@PathVariable Long aId, @PathVariable Long bId, Model model) {
		this.bookService.deleteAuthorFromBook(aId, bId);
		return "redirect:/admin/editBooks/" + bId;
	}

	/* Mostra gli autori che possono venire aggiunti al libro */
	@GetMapping("/admin/editBooks/{id}/addAuthors")
	public String showAuthorSelection(@PathVariable Long id, Model model) {
		model.addAttribute("allAuthors", this.authorService.findAuthorsNotInBook(id));
		model.addAttribute("bId", id);
		return "admin/selectAuthors";
	}

	/* Aggiunge un autore già esistente nel sistema al libro */
	@PostMapping("/admin/editBooks/{bId}/addAuthors/{aId}")
	public String addAuthorToBook(@PathVariable Long aId, @PathVariable Long bId, Model model) {
		this.bookService.addAuthorToBook(aId, bId);
		return "redirect:/admin/editBooks/" + bId;
	}

	@PostMapping("/admin/editBooks/{id}/update")
	public String updateBook(@PathVariable Long id, @ModelAttribute("currBook") Book updatedBook,
			BindingResult result, Model model) {
		updatedBook.setId(id); // molto importante: necessario per validare correttamente
		bookValidator.validate(updatedBook, result);
		if (result.hasErrors())
			return "admin/editCurrBook";
		Book existingBook = this.bookService.findById(id);

		// aggiorna i campi
		existingBook.setTitle(updatedBook.getTitle());
		existingBook.setReleaseYear(updatedBook.getReleaseYear());
		this.bookService.save(existingBook);
		return "redirect:/admin/editBooks";
	}

	@ResponseBody
	@Transactional
	@GetMapping("/books/{id}/imageIds")
	public List<Long> getBookImageIds(@PathVariable Long id) {
		Book book = bookService.findById(id);
		if (book == null)
			return List.of();
		return book.getImages().stream()
				.map(Image::getId)
				.collect(Collectors.toList());
	}

}
