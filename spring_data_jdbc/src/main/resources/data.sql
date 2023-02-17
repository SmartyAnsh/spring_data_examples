insert into book (name, summary) values ('Atomic Habits', 'An easy & proven way to build good habits & break bad ones');
insert into book (name, summary) values ('The psychology of money', 'Timeless lessons on wealth, greed, and happiness');
insert into book (name, summary) values ('Everyone Believes It; Most Will Be Wrong', 'Motley Thoughts on Investing and the Economy');

insert into author (first_name, last_name) values ('James', 'Clear');
insert into author (first_name, last_name) values ('Morgan', 'Housel');

insert into author_book (book_id, author_id) values (1, 1);
insert into author_book (book_id, author_id) values (2, 2);
insert into author_book (book_id, author_id) values (3, 2);