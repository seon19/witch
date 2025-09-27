package com.sp.app.admin.controller;

import com.sp.app.admin.service.BoardService;
import com.sp.app.entity.Board;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin/board")
@RequiredArgsConstructor
public class BoardController {

    private final BoardService boardService;

    @GetMapping("/boardList")
    public String list(@RequestParam(name = "kwd",    defaultValue = "")  String kwd,
                       @RequestParam(name = "target", defaultValue = "TITLE_CONTENT") String target,
                       @RequestParam(name = "page",   defaultValue = "1") int page,
                       @RequestParam(name = "size",   defaultValue = "10") int size,
                       Model model) {

        Pageable pageable = PageRequest.of(Math.max(0, page - 1), size);
        Page<Board> result = boardService.list(kwd, target, pageable);

        model.addAttribute("list", result.getContent());
        model.addAttribute("page", result);              
        model.addAttribute("pageNo", result.getNumber()+1);
        model.addAttribute("size", result.getSize());
        model.addAttribute("dataCount", result.getTotalElements());
        model.addAttribute("kwd", kwd);
        model.addAttribute("target", target);
        return "admin/board/boardList";
    }


    @GetMapping("/{id}")
    public String detail(@PathVariable("id") Long id,
                         @RequestParam(name = "page", defaultValue = "1") int page,
                         @RequestParam(name = "size", defaultValue = "10") int size,
                         Model model) {

        Board board = boardService.get(id);
        model.addAttribute("board", board);
        model.addAttribute("page", page);
        model.addAttribute("size", size);
        return "admin/board/boardDetail";
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable("id") Long id,
                         @RequestParam(name = "page", defaultValue = "1") int page,
                         @RequestParam(name = "size", defaultValue = "10") int size,
                         RedirectAttributes ra) {

        boardService.delete(id);

        ra.addAttribute("page", page);
        ra.addAttribute("size", size);
        return "redirect:/admin/board/boardList";
    }
}
