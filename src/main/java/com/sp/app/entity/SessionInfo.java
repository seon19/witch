package com.sp.app.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SessionInfo {
	private long memberId;
	private String userId;
	private String name;
	private String nickname;
	private String email;
	private int currentLevel;
	private int currentExp;
	private int currentBalance;
	private String profilePhoto;
}
