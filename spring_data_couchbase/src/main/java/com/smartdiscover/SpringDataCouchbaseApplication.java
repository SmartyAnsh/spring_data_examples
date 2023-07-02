package com.smartdiscover;

import com.couchbase.client.java.query.QueryScanConsistency;
import com.couchbase.transactions.Transactions;
import com.smartdiscover.model.Author;
import com.smartdiscover.model.Book;
import com.smartdiscover.repository.AuthorRepository;
import com.smartdiscover.repository.BookRepository;
import com.smartdiscover.repository.ReactiveAuthorRepository;
import com.smartdiscover.repository.ReactiveBookRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.couchbase.CouchbaseClientFactory;
import org.springframework.data.couchbase.core.CouchbaseTemplate;
import org.springframework.data.couchbase.core.ReactiveCouchbaseTemplate;
import org.springframework.data.couchbase.repository.auditing.EnableCouchbaseAuditing;
import org.springframework.data.domain.AuditorAware;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.springframework.data.couchbase.core.query.QueryCriteria.where;

@SpringBootApplication
@EnableCouchbaseAuditing
public class SpringDataCouchbaseApplication implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(SpringDataCouchbaseApplication.class);

    @Autowired
    Transactions transactions;

    @Autowired
    CouchbaseClientFactory couchbaseClientFactory;

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private AuthorRepository authorRepository;

    @Autowired
    private CouchbaseTemplate couchbaseTemplate;

    @Autowired
    private ReactiveBookRepository reactiveBookRepository;

    @Autowired
    private ReactiveAuthorRepository reactiveAuthorRepository;

    @Autowired
    private ReactiveCouchbaseTemplate reactiveCouchbaseTemplate;

    public static void main(String[] args) {
        SpringApplication.run(SpringDataCouchbaseApplication.class, args);
    }

    @Bean
    AuditorAware<String> auditorProvider() {
        return () -> Optional.of("Admin");
    }

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        //cleanup
        bookRepository.deleteAll();
        authorRepository.deleteAll();

        // CRUD started

        //create author
        Author andyWeir = new Author();
        andyWeir.setFirstName("Andy");
        andyWeir.setLastName("Weir");
        authorRepository.save(andyWeir);

        //read author
        log.info(String.valueOf(authorRepository.findAll(QueryScanConsistency.REQUEST_PLUS)));

        ArrayList<Author> martianAuthors = new ArrayList<>();
        martianAuthors.add(andyWeir);

        //create book
        Book martian = new Book();
        martian.setName("Martian");
        martian.setSummary("One problem at a time and survive");
        bookRepository.save(martian);

        //read book
        log.info(String.valueOf(bookRepository.findAll(QueryScanConsistency.REQUEST_PLUS)));

        //update book
        martian.setAuthors(martianAuthors);
        bookRepository.save(martian);

        //read book
        log.info(String.valueOf(bookRepository.findAll(QueryScanConsistency.REQUEST_PLUS)));

        //delete author
        //authorRepository.delete(andyWeir);

        //read author
        log.info(String.valueOf(authorRepository.findAll(QueryScanConsistency.REQUEST_PLUS)));

        // CRUD finished

        //couchbase supported operations

        // Create an Entity
        Author morganHousel = new Author();
        morganHousel.setFirstName("Morgan");
        morganHousel.setLastName("Housel");

        // Upsert it
        couchbaseTemplate.upsertById(Author.class).one(morganHousel);

        // FindById
        Author found1 = couchbaseTemplate.findById(Author.class).one(morganHousel.getId());
        log.info(String.valueOf(found1));

        //findByQuery
        List<Author> authorList = couchbaseTemplate.findByQuery(Author.class).matching(where("firstName").is("Morgan")).all();
        log.info(String.valueOf(authorList));

        // Retrieve all authors
        List<Author> authors = couchbaseTemplate.findByQuery(Author.class)
                .consistentWith(QueryScanConsistency.REQUEST_PLUS)
                .all();
        log.info(String.valueOf(authors));

        Author jkRowling = new Author();
        jkRowling.setId("jkRowling");
        jkRowling.setFirstName("Joanne");
        jkRowling.setLastName("Rowling");

        //transactions
        transactions.run(ctx -> {
            ctx.insert(couchbaseClientFactory.getDefaultCollection(), jkRowling.getId(), jkRowling);
            ctx.rollback();
        });
        log.info(String.valueOf(couchbaseTemplate.findById(Author.class).one(jkRowling.getId())));

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

        //reactive couchbase

        //create Author object and save using reactiveAuthorRepository
        Author andyWeir1 = new Author();
        andyWeir1.setFirstName("Andy");
        andyWeir1.setLastName("Weir");
        reactiveAuthorRepository.save(andyWeir1).subscribe();

        //find all authors using reactiveAuthorRepository
        log.info("read all authors using ReactiveAuthorRepository");
        String[] ids = {andyWeir1.getId()};
        Flux<Author> authorFlux = reactiveAuthorRepository.findAllById(Arrays.asList(ids));
        authorFlux.subscribe(author1 -> log.info(author1.toString()));

        Author pauloCoelho = new Author();
        pauloCoelho.setFirstName("Paulo");
        pauloCoelho.setLastName("Coelho");
        reactiveCouchbaseTemplate.upsertById(Author.class).one(pauloCoelho).subscribe();

        reactiveCouchbaseTemplate.findByQuery(Author.class)
                .all().doOnNext(a -> log.info(a.toString())).subscribe();

    }
}
