package com.example.librarymanagement.repository;

import com.example.librarymanagement.model.Member;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberRepository extends JpaRepository<Member, Long> {

    List<Member> findAllByOrderByNameAsc();
}
