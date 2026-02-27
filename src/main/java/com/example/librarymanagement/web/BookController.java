package com.example.librarymanagement.web;

import com.example.librarymanagement.model.Book;
import com.example.librarymanagement.repository.BookRepository;
import com.example.librarymanagement.repository.LoanRepository;
import com.example.librarymanagement.service.LoanService;
import jakarta.validation.Valid;
import org.springframework.dao.DataIntegrityViolationException;
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
@RequestMapping("/books")
public class BookController {

    private final BookRepository bookRepository;
    private final LoanRepository loanRepository;
    private final LoanService loanService;

    public BookController(BookRepository bookRepository, LoanRepository loanRepository, LoanService loanService) {
        this.bookRepository = bookRepository;
        this.loanRepository = loanRepository;
        this.loanService = loanService;
    }

    @GetMapping
    public String list(Model model) {
        model.addAttribute("books", bookRepository.findAllByOrderByTitleAsc());
        return "books/list";
    }

    @GetMapping("/new")
    public String createForm(Model model) {
        Book book = new Book();
        book.setTotalCopies(1);
        book.setAvailableCopies(1);
        model.addAttribute("book", book);
        return "books/form";
    }

    @PostMapping
    public String create(
            @Valid @ModelAttribute("book") Book book,
            BindingResult bindingResult,
            RedirectAttributes redirectAttributes) {
        validateCopies(book, bindingResult);
        if (bindingResult.hasErrors()) {
            return "books/form";
        }

        try {
            bookRepository.save(book);
            redirectAttributes.addFlashAttribute("success", "Book created successfully.");
            return "redirect:/books";
        } catch (DataIntegrityViolationException ex) {
            bindingResult.rejectValue("isbn", "book.isbn.duplicate", "ISBN already exists.");
            return "books/form";
        }
    }

    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        Book book = bookRepository.findById(id).orElse(null);
        if (book == null) {
            redirectAttributes.addFlashAttribute("error", "Book not found.");
            return "redirect:/books";
        }

        model.addAttribute("book", book);
        return "books/form";
    }

    @PostMapping("/{id}")
    public String update(
            @PathVariable Long id,
            @Valid @ModelAttribute("book") Book book,
            BindingResult bindingResult,
            RedirectAttributes redirectAttributes) {
        if (!bookRepository.existsById(id)) {
            redirectAttributes.addFlashAttribute("error", "Book not found.");
            return "redirect:/books";
        }

        book.setId(id);
        validateCopies(book, bindingResult);
        if (bindingResult.hasErrors()) {
            return "books/form";
        }

        try {
            bookRepository.save(book);
            redirectAttributes.addFlashAttribute("success", "Book updated successfully.");
            return "redirect:/books";
        } catch (DataIntegrityViolationException ex) {
            bindingResult.rejectValue("isbn", "book.isbn.duplicate", "ISBN already exists.");
            return "books/form";
        }
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        if (!bookRepository.existsById(id)) {
            redirectAttributes.addFlashAttribute("error", "Book not found.");
            return "redirect:/books";
        }

        if (loanRepository.existsByBookIdAndStatusIn(id, loanService.activeStatuses())) {
            redirectAttributes.addFlashAttribute("error", "Book has active loans and cannot be deleted.");
            return "redirect:/books";
        }

        try {
            bookRepository.deleteById(id);
            redirectAttributes.addFlashAttribute("success", "Book deleted successfully.");
        } catch (DataIntegrityViolationException ex) {
            redirectAttributes.addFlashAttribute("error", "Book cannot be deleted because loan history exists.");
        }
        return "redirect:/books";
    }

    private void validateCopies(Book book, BindingResult bindingResult) {
        Integer total = book.getTotalCopies();
        Integer available = book.getAvailableCopies();

        if (total != null && available != null && available > total) {
            bindingResult.rejectValue(
                    "availableCopies",
                    "book.availableCopies.invalid",
                    "Available copies cannot be greater than total copies.");
        }
    }
}
