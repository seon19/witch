	package com.sp.app.service;
	
	import java.util.List;
	
	import org.springframework.data.domain.Page;
	
	import com.sp.app.entity.CraftLog;
	
	public interface CraftLogService {
		public List<CraftLog> listAll(long memberId);
		
		public Page<CraftLog> listPage(long memberId, String schType, String kwd, int current_page, int size);
	
	}
