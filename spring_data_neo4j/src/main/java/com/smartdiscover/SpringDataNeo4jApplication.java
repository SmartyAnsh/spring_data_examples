package com.smartdiscover;

import com.smartdiscover.model.Author;
import com.smartdiscover.model.Book;
import com.smartdiscover.model.BookRating;
import com.smartdiscover.model.Genre;
import com.smartdiscover.model.Genre.GenreText;
import com.smartdiscover.repository.*;
import org.neo4j.driver.Driver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.transaction.TransactionManagerCustomizers;
import org.springframework.context.annotation.Bean;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.neo4j.config.EnableNeo4jAuditing;
import org.springframework.data.neo4j.core.DatabaseSelectionProvider;
import org.springframework.data.neo4j.core.Neo4jTemplate;
import org.springframework.data.neo4j.core.ReactiveDatabaseSelectionProvider;
import org.springframework.data.neo4j.core.ReactiveNeo4jTemplate;
import org.springframework.data.neo4j.core.transaction.Neo4jTransactionManager;
import org.springframework.data.neo4j.core.transaction.ReactiveNeo4jTransactionManager;
import org.springframework.data.neo4j.repository.config.Neo4jRepositoryConfigurationExtension;
import org.springframework.data.neo4j.repository.config.ReactiveNeo4jRepositoryConfigurationExtension;
import reactor.core.publisher.Flux;

import java.util.*;
import java.util.stream.Collectors;

import static com.smartdiscover.model.Genre.GenreText.*;

@SpringBootApplication
@EnableNeo4jAuditing
public class SpringDataNeo4jApplication implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(SpringDataNeo4jApplication.class);

    @Autowired
    private AuthorRepository authorRepository;

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private BookRatingRepository bookRatingRepository;

    @Autowired
    private GenreRepository genreRepository;

    @Autowired
    private Neo4jTemplate neo4jTemplate;

    @Autowired
    private ReactiveAuthorRepository reactiveAuthorRepository;

    @Autowired
    private ReactiveBookRepository reactiveBookRepository;

    @Autowired
    private ReactiveNeo4jTemplate reactiveNeo4jTemplate;

    public static void main(String[] args) {
        SpringApplication.run(SpringDataNeo4jApplication.class, args);
    }

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

    @Override
    public void run(String... args) throws Exception {
        //cleanup
        authorRepository.deleteAll();
        bookRepository.deleteAll();
        bookRatingRepository.deleteAll();
        genreRepository.deleteAll();

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

        //bootstrap data

        //create some genres

        Genre scienceFiction = genreRepository.save(new Genre(SCIENCE_FICTION));
        Genre fiction = genreRepository.save(new Genre(FICTION));
        Genre thriller = genreRepository.save(new Genre(THRILLER));
        Genre fantasy = genreRepository.save(new Genre(FANTASY));
        Genre nonFiction = genreRepository.save(new Genre(NONFICTION));
        Genre selfHelp = genreRepository.save(new Genre(SELF_HELP));
        Genre adventure = genreRepository.save(new Genre(ADVENTURE));
        Genre biography = genreRepository.save(new Genre(BIOGRAPHY));

        //create 5 books with different genres and ratings

        Book book1 = new Book();
        book1.setName("Martian");
        book1.setSummary("One problem at a time and survive");
        book1.setGenres(Arrays.asList(scienceFiction, fiction, thriller));
        book1.setRatings(Arrays.asList(new BookRating(8), new BookRating(7), new BookRating(9), new BookRating(9), new BookRating(10)));
        bookRepository.save(book1);

        Book book2 = new Book();
        book2.setName("Dune");
        book2.setSummary("Set on the desert planet Arrakis, Dune is the story of the boy Paul");
        book2.setGenres(Arrays.asList(scienceFiction, fiction));
        book2.setRatings(Arrays.asList(new BookRating(9), new BookRating(7), new BookRating(6), new BookRating(9), new BookRating(8)));
        bookRepository.save(book2);

        Book book3 = new Book();
        book3.setName("Harry Potter and the Prisoner of Azkaban");
        book3.setSummary("Harry Potter, along with his best friends, Ron and Hermione, is about to start his third year at Hogwarts School");
        book3.setGenres(Arrays.asList(adventure, fiction, fantasy));
        book3.setRatings(Arrays.asList(new BookRating(7), new BookRating(7), new BookRating(6), new BookRating(9), new BookRating(8)));
        bookRepository.save(book3);

        Book book4 = new Book();
        book4.setName("Atomic Habits");
        book4.setSummary("An Easy & Proven Way to Build Good Habits & Break Bad Ones");
        book4.setGenres(Arrays.asList(selfHelp, nonFiction));
        book4.setRatings(Arrays.asList(new BookRating(9), new BookRating(7), new BookRating(7), new BookRating(9), new BookRating(8)));
        bookRepository.save(book4);

        Book book5 = new Book();
        book5.setName("Will");
        book5.setSummary("One of the most dynamic and globally recognized entertainment forces of our time");
        book5.setGenres(Arrays.asList(biography, nonFiction));
        book5.setRatings(Arrays.asList(new BookRating(5), new BookRating(7), new BookRating(8), new BookRating(9), new BookRating(8)));
        bookRepository.save(book5);

        // recommendBook
        log.info(String.valueOf(recommendBook(SCIENCE_FICTION)));
        log.info(String.valueOf(recommendBook(SELF_HELP)));
        log.info(String.valueOf(recommendBook(THRILLER)));
        log.info(String.valueOf(recommendBook(FICTION)));
        log.info(String.valueOf(recommendBook(NONFICTION)));
    }

    public List<String> recommendBook(GenreText genre) {
        List<Book> books = bookRepository.findAllByGenres_Id(genreRepository.findByGenreText(genre.toString()).getId());

        books.sort(Comparator.comparingDouble(Book::getAverageRating).reversed());
        return books.stream().map(i -> i.getName() + ":" + i.getAverageRating()).collect(Collectors.toList());
    }
}
