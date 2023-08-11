package com.smartdiscover;

import com.smartdiscover.model.Author;
import com.smartdiscover.model.Book;
import com.smartdiscover.repositories.AuthorRepository;
import com.smartdiscover.repositories.BookRepository;
import com.smartdiscover.repositories.ReactiveAuthorRepository;
import com.smartdiscover.repositories.ReactiveBookRepository;
import org.elasticsearch.client.RestClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.client.elc.ReactiveElasticsearchClient;
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

@SpringBootApplication
@EnableElasticsearchAuditing
public class SpringDataElasticsearchApplication implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(SpringDataElasticsearchApplication.class);

    @Autowired
    private ElasticsearchOperations elasticsearchOperations;

    @Autowired
    private ReactiveElasticsearchTemplate reactiveElasticsearchTemplate;

    @Autowired
    private ReactiveElasticsearchClient elasticsearchClient;

    @Autowired
    private RestClient restClient;

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private AuthorRepository authorRepository;

    @Autowired
    private ReactiveBookRepository reactiveBookRepository;

    @Autowired
    private ReactiveAuthorRepository reactiveAuthorRepository;

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
    }
}
