package it.uniroma3.siw.controller.validator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import it.uniroma3.siw.model.Book;
import it.uniroma3.siw.service.BookService;

@Component
public class BookValidator implements Validator {

	@Autowired
	private BookService bookService;

	@Override
	public void validate(Object o, Errors errors) {
		Book book = (Book) o;
		// Cerca un libro con stesso titolo e anno
		Book existing = bookService.findByTitleAndReleaseYear(book.getTitle(), book.getReleaseYear());
		// Se ne esiste uno, ed è diverso da quello che stiamo validando (per update)
		if (existing != null && (book.getId() == null || !existing.getId().equals(book.getId())))
			errors.reject("book.duplicate");
	}

	@Override
	public boolean supports(Class<?> aClass) {
		return Book.class.equals(aClass);
	}

}
