package com.example.librarymanagement.repository;

import com.example.librarymanagement.model.Book;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookRepository extends JpaRepository<Book, Long> {

    List<Book> findAllByOrderByTitleAsc();

    List<Book> findByAvailableCopiesGreaterThanOrderByTitleAsc(Integer copies);
}
