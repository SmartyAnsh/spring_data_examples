package com.smartdiscover;

import com.smartdiscover.model.Book;
import com.smartdiscover.repository.BookRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.gemfire.config.annotation.ClientCacheApplication;
import org.springframework.data.gemfire.config.annotation.EnableEntityDefinedRegions;
import org.springframework.data.gemfire.repository.config.EnableGemfireRepositories;

@SpringBootApplication
@ClientCacheApplication(subscriptionEnabled = true)
@EnableEntityDefinedRegions(basePackageClasses = Book.class)
@EnableGemfireRepositories(basePackageClasses = BookRepository.class)
public class SpringDataGeodeApplication implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(SpringDataGeodeApplication.class);

    @Autowired
    private BookRepository bookRepository;

    public static void main(String[] args) {
        SpringApplication.run(SpringDataGeodeApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        Book book = new Book();
        book.setId(100L);
        book.setName("Zero to one");
        book.setSummary("Notes on startups, or how to build the future");
        bookRepository.save(book);

        log.info(String.valueOf(bookRepository.findAll()));
    }
}
