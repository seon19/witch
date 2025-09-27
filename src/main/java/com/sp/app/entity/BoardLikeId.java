package com.sp.app.entity;

import java.io.Serializable;

import jakarta.persistence.*;
import lombok.*;

@Embeddable
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class BoardLikeId implements Serializable {
	private static final long serialVersionUID = 1L;

	@Column(name = "boardId")
    private Long boardId;

    @Column(name = "memberId")
    private Long memberId;
}
