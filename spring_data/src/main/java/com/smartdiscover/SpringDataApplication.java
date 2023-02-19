package com.smartdiscover;

import com.smartdiscover.entity.Person;
import com.smartdiscover.repository.PersonRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class SpringDataApplication implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(SpringDataApplication.class);

    @Autowired
    private PersonRepository personRepository;

    public static void main(String[] args) {
        SpringApplication.run(SpringDataApplication.class, args);
    }

    @Override
    public void run(String... strings) throws Exception {
        Person person = new Person();
        person.setFirstName("Anshul");
        person.setLastName("Bansal");
        personRepository.save(person);

        log.info(String.valueOf(personRepository.findAll()));
    }

}
