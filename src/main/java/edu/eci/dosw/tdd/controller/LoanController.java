package edu.eci.dosw.tdd.controller;

import edu.eci.dosw.tdd.exception.BookNotAvailableException;
import edu.eci.dosw.tdd.model.Loan;
import edu.eci.dosw.tdd.service.LibraryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/loans")
public class LoanController {
    @Autowired
    private LibraryService libraryService;

    @PostMapping
    public ResponseEntity<String> createLoan(@RequestBody Loan loan) {
        try {
            libraryService.loanBook(loan);
            return ResponseEntity.ok("Loan created successfully");
        } catch (BookNotAvailableException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
