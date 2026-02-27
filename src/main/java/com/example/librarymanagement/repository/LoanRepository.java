package com.example.librarymanagement.repository;

import com.example.librarymanagement.model.Loan;
import com.example.librarymanagement.model.LoanStatus;
import java.util.Collection;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.EntityGraph;

public interface LoanRepository extends JpaRepository<Loan, Long> {

    @EntityGraph(attributePaths = {"book", "member"})
    List<Loan> findAllByOrderByIssueDateDesc();

    @EntityGraph(attributePaths = {"book", "member"})
    List<Loan> findTop5ByOrderByIssueDateDesc();

    List<Loan> findByStatusInOrderByDueDateAsc(Collection<LoanStatus> statuses);

    long countByStatusIn(Collection<LoanStatus> statuses);

    boolean existsByBookIdAndStatusIn(Long bookId, Collection<LoanStatus> statuses);

    boolean existsByMemberIdAndStatusIn(Long memberId, Collection<LoanStatus> statuses);
}
