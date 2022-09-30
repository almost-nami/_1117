package org.zerock.domain;

import lombok.Data;

import java.util.Date;
import java.util.List;

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

    게시물의 목록 페이지에서 일반적으로 댓글의 숫자고 같이 표시
        -> 댓글을 추가한 뒤에 댓글의 숫자를 표시하려면 조인을 하거나 서브쿼리를 이용해서 처리해야함
        -> tbl_board에 댓글의 숫자를 컬럼으로 처리하는 경우기 많음
        But, 댓글이 추가될 때에는 댓글을 의미하는 tbl_reply 테이블에 insert하고
        댓글의 숫자는 tbl_board 테이블에 update 시켜주는 작업이 필요하고
        두 작업은 하나의 트랜잭션으로 관리되어야 하는 작업 !
 */

// 테이블의 컬럼 구조를 반영하는 VO(Value Object) 클래스 생성
@Data
public class BoardVO {
    private Long bno;
    private String title;
    private String content;
    private String writer;
    private Date regdate;
    private Date updatedate;

    // 댓글의 숫자를 의미
    private int replyCnt;

    // 첨부파일 처리
    private List<BoardAttachVO> attachList;
}
