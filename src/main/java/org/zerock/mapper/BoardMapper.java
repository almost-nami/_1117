package org.zerock.mapper;

import org.apache.ibatis.annotations.Param;
import org.zerock.domain.BoardVO;
import org.zerock.domain.Criteria;

import java.util.List;

public interface BoardMapper {
    // @Select("select * from tbl_board where bno > 0")
    //  -> XML에서 SQL문 처리하였으므로 제거
    public List<BoardVO> getList();

    // 페이징 처리 -> Criteria : pageNum과 amount를 같이 전달하는 용도
    public List<BoardVO> getListWithPaging(Criteria cri);

    // insert만 처리되고 생성된 PK 값을 알 필요가 없는 경우
    public void insert(BoardVO board);

    // insert문이 실행되고 생성된 PK 값을 알아야 하는 경우
    public void insertSelectKey(BoardVO board);

    public BoardVO read(Long bno);

    // 정상 삭제되면 1, 해당 게시물이 없으면 0 리턴
    public int delete(Long bno);

    // 수정되면 1 리턴
    public int update(BoardVO board);

    // 전체 개수 구하기
    public int getTotalCount(Criteria cri);

    // replyCnt 업데이트
    // 해당 게시물의 번호 bno와 증가와 감소를 의미하는 amount
    public void updateReplyCnt(@Param("bno") Long bno, @Param("amount") int amount);
}
