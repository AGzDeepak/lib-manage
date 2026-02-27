package com.example.librarymanagement.web;

import com.example.librarymanagement.model.Member;
import com.example.librarymanagement.repository.MemberRepository;
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
@RequestMapping("/members")
public class MemberController {

    private final MemberRepository memberRepository;
    private final LoanRepository loanRepository;
    private final LoanService loanService;

    public MemberController(MemberRepository memberRepository, LoanRepository loanRepository, LoanService loanService) {
        this.memberRepository = memberRepository;
        this.loanRepository = loanRepository;
        this.loanService = loanService;
    }

    @GetMapping
    public String list(Model model) {
        model.addAttribute("members", memberRepository.findAllByOrderByNameAsc());
        return "members/list";
    }

    @GetMapping("/new")
    public String createForm(Model model) {
        model.addAttribute("member", new Member());
        return "members/form";
    }

    @PostMapping
    public String create(
            @Valid @ModelAttribute("member") Member member,
            BindingResult bindingResult,
            RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            return "members/form";
        }

        try {
            memberRepository.save(member);
            redirectAttributes.addFlashAttribute("success", "Member created successfully.");
            return "redirect:/members";
        } catch (DataIntegrityViolationException ex) {
            bindingResult.rejectValue("email", "member.email.duplicate", "Email already exists.");
            return "members/form";
        }
    }

    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        Member member = memberRepository.findById(id).orElse(null);
        if (member == null) {
            redirectAttributes.addFlashAttribute("error", "Member not found.");
            return "redirect:/members";
        }

        model.addAttribute("member", member);
        return "members/form";
    }

    @PostMapping("/{id}")
    public String update(
            @PathVariable Long id,
            @Valid @ModelAttribute("member") Member member,
            BindingResult bindingResult,
            RedirectAttributes redirectAttributes) {
        if (!memberRepository.existsById(id)) {
            redirectAttributes.addFlashAttribute("error", "Member not found.");
            return "redirect:/members";
        }

        if (bindingResult.hasErrors()) {
            return "members/form";
        }

        member.setId(id);
        try {
            memberRepository.save(member);
            redirectAttributes.addFlashAttribute("success", "Member updated successfully.");
            return "redirect:/members";
        } catch (DataIntegrityViolationException ex) {
            bindingResult.rejectValue("email", "member.email.duplicate", "Email already exists.");
            return "members/form";
        }
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        if (!memberRepository.existsById(id)) {
            redirectAttributes.addFlashAttribute("error", "Member not found.");
            return "redirect:/members";
        }

        if (loanRepository.existsByMemberIdAndStatusIn(id, loanService.activeStatuses())) {
            redirectAttributes.addFlashAttribute("error", "Member has active loans and cannot be deleted.");
            return "redirect:/members";
        }

        try {
            memberRepository.deleteById(id);
            redirectAttributes.addFlashAttribute("success", "Member deleted successfully.");
        } catch (DataIntegrityViolationException ex) {
            redirectAttributes.addFlashAttribute("error", "Member cannot be deleted because loan history exists.");
        }

        return "redirect:/members";
    }
}
