package com.smartdiscover;

import com.smartdiscover.model.Author;
import com.smartdiscover.model.Book;
import com.smartdiscover.model.BookAnalytics;
import com.smartdiscover.repositories.AuthorRepository;
import com.smartdiscover.repositories.BookAnalyticsRepository;
import com.smartdiscover.repositories.BookRepository;
import com.smartdiscover.repositories.ReactiveAuthorRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.client.elc.ReactiveElasticsearchTemplate;
import org.springframework.data.elasticsearch.config.EnableElasticsearchAuditing;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.MultiGetItem;
import org.springframework.data.elasticsearch.core.SearchHitsIterator;
import org.springframework.data.elasticsearch.core.query.Query;
import reactor.core.publisher.Flux;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@SpringBootApplication
@EnableElasticsearchAuditing
public class SpringDataElasticsearchApplication implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(SpringDataElasticsearchApplication.class);

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private AuthorRepository authorRepository;

    @Autowired
    private BookAnalyticsRepository bookAnalyticsRepository;

    @Autowired
    private ElasticsearchOperations elasticsearchOperations;

    @Autowired
    private ReactiveAuthorRepository reactiveAuthorRepository;

    @Autowired
    private ReactiveElasticsearchTemplate reactiveElasticsearchTemplate;

    public static void main(String[] args) {
        SpringApplication.run(SpringDataElasticsearchApplication.class, args);
    }

    @Bean
    AuditorAware<String> auditorProvider() {
        return () -> Optional.of("Admin");
    }

    @Override
    public void run(String... args) throws Exception {
        //cleanup
        bookRepository.deleteAll();
        authorRepository.deleteAll();
        bookAnalyticsRepository.deleteAll();

        // CRUD started

        //create author
        Author andyWeir = new Author();
        andyWeir.setFirstName("Andy");
        andyWeir.setLastName("Weir");
        authorRepository.save(andyWeir);

        //read author
        log.info(String.valueOf(authorRepository.findAll()));


        ArrayList<Author> martianAuthors = new ArrayList<>();
        martianAuthors.add(andyWeir);

        //create book
        Book martian = new Book();
        martian.setName("Martian");
        martian.setSummary("One problem at a time and survive");
        bookRepository.save(martian);

        //read book
        log.info(String.valueOf(bookRepository.findAll()));

        //update book
        martian.setAuthors(martianAuthors);
        bookRepository.save(martian);

        //read book
        log.info(String.valueOf(bookRepository.findAll()));

        //delete author
        //authorRepository.delete(andyWeir);

        //read author
        log.info(String.valueOf(authorRepository.findAll()));

        // CRUD finished

        //elasticsearchOperations

        // Create an Entity
        Author morganHousel = new Author();
        morganHousel.setFirstName("Morgan");
        morganHousel.setLastName("Housel");

        // save
        elasticsearchOperations.save(morganHousel);

        Author jkRowling = new Author();
        jkRowling.setFirstName("Joane");
        jkRowling.setLastName("Rowling");

        // save
        elasticsearchOperations.save(jkRowling);

        // get
        Author found1 = elasticsearchOperations.get(morganHousel.getId(), Author.class);
        log.info(String.valueOf(found1));

        log.info("searchQuery...");
        //search using Query
        Query searchQuery = NativeQuery.builder()
                .withQuery(q -> q.matchAll(ma -> ma))
                .withFields("firstName")
                .build();

        SearchHitsIterator<Author> authorIterator = elasticsearchOperations.searchForStream(searchQuery, Author.class);
        while (authorIterator.hasNext()) {
            log.info(String.valueOf(authorIterator.next()));
        }

        //auditing

        Book zeroToOne = new Book();
        zeroToOne.setName("Zero to One");
        zeroToOne.setSummary("Notes on Startups, or How to Build the Future");
        bookRepository.save(zeroToOne);

        Author peterThiel = new Author();
        peterThiel.setFirstName("Peter");
        peterThiel.setLastName("Thiel");
        authorRepository.save(peterThiel);

        log.info(String.valueOf(bookRepository.findById(zeroToOne.getId())));

        log.info(String.valueOf(authorRepository.findById(peterThiel.getId())));

        log.info(String.valueOf(authorRepository.findAll()));

        //make an update after 5 second
        Thread.sleep(5000);

        //update
        List<Book> peterThielBooks = new ArrayList<>();
        peterThielBooks.add(zeroToOne);

        peterThiel.setBooks(peterThielBooks);
        authorRepository.save(peterThiel);

        log.info(String.valueOf(authorRepository.findById(peterThiel.getId())));

        //reactive elasticsearch

        //create Author object and save using reactiveAuthorRepository
        Author andyWeir1 = new Author();
        andyWeir1.setFirstName("Andy");
        andyWeir1.setLastName("Weir");
        reactiveAuthorRepository.save(andyWeir1).subscribe();

        //find all authors using reactiveAuthorRepository
        log.info("read all authors using ReactiveAuthorRepository");
        String[] ids = {andyWeir1.getId()};
        Flux<Author> authorFlux = reactiveAuthorRepository.findAllById(Arrays.asList(ids));

        //create Author object and save using reactiveElasticsearchOperations
        Author pauloCoelho = new Author();
        pauloCoelho.setFirstName("Paulo");
        pauloCoelho.setLastName("Coelho");

        reactiveElasticsearchTemplate.save(pauloCoelho).doOnNext((x) -> log.info(String.valueOf(x))).subscribe();

        //find all authors using reactiveAuthorRepository
        log.info("read all authors using ReactiveElasticsearchTemplate");

        Query idQuery = NativeQuery.builder()
                .withIds(andyWeir.getId(), morganHousel.getId())
                .build();

        Flux<MultiGetItem<Author>> authorFlux1 = reactiveElasticsearchTemplate.multiGet(idQuery, Author.class);
        authorFlux1.doOnNext(item -> {
            log.info(item.getItem().toString());
        }).subscribe();

        reactiveElasticsearchTemplate.get(andyWeir.getId(), Author.class).doOnNext((x) -> log.info(String.valueOf(x))).subscribe();

        //challenge: implement library reporting
        //1. most popular author
        //2. most popular book

        Book book1 = new Book();
        book1.setName("Atomic Habits");
        book1.setAuthors(List.of(authorRepository.save(new Author("James", "Clear"))));
        bookRepository.save(book1);
        bookAnalyticsRepository.save(new BookAnalytics(book1));

        Book book2 = new Book();
        book2.setName("Harry Potter and the chamber of secrets");
        book2.setAuthors(List.of(authorRepository.save(new Author("Joane", "Rowling"))));
        bookRepository.save(book2);
        bookAnalyticsRepository.save(new BookAnalytics(book2));

        Book book3 = new Book();
        book3.setName("Zero to One");
        book3.setAuthors(Arrays.asList(authorRepository.save(new Author("Blake", "Masters")), authorRepository.save(new Author("Peter", "Theil"))));
        bookRepository.save(book3);
        bookAnalyticsRepository.save(new BookAnalytics(book3));

        Book book4 = new Book();
        book4.setName("Dune");
        book4.setAuthors(List.of(authorRepository.save(new Author("Frank", "Herbert"))));
        bookRepository.save(book4);
        bookAnalyticsRepository.save(new BookAnalytics(book4));

        Book book5 = new Book();
        book5.setName("Sapiens: A Brief History of Humankind");
        book5.setAuthors(List.of(authorRepository.save(new Author("Yuval", "Harari"))));
        bookRepository.save(book5);
        bookAnalyticsRepository.save(new BookAnalytics(book5));

        //update borrowedCount
        updateBorrowedCount(book1.getId());
        updateBorrowedCount(book1.getId());
        updateBorrowedCount(book1.getId());
        updateBorrowedCount(book3.getId());
        updateBorrowedCount(book3.getId());
        updateBorrowedCount(book3.getId());

        updateViewedCount(book1.getId());
        updateViewedCount(book1.getId());
        updateViewedCount(book1.getId());
        updateViewedCount(book1.getId());
        updateViewedCount(book5.getId());

        log.info(String.valueOf(list3PopularBooks()));
        log.info(String.valueOf(listPopularAuthors()));
    }

    public void updateBorrowedCount(String bookId) {
        BookAnalytics bookAnalytics = bookAnalyticsRepository.findByBook_Id(bookId);
        bookAnalytics.incrementBorrowedCount();
        bookAnalyticsRepository.save(bookAnalytics);
    }

    public void updateViewedCount(String bookId) {
        BookAnalytics bookAnalytics = bookAnalyticsRepository.findByBook_Id(bookId);
        bookAnalytics.incrementViewedCount();
        bookAnalyticsRepository.save(bookAnalytics);
    }

    public List<String> list3PopularBooks() {
        return bookAnalyticsRepository.findTop3ByOrderByBorrowedCountDescViewedCountDesc()
                .stream().map(i -> i.getBook().getName()).collect(Collectors.toList());
    }

    public List<String> listPopularAuthors() {
        return bookAnalyticsRepository.findTop3ByOrderByBorrowedCountDescViewedCountDesc()
                .stream().map(i -> i.getBook()).flatMap(j -> j.getAuthors().stream()).map(k -> k.getFullName()).collect(Collectors.toList());
    }

}
