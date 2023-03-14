package com.smartdiscover.entity;

import com.smartdiscover.entity.events.PersonCreationEvent;
import jakarta.persistence.*;
import lombok.Data;
import org.springframework.data.domain.AbstractAggregateRoot;

@Data
@Entity
@NamedQuery(name = "Person.searchUsingNamedQuery",
        query = "select p from Person p where p.lastName = ?1 order by p.firstName desc limit 1")
public class Person extends AbstractAggregateRoot<Person> {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    private String firstName;

    private String lastName;

    public void afterSave() {
        //registers the PersonCreationEvent using the AbstractAggregateRoot's registerEvent method
        registerEvent(new PersonCreationEvent());
    }

}
