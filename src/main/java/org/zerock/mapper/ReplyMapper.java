package org.zerock.mapper;

import org.apache.ibatis.annotations.Param;
import org.zerock.domain.Criteria;
import org.zerock.domain.ReplyVO;

import java.util.List;

public interface ReplyMapper {
    // 댓글 등록
    public int insert(ReplyVO vo);

    // 특정 댓글 읽기
    public ReplyVO read(Long rno);

    // 댓글 삭제
    public int delete(Long rno);

    // 댓글 수정
    public int update(ReplyVO reply);

    // 댓글 목록 페이징
    public List<ReplyVO> getListWithPaging(
            @Param("cri") Criteria cri, @Param("bno") Long bno);

    // 특정 게시물의 전체 댓글 수
    public int getCountByBno(Long bno);
}
