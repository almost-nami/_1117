package org.zerock.service;

import lombok.Setter;
import lombok.extern.log4j.Log4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.zerock.domain.BoardVO;
import org.zerock.domain.Criteria;
import org.zerock.mapper.BoardMapper;

import java.util.List;

// @Service : 비즈니스 영역을 담당하는 객체임을 표시
@Log4j
@Service
public class BoardServiceImpl implements BoardService {

    // spring 4.3이상에서 자동 처리
    @Setter(onMethod_ = @Autowired)
    private BoardMapper mapper;

    @Override
    public void register(BoardVO board) {
        log.info("register......." + board);

        mapper.insertSelectKey(board);
    }

    @Override
    public BoardVO get(Long bno) {
        log.info("get........." + bno);

        return mapper.read(bno);
    }

    @Override
    public boolean modify(BoardVO board) {
        log.info("modify.........." + board);

        // 수정처리가 되면 1이 리턴됨
        return mapper.update(board) == 1;
    }

    @Override
    public boolean remove(Long bno) {
        log.info("remove.........." + bno);

        // 삭제처리가 되면 1이 리턴됨
        return mapper.delete(bno) == 1;
    }

    /*
    @Override
    public List<BoardVO> getList() {
        log.info("getList..........");

        return mapper.getList();
    }
     */

    @Override
    public List<BoardVO> getList(Criteria cri) {
        log.info("get List with criteria : " + cri);
        return mapper.getListWithPaging(cri);
    }

    @Override
    public int getTotal(Criteria cri) {
        log.info("get total count");

        return mapper.getTotalCount(cri);
    }
}
