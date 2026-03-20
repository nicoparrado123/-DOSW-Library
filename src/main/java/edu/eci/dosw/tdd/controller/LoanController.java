package edu.eci.dosw.tdd.controller;

import edu.eci.dosw.tdd.controller.mapper.LoanDTO;
import edu.eci.dosw.tdd.controller.mapper.LoanMapper;
import edu.eci.dosw.tdd.core.service.LoanService;
import org.springframework.http.ResponseEntity;
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
    public List<LoanDTO> obtenerTodos() {
        return loanService.obtenerTodos().stream().map(loanMapper::toDTO).toList();
    }

    @PostMapping("/{idUsuario}/{idLibro}")
    public ResponseEntity<LoanDTO> prestar(@PathVariable String idUsuario, @PathVariable String idLibro) throws Exception {
        return ResponseEntity.ok(loanMapper.toDTO(loanService.prestar(idUsuario, idLibro)));
    }

    @PutMapping("/devolver/{idUsuario}/{idLibro}")
    public ResponseEntity<LoanDTO> devolver(@PathVariable String idUsuario, @PathVariable String idLibro) throws Exception {
        return ResponseEntity.ok(loanMapper.toDTO(loanService.devolver(idUsuario, idLibro)));
    }
}
