package com.smartdiscover;

import com.smartdiscover.entity.Author;
import com.smartdiscover.entity.Book;
import com.smartdiscover.entity.BookLoanEntry;
import com.smartdiscover.repository.AuthorRepository;
import com.smartdiscover.repository.BookLoanEntryRepository;
import com.smartdiscover.repository.BookRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;

@SpringBootApplication
@EnableJpaAuditing
public class SpringDataJpaApplication implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(SpringDataJpaApplication.class);

    public static void main(String[] args) {
        SpringApplication.run(SpringDataJpaApplication.class, args);
    }

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private AuthorRepository authorRepository;

    @Autowired
    private BookLoanEntryRepository bookLoanEntryRepository;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Bean
    AuditorAware<String> auditorProvider() {
        return () -> Optional.of("Administrator");
    }

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        Author author1 = new Author();
        author1.setFirstName("Peter");
        author1.setLastName("Thiel");
        authorRepository.save(author1);

        Author author2 = new Author();
        author2.setFirstName("Blake");
        author2.setLastName("Masters");
        authorRepository.save(author2);

        Book book = new Book();
        book.setName("Zero to one");
        book.setSummary("Notes on startups, or how to build the future");

        List<Author> authors = new ArrayList<>();
        authors.add(author1);
        authors.add(author2);

        book.setAuthors(authors);

        List<Book> books = new ArrayList<>();
        books.add(book);

        bookRepository.save(book);

        author1.setBooks(books);
        author2.setBooks(books);

        authorRepository.save(author1);
        authorRepository.save(author2);

        log.info(String.valueOf(bookRepository.findAll()));
        log.info(String.valueOf(authorRepository.findAll()));

        List<Map<String, Object>> rows = jdbcTemplate.queryForList("select * from book_authors");

        log.info(String.valueOf(rows));

        // stored procedures

        //transactions

        //auditing
        log.info("insert book and author using Jdbc repositories");

        Book martian = new Book();
        martian.setName("Martian");
        martian.setSummary("After a dust storm nearly kills him and forces his crew to evacuate while thinking him dead.");
        bookRepository.save(martian);

        Author andyWeir = new Author();
        andyWeir.setFirstName("Andy");
        andyWeir.setLastName("Weir");
        authorRepository.save(andyWeir);

        log.info(String.valueOf(bookRepository.findById(103L)));

        log.info(String.valueOf(authorRepository.findById(54L)));

        //book loan system

        //loan book
        BookLoanEntry bookLoanEntry = loanBook();
        log.info(String.valueOf(bookLoanEntry));

        //return book
        returnBook(bookLoanEntry.getId());

    }

    @Transactional
    public BookLoanEntry loanBook() {
        //default time zone
        ZoneId defaultZoneId = ZoneId.systemDefault();

        //search book
        String bookName = "Atomic Habits";
        Book book = bookRepository.findByName(bookName);

        //loan user info
        String firstName = "John";
        String lastName = "Smith";

        //loan entry dates
        Date loanDate = new Date();
        Date dueDate = Date.from(LocalDate.now().plusDays(15).atStartOfDay(defaultZoneId).toInstant());

        //book loan entry
        BookLoanEntry bookLoanEntry = new BookLoanEntry();
        bookLoanEntry.setBook(book);
        bookLoanEntry.setUserFirstName(firstName);
        bookLoanEntry.setUserFirstName(lastName);
        bookLoanEntry.setLoanDate(loanDate);
        bookLoanEntry.setDueDate(dueDate);
        bookLoanEntry.setStatus("active");

        bookLoanEntryRepository.save(bookLoanEntry);

        //update book availability
        book.setAvailable(false);
        bookRepository.save(book);

        return bookLoanEntry;
    }

    @Transactional
    public void returnBook(long bookLoanEntryId) {
        //fetch the loan entry
        BookLoanEntry bookLoanEntry = bookLoanEntryRepository.findById(bookLoanEntryId).get();

        //update the loan entry
        bookLoanEntry.setReturnDate(new Date());
        bookLoanEntry.setStatus("returned");

        bookLoanEntryRepository.save(bookLoanEntry);

        //update the book availability
        Book book = bookLoanEntry.getBook();
        book.setAvailable(true);
        bookRepository.save(book);

    }
}
