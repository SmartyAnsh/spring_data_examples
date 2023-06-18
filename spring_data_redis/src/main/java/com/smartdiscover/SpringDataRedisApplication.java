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
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.connection.stream.*;
import org.springframework.data.redis.core.*;
import org.springframework.data.redis.hash.DecoratingStringHashMapper;
import org.springframework.data.redis.hash.HashMapper;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.util.*;

@SpringBootApplication
public class SpringDataRedisApplication implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(SpringDataRedisApplication.class);
    @Autowired
    private BookRepository bookRepository;
    @Autowired
    private AuthorRepository authorRepository;
    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private ReactiveRedisTemplate reactiveRedisTemplate;

    public static void main(String[] args) {
        SpringApplication.run(SpringDataRedisApplication.class, args);
    }

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

        //set the String serializer for key and value
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        redisTemplate.setValueSerializer(new StringRedisSerializer());

        //string operations
        redisTemplate.opsForValue().set("Book", "Author");

        log.info(String.valueOf(redisTemplate.hasKey("Book")));
        log.info(String.valueOf(redisTemplate.opsForValue().get("Book")));

        //list operator
        ListOperations redisListOperator = redisTemplate.opsForList();

        //list operations
        redisListOperator.rightPush("BookList", "Atomic Habits");
        redisListOperator.rightPush("BookList", "Martian");
        redisListOperator.rightPush("BookList", "The Psychology Of Money");
        redisListOperator.rightPush("BookList", "Zero To One");

        log.info(String.valueOf(redisTemplate.hasKey("BookList")));
        log.info(String.valueOf(redisListOperator.size("BookList")));
        log.info(String.valueOf(redisListOperator.index("BookList", 1)));
        log.info(String.valueOf(redisListOperator.range("BookList", 2, 3)));

        redisListOperator.set("BookList", 1, "Project Hail Mary");
        log.info(String.valueOf(redisListOperator.index("BookList", 1)));

        //set operator
        SetOperations redisSetOperator = redisTemplate.opsForSet();

        //set operations
        redisSetOperator.add("BookSet", "Atomic Habits", "Martian", "The Psychology Of Money", "Leaders eat last");

        log.info(String.valueOf(redisSetOperator.size("BookSet")));

        log.info(String.valueOf(redisSetOperator.isMember("BookSet", "Martian")));
        log.info(String.valueOf(redisSetOperator.isMember("BookSet", "Zero To One")));

        log.info(redisSetOperator.members("BookSet").toString());

        redisSetOperator.remove("BookSet", "Martian");

        log.info(redisSetOperator.members("BookSet").toString());

        //set the String serializer for Hash key and value
        redisTemplate.setHashKeySerializer(new StringRedisSerializer());
        redisTemplate.setHashValueSerializer(new StringRedisSerializer());

        //Hash operator
        HashOperations redisHashOperator = redisTemplate.opsForHash();

        //Hash operations
        Map thePsychologyOfMoney = new HashMap<String, String>();
        thePsychologyOfMoney.put("id", "thePsychologyOfMoney");
        thePsychologyOfMoney.put("name", "The Psychology of Money");
        thePsychologyOfMoney.put("summary", "Timeless Lessons on Wealth, Greed, and Happiness");

        redisHashOperator.putAll("Book" + thePsychologyOfMoney.get("id"), thePsychologyOfMoney);

        log.info(String.valueOf(redisHashOperator.entries("Book" + thePsychologyOfMoney.get("id"))));

        HashMapper<Book, String, String> bookHashMapper = new DecoratingStringHashMapper<>(new PojoHashMapper<>(Book.class));
        log.info(String.valueOf(bookHashMapper.fromHash(redisHashOperator.entries("Book" + thePsychologyOfMoney.get("id")))));

        //Redis Streams

        String streamKey = "bookStream";
        String consumerGroupKey = "educativeGroup";
        String consumerNameKey = "educativeConsumer";
        List<String> books = Arrays.asList("Martian", "Atomic Habits", "The Psychology Of Money", "Project Hail Mary", "Zero To One");

        StreamOperations redisStreamOperator = redisTemplate.opsForStream();

        for (int i = 0; i < books.size(); i++) {
            Map bookMap = new HashMap<String, String>();
            bookMap.put("book", books.get(i));

            //appending
            StringRecord record = StreamRecords.string(bookMap).withStreamKey(streamKey);
            redisStreamOperator.add(record);
        }

        Consumer educativeConsumer = Consumer.from(consumerGroupKey, consumerNameKey);
        StreamReadOptions readOptions = StreamReadOptions.empty();
        StreamOffset streamOffset = StreamOffset.create(streamKey, ReadOffset.lastConsumed());
        ReadOffset readOffset = ReadOffset.from("0-0");

        //create group if it doesn't exist
        if (redisStreamOperator.groups(streamKey).isEmpty()) {
            redisStreamOperator.createGroup(streamKey, readOffset, consumerGroupKey);
        }

        //synchronous read
        List<MapRecord> messages = redisStreamOperator.read(educativeConsumer, readOptions.count(2), streamOffset);
        log.info(String.valueOf(messages));

        //acknowledge the message
        messages.forEach(map -> {
            redisStreamOperator.acknowledge(streamKey, consumerGroupKey, map.getId());
        });

        //read again from the last consumed
        messages = redisStreamOperator.read(educativeConsumer, readOptions.count(2), streamOffset);
        log.info(String.valueOf(messages));

        //Redis Transactions
        log.info("starting Redis transactions...");
        redisTemplate.setEnableTransactionSupport(true);

        RedisOperations<String, String> redisOperations = redisTemplate.opsForValue().getOperations();
        //execute a transaction
        SessionCallback<Object> sessionCallback = new SessionCallback<Object>() {
            @Override
            public Object execute(RedisOperations operations) throws DataAccessException {

                //invoke redis multi command
                operations.multi();

                ValueOperations<String, String> valueOps = operations.opsForValue();
                valueOps.set("book1", "Martian");
                valueOps.set("book2", "Atomic Habits");
                valueOps.increment("book3", 1);

                //invoke redis exec command
                return operations.exec();
            }
        };

        redisOperations.execute(sessionCallback);
        log.info(String.valueOf(redisOperations.opsForValue().get("book1")));
        log.info(String.valueOf(redisOperations.opsForValue().get("book2")));
        log.info(String.valueOf(redisOperations.opsForValue().get("book3")));

        //Reactive Redis

        reactiveRedisTemplate.opsForValue().set("Netherlands", "Amsterdam").subscribe();

        reactiveRedisTemplate.hasKey("Netherlands").doOnNext(a -> log.info(a.toString())).subscribe();
        reactiveRedisTemplate.opsForValue().get("Netherlands").doOnNext(a -> log.info(a.toString())).subscribe();
    }
}
