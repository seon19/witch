package com.sp.app.admin.service;

public interface BoardLikeService {
    boolean addLike(Long boardId, Long memberId);
    boolean removeLike(Long boardId, Long memberId);
    boolean toggleLike(Long boardId, Long memberId);
    boolean hasLiked(Long boardId, Long memberId);
    long countLikes(Long boardId);
}
