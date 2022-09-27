package org.zerock.domain;

import lombok.Data;

import java.util.Date;

/*
    각 게시물마다 고유의 번호가 필요
        -> 오라클의 경우 시퀀스(sequence)를 이용

    * 시퀀스 생성
    create sequence seq_board;

    * 테이블 생성
    create table tbl_board (
        bno number(10, 0),
        title varchar2(200) not null,
        content varchar2(2000) not null,
        writer varchar2(50) not null,
        regdate date default sysdate,
        updatedate date default sysdate
    );

    * Primary key 지정
    alter table tbl_board add constraint pk_board
        primary key (bno);

    * 토이데이터(toy data) / 더미데이터(dummy data) 추가
    insert into tbl_board (bno, title, content, writer)
        values (seq_board.nextval, 'test_title', 'test_content', 'user00');

    *오라클은 insert 후 commit을 수동으로 처리해야 함 !
 */

// 테이블의 컬럼 구조를 반영하는 VO(Value Object) 클래스 생성
@Data
public class BoardVO {
    private Long bno;
    private String title;
    private String content;
    private String writer;
    private Date regdate;
    private Date updateDate;

    // 댓글의 숫자를 의미
    private int replyCnt;
}
