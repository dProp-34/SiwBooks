package it.uniroma3.siw.controller;

import java.sql.Date;
import java.text.SimpleDateFormat;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import it.uniroma3.siw.controller.validator.AuthorValidator;
import it.uniroma3.siw.model.Author;
import it.uniroma3.siw.service.AuthorService;
import it.uniroma3.siw.service.BookService;
import it.uniroma3.siw.service.ImageService;
import jakarta.validation.Valid;

@Controller
public class AuthorController {

	@Autowired
	private AuthorService authorService;
	@Autowired
	private AuthorValidator authorValidator;
	@Autowired
	private BookService bookService;
	@Autowired
	private ImageService imageService;

	/* Mostra la lista di tutti gli autori */
	@GetMapping("/authors")
	public String findAll(Model model) {
		model.addAttribute("allAuthors", this.authorService.findAll());
		return "allAuthors";
	}

	/* Mostra il dettaglio di un singolo autore @Transactional */
	@GetMapping("/authors/{id}")
	public String findById(@PathVariable Long id, Model model) {
		model.addAttribute("currAuthor", this.authorService.findById(id));
		return "currAuthor";
	}

	/* Form per creare un nuovo autore */
	@GetMapping("/admin/newAuthorForm")
	public String newAuthorForm(Model model) {
		model.addAttribute("currAuthor", new Author());
		return "admin/newAuthorForm";
	}

	/* Crea un nuovo autore da aggiungere al sistema */
	@Transactional
	@PostMapping("/admin/authors")
	public String saveAuthor(@Valid @ModelAttribute("currAuthor") Author author, BindingResult bindingResult,
			@RequestParam MultipartFile imageFile, Model model) {
		authorValidator.validate(author, bindingResult);
		if (bindingResult.hasErrors())
			return "admin/newAuthorForm";
		Author savedAuthor = this.authorService.save(author);
		if (!imageFile.isEmpty())
			imageService.savePictureToAuthor(savedAuthor.getId(), imageFile);
		model.addAttribute("currAuthor", savedAuthor);
		return "redirect:/admin/editAuthors";
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

	/* Metodo per gestire la conversione delle date */
	@InitBinder
	public void initBinder(WebDataBinder binder) {
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		dateFormat.setLenient(false);
		binder.registerCustomEditor(Date.class, new CustomDateEditor(dateFormat, true));
	}

	/* Cancella un autore dal sistema */
	@PostMapping("/admin/editAuthors/{id}/delete")
	public String deleteAuthorById(@PathVariable Long id, Model model) {
		this.authorService.deleteAuthorById(id);
		model.addAttribute("allAuthors", this.authorService.findAll());
		return "redirect:/admin/editAuthors";
	}

	/* Rimuove un libro dall'autore */
	@PostMapping("/admin/editAuthors/{aId}/deleteBooks/{bId}")
	public String deleteBookFromAuthor(@PathVariable Long bId, @PathVariable Long aId, Model model) {
		this.authorService.deleteBookFromAuthor(bId, aId);
		return "redirect:/admin/editAuthors/" + aId;
	}

	/* Mostra i libri che possono venire aggiunti all'autore */
	@GetMapping("/admin/editAuthors/{id}/addBooks")
	public String showBookSelection(@PathVariable Long id, Model model) {
		model.addAttribute("allBooks", this.bookService.findBooksNotInAuthor(id));
		model.addAttribute("aId", id);
		return "admin/selectBooks";
	}

	/* Aggiunge un libro già esistente nel sistema all'autore */
	@PostMapping("/admin/editAuthors/{aId}/addBooks/{bId}")
	public String addBookToAuthor(@PathVariable Long bId, @PathVariable Long aId, Model model) {
		this.authorService.addBookToAuthor(bId, aId);
		return "redirect:/admin/editAuthors/" + aId;
	}

	@PostMapping("/admin/editAuthors/{id}/update")
	public String updateAuthor(@PathVariable Long id, @ModelAttribute("currAuthor") Author updatedAuthor,
			BindingResult result, Model model) {
		updatedAuthor.setId(id); // molto importante: necessario per validare correttamente
		authorValidator.validate(updatedAuthor, result);
		if (result.hasErrors())
			return "admin/editCurrAuthor";
		Author existingAuthor = this.authorService.findById(id);

		// aggiorna i campi
		existingAuthor.setName(updatedAuthor.getName());
		existingAuthor.setSurname(updatedAuthor.getSurname());
		existingAuthor.setNationality(updatedAuthor.getNationality());
		existingAuthor.setDateOfBirth(updatedAuthor.getDateOfBirth());
		existingAuthor.setDateOfDeath(updatedAuthor.getDateOfDeath());
		this.authorService.save(existingAuthor);
		return "redirect:/admin/editAuthors";
	}

	@ResponseBody
	@GetMapping("/authors/{id}/pictureId")
	public Long getAuthorPictureId(@PathVariable Long id) {
		return authorService.findById(id).getPicture().getId();
	}

}
