package com.sp.app.admin.service;

import com.sp.app.entity.Board;
import com.sp.app.entity.BoardReply;
import com.sp.app.entity.Member;
import com.sp.app.repository.BoardReplyRepository;
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
public class BoardReplyServiceImpl implements BoardReplyService {

    private final BoardReplyRepository boardReplyRepository;
    private final BoardRepository boardRepository;
    private final MemberRepository memberRepository;

    @Override
    @Transactional(readOnly = true)
    public Page<BoardReply> listByBoard(Long boardId, Pageable pageable) {
        return boardReplyRepository.findByBoard_BoardIdOrderByRegDateDesc(boardId, pageable);
    }

    @Override
    public Long write(Long boardId, Long memberId, String content) {
        Board board = boardRepository.getReferenceById(boardId);
        Member member = memberRepository.getReferenceById(memberId);

        BoardReply reply = new BoardReply();
        reply.setBoard(board);
        reply.setMember(member);
        reply.setContent(content);
        reply.setRegDate(LocalDateTime.now());

        return boardReplyRepository.save(reply).getReplyId();
    }

    @Override
    public void delete(Long replyId) {
        boardReplyRepository.deleteById(replyId);
    }

    @Override
    @Transactional(readOnly = true)
    public long countByBoard(Long boardId) {
        return boardReplyRepository.countByBoard_BoardId(boardId);
    }
}
