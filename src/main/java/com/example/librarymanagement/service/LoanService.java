package com.example.librarymanagement.service;

import com.example.librarymanagement.model.Book;
import com.example.librarymanagement.model.Loan;
import com.example.librarymanagement.model.LoanStatus;
import com.example.librarymanagement.model.Member;
import com.example.librarymanagement.repository.BookRepository;
import com.example.librarymanagement.repository.LoanRepository;
import com.example.librarymanagement.repository.MemberRepository;
import java.time.LocalDate;
import java.util.List;
import java.util.NoSuchElementException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class LoanService {

    private static final List<LoanStatus> ACTIVE_STATUSES = List.of(LoanStatus.ISSUED, LoanStatus.OVERDUE);

    private final LoanRepository loanRepository;
    private final BookRepository bookRepository;
    private final MemberRepository memberRepository;

    public LoanService(LoanRepository loanRepository, BookRepository bookRepository, MemberRepository memberRepository) {
        this.loanRepository = loanRepository;
        this.bookRepository = bookRepository;
        this.memberRepository = memberRepository;
    }

    @Transactional
    public Loan issueLoan(Long bookId, Long memberId, LocalDate dueDate) {
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new NoSuchElementException("Book not found"));
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new NoSuchElementException("Member not found"));

        if (book.getAvailableCopies() == null || book.getAvailableCopies() <= 0) {
            throw new IllegalStateException("No available copies left for this book.");
        }

        book.setAvailableCopies(book.getAvailableCopies() - 1);
        bookRepository.save(book);

        Loan loan = new Loan();
        loan.setBook(book);
        loan.setMember(member);
        loan.setIssueDate(LocalDate.now());
        loan.setDueDate(dueDate);
        loan.setStatus(dueDate.isBefore(LocalDate.now()) ? LoanStatus.OVERDUE : LoanStatus.ISSUED);

        return loanRepository.save(loan);
    }

    @Transactional
    public Loan returnLoan(Long loanId) {
        Loan loan = loanRepository.findById(loanId)
                .orElseThrow(() -> new NoSuchElementException("Loan not found"));

        if (!loan.isActive()) {
            throw new IllegalStateException("This loan is already closed.");
        }

        Book book = loan.getBook();
        int currentAvailable = book.getAvailableCopies() == null ? 0 : book.getAvailableCopies();
        int totalCopies = book.getTotalCopies() == null ? currentAvailable : book.getTotalCopies();
        book.setAvailableCopies(Math.min(currentAvailable + 1, totalCopies));
        bookRepository.save(book);

        loan.setStatus(LoanStatus.RETURNED);
        loan.setReturnDate(LocalDate.now());
        return loanRepository.save(loan);
    }

    @Transactional
    public void refreshOverdueLoans() {
        LocalDate today = LocalDate.now();
        List<Loan> activeLoans = loanRepository.findByStatusInOrderByDueDateAsc(ACTIVE_STATUSES);

        for (Loan loan : activeLoans) {
            if (loan.getStatus() == LoanStatus.ISSUED && loan.getDueDate().isBefore(today)) {
                loan.setStatus(LoanStatus.OVERDUE);
            }
        }
    }

    public List<LoanStatus> activeStatuses() {
        return ACTIVE_STATUSES;
    }
}
