package it.uniroma3.siw.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import it.uniroma3.siw.model.Author;
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
	@GetMapping("/authors")
	public String findAll(Model model) {
		model.addAttribute("allAuthors", this.authorService.findAll());
		return "allAuthors";
	}

	/* Mostra il dettaglio di un singolo autore */
	@GetMapping("/authors/{id}")
	public String findById(@PathVariable Long id, Model model) {
		model.addAttribute("currAuthor", this.authorService.findById(id));
		return "currAuthor";
	}

	/* Form per inserire un nuovo autore */
	@GetMapping("/admin/newAuthorForm")
	public String newAuthorForm(Model model) {
		model.addAttribute("currAuthor", new Author());
		return "newAuthorForm";
	}

	/* Aggiunge un autore al sistema */
	@PostMapping("/authors")
	public String saveAuthor(@Valid @ModelAttribute("currAuthor") Author author, BindingResult bindingResult,
			Model model) {
		if (bindingResult.hasErrors()) {
			return "newAuthorForm";
		}
		Author savedAuthor = this.authorService.save(author);
		model.addAttribute("currAuthor", savedAuthor);
		return "redirect:/authors/" + savedAuthor.getId();
	}

	/* Pagina per la gestione di tutti gli autori */
	@GetMapping("/admin/editAuthors")
	public String editAllAuthors(Model model) {
		model.addAttribute("allAuthors", this.authorService.findAll());
		return "admin/editAllAuthors";
	}

	/* Pagina di modifica di un singolo autore */
	@GetMapping("/admin/editAuthors/{id}")
	public String editAuthorById(@PathVariable Long id, Model model) {
		model.addAttribute("currAuthor", this.authorService.findById(id));
		return "admin/editCurrAuthor";
	}

	/* Cancella un autore */
	@PostMapping("/admin/editAuthors/{id}/delete")
	public String deleteAuthorById(@PathVariable Long id, Model model) {
		this.authorService.deleteAuthorById(id);
		model.addAttribute("allAuthors", this.authorService.findAll());
		return "redirect:/admin/editAllAuthors";
	}

	/* Aggiunge un libro già esistente nel sistema all'autore */
	@PostMapping("/admin/editAuthors/{aId}/addBooks/{bId}")
	public String addBookToAuthor(@PathVariable Long bId, @PathVariable Long aId, Model model) {
		this.authorService.addBookToAuthor(bId, aId);
		return "redirect:/admin/editAllAuthors/" + aId;
	}

	/* Rimuove un libro dall'autore */
	@PostMapping("/admin/editAuthors/{aId}/deleteBooks/{bId}")
	public String deleteBookFromAuthor(@PathVariable Long bId, @PathVariable Long aId, Model model) {
		this.authorService.deleteBookFromAuthor(bId, aId);
		return "redirect:/admin/editAllAuthors/" + aId;
	}

	/* Mostra i libri che possono venire aggiunti all'autore */
	@GetMapping("/admin/editAuthors/{id}/selectBooks")
	public String showBookSelection(@PathVariable Long id, Model model) {
		model.addAttribute("allBooks", this.bookService.findBooksNotInAuthor(id));
		return "admin/selectBook";
	}

	@GetMapping("/authors/{id}/image")
	public ResponseEntity<byte[]> getAuthorPicture(@PathVariable Long id) {
		Author author = authorService.findById(id);
		if (author == null || author.getPicture() == null || author.getPicture().getBytes() == null) {
			return ResponseEntity.notFound().build();
		}
		byte[] imageBytes = author.getPicture().getBytes();
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.IMAGE_PNG);
		return new ResponseEntity<>(imageBytes, headers, HttpStatus.OK);
	}

	@PostMapping("/admin/editAuthors/{id}/image")
	public String uploadAuthorPicture(@PathVariable Long id, @RequestParam MultipartFile imageFile, Model model) {
		try {
			authorService.saveAuthorPicture(id, imageFile);
		} catch (Exception e) {
			model.addAttribute("uploadError", "Errore nel caricamento dell'immagine.");
			return "admin/editAllAuthors";
		}
		return "redirect:/admin/editAllAuthors/" + id;
	}

}
