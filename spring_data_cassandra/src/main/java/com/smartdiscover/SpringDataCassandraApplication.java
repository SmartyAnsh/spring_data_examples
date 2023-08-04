package com.smartdiscover;

import com.smartdiscover.model.Author;
import com.smartdiscover.model.Book;
import com.smartdiscover.model.BookAnalytics;
import com.smartdiscover.model.BookByAuthor;
import com.smartdiscover.repository.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.cassandra.config.EnableCassandraAuditing;
import org.springframework.data.cassandra.core.CassandraTemplate;
import org.springframework.data.cassandra.core.ReactiveCassandraTemplate;
import org.springframework.data.cassandra.core.query.Query;
import org.springframework.data.domain.AuditorAware;
import reactor.core.publisher.Flux;

import java.util.*;

import static org.springframework.data.cassandra.core.query.Criteria.where;
import static org.springframework.data.cassandra.core.query.Query.query;

@SpringBootApplication
@EnableCassandraAuditing
public class SpringDataCassandraApplication implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(SpringDataCassandraApplication.class);

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private AuthorRepository authorRepository;

    @Autowired
    private BookByAuthorRepository bookByAuthorRepository;

    @Autowired
    private BookAnalyticsRepository bookAnalyticsRepository;

    @Autowired
    private CassandraTemplate cassandraTemplate;

    @Autowired
    private ReactiveAuthorRepository reactiveAuthorRepository;

    @Autowired
    private ReactiveCassandraTemplate reactiveCassandraTemplate;

    @Bean
    AuditorAware<String> auditorProvider() {
        return () -> Optional.of("Admin");
    }

    public static void main(String[] args) {
        SpringApplication.run(SpringDataCassandraApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        //cleanup
        authorRepository.deleteAll();
        bookRepository.deleteAll();
        bookByAuthorRepository.deleteAll();

        // CRUD started

        //create author
        Author andyWeir = new Author();
        andyWeir.setFirstName("Andy");
        andyWeir.setLastName("Weir");
        andyWeir.setId(UUID.randomUUID());
        authorRepository.save(andyWeir);

        //read author
        log.info(String.valueOf(authorRepository.findAll()));

        ArrayList<Author> martianAuthors = new ArrayList<>();
        martianAuthors.add(andyWeir);

        //create book
        Book martian = new Book();
        martian.setName("Martian");
        martian.setSummary("One problem at a time and survive");
        martian.setId(UUID.randomUUID());
        bookRepository.save(martian);

        //create bookByAuthor
        BookByAuthor bookByAuthor = new BookByAuthor();
        bookByAuthor.setBookName(martian.getName());
        bookByAuthor.setAuthorNames(Collections.singletonList(andyWeir.getFullName()));
        bookByAuthor.setId(UUID.randomUUID());
        bookByAuthorRepository.save(bookByAuthor);

        //read book
        log.info(String.valueOf(bookRepository.findAll()));

        //read BookByAuthor
        log.info(String.valueOf(bookByAuthorRepository.findAll()));

        //update book
        martian.setSummary("Six days ago, astronaut Mark Watney became one of the first people to walk on Mars.");
        bookRepository.save(martian);

        //read book
        log.info(String.valueOf(bookRepository.findAll()));

        //delete author
        authorRepository.delete(andyWeir);

        //read author
        log.info(String.valueOf(authorRepository.findAll()));

        // CRUD finished

        //cassandraTemplate operations

        // Create an Entity
        Author morganHousel = new Author();
        morganHousel.setFirstName("Morgan");
        morganHousel.setLastName("Housel");
        morganHousel.setId(UUID.randomUUID());

        // save
        cassandraTemplate.insert(morganHousel);

        Author jkRowling = new Author();
        jkRowling.setFirstName("Joane");
        jkRowling.setLastName("Rowling");
        jkRowling.setId(UUID.randomUUID());

        // save
        cassandraTemplate.insert(jkRowling);

        Book psychologyOfMoney = new Book();
        psychologyOfMoney.setName("The Psychology of Money");
        psychologyOfMoney.setSummary("Timeless Lessons on Wealth, Greed, and Happiness");
        psychologyOfMoney.setId(UUID.randomUUID());
        cassandraTemplate.insert(psychologyOfMoney);

        // get by id
        Author found1 = cassandraTemplate.selectOneById(morganHousel.getId(), Author.class);
        log.info(String.valueOf(found1));

        //find all authors
        List<Author> allAuthors = cassandraTemplate.select(Query.empty(), Author.class);
        log.info(String.valueOf(allAuthors));

        //search using Query
        List<Book> books = cassandraTemplate.select(
                query(where("id").in(psychologyOfMoney.getId(), UUID.randomUUID())).withAllowFiltering(),
                Book.class);
        log.info(String.valueOf(books));

        //reactive

        //create Author object and save using reactiveAuthorRepository
        Author pauloCoelho = new Author();
        pauloCoelho.setFirstName("Paulo");
        pauloCoelho.setLastName("Coelho");
        pauloCoelho.setId(UUID.randomUUID());
        reactiveAuthorRepository.save(pauloCoelho).doOnNext(author -> log.info(String.valueOf(author))).subscribe();

        reactiveAuthorRepository.findAll().doOnNext(author -> log.info(String.valueOf(author))).subscribe();

        //find all authors using reactiveAuthorRepository

        Flux<Author> authorFlux = reactiveAuthorRepository.findAllById(Arrays.asList(andyWeir.getId(), pauloCoelho.getId()));
        authorFlux.subscribe(author1 -> log.info(author1.toString()));

        Author jamesClear = new Author();
        jamesClear.setFirstName("James");
        jamesClear.setLastName("Clear");
        jamesClear.setId(UUID.randomUUID());

        reactiveCassandraTemplate.insert(Author.class).one(jamesClear).subscribe();

        reactiveCassandraTemplate.select(Query.empty(), Author.class).doOnNext(a -> log.info(a.toString())).subscribe();


        //bootstrap data

        Book book1 = new Book();
        book1.setId(UUID.randomUUID());
        book1.setName("Atomic Habits");
        bookRepository.save(book1);
        bookAnalyticsRepository.save(new BookAnalytics(book1.getId(), book1.getName()));

        Book book2 = new Book();
        book2.setId(UUID.randomUUID());
        book2.setName("Harry Potter and the chamber of secrets");
        bookRepository.save(book2);
        bookAnalyticsRepository.save(new BookAnalytics(book2.getId(), book2.getName()));

        Book book3 = new Book();
        book3.setId(UUID.randomUUID());
        book3.setName("Lord of the rings");
        bookRepository.save(book3);
        bookAnalyticsRepository.save(new BookAnalytics(book3.getId(), book3.getName()));

        Book book4 = new Book();
        book4.setId(UUID.randomUUID());
        book4.setName("Dune");
        bookRepository.save(book4);
        bookAnalyticsRepository.save(new BookAnalytics(book4.getId(), book4.getName()));

        Book book5 = new Book();
        book5.setId(UUID.randomUUID());
        book5.setName("Sapiens: A Brief History of Humankind");
        bookRepository.save(book5);
        bookAnalyticsRepository.save(new BookAnalytics(book5.getId(), book5.getName()));

        //update borrowedCount
        updateBorrowedCount("Atomic Habits");
        updateBorrowedCount("Harry Potter and the chamber of secrets");
        updateBorrowedCount("Lord of the rings");
        updateBorrowedCount("Dune");
        updateBorrowedCount("Sapiens: A Brief History of Humankind");

        updateViewedCount("Atomic Habits");
        updateViewedCount("Harry Potter and the chamber of secrets");
        updateViewedCount("Lord of the rings");
        updateViewedCount("Dune");
        updateViewedCount("Sapiens: A Brief History of Humankind");

        updateAvgRating("Dune", 9);
        updateAvgRating("Dune", 7);

        updateAvgRating("Atomic Habits", 8);
        updateAvgRating("Atomic Habits", 9);

        updateViewedCount("Dune");
        updateAvgRating("Lord of the rings", 9);

        log.info(String.valueOf(checkBookAnalytics("Dune")));
        log.info(String.valueOf(checkBookAnalytics("Lord of the rings")));
        log.info(String.valueOf(checkBookAnalytics("Atomic Habits")));
        log.info(String.valueOf(checkBookAnalytics("Sapiens: A Brief History of Humankind")));
    }

    public void updateBorrowedCount(String bookName) {
        BookAnalytics bookAnalytics = bookAnalyticsRepository.findByBookName(bookName);
        bookAnalytics.incrementBorrowedCount();
        bookAnalyticsRepository.save(bookAnalytics);
    }

    public void updateViewedCount(String bookName) {
        BookAnalytics bookAnalytics = bookAnalyticsRepository.findByBookName(bookName);
        bookAnalytics.incrementViewedCount();
        bookAnalyticsRepository.save(bookAnalytics);
    }

    public void updateAvgRating(String bookName, int rating) {
        BookAnalytics bookAnalytics = bookAnalyticsRepository.findByBookName(bookName);

        double avgRating = (bookAnalytics.getAverageRating() * bookAnalytics.getTotalRatings() + rating)
                / (bookAnalytics.getTotalRatings() + 1);

        bookAnalytics.setAverageRating(avgRating);
        bookAnalytics.incrementTotalRatings();

        bookAnalyticsRepository.save(bookAnalytics);
    }

    public BookAnalytics checkBookAnalytics(String bookName) {
        return bookAnalyticsRepository.findByBookName(bookName);
    }

}
