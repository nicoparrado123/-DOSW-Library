package edu.eci.dosw.tdd.controller;

import edu.eci.dosw.tdd.controller.mapper.BookDTO;
import edu.eci.dosw.tdd.controller.mapper.BookMapper;
import edu.eci.dosw.tdd.core.exception.BookNotFoundException;
import edu.eci.dosw.tdd.core.service.BookService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/libros")
public class BookController {

    private final BookService bookService;
    private final BookMapper bookMapper;

    public BookController(BookService bookService, BookMapper bookMapper) {
        this.bookService = bookService;
        this.bookMapper = bookMapper;
    }

    @GetMapping
    public List<BookDTO> obtenerTodos() {
        return bookService.obtenerTodos().stream().map(bookMapper::toDTO).toList();
    }

    @GetMapping("/{id}")
    public BookDTO obtenerPorId(@PathVariable String id) throws BookNotFoundException {
        return bookMapper.toDTO(bookService.buscarPorId(id));
    }

    @PostMapping
    public ResponseEntity<BookDTO> agregar(@RequestBody BookDTO dto) {
        bookService.agregarLibro(bookMapper.toModel(dto), dto.getCopies());
        return ResponseEntity.ok(dto);
    }
}
