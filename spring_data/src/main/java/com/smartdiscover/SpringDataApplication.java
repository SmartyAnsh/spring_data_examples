package com.smartdiscover;

import com.querydsl.core.types.Predicate;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.smartdiscover.entity.Customer;
import com.smartdiscover.entity.QCustomer;
import com.smartdiscover.entity.events.CustomerCreationEvent;
import com.smartdiscover.repository.CustomCustomerRepository;
import com.smartdiscover.repository.CustomCustomerRepositoryImpl;
import com.smartdiscover.repository.CustomerRepository;
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
import java.util.stream.Collectors;

@SpringBootApplication
public class SpringDataApplication implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(SpringDataApplication.class);

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private EntityManager entityManager;

    public static void main(String[] args) {
        SpringApplication.run(SpringDataApplication.class, args);
    }

    //manual wiring
    @Bean
    CustomCustomerRepository getCustomCustomerRepository() {
        return new CustomCustomerRepositoryImpl();
    }

    /**
     * listener method to handle the CustomerCreationEvent using the After Commit transaction phase
     */
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleCustomerCreationEvent(CustomerCreationEvent event) {
        log.info("Handled CustomerCreationEvent...");
    }

    @Override
    @Transactional
    public void run(String... strings) throws Exception {
        Customer customer1 = new Customer();
        customer1.setFirstName("Anshul");
        customer1.setLastName("Bansal");
        customerRepository.save(customer1);

        Customer customer2 = new Customer();
        customer2.setFirstName("John");
        customer2.setLastName("Smith");
        customerRepository.save(customer2);

        Customer customer3 = new Customer();
        customer3.setFirstName("Harry");
        customer3.setLastName("Potter");
        customerRepository.save(customer3);

        Customer customer4 = new Customer();
        customer4.setFirstName("Ronald");
        customer4.setLastName("Weasely");
        customer4.afterSave();
        customerRepository.save(customer4);

        //dynamic query methods
        log.info(String.valueOf(customerRepository.findAll()));

        log.info(String.valueOf(customerRepository.findAllByFirstName("Anshul")));

        log.info(String.valueOf(customerRepository.findFirstByLastNameOrderByFirstNameDesc("Weasely").orElse(new Customer())));

        log.info(String.valueOf(customerRepository.findFirstByOrderByFirstNameAsc()));

        //@Query
        log.info(String.valueOf(customerRepository.fetchFirstCustomerUsingQuery()));

        //@NamedQuery
        log.info(String.valueOf(customerRepository.searchUsingNamedQuery("Bansal")));
        log.info(String.valueOf(entityManager.createNamedQuery("Customer.searchUsingNamedQuery").setParameter(1, "Edison").getResultList()));

        log.info(
                String.valueOf(customerRepository
                        .readAllByOrderById()
                        .map(i -> i.getLastName() + " " + i.getFirstName())
                        .collect(Collectors.toList())));

        log.info(String.valueOf(customerRepository.findCustomerCustom("Anshul", "Bansal")));

        log.info(String.valueOf(getCustomCustomerRepository().findCustomerCustom("Anshul", "Bansal")));

        log.info(customerRepository.findFirstByOrderByFirstNameDesc().getFullName());

        log.info(customerRepository.findFirstByOrderByLastNameAsc().getFirstName());

        log.info("let's try query dsl");

        QCustomer customer = QCustomer.customer;
        Predicate predicate = customer.firstName.equalsIgnoreCase("anshul")
                .and(customer.lastName.startsWithIgnoreCase("Bansal"));

        log.info("Fetched results using CustomerRepository and Predicate: " + customerRepository.findAll(predicate));


        JPAQueryFactory queryFactory = new JPAQueryFactory(entityManager);
        List<Customer> customers = queryFactory.selectFrom(customer).where(customer.firstName.eq("Anshul")).fetch();

        log.info("Fetched results using JPAQueryFactory and EntityManager: " + customers);

        // CRUD features

        log.info("CRUD features");

        //Create
        Customer customer5 = new Customer();
        customer5.setFirstName("Hormonie");
        customer5.setLastName("Gringer");
        customer5.afterSave();
        customerRepository.save(customer5);

        //Read
        Customer c = customerRepository.findFirstByLastNameOrderByFirstNameDesc("Gringer").get();
        log.info(String.valueOf(c));

        //Update
        c.setLastName("Gringer1");
        customerRepository.save(c);
        log.info(String.valueOf(c));

        //Delete
        customerRepository.delete(c);
    }

}
