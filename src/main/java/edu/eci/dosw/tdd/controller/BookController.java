package edu.eci.dosw.tdd.controller;

import edu.eci.dosw.tdd.model.Book;
import edu.eci.dosw.tdd.service.LibraryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/books")
public class BookController {
    @Autowired
    private LibraryService libraryService;

    @PostMapping
    public void addBook(@RequestBody Book book) {
        libraryService.addBook(book);
    }

    @GetMapping("/{id}")
    public Book getBook(@PathVariable String id) {
        return libraryService.getBook(id);
    }

    @GetMapping
    public List<Book> getAllBooks() {
        return libraryService.getAllBooks();
    }
}
