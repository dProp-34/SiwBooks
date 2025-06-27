package it.uniroma3.siw.service;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import it.uniroma3.siw.model.Author;
import it.uniroma3.siw.model.Book;
import it.uniroma3.siw.model.Image;
import it.uniroma3.siw.repository.AuthorRepository;
import it.uniroma3.siw.repository.BookRepository;
import it.uniroma3.siw.repository.ImageRepository;

@Service
public class ImageService {

	@Autowired
	private ImageRepository imageRepository;
	@Autowired
	private BookRepository bookRepository;
	@Autowired
	private AuthorRepository authorRepository;

	public Image findById(Long id) {
		Optional<Image> result = imageRepository.findById(id);
		return result.orElse(null);
	}

	public List<Long> getBookImageIds(Long bookId) {
		Book book = bookRepository.findById(bookId).orElse(null);
		if (book == null)
			return List.of();
		return book.getImages().stream()
				.map(Image::getId)
				.collect(Collectors.toList());
	}

	public Long getAuthorPictureId(Long authorId) {
		Author author = authorRepository.findById(authorId).orElse(null);
		if (author == null || author.getPicture() == null)
			return null;
		return author.getPicture().getId();
	}

	@Transactional // La rimozione è sicura anche in presenza di eccezioni intermedie
	public void deleteImageFromBook(Long imageId, Long bookId) {
		Image img = imageRepository.findById(imageId).orElse(null);
		Book book = bookRepository.findById(bookId).orElse(null);
		if (img != null && book != null && book.getImages().contains(img)) {
			book.getImages().remove(img);
			bookRepository.save(book);
			// Due immagini identiche caricate separatamente sono rappresentate da
			// due entità Image diverse
			imageRepository.delete(img);
		}
	}

	@Transactional
	public void deletePictureFromAuthor(Long imageId, Long authorId) {
		Image img = imageRepository.findById(imageId).orElse(null);
		Author author = authorRepository.findById(authorId).orElse(null);
		if (img != null && author != null && author.getPicture().equals(img)) {
			author.setPicture(null);
			authorRepository.save(author);
			imageRepository.delete(img);
		}
	}

	@Transactional
	public void saveImageToBook(Long bookId, MultipartFile file) {
		Book book = bookRepository.findById(bookId).orElse(null);
		if (book != null && !file.isEmpty()) {
			try {
				Image img = new Image();
				img.setName(file.getOriginalFilename());
				img.setContentType(file.getContentType());
				img.setBytes(file.getBytes());

				imageRepository.save(img);
				book.getImages().add(img);
				bookRepository.save(book);
			} catch (IOException e) {
				throw new RuntimeException("Errore durante il caricamento dell'immagine", e);
			}
		}
	}

	@Transactional
	public void savePictureToAuthor(Long authorId, MultipartFile file) {
		Author author = authorRepository.findById(authorId).orElse(null);
		if (author != null && !file.isEmpty()) {
			try {
				Image img = new Image();
				img.setName(file.getOriginalFilename());
				img.setContentType(file.getContentType());
				img.setBytes(file.getBytes());

				imageRepository.save(img);
				author.setPicture(img);
				authorRepository.save(author);
			} catch (IOException e) {
				throw new RuntimeException("Errore durante il caricamento dell'immagine", e);
			}
		}
	}

}
