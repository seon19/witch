package com.sp.app.entity;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "craftLog")
@Getter
@Setter
public class CraftLog {
	 	@Id
	    @GeneratedValue(strategy = GenerationType.IDENTITY)
	    @Column(name = "craftLogid")
	    private Long craftLogId;

	    @CreationTimestamp
	    @Column(name = "createtime", updatable = false)
	    private LocalDateTime createTime;
	    
	    @Column(name = "result", length = 255)
	    private String result;
	    
	    @Column(name = "potionlevel")
	    private Integer potionLevel;
	    
	    @ManyToOne(fetch = FetchType.LAZY)
	    @JoinColumn(name = "memberid", nullable = false)
	    private Member member;
	    
	    @ManyToOne(fetch = FetchType.LAZY)
	    @JoinColumn(name = "potionid", nullable = false)
	    private Potion potion;
}
