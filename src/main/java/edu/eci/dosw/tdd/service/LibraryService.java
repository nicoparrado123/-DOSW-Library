package edu.eci.dosw.tdd.service;

import edu.eci.dosw.tdd.exception.BookNotAvailableException;
import edu.eci.dosw.tdd.model.Book;
import edu.eci.dosw.tdd.model.Loan;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;

@Service
public class LibraryService {
    private List<Book> books = new ArrayList<>();
    private List<Loan> loans = new ArrayList<>();

    public void addBook(Book book) {
        books.add(book);
    }

    public Book getBook(String id) {
        return books.stream().filter(b -> b.getId().equals(id)).findFirst().orElse(null);
    }

    public List<Book> getAllBooks() {
        return books;
    }

    public void loanBook(Loan loan) throws BookNotAvailableException {
        Book book = getBook(loan.getBookId());
        if (book == null || !book.isAvailable()) {
            throw new BookNotAvailableException("Book not available");
        }
        book.setAvailable(false);
        loans.add(loan);
    }
}
