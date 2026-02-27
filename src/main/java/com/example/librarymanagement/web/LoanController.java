package com.example.librarymanagement.web;

import com.example.librarymanagement.repository.BookRepository;
import com.example.librarymanagement.repository.LoanRepository;
import com.example.librarymanagement.repository.MemberRepository;
import com.example.librarymanagement.service.LoanService;
import jakarta.validation.Valid;
import java.time.LocalDate;
import java.util.NoSuchElementException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/loans")
public class LoanController {

    private final LoanRepository loanRepository;
    private final BookRepository bookRepository;
    private final MemberRepository memberRepository;
    private final LoanService loanService;

    public LoanController(
            LoanRepository loanRepository,
            BookRepository bookRepository,
            MemberRepository memberRepository,
            LoanService loanService) {
        this.loanRepository = loanRepository;
        this.bookRepository = bookRepository;
        this.memberRepository = memberRepository;
        this.loanService = loanService;
    }

    @GetMapping
    public String list(Model model) {
        loanService.refreshOverdueLoans();
        model.addAttribute("loans", loanRepository.findAllByOrderByIssueDateDesc());
        return "loans/list";
    }

    @GetMapping("/new")
    public String createForm(Model model) {
        LoanRequest loanRequest = new LoanRequest();
        loanRequest.setDueDate(LocalDate.now().plusDays(14));
        model.addAttribute("loanRequest", loanRequest);
        model.addAttribute("books", bookRepository.findByAvailableCopiesGreaterThanOrderByTitleAsc(0));
        model.addAttribute("members", memberRepository.findAllByOrderByNameAsc());
        return "loans/form";
    }

    @PostMapping
    public String create(
            @Valid @ModelAttribute("loanRequest") LoanRequest loanRequest,
            BindingResult bindingResult,
            Model model,
            RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            populateForm(model);
            return "loans/form";
        }

        try {
            loanService.issueLoan(loanRequest.getBookId(), loanRequest.getMemberId(), loanRequest.getDueDate());
            redirectAttributes.addFlashAttribute("success", "Loan created successfully.");
            return "redirect:/loans";
        } catch (NoSuchElementException | IllegalStateException ex) {
            bindingResult.reject("loan.error", ex.getMessage());
            populateForm(model);
            return "loans/form";
        }
    }

    @PostMapping("/{id}/return")
    public String returnLoan(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            loanService.returnLoan(id);
            redirectAttributes.addFlashAttribute("success", "Book returned successfully.");
        } catch (NoSuchElementException | IllegalStateException ex) {
            redirectAttributes.addFlashAttribute("error", ex.getMessage());
        }
        return "redirect:/loans";
    }

    private void populateForm(Model model) {
        model.addAttribute("books", bookRepository.findByAvailableCopiesGreaterThanOrderByTitleAsc(0));
        model.addAttribute("members", memberRepository.findAllByOrderByNameAsc());
    }
}
