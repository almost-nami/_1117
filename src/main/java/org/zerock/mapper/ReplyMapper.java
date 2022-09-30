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

    // 댓글 수정 -> 댓글의 내용(reply)과 최종수정시간(updatedate)을 수정
    public int update(ReplyVO reply);

    // 댓글 목록 페이징
    // @Param의 속성값은 MyBatis에서 SQL을 이용할 때 '#{}'의 이름으로 사용 가능
    public List<ReplyVO> getListWithPaging(
            @Param("cri") Criteria cri, @Param("bno") Long bno);

    // 특정 게시물의 전체 댓글 수
    public int getCountByBno(Long bno);
}
