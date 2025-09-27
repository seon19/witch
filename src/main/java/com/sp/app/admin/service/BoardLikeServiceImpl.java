package com.sp.app.admin.service;

import com.sp.app.entity.BoardLike;
import com.sp.app.entity.BoardLikeId;
import com.sp.app.repository.BoardLikeRepository;
import com.sp.app.repository.BoardRepository;
import com.sp.app.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class BoardLikeServiceImpl implements BoardLikeService {

    private final BoardLikeRepository boardLikeRepository;
    private final BoardRepository boardRepository;
    private final MemberRepository memberRepository;

    @Override
    public boolean addLike(Long boardId, Long memberId) {
        if (boardLikeRepository.existsByBoard_BoardIdAndMember_MemberId(boardId, memberId)) {
            return false;
        }
        BoardLike like = new BoardLike();
        like.setId(new BoardLikeId(boardId, memberId));
        like.setBoard(boardRepository.getReferenceById(boardId));
        like.setMember(memberRepository.getReferenceById(memberId));
        boardLikeRepository.save(like);
        return true;
    }

    @Override
    public boolean removeLike(Long boardId, Long memberId) {
        long deleted = boardLikeRepository.deleteByBoard_BoardIdAndMember_MemberId(boardId, memberId);
        return deleted > 0;
    }

    @Override
    public boolean toggleLike(Long boardId, Long memberId) {
        if (boardLikeRepository.existsByBoard_BoardIdAndMember_MemberId(boardId, memberId)) {
            boardLikeRepository.deleteByBoard_BoardIdAndMember_MemberId(boardId, memberId);
            return false;
        }
        addLike(boardId, memberId);
        return true;
    }

    @Override
    @Transactional(readOnly = true)
    public boolean hasLiked(Long boardId, Long memberId) {
        return boardLikeRepository.existsByBoard_BoardIdAndMember_MemberId(boardId, memberId);
    }

    @Override
    @Transactional(readOnly = true)
    public long countLikes(Long boardId) {
        return boardLikeRepository.countByBoard_BoardId(boardId);
    }
}
