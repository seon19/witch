package com.sp.app.admin.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.sp.app.repository.BoardReplyRepository;

import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/admin/board/replies")
@RequiredArgsConstructor
public class BoardReplyController {
    private final BoardReplyRepository replyRepository;

    @PostMapping("/{replyId}/delete")
    public String delete(@PathVariable("replyId") Long replyId,
                         @RequestParam("boardId") Long boardId,
                         @RequestParam(name="page", defaultValue="1") int page,
                         @RequestParam(name="size", defaultValue="10") int size,
                         RedirectAttributes ra) {
        replyRepository.deleteById(replyId);
        ra.addAttribute("page", page);
        ra.addAttribute("size", size);
        return "redirect:/admin/board/" + boardId;
    }
}
