package it.uniroma3.siw.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import it.uniroma3.siw.model.Image;
import it.uniroma3.siw.service.ImageService;

@Controller
public class ImageController {

	@Autowired
	private ImageService imageService;

	@GetMapping("/image/{id}")
	public ResponseEntity<byte[]> getImage(@PathVariable Long id) {
		Image img = imageService.findById(id);
		if (img == null)
			return ResponseEntity.notFound().build();
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.parseMediaType(img.getContentType()));
		return new ResponseEntity<>(img.getBytes(), headers, HttpStatus.OK);
	}

	@Transactional
	@PostMapping("/admin/editBooks/{id}/image")
	public String saveImagesToBook(@PathVariable Long id, @RequestParam MultipartFile[] imageFiles) {
		for (MultipartFile img : imageFiles)
			imageService.saveImageToBook(id, img);
		return "redirect:/admin/editBooks/" + id;
	}

	@Transactional
	@PostMapping("/admin/editAuthors/{id}/image")
	public String savePictureToAuthor(@PathVariable Long id, @RequestParam MultipartFile imageFile) {
		imageService.savePictureToAuthor(id, imageFile);
		return "redirect:/admin/editAuthors/" + id;
	}

	@Transactional
	@PostMapping("/admin/editBooks/{bookId}/image/{imageId}/delete")
	public String deleteImageFromBook(@PathVariable Long bookId, @PathVariable Long imageId) {
		imageService.deleteImageFromBook(imageId, bookId);
		return "redirect:/admin/editBooks/" + bookId;
	}

	@Transactional
	@PostMapping("/admin/editAuthors/{authorId}/image/{imageId}/delete")
	public String deletePictureFromAuthor(@PathVariable Long authorId, @PathVariable Long imageId) {
		imageService.deletePictureFromAuthor(imageId, authorId);
		return "redirect:/admin/editAuthors/" + authorId;
	}

}
