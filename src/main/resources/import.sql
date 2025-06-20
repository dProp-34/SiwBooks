CREATE SEQUENCE IF NOT EXISTS hibernate_sequence START 1;

insert into book (id, title, release_year) values(nextval('hibernate_sequence'), 'Full metal jacket', 1987);
insert into book (id, title, release_year) values(nextval('hibernate_sequence'), 'Non e'' un paese per vecchi', 2007);
insert into book (id, title, release_year) values(nextval('hibernate_sequence'), 'The founder', 2016);
insert into book (id, title, release_year) values(nextval('hibernate_sequence'), 'Harry Potter e la pietra filosofale', 2001);
insert into book (id, title, release_year) values(nextval('hibernate_sequence'), 'Il pianeta delle scimmie', 2001);
insert into book (id, title, release_year) values(nextval('hibernate_sequence'), 'Lo chiamavano Jeeg Robot', 2015);
insert into book (id, title, release_year) values(nextval('hibernate_sequence'), 'Yesterday', 2019);

insert into author (id, name, surname, date_of_birth) values(nextval('hibernate_sequence'), 'Tim', 'Burton', '1958-08-25');
insert into author (id, name, surname, date_of_birth) values(nextval('hibernate_sequence'), 'Himesh', 'Patel', '1990-10-13');
insert into author (id, name, surname, date_of_birth) values(nextval('hibernate_sequence'), 'Danny', 'Boyle', '1956-10-20');
insert into author (id, name, surname, date_of_birth) values(nextval('hibernate_sequence'), 'Tim', 'Roth', '1961-05-14');
insert into author (id, name, surname, date_of_birth) values(nextval('hibernate_sequence'), 'Michael', 'Keaton', '1951-09-05');
insert into author (id, name, surname, date_of_birth) values(nextval('hibernate_sequence'), 'Stanley', 'Kubrick', '1928-07-26');
insert into author (id, name, surname, date_of_birth) values(nextval('hibernate_sequence'), 'Ronald Lee', 'Ermey', '1944-03-24');	
insert into author (id, name, surname, date_of_birth) values(nextval('hibernate_sequence'), 'Claudio', 'Santamaria', '1974-07-22');
insert into author (id, name, surname, date_of_birth) values(nextval('hibernate_sequence'), 'Gabriele', 'Mainetti', '1976-11-07');

insert into users(id, name, surname, email) values(1, 'sim', 'gia', 'sim@gia');
insert into credentials(id, password, role, username, user_id) values(1, '$2a$10$okIBAVJOnTdpl/7j0DUoL.SEqS4FsOCJSE7cjO9zDkPNM7hxikuQu', 'ADMIN', 'sim.gia', 1);
