package com.example.librarymanagement.config;

import com.example.librarymanagement.model.Book;
import com.example.librarymanagement.model.Member;
import com.example.librarymanagement.repository.BookRepository;
import com.example.librarymanagement.repository.MemberRepository;
import java.util.List;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@ConditionalOnProperty(name = "app.seed.enabled", havingValue = "true", matchIfMissing = true)
public class DataSeeder implements ApplicationRunner {

    private final BookRepository bookRepository;
    private final MemberRepository memberRepository;

    public DataSeeder(BookRepository bookRepository, MemberRepository memberRepository) {
        this.bookRepository = bookRepository;
        this.memberRepository = memberRepository;
    }

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        seedBooks();
        seedStudents();
    }

    private void seedBooks() {
        if (bookRepository.count() > 0) {
            return;
        }

        List<Book> books = List.of(
                createBook("Clean Code", "Robert C. Martin", "9780132350884", 6, 6),
                createBook("Effective Java", "Joshua Bloch", "9780134685991", 5, 5),
                createBook("Design Patterns", "Erich Gamma", "9780201633610", 4, 4),
                createBook("Java: The Complete Reference", "Herbert Schildt", "9781260440232", 7, 7),
                createBook("Head First Java", "Kathy Sierra", "9780596009205", 5, 5),
                createBook("Spring in Action", "Craig Walls", "9781617297571", 4, 4),
                createBook("Algorithms", "Robert Sedgewick", "9780321573513", 3, 3),
                createBook("Refactoring", "Martin Fowler", "9780134757599", 4, 4));

        bookRepository.saveAll(books);
    }

    private void seedStudents() {
        if (memberRepository.count() > 0) {
            return;
        }

        List<Member> students = List.of(
                createStudent("Aarav Sharma", "aarav.sharma@college.edu", "9876500011"),
                createStudent("Ishita Verma", "ishita.verma@college.edu", "9876500012"),
                createStudent("Rohan Patel", "rohan.patel@college.edu", "9876500013"),
                createStudent("Ananya Gupta", "ananya.gupta@college.edu", "9876500014"),
                createStudent("Karan Mehta", "karan.mehta@college.edu", "9876500015"),
                createStudent("Nisha Singh", "nisha.singh@college.edu", "9876500016"),
                createStudent("Vikram Rao", "vikram.rao@college.edu", "9876500017"),
                createStudent("Meera Nair", "meera.nair@college.edu", "9876500018"));

        memberRepository.saveAll(students);
    }

    private Book createBook(String title, String author, String isbn, int totalCopies, int availableCopies) {
        Book book = new Book();
        book.setTitle(title);
        book.setAuthor(author);
        book.setIsbn(isbn);
        book.setTotalCopies(totalCopies);
        book.setAvailableCopies(availableCopies);
        return book;
    }

    private Member createStudent(String name, String email, String phone) {
        Member member = new Member();
        member.setName(name);
        member.setEmail(email);
        member.setPhone(phone);
        return member;
    }
}
