package com.sp.app.controller;

import com.sp.app.dto.NoticeDetailDTO;
import com.sp.app.dto.NoticeTitleDTO;
import com.sp.app.service.UserNoticeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/notice")
@RequiredArgsConstructor
public class NoticeController {
    private final UserNoticeService noticeService;

    @GetMapping
    public String board() {
        return "notice/notice";
    }

    @GetMapping("/titles")
    @ResponseBody
    public List<NoticeTitleDTO> titles() {
        return noticeService.getTitleList(null); 
    }

    @GetMapping("/{id}")
    @ResponseBody
    public NoticeDetailDTO one(@PathVariable("id") Long id) {
        return noticeService.getDetail(id);
    }
}
