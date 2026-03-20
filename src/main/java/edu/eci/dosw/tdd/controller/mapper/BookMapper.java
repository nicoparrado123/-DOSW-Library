package edu.eci.dosw.tdd.controller.mapper;

import edu.eci.dosw.tdd.core.model.Book;
import org.springframework.stereotype.Component;

@Component
public class BookMapper {
    public Book toModel(BookDTO dto) {
        return new Book(dto.getId(), dto.getTitulo(), dto.getAutor());
    }

    public BookDTO toDTO(Book libro) {
        BookDTO dto = new BookDTO();
        dto.setId(libro.getId());
        dto.setTitulo(libro.getTitulo());
        dto.setAutor(libro.getAutor());
        return dto;
    }
}
