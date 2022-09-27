package org.zerock.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;

import java.util.List;

// 댓글목록과 댓글의 수를 같이 전달하는 클래스
@Data
@AllArgsConstructor
@Getter
public class ReplyPageDTO {

    private int replyCnt;
    private List<ReplyVO> list;
}
