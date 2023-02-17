package com.smartdiscover.services;

import com.smartdiscover.entity.Book;
import com.smartdiscover.repository.AuthorRepository;
import com.smartdiscover.repository.BookRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LibraryService {

    @Autowired
    private AuthorRepository authorRepository;

    @Autowired
    private BookRepository bookRepository;

    public void listBook() {

        List<Book> books = (List<Book>) bookRepository.findAll();

        System.out.println(books);

    }
}
