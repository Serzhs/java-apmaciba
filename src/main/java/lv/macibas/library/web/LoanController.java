package lv.macibas.library.web;

import lv.macibas.library.dto.LoanResponse;
import lv.macibas.library.service.LoanService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/loans")
public class LoanController {

    private final LoanService loanService;

    public LoanController(LoanService loanService) {
        this.loanService = loanService;
    }

    // GET /api/loans?borrower=Jānis
    @GetMapping
    public List<LoanResponse> activeLoans(@RequestParam String borrower) {
        return loanService.findActiveLoans(borrower);
    }

    // Darbība, kas nav tīrs CRUD -> POST uz apakš-URL. (Alternatīva: PATCH /api/loans/{id} ar {"returned":true})
    @PostMapping("/{id}/return")
    public LoanResponse returnBook(@PathVariable Long id) {
        return loanService.returnBook(id);
    }
}
