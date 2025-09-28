package com.sp.app.admin.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.sp.app.admin.service.NoticeService;
import com.sp.app.entity.Notice;
import com.sp.app.entity.SessionInfo;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/admin/notice")
@RequiredArgsConstructor
public class NoticeManageController {

    private final NoticeService noticeService;

    @GetMapping("/noticeList")
    public String list(
            @RequestParam(name = "kwd", defaultValue = "") String kwd,
            @RequestParam(name = "target", defaultValue = "TITLE_CONTENT") String target,
            @RequestParam(name = "visibility", required = false) Integer visibility,
            @RequestParam(name = "page", defaultValue = "1") int page,
            @RequestParam(name = "size", defaultValue = "10") int size,
            Model model) {

        Pageable pageable = PageRequest.of(Math.max(0, page - 1), size);
        Page<Notice> result = noticeService.list(kwd, visibility, target, pageable);

        model.addAttribute("list", result.getContent());
        model.addAttribute("page", result);                       
        model.addAttribute("size", result.getSize());            
        model.addAttribute("dataCount", result.getTotalElements());

        model.addAttribute("kwd", kwd);
        model.addAttribute("target", target);
        model.addAttribute("visibility", visibility);

        return "admin/notice/noticeList";
    }

    @GetMapping("/{id}")
    public String detail(@PathVariable(name = "id") Long id,
                         @RequestParam(name = "page", defaultValue = "1") int page,
                         @RequestParam(name = "size", defaultValue = "10") int size,
                         @RequestParam(name = "kwd", required = false) String kwd,
                         @RequestParam(name = "visibility", defaultValue = "1") Integer visibility,
                         Model model) {
        Notice n = noticeService.get(id);
        model.addAttribute("notice", n);
        model.addAttribute("page", page);
        model.addAttribute("size", size);
        model.addAttribute("kwd", kwd);
        model.addAttribute("visibility", visibility);
        return "admin/notice/noticeDetail";
    }

    @GetMapping("/new")
    public String writeForm(Model model,
                            @RequestParam(name = "page", defaultValue = "1") int page,
                            @RequestParam(name = "size", defaultValue = "10") int size,
                            @RequestParam(name = "kwd", required = false) String kwd,
                            @RequestParam(name = "visibility", defaultValue = "1") Integer visibility) {
        model.addAttribute("notice", new Notice());
        model.addAttribute("mode", "create");
        model.addAttribute("page", page);
        model.addAttribute("size", size);
        model.addAttribute("kwd", kwd);
        model.addAttribute("visibility", visibility);
        return "admin/notice/noticeWrite";
    }

    @PostMapping("/write")
    public String create(@ModelAttribute Notice notice,
                         @RequestParam(name = "page", defaultValue = "1") int page,
                         @RequestParam(name = "size", defaultValue = "10") int size,
                         @RequestParam(name = "kwd", required = false) String kwd,
                         @RequestParam(name = "visibility", defaultValue = "1") Integer visibility,
                         HttpSession session,
                         RedirectAttributes ra) {

        SessionInfo info = (SessionInfo) session.getAttribute("loginUser");
        if (info == null) {
            return "redirect:/member/login";
        }

        Long id = noticeService.create(notice, info.getMemberId());

        ra.addAttribute("page", page);
        ra.addAttribute("size", size);
        if (kwd != null && !kwd.isBlank()) ra.addAttribute("kwd", kwd);
        ra.addAttribute("visibility", visibility);

        return "redirect:/admin/notice/noticeList";
    }

    @GetMapping("/{id}/update")
    public String updateForm(@PathVariable(name = "id") Long id,
                             @RequestParam(name = "page", defaultValue = "1") int page,
                             @RequestParam(name = "size", defaultValue = "10") int size,
                             @RequestParam(name = "kwd", required = false) String kwd,
                             @RequestParam(name = "visibility", defaultValue = "1") Integer visibility,
                             Model model) {
        Notice n = noticeService.get(id);
        model.addAttribute("notice", n);
        model.addAttribute("mode", "update");
        model.addAttribute("page", page);
        model.addAttribute("size", size);
        model.addAttribute("kwd", kwd);
        model.addAttribute("visibility", visibility);
        return "admin/notice/noticeWrite";
    }

    @PostMapping("/{id}/update")
    public String update(@PathVariable(name = "id") Long id,
                         @ModelAttribute Notice form,
                         @RequestParam(name = "page", defaultValue = "1") int page,
                         @RequestParam(name = "size", defaultValue = "10") int size,
                         @RequestParam(name = "kwd", required = false) String kwd,
                         @RequestParam(name = "visibility", defaultValue = "1") Integer visibility,
                         RedirectAttributes ra) {

        form.setNoticeId(id);
        noticeService.update(form);

        ra.addAttribute("page", page);
        ra.addAttribute("size", size);
        if (kwd != null && !kwd.isBlank()) ra.addAttribute("kwd", kwd);
        ra.addAttribute("visibility", visibility);

        return "redirect:/admin/notice/" + id;
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable(name = "id") Long id,
                         @RequestParam(name = "page", defaultValue = "1") int page,
                         @RequestParam(name = "size", defaultValue = "10") int size,
                         @RequestParam(name = "kwd", required = false) String kwd,
                         @RequestParam(name = "visibility", defaultValue = "1") Integer visibility,
                         RedirectAttributes ra) {

        noticeService.delete(id);

        ra.addAttribute("page", page);
        ra.addAttribute("size", size);
        if (kwd != null && !kwd.isBlank()) ra.addAttribute("kwd", kwd);
        ra.addAttribute("visibility", visibility);

        return "redirect:/admin/notice/noticeList";
    }
}
