package it.uniroma3.siw.authentication;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class AuthConfiguration {

	@Autowired
	private DataSource dataSource;

	@Autowired
	public void configureGlobal(AuthenticationManagerBuilder auth)
			throws Exception {
		auth.jdbcAuthentication()
				.dataSource(dataSource)
				.authoritiesByUsernameQuery("SELECT username, role from credentials WHERE username=?")
				.usersByUsernameQuery(
						"SELECT username, password, 1 as enabled FROM credentials WHERE username=?");
	}

	@Bean
	PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Bean
	AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration)
			throws Exception {
		return authenticationConfiguration.getAuthenticationManager();
	}

	@Bean
	SecurityFilterChain configure(final HttpSecurity http) throws Exception {
		http.authorizeHttpRequests(requests -> requests
				.requestMatchers(PathRequest.toStaticResources().atCommonLocations()).permitAll()
				// Allow access to login and register pages
				.requestMatchers(HttpMethod.GET, "/login", "/register").permitAll()
				.requestMatchers(HttpMethod.POST, "/login", "/register").permitAll()
				// Allow public access to viewing pages
				.requestMatchers("/", "/books", "/books/**", "/authors", "/authors/**").permitAll()
				// Allow public access to images and static resources
				.requestMatchers("/images/**", "/css/**", "/js/**").permitAll()
				// Allow public access to author images endpoint
				.requestMatchers(HttpMethod.GET, "/authors/*/image").permitAll()
				// Accesso alle recensioni per utenti registrati
				.requestMatchers("/books/*/addReview").hasAuthority("ROLE_USER")
				.requestMatchers(HttpMethod.POST, "/books/*/addReview").hasAuthority("ROLE_USER")
				// Accesso riservato agli amministratori
				.requestMatchers("/admin/**").hasAuthority("ROLE_ADMIN")
				.requestMatchers(HttpMethod.POST, "/admin/reviews/**/delete").hasAuthority("ROLE_ADMIN")
				// Tutto il resto è permesso
				.anyRequest().permitAll())
				.formLogin(login -> login
						.loginPage("/login")
						.permitAll()
						.defaultSuccessUrl("/", true)
						.failureUrl("/login?error=true"))
				.logout(logout -> logout
						.logoutUrl("/logout")
						.logoutSuccessUrl("/")
						.invalidateHttpSession(true)
						.deleteCookies("JSESSIONID")
						.clearAuthentication(true)
						.permitAll());
		return http.build();
	}

}
