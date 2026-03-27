package edu.eci.dosw.tdd.controller;

import edu.eci.dosw.tdd.controller.mapper.LoanDTO;
import edu.eci.dosw.tdd.controller.mapper.LoanMapper;
import edu.eci.dosw.tdd.core.service.LoanService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/prestamos")
public class LoanController {

    private final LoanService loanService;
    private final LoanMapper loanMapper;

    public LoanController(LoanService loanService, LoanMapper loanMapper) {
        this.loanService = loanService;
        this.loanMapper = loanMapper;
    }

    @GetMapping
    @PreAuthorize("hasRole('LIBRARIAN')")
    public List<LoanDTO> obtenerTodos() {
        return loanService.obtenerTodos().stream().map(loanMapper::toDTO).toList();
    }

    @GetMapping("/usuario/{idUsuario}")
    @PreAuthorize("hasRole('LIBRARIAN') or #idUsuario == authentication.principal")
    public List<LoanDTO> obtenerPorUsuario(@PathVariable String idUsuario, Authentication auth) throws Exception {
        return loanService.obtenerPorUsuario(idUsuario).stream().map(loanMapper::toDTO).toList();
    }

    @PostMapping("/{idUsuario}/{idLibro}")
    @PreAuthorize("hasRole('LIBRARIAN') or #idUsuario == authentication.principal")
    public ResponseEntity<LoanDTO> prestar(@PathVariable String idUsuario, @PathVariable String idLibro) throws Exception {
        return ResponseEntity.ok(loanMapper.toDTO(loanService.prestar(idUsuario, idLibro)));
    }

    @PutMapping("/devolver/{idUsuario}/{idLibro}")
    @PreAuthorize("hasRole('LIBRARIAN') or #idUsuario == authentication.principal")
    public ResponseEntity<LoanDTO> devolver(@PathVariable String idUsuario, @PathVariable String idLibro) throws Exception {
        return ResponseEntity.ok(loanMapper.toDTO(loanService.devolver(idUsuario, idLibro)));
    }
}
