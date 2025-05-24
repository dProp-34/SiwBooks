package it.uniroma3.siw;

import static org.springframework.security.config.Customizer.withDefaults;
import javax.sql.DataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class AuthConfiguration {

	private static final String ADMIN_ROLE = "ROLE_ADMIN";
	@Autowired
	private DataSource dataSource;

	@Autowired
	public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
		auth.jdbcAuthentication().dataSource(dataSource)
				.authoritiesByUsernameQuery("SELECT username, role FROM credentials WHERE username=?")
				.usersByUsernameQuery("SELECT username, password, 1 as enabled FROM credentials WHERE username=?");
	}

	@Bean
	PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Bean
	protected SecurityFilterChain configure(final HttpSecurity httpSecurity)
			throws Exception {
		httpSecurity
				.csrf(withDefaults()).cors(cors -> cors.disable())
				.authorizeHttpRequests(requests -> requests
						// .requestMatchers("/**").permitAll()
						// chiunque (autenticato o no) può accedere alle pagine index, login, register,
						// ai css e alle immagini
						.requestMatchers(HttpMethod.GET, "/", "/index", "/register", "/css/**", "/images/**",
								"favicon.ico")
						.permitAll()
						// chiunque (autenticato o no) può mandare richieste POST al punto di accesso
						// per login e register
						.requestMatchers(HttpMethod.POST, "/register", "/login").permitAll()
						.requestMatchers(HttpMethod.GET, "/admin/**").hasAnyAuthority(ADMIN_ROLE)
						.requestMatchers(HttpMethod.POST, "/admin/**").hasAnyAuthority(ADMIN_ROLE)
						// tutti gli utenti autenticati possono accere alle pagine rimanenti
						.anyRequest().authenticated())
				.formLogin(login -> login
						.loginPage("/login")
						.permitAll()
						.defaultSuccessUrl("/success", true)
						.failureUrl("/login?error=true"))
				.logout(logout -> logout
						// il logout è attivato con una richiesta GET a "/logout"
						.logoutUrl("/logout")
						// in caso di successo, si viene reindirizzati alla home
						.logoutSuccessUrl("/")
						.invalidateHttpSession(true)
						.deleteCookies("JSESSIONID")
						.logoutUrl("/logout")
						.logoutSuccessUrl("/")
						.invalidateHttpSession(true)
						.deleteCookies("JSESSIONID")
						.clearAuthentication(true)
						.permitAll()
						.clearAuthentication(true).permitAll());
		return httpSecurity.build();
	}
}
