package com.smartdiscover;

import com.smartdiscover.entity.Author;
import com.smartdiscover.entity.Book;
import com.smartdiscover.mapper.AuthorMapper;
import com.smartdiscover.mapper.BookMapper;
import com.smartdiscover.repository.AuthorRepository;
import com.smartdiscover.repository.BookRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jdbc.repository.config.EnableJdbcAuditing;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.Optional;

@SpringBootApplication
@EnableJdbcAuditing
public class SpringDataJdbcApplication implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(SpringDataJdbcApplication.class);

    public static void main(String[] args) {
        SpringApplication.run(SpringDataJdbcApplication.class, args);
    }

    @Autowired
    JdbcTemplate jdbcTemplate;

    @Autowired
    BookRepository bookRepository;

    @Autowired
    AuthorRepository authorRepository;

    @Autowired
    BookMapper bookMapper;

    @Autowired
    AuthorMapper authorMapper;

    @Bean
    AuditorAware<String> auditorProvider() {
        return () -> Optional.of("Administrator");
    }

    @Override
    public void run(String... strings) throws Exception {
        log.info("insert book and author using Jdbc repositories");

        Book book = new Book();
        book.setName("Martian");
        book.setSummary("After a dust storm nearly kills him and forces his crew to evacuate while thinking him dead.");
        bookRepository.save(book);

        Author author = new Author();
        author.setFirstName("Andy");
        author.setLastName("Weir");
        authorRepository.save(author);

        jdbcTemplate.update("insert into author_book (author_id, book_id) values (?, ?)", author.getId(), book.getId());

        log.info("find all books");

        log.info(String.valueOf(bookRepository.findAll()));

        log.info("find all authors");

        log.info(String.valueOf(authorRepository.findAll()));

        log.info("CRUD features started");

        log.info("CREATE");
        Book egoIsTheEnemy = new Book();
        egoIsTheEnemy.setName("Ego is the enemy");
        egoIsTheEnemy.setSummary("The fight to master our greatest opponent");
        bookRepository.save(egoIsTheEnemy);

        Author ryanHoliday = new Author();
        ryanHoliday.setFirstName("Ryan");
        ryanHoliday.setLastName("Holiday");
        authorRepository.save(ryanHoliday);

        log.info("READ");
        log.info(String.valueOf(bookRepository.findById(egoIsTheEnemy.getId())));
        log.info(String.valueOf(authorRepository.findById(ryanHoliday.getId())));

        log.info("UDPATE");
        egoIsTheEnemy.setSummary("The fight to master our greatest opponent from Ryan Holiday");
        bookRepository.save(egoIsTheEnemy);

        ryanHoliday.setLastName("Holi");
        authorRepository.save(ryanHoliday);

        log.info("DELETE");
        bookRepository.delete(egoIsTheEnemy);
        authorRepository.delete(ryanHoliday);

        log.info("CRUD features done");

        log.info("find and insert book using ibatis mapper");

        log.info(String.valueOf(bookMapper.getBook(1L)));
        log.info(String.valueOf(bookMapper.saveBook("Zero to one", "Notes on startups, or How to build the future")));

        log.info(String.valueOf(authorMapper.getAuthor(1L)));
        log.info(String.valueOf(authorMapper.saveAuthor("Peter", "Theil")));

        //check lock
        log.info(String.valueOf(bookRepository.findFirstByNameOrderByNameAsc("Zero to one")));
    }

}
