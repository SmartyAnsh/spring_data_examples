package com.smartdiscover;

import com.smartdiscover.entity.Person;
import com.smartdiscover.repository.CustomPersonRepository;
import com.smartdiscover.repository.CustomPersonRepositoryImpl;
import com.smartdiscover.repository.PersonRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.transaction.annotation.Transactional;

import java.util.stream.Collectors;

@SpringBootApplication
public class SpringDataApplication implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(SpringDataApplication.class);

    @Autowired
    private PersonRepository personRepository;

    public static void main(String[] args) {
        SpringApplication.run(SpringDataApplication.class, args);
    }

    //manual wiring
    @Bean
    CustomPersonRepository getCustomPersonRepository() {
        return new CustomPersonRepositoryImpl();
    }

    @Override
    @Transactional
    public void run(String... strings) throws Exception {
        Person person1 = new Person();
        person1.setFirstName("Anshul");
        person1.setLastName("Bansal");
        personRepository.save(person1);

        Person person2 = new Person();
        person2.setFirstName("John");
        person2.setLastName("Smith");
        personRepository.save(person2);

        Person person3 = new Person();
        person3.setFirstName("Harry");
        person3.setLastName("Potter");
        personRepository.save(person3);

        Person person4 = new Person();
        person4.setFirstName("Ronald");
        person4.setLastName("Weasely");
        personRepository.save(person4);

        log.info(String.valueOf(personRepository.findAll()));

        log.info(String.valueOf(personRepository.findAllByFirstName("Anshul")));

        log.info(String.valueOf(personRepository.findFirstByLastNameOrderByFirstNameDesc("Weasely").orElse(new Person())));

        log.info(String.valueOf(personRepository.findFirstByOrderByFirstNameAsc()));

        log.info(String.valueOf(personRepository.fetchFirstPerson()));

        log.info(
                String.valueOf(personRepository
                        .readAllByOrderById()
                        .map(i -> i.getLastName() + " " + i.getFirstName())
                        .collect(Collectors.toList())));

        log.info(String.valueOf(personRepository.findPersonCustom("Anshul", "Bansal")));

        log.info(String.valueOf(getCustomPersonRepository().findPersonCustom("Anshul", "Bansal")));

        log.info(String.valueOf(personRepository.findFirstByOrderByFirstNameDesc().getFullName()));
    }

}
