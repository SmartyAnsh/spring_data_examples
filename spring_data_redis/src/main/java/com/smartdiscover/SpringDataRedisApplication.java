package com.smartdiscover;

import com.smartdiscover.model.Author;
import com.smartdiscover.model.Book;
import com.smartdiscover.repository.AuthorRepository;
import com.smartdiscover.repository.BookRepository;
import com.smartdiscover.util.PojoHashMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.hash.DecoratingStringHashMapper;
import org.springframework.data.redis.hash.HashMapper;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

@SpringBootApplication
public class SpringDataRedisApplication implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(SpringDataRedisApplication.class);

    public static void main(String[] args) {
        SpringApplication.run(SpringDataRedisApplication.class, args);
    }

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private AuthorRepository authorRepository;

    @Autowired
    private RedisTemplate redisTemplate;

    @Override
    //@Transactional
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

        //redisTemplate
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        redisTemplate.setValueSerializer(new StringRedisSerializer());

        //string operations
        redisTemplate.opsForValue().set("Romania", "Bucharest");

        log.info(String.valueOf(redisTemplate.hasKey("Romania")));
        log.info(String.valueOf(redisTemplate.opsForValue().get("Romania")));

        //list operations

        ListOperations redisListOperator = redisTemplate.opsForList();

        redisListOperator.rightPush("India", "Delhi");
        redisListOperator.rightPush("India", "Mumbai");
        redisListOperator.rightPush("India", "Chennai");
        redisListOperator.rightPush("India", "Kolkata");

        log.info(String.valueOf(redisTemplate.hasKey("India")));
        log.info(String.valueOf(redisListOperator.size("India")));
        log.info(String.valueOf(redisListOperator.index("India", 1)));

        redisListOperator.set("India", 1, "Bangalore");
        log.info(String.valueOf(redisListOperator.index("India", 1)));

        //hash operations
        redisTemplate.setHashKeySerializer(new StringRedisSerializer());
        redisTemplate.setHashValueSerializer(new StringRedisSerializer());

        HashOperations redisHashOperator = redisTemplate.opsForHash();

        Map thePsychologyOfMoney = new HashMap<String, String>();
        thePsychologyOfMoney.put("id", "thePsychologyOfMoney");
        thePsychologyOfMoney.put("name", "The Psychology of Money");
        thePsychologyOfMoney.put("summary", "Timeless Lessons on Wealth, Greed, and Happiness");

        redisHashOperator.putAll("Book" + thePsychologyOfMoney.get("id"), thePsychologyOfMoney);

        log.info(String.valueOf(redisHashOperator.entries("Book" + thePsychologyOfMoney.get("id"))));

        HashMapper<Book, String, String> bookHashMapper = new DecoratingStringHashMapper<>(new PojoHashMapper<>(Book.class));
        log.info(String.valueOf(bookHashMapper.fromHash(redisHashOperator.entries("Book" + thePsychologyOfMoney.get("id")))));

        //Redis Streams

        //Redis Transactions

        //Reactive Redis
    }
}
