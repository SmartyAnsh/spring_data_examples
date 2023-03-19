insert into book (id, name, summary) values (nextval('book_seq'), 'Atomic Habits', 'An easy & proven way to build good habits & break bad ones');
insert into book (id, name, summary) values (nextval('book_seq'), 'The psychology of money', 'Timeless lessons on wealth, greed, and happiness');
insert into book (id, name, summary) values (nextval('book_seq'), 'Everyone Believes It; Most Will Be Wrong', 'Motley Thoughts on Investing and the Economy');

insert into author (id, first_name, last_name) values (nextval('author_seq'), 'James', 'Clear');
insert into author (id, first_name, last_name) values (nextval('author_seq'), 'Morgan', 'Housel');

insert into book_authors (book_id, author_id) values (1, 1);
insert into book_authors (book_id, author_id) values (51, 51);
insert into book_authors (book_id, author_id) values (101, 51);