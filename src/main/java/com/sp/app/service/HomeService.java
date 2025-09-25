package com.sp.app.service;

import com.sp.app.entity.DailyReward;

public interface HomeService {

	public boolean hasReceivedTodayReward(long memberId);

	public DailyReward giveDailyReward(long memberId);

	public int updateConsecutiveDays(long memberId);

	public DailyReward getTodayReward(long memberId);
}
