create table AUTHOR (
    id         bigint PRIMARY KEY auto_increment,
    first_name varchar(255),
    last_name  varchar(255)
);

create table book (
    id      bigint primary key auto_increment,
    name    varchar(255),
    summary varchar(255),
    created_at timestamp,
    updated_at timestamp,
    created_by varchar(255),
    updated_by varchar(255)
);

create table author_book (
    author_id bigint,
    book_id bigint,
    constraint fk_author_book_author_id foreign key (author_id) references author(id),
    constraint fk_author_book_book_id foreign key (book_id) references book(id)
)