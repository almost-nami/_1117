package org.zerock.domain;

import lombok.Data;

import java.util.Date;

/*
    댓글 저장을 위한 클래스
    댓글 자체는 단독으로 CRUD가 가능하므로 PK 부여
    FK설정을 통해서 tbl_board 테이블을 참조하도록 설정
 */
@Data
public class ReplyVO {

    private Long rno;
    private Long bno;

    private String reply;
    private String replyer;
    private Date replyDate;
    private Date updateDate;
}
