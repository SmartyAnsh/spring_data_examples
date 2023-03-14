package com.smartdiscover;

import com.querydsl.core.types.Predicate;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.smartdiscover.entity.Person;
import com.smartdiscover.entity.QPerson;
import com.smartdiscover.entity.events.PersonCreationEvent;
import com.smartdiscover.repository.CustomPersonRepository;
import com.smartdiscover.repository.CustomPersonRepositoryImpl;
import com.smartdiscover.repository.PersonRepository;
import jakarta.persistence.EntityManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@SpringBootApplication
public class SpringDataApplication implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(SpringDataApplication.class);

    @Autowired
    private PersonRepository personRepository;

    @Autowired
    private EntityManager entityManager;

    public static void main(String[] args) {
        SpringApplication.run(SpringDataApplication.class, args);
    }

    //manual wiring
    @Bean
    CustomPersonRepository getCustomPersonRepository() {
        return new CustomPersonRepositoryImpl();
    }

    /**
     * listener method to handle the PersonCreationEvent using the After Commit transaction phase
     */
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handlePersonCreationEvent(PersonCreationEvent event) {
        log.info("Handled PersonCreationEvent...");
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
        person4.afterSave();
        personRepository.save(person4);

        //dynamic query methods
        log.info(String.valueOf(personRepository.findAll()));

        log.info(String.valueOf(personRepository.findAllByFirstName("Anshul")));

        log.info(String.valueOf(personRepository.findFirstByLastNameOrderByFirstNameDesc("Weasely").orElse(new Person())));

        log.info(String.valueOf(personRepository.findFirstByOrderByFirstNameAsc()));

        //@Query
        log.info(String.valueOf(personRepository.fetchFirstPersonUsingQuery()));

        //@NamedQuery
        log.info(String.valueOf(personRepository.searchUsingNamedQuery("Bansal")));

        log.info(
                String.valueOf(personRepository
                        .readAllByOrderById()
                        .map(i -> i.getLastName() + " " + i.getFirstName())
                        .collect(Collectors.toList())));

        log.info(String.valueOf(personRepository.findPersonCustom("Anshul", "Bansal")));

        log.info(String.valueOf(getCustomPersonRepository().findPersonCustom("Anshul", "Bansal")));

        log.info(personRepository.findFirstByOrderByFirstNameDesc().getFullName());
        
        log.info(personRepository.findFirstByOrderByLastNameAsc().getFirstName());

        log.info("let's try query dsl");

        QPerson person = QPerson.person;
        Predicate predicate = person.firstName.equalsIgnoreCase("anshul")
                .and(person.lastName.startsWithIgnoreCase("Bansal"));

        log.info("Fetched results using PersonRepository and Predicate: " + personRepository.findAll(predicate));


        JPAQueryFactory queryFactory = new JPAQueryFactory(entityManager);
        List<Person> persons = queryFactory.selectFrom(person).where(person.firstName.eq("Anshul")).fetch();

        log.info("Fetched results using JPAQueryFactory and EntityManager: " + persons);

        // CRUD features

        log.info("CRUD features");

        //Create
        Person person5 = new Person();
        person5.setFirstName("Hormonie");
        person5.setLastName("Gringer");
        person5.afterSave();
        personRepository.save(person5);

        //Read
        Person p = personRepository.findFirstByLastNameOrderByFirstNameDesc("Gringer").get();
        log.info(String.valueOf(p));

        //Update
        p.setLastName("Gringer1");
        personRepository.save(p);
        log.info(String.valueOf(p));

        //Delete
        personRepository.delete(p);
    }

}
