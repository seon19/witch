package com.sp.app.admin.service;

import com.sp.app.entity.Board;
import com.sp.app.entity.Member;
import com.sp.app.repository.BoardRepository;
import com.sp.app.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Transactional
public class BoardServiceImpl implements BoardService {

    private final BoardRepository boardRepository;
    private final MemberRepository memberRepository;

    @Override
    @Transactional(readOnly = true)
    public Page<Board> list(Pageable pageable) {
        return boardRepository.findAll(pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public Board get(Long boardId) {
        return boardRepository.findById(boardId)
                .orElseThrow(() -> new IllegalArgumentException("게시글이 존재하지 않습니다: " + boardId));
    }

    @Override
    public Long create(Board form, Long writerMemberId) {
        Member writer = memberRepository.getReferenceById(writerMemberId);
        form.setMember(writer);
        if (form.getPostDate() == null) {
            form.setPostDate(LocalDateTime.now());
        }
        if (form.getHitCount() == null) {
            form.setHitCount(0);
        }
        return boardRepository.save(form).getBoardId();
    }

    @Override
    public void update(Board form) {
        Board saved = boardRepository.findById(form.getBoardId())
                .orElseThrow(() -> new IllegalArgumentException("게시글이 존재하지 않습니다: " + form.getBoardId()));

        saved.setPostName(form.getPostName());
        saved.setPostContent(form.getPostContent());
    }

    @Override
    public void delete(Long boardId) {
        boardRepository.deleteById(boardId);
    }

    @Override
    public void increaseHitCount(Long boardId) {
        Board b = boardRepository.findById(boardId)
                .orElseThrow(() -> new IllegalArgumentException("게시글이 존재하지 않습니다: " + boardId));
        b.setHitCount(b.getHitCount() + 1);
    }
}
