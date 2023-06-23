package com.smartdiscover;

import com.couchbase.client.java.query.QueryScanConsistency;
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
import org.springframework.data.couchbase.core.CouchbaseTemplate;
import org.springframework.data.couchbase.core.ExecutableFindByQueryOperation;
import org.springframework.data.couchbase.core.ReactiveCouchbaseTemplate;
import org.springframework.data.couchbase.core.query.Query;
import org.springframework.data.couchbase.core.query.QueryCriteria;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@SpringBootApplication
//@Transactional
public class SpringDataCouchbaseApplication implements CommandLineRunner {

    public static void main(String[] args) {
        SpringApplication.run(SpringDataCouchbaseApplication.class, args);
    }

    private static final Logger log = LoggerFactory.getLogger(SpringDataCouchbaseApplication.class);

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

    @Override
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

        // Retrieve all authors
        List<Author> authors = couchbaseTemplate.findByQuery(Author.class)
                .consistentWith(QueryScanConsistency.REQUEST_PLUS)
                .all();
        log.info(String.valueOf(authors));


        //transactions

        //reactive couchbase
        log.info("reactive couchbase");

        Author pauloCoelho = new Author();
        pauloCoelho.setFirstName("Paulo");
        pauloCoelho.setLastName("Coelho");
        reactiveAuthorRepository.save(pauloCoelho).subscribe();

        //authorRepository.findAll(QueryScanConsistency.REQUEST_PLUS);

        //reactiveAuthorRepository.findAll().doOnNext(a -> log.info(a.toString())).subscribe();

        reactiveCouchbaseTemplate.findByQuery(Author.class)
                .all().doOnNext(a -> log.info(a.toString())).subscribe();



    }
}
