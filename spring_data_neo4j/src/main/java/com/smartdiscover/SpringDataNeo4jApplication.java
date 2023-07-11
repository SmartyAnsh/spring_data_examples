package com.smartdiscover;

import com.smartdiscover.model.Author;
import com.smartdiscover.model.Book;
import com.smartdiscover.repository.AuthorRepository;
import com.smartdiscover.repository.BookRepository;
import com.smartdiscover.repository.ReactiveAuthorRepository;
import com.smartdiscover.repository.ReactiveBookRepository;
import org.neo4j.cypherdsl.core.Cypher;
import org.neo4j.cypherdsl.core.Node;
import org.neo4j.cypherdsl.core.Property;
import org.neo4j.driver.Driver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.transaction.TransactionManagerCustomizers;
import org.springframework.context.annotation.Bean;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.neo4j.config.EnableNeo4jAuditing;
import org.springframework.data.neo4j.core.*;
import org.springframework.data.neo4j.core.transaction.Neo4jTransactionManager;
import org.springframework.data.neo4j.core.transaction.ReactiveNeo4jTransactionManager;
import org.springframework.data.neo4j.repository.config.Neo4jRepositoryConfigurationExtension;
import org.springframework.data.neo4j.repository.config.ReactiveNeo4jRepositoryConfigurationExtension;
import org.springframework.data.neo4j.repository.query.Query;
import org.springframework.transaction.ReactiveTransactionManager;
import org.springframework.transaction.TransactionManager;
import reactor.core.publisher.Flux;

import java.util.*;

@SpringBootApplication
@EnableNeo4jAuditing
public class SpringDataNeo4jApplication implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(SpringDataNeo4jApplication.class);

    @Autowired
    private AuthorRepository authorRepository;

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private Neo4jTemplate neo4jTemplate;

    @Autowired
    private ReactiveAuthorRepository reactiveAuthorRepository;

    @Autowired
    private ReactiveBookRepository reactiveBookRepository;

    @Autowired
    private ReactiveNeo4jTemplate reactiveNeo4jTemplate;

    @Bean(ReactiveNeo4jRepositoryConfigurationExtension.DEFAULT_TRANSACTION_MANAGER_BEAN_NAME)
    public ReactiveNeo4jTransactionManager reactiveTransactionManager(
            Driver driver,
            ReactiveDatabaseSelectionProvider databaseNameProvider) {
        return new ReactiveNeo4jTransactionManager(driver, databaseNameProvider);
    }

    @Bean(Neo4jRepositoryConfigurationExtension.DEFAULT_TRANSACTION_MANAGER_BEAN_NAME)
    public Neo4jTransactionManager transactionManager(Driver driver, DatabaseSelectionProvider databaseNameProvider,
                                                      ObjectProvider<TransactionManagerCustomizers> optionalCustomizers) {
        Neo4jTransactionManager transactionManager = new Neo4jTransactionManager(driver, databaseNameProvider);
        optionalCustomizers.ifAvailable((customizer) -> customizer.customize(transactionManager));
        return transactionManager;
    }

    @Bean
    AuditorAware<String> auditorProvider() {
        return () -> Optional.of("Admin");
    }

    public static void main(String[] args) {
        SpringApplication.run(SpringDataNeo4jApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        //cleanup
        authorRepository.deleteAll();
        bookRepository.deleteAll();

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

        //neo4jTemplate operations

        // Create an Entity
        Author morganHousel = new Author();
        morganHousel.setFirstName("Morgan");
        morganHousel.setLastName("Housel");

        // save
        neo4jTemplate.save(morganHousel);

        Author jkRowling = new Author();
        jkRowling.setFirstName("Joane");
        jkRowling.setLastName("Rowling");

        // save
        neo4jTemplate.save(jkRowling);

        // get
        Optional<Author> found1 = neo4jTemplate.findById(morganHousel.getId(), Author.class);
        log.info(String.valueOf(found1.get()));

        //find all
        neo4jTemplate.findAll(Author.class).forEach(author -> log.info(String.valueOf(author)));

        //search using Query

        String cypherQuery = "MATCH (a:Author) WHERE a.firstName = $firstName RETURN a";
        log.info(String.valueOf(neo4jTemplate.findAll(cypherQuery, Map.of("firstName", "Andy"), Author.class)));

        //reactive

        //create Author object and save using reactiveAuthorRepository
        Author andyWeir1 = new Author();
        andyWeir1.setFirstName("Andy");
        andyWeir1.setLastName("Weir");
        reactiveAuthorRepository.save(andyWeir1).doOnNext(author -> log.info(String.valueOf(author))).subscribe();

        reactiveAuthorRepository.findAll().doOnNext(author -> log.info(String.valueOf(author))).subscribe();

        //find all authors using reactiveAuthorRepository
        log.info("read all authors using ReactiveAuthorRepository");
        String[] ids = {andyWeir1.getId()};
        Flux<Author> authorFlux = reactiveAuthorRepository.findAllById(Arrays.asList(ids));
        authorFlux.subscribe(author1 -> log.info(author1.toString()));

        Author pauloCoelho = new Author();
        pauloCoelho.setFirstName("Paulo");
        pauloCoelho.setLastName("Coelho");
        reactiveNeo4jTemplate.save(Author.class).one(pauloCoelho).subscribe();

        reactiveNeo4jTemplate.find(Author.class)
                .all().doOnNext(a -> log.info(a.toString())).subscribe();

    }
}
