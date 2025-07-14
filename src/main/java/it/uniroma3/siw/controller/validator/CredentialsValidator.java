package it.uniroma3.siw.controller.validator;

import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import it.uniroma3.siw.model.Credentials;

@Component
public class CredentialsValidator implements Validator {

	@Override
	public boolean supports(Class<?> clazz) {
		return Credentials.class.equals(clazz);
	}

	@Override
	public void validate(Object target, Errors errors) {
		Credentials credentials = (Credentials) target;
		if (!credentials.getPassword().equals(credentials.getConfirmPassword())) {
			errors.rejectValue("confirmPassword", "Match");
		}
	}
}
