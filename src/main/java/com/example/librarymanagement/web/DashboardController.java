package com.example.librarymanagement.web;

import com.example.librarymanagement.repository.BookRepository;
import com.example.librarymanagement.repository.LoanRepository;
import com.example.librarymanagement.repository.MemberRepository;
import com.example.librarymanagement.service.LoanService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class DashboardController {

    private final BookRepository bookRepository;
    private final MemberRepository memberRepository;
    private final LoanRepository loanRepository;
    private final LoanService loanService;

    public DashboardController(
            BookRepository bookRepository,
            MemberRepository memberRepository,
            LoanRepository loanRepository,
            LoanService loanService) {
        this.bookRepository = bookRepository;
        this.memberRepository = memberRepository;
        this.loanRepository = loanRepository;
        this.loanService = loanService;
    }

    @GetMapping({"/", "/dashboard"})
    public String dashboard(Model model) {
        loanService.refreshOverdueLoans();

        model.addAttribute("bookCount", bookRepository.count());
        model.addAttribute("memberCount", memberRepository.count());
        model.addAttribute("activeLoanCount", loanRepository.countByStatusIn(loanService.activeStatuses()));
        model.addAttribute("recentLoans", loanRepository.findTop5ByOrderByIssueDateDesc());

        return "dashboard";
    }
}
