package com.smartdiscover;

import com.mongodb.reactivestreams.client.MongoClients;
import com.smartdiscover.document.Author;
import com.smartdiscover.document.Book;
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
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.mongodb.config.EnableMongoAuditing;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.query.BasicQuery;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CountDownLatch;

import static org.springframework.data.domain.ExampleMatcher.GenericPropertyMatchers.endsWith;
import static org.springframework.data.domain.ExampleMatcher.GenericPropertyMatchers.startsWith;
import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;

@SpringBootApplication
@EnableMongoAuditing
public class SpringDataMongodbApplication implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(SpringDataMongodbApplication.class);

    public static void main(String[] args) {
        SpringApplication.run(SpringDataMongodbApplication.class, args);
    }

    @Autowired
    private AuthorRepository authorRepository;

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private MongoOperations mongoOperations;

    @Bean
    public AuditorAware<String> auditorProvider() {
        return () -> Optional.of("Administrator");
    }

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        log.info(String.valueOf(bookRepository.findAll()));

        // CRUD started
        Author author = new Author();
        author.setFirstName("Andy123");
        author.setLastName("Weir123");
        authorRepository.save(author);

        log.info(String.valueOf(authorRepository.findAll()));

        ArrayList<Author> authors = new ArrayList<>();
        authors.add(author);

        Book book = new Book();
        book.setName("Martian");
        book.setSummary("One problem at a time and survive");
        book.setAuthors(authors);
        bookRepository.save(book);

        ArrayList<Book> books = new ArrayList<>();
        books.add(book);

        author.setBooks(books);
        authorRepository.save(author);

        log.info(String.valueOf(bookRepository.findAll()));

        //authorRepository.delete(author);

        // CRUD finished

        // Querying documents

        /*BasicQuery query = new BasicQuery("{ name : { $eq : 'Martian' }, dateCreated: {$gt: ISODate(\"2023-03-06\")}}");
        List<Book> result = mongoTemplate.find(query, Book.class);

        log.info(String.valueOf(result));

        List<Author> result = mongoTemplate.query(Author.class)
                .matching(query(where("firstName").is("Andy").and("lastName").is("Weir")))
                .all();
        log.info(String.valueOf(result));

        List<String> result = mongoTemplate.query(Author.class)
                .distinct("lastName")
                .as(String.class)
                .all();
        log.info(String.valueOf(result));*/

        /*author.setId(null);
        author.setFirstName("Something");


        mongoOperations.insert(author);
        log.info(String.valueOf(mongoOperations.count(query(where("firstName").is("Andy")), Author.class)));

        ExampleMatcher matcher = ExampleMatcher.matching()
                .withMatcher("firstName", endsWith())
                .withMatcher("lastName", startsWith().ignoreCase());

        Example<Author> example = Example.of(author, matcher);

        log.info("Example");

        log.info(String.valueOf(authorRepository.findAll(example)));*/

        // Reactive

        CountDownLatch latch = new CountDownLatch(1);

        ReactiveMongoTemplate mongoOps = new ReactiveMongoTemplate(MongoClients.create(), "spring_data_mongodb");

        mongoOps.insert(new Author("Anshul", "Bansal"))
                .flatMap(p -> mongoOps.findOne(new Query(where("name").is("Joe")), Author.class))
                .doOnNext(a -> log.info(a.toString()))
                .subscribe();

        latch.await();
    }

}
