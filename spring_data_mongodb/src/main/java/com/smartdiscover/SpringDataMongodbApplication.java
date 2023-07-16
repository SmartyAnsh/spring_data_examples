package com.smartdiscover;

import com.mongodb.ClientSessionOptions;
import com.mongodb.client.MongoClient;
import com.mongodb.session.ClientSession;
import com.smartdiscover.document.Author;
import com.smartdiscover.document.Book;
import com.smartdiscover.document.BookLoanEntry;
import com.smartdiscover.document.FinePolicy;
import com.smartdiscover.repository.*;
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
import org.springframework.data.mongodb.MongoTransactionManager;
import org.springframework.data.mongodb.config.EnableMongoAuditing;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.ReactiveMongoOperations;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.query.BasicQuery;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import org.springframework.transaction.support.TransactionTemplate;
import reactor.core.publisher.Flux;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;
import java.util.concurrent.TimeUnit;

import static org.springframework.data.domain.ExampleMatcher.GenericPropertyMatchers.endsWith;
import static org.springframework.data.domain.ExampleMatcher.GenericPropertyMatchers.startsWith;
import static org.springframework.data.mongodb.SessionSynchronization.ALWAYS;
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
    private BookLoanEntryRepository bookLoanEntryRepository;

    @Autowired
    private FinePolicyRepository finePolicyRepository;

    @Autowired
    private ReactiveAuthorRepository reactiveAuthorRepository;

    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private MongoClient mongoClient;

    @Autowired
    private ReactiveMongoTemplate reactiveMongoTemplate;

    @Autowired
    private ReactiveMongoOperations reactiveMongoOperations;

    @Autowired
    private MongoTransactionManager mongoTransactionManager;

    @Bean
    public AuditorAware<String> auditorProvider() {
        return () -> Optional.of("Administrator");
    }

    @Override
    //@Transactional
    public void run(String... args) throws Exception {
        //cleanup
        bookRepository.deleteAll();
        authorRepository.deleteAll();
        finePolicyRepository.deleteAll();

        // CRUD started
        Author author = new Author();
        author.setFirstName("Andy");
        author.setLastName("Weir");
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

        log.info("Querying documents using MongoTemplate");
        //Using Query
        Query query = query(where("firstName").is("Morgan"));
        Author morgan = mongoTemplate.findOne(query, Author.class);
        log.info(String.valueOf(morgan));

        //Using BasicQuery
        BasicQuery basicQuery = new BasicQuery("{ name : { $eq : 'Martian' }, dateCreated: {$gt: ISODate(\"2023-03-06\")}}");
        List<Book> martian = mongoTemplate.find(basicQuery, Book.class);
        log.info(String.valueOf(martian));

        //Using Criteria
        Criteria c = where("firstName").is("Andy").and("lastName").is("Weir");
        List<Author> authorList = mongoTemplate.query(Author.class)
                .matching(query(c))
                .all();
        log.info(String.valueOf(authorList));

        List<String> authorLastNames = mongoTemplate.query(Author.class)
                .distinct("lastName")
                .as(String.class)
                .all();
        log.info(String.valueOf(authorLastNames));

        log.info(String.valueOf(mongoTemplate.count(query(c), Author.class)));

        //using mongoRepository
        log.info("Querying documents using MongoRepository");
        log.info(String.valueOf(bookRepository.findFirstByName("Martian")));
        log.info(String.valueOf(bookRepository.findAllByName("Martian")));

        //Using Example
        ExampleMatcher matcher = ExampleMatcher.matching()
                .withMatcher("firstName", endsWith())
                .withMatcher("lastName", startsWith().ignoreCase());

        Example<Author> example = Example.of(author, matcher);

        log.info(String.valueOf(authorRepository.findAll(example)));

        //Mongo Sessions

        ClientSessionOptions sessionOptions = ClientSessionOptions.builder().causallyConsistent(true).build();

        ClientSession session = mongoClient.startSession(sessionOptions);

        mongoTemplate.withSession(() -> (com.mongodb.client.ClientSession) session).execute(action -> {

            Query query1 = query(where("firstName").is("Andy"));
            Author andyWeir = action.findOne(query1, Author.class);

            List<Author> projectHailMaryAuthors = new ArrayList<>();
            projectHailMaryAuthors.add(andyWeir);

            Book projectHailMary = new Book();
            projectHailMary.setName("Project Hail Mary");
            projectHailMary.setSummary("It's a simple idea, but also stupid. Thing is, when stupid ideas work, they become genius ideas. We'll see which way this one falls.");
            projectHailMary.setAuthors(projectHailMaryAuthors);

            action.insert(projectHailMary);
            return projectHailMary;
        });

        session.close();

        //Mongo Transactions

        /*for transactional start mongodb with --replSet "rs0"
        example: /usr/bin/mongod --fork --logpath /var/log/mongodb/mongod.log --replSet "rs0" --bind_ip localhost
        then initiate replica using mongo shell
        example /usr/bin/mongosh - rs.initiate()*/

        /*
        ClientSession session1 = mongoClient.startSession(sessionOptions);
        mongoTemplate.withSession(() -> (com.mongodb.client.ClientSession) session1).execute(action -> {

            ((com.mongodb.client.ClientSession) session1).startTransaction();
            Book zeroToOne = new Book();
            try {
                zeroToOne.setName("Zero to One");
                zeroToOne.setSummary("Notes on Startups, or How to Build the Future");
                action.insert(zeroToOne);

                action.updateFirst(query(where("name").is("Zero to One")), Update.update("summary", "Notes on Startups"), Book.class);
                ((com.mongodb.client.ClientSession) session1).commitTransaction();

            } catch (RuntimeException e) {
                log.error("something wrong...", e);
                ((com.mongodb.client.ClientSession) session1).abortTransaction();
            }

            return zeroToOne;

        }, ClientSession::close);

        log.info(String.valueOf(bookRepository.findAll()));

        mongoTemplate.setSessionSynchronization(ALWAYS);
        TransactionTemplate txTemplate = new TransactionTemplate(mongoTransactionManager);

        txTemplate.execute(new TransactionCallbackWithoutResult() {

            @Override
            protected void doInTransactionWithoutResult(TransactionStatus status) {
                Book zeroToOne = new Book();
                zeroToOne.setName("Zero to One");
                zeroToOne.setSummary("Notes on Startups, or How to Build the Future");
                mongoTemplate.insert(zeroToOne);
                mongoTemplate.updateFirst(query(where("name").is("Zero to One")), Update.update("summary", "Notes on Startups"), Book.class);
            }

            ;
        });*/

        // Reactive
        Flux<Author> authorFlux = reactiveAuthorRepository.findAll();

        authorFlux.subscribe(author1 -> log.info(author1.toString()));

        //reactiveMongoTemplate
        Author ryanHoliday = new Author();
        ryanHoliday.setFirstName("Ryan");
        ryanHoliday.setLastName("Holiday");
        reactiveMongoTemplate.insert(ryanHoliday).subscribe();

        reactiveMongoTemplate.findOne(new Query(where("firstName").is("Ryan")), Author.class)
                .doOnNext(a -> log.info(a.toString()))
                .subscribe();


        //reactiveMongoOperations
        Author carmineGallo = new Author();
        carmineGallo.setFirstName("Carmine");
        carmineGallo.setLastName("Gallo");
        reactiveMongoOperations.insert(carmineGallo).subscribe();

        reactiveMongoOperations.findOne(query(where("firstName").is("Carmine")), Author.class)
                .doOnNext(a -> log.info(a.toString()))
                .subscribe();

        //Mongo auditing
        Book thinkAgain = new Book();
        thinkAgain.setName("Think Again");
        thinkAgain.setSummary("The Power of Knowing What You Don't Know");
        bookRepository.save(thinkAgain);

        //create
        Author adamGrant = new Author();
        adamGrant.setFirstName("Adam");
        adamGrant.setLastName("Grant");
        authorRepository.save(adamGrant);

        log.info(String.valueOf(authorRepository.findAll()));

        //make an update after 5 second
        Thread.sleep(5000);

        //update
        List<Book> adamGrantBooks = new ArrayList<>();
        adamGrantBooks.add(thinkAgain);

        adamGrant.setBooks(adamGrantBooks);
        authorRepository.save(adamGrant);

        log.info(String.valueOf(authorRepository.findAll()));

        //bootstrap fine policy
        FinePolicy finePolicy = new FinePolicy();
        finePolicy.setDailyFineRate(10);
        finePolicy.setMaxFineAmount(100);
        finePolicy.setActive(true);
        finePolicyRepository.save(finePolicy);

        //book loan system

        //loan book
        BookLoanEntry bookLoanEntry = loanBook();
        log.info(String.valueOf(bookLoanEntry));

        //return book
        ZoneId defaultZoneId = ZoneId.systemDefault();
        Date returnDate = Date.from(LocalDate.now().plusDays(20).atStartOfDay(defaultZoneId).toInstant());
        bookLoanEntry = returnBook(bookLoanEntry.getId(), returnDate);

        //calculate fine
        double fineAmount = calculateFine(bookLoanEntry);
        log.info(String.valueOf(fineAmount));

    }

    @Transactional
    public BookLoanEntry loanBook() {
        //default time zone
        ZoneId defaultZoneId = ZoneId.systemDefault();

        //search book
        String bookName = "Martian";
        Book book = bookRepository.findByNameAndAvailableIsNullOrAvailableIsTrue(bookName);

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
    public BookLoanEntry returnBook(String bookLoanEntryId, Date returnDate) {
        //fetch the loan entry
        BookLoanEntry bookLoanEntry = bookLoanEntryRepository.findById(bookLoanEntryId).get();

        //update the loan entry
        bookLoanEntry.setReturnDate(returnDate);
        bookLoanEntry.setStatus("returned");

        bookLoanEntryRepository.save(bookLoanEntry);

        //update the book availability
        Book book = bookLoanEntry.getBook();
        book.setAvailable(true);
        bookRepository.save(book);

        return bookLoanEntry;
    }

    public double calculateFine(BookLoanEntry bookLoanEntry) {
        if (bookLoanEntry.getReturnDate().before(bookLoanEntry.getDueDate())) {
            return new Double("0");
        } else {
            FinePolicy finePolicy = finePolicyRepository.findByActive(true);
            long diffInMilliseconds = Math.abs(bookLoanEntry.getReturnDate().getTime() - bookLoanEntry.getDueDate().getTime());
            long overDueDays = TimeUnit.DAYS.convert(diffInMilliseconds, TimeUnit.MILLISECONDS);

            double totalFine = overDueDays * finePolicy.getDailyFineRate();
            return (totalFine < finePolicy.getMaxFineAmount()) ? totalFine : finePolicy.getMaxFineAmount();
        }
    }

}
