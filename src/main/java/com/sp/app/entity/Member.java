package com.sp.app.entity;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrimaryKeyJoinColumn;
import jakarta.persistence.SecondaryTable;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "member1")
@SecondaryTable(
    name = "member2",
    pkJoinColumns = @PrimaryKeyJoinColumn(name = "memberid")
)
@Getter
@Setter
@ToString
public class Member {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "memberid")
	private Long memberId;

    @Column(name = "userid", nullable = false, length = 100)
    private String userId;

    @Column(name = "password", nullable = false, length = 100)
    private String password;

    @Column(name = "role")
    private Integer role;
    
    @CreationTimestamp
    @Column(name = "createDate", nullable = false, updatable = false)
    private LocalDateTime createDate;

    @UpdateTimestamp
    @Column(name = "updateDate", nullable = false)
    private LocalDateTime updateDate;

    @Column(name = "status")
    private Integer status;

    @Column(name = "currentlevel")
    private Integer currentLevel;

    @Column(name = "currentexp")
    private Integer currentExp;

    @Column(name = "currentbalance")
    private Long currentBalance;

    @Column(name = "name", table = "member2", length = 100)
    private String name;

    @Column(name = "nickname", table = "member2", length = 100)
    private String nickname;

    @Column(name = "birth", table = "member2")
    private LocalDateTime birth;

    @Column(name = "tel", table = "member2", length = 20)
    private String tel;

    @Column(name = "email", table = "member2", length = 100)
    private String email;

    @Column(name = "profilePhoto", table = "member2", length = 255)
    private String profilePhoto;
}