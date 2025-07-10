package it.uniroma3.siw.model;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;

@Entity
public class Credentials {
	/*
	 * Al sistema possono accedere utenti occasionali, utenti registrati ed
	 * amministratori:
	 * Gli utenti occasionali possono consultare tutte le informazioni sui libri
	 * (incluse le recensioni) e sugli autori, ma non possono apportare nessun tipo
	 * di modifica ai dati
	 * Gli utenti registrati, oltre a poter consultare le informazioni, possono
	 * aggiungere recensioni ai libri (ogni utente registrato può aggiungere al più
	 * una recensione, ogni libro può avere più recensioni, al più una per ogni
	 * utente)
	 * Gli amministratori possono aggiungere, modificare, cancellare libri e autori.
	 * Gli amministratori possono anche cancellare le recensioni (ma non possono
	 * modificarle)
	 */
	public static final String DEFAULT_ROLE = "ROLE_USER";
	public static final String ADMIN_ROLE = "ROLE_ADMIN";
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;
	private String username;
	private String password;
	private String role;
	@OneToOne(cascade = CascadeType.ALL)
	private User user;

	public String getUsername() {
		return username;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getRole() {
		return role;
	}

	public void setRole(String role) {
		this.role = role;
	}

}
