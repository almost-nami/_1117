package org.zerock.service;

import lombok.Setter;
import lombok.extern.log4j.Log4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.zerock.domain.BoardAttachVO;
import org.zerock.domain.BoardVO;
import org.zerock.domain.Criteria;
import org.zerock.mapper.BoardAttachMapper;
import org.zerock.mapper.BoardMapper;

import java.util.List;

// @Service : 비즈니스 영역을 담당하는 객체임을 표시
@Log4j
@Service
public class BoardServiceImpl implements BoardService {

    // spring 4.3이상에서 자동 처리
    @Setter(onMethod_ = @Autowired)
    private BoardMapper mapper;

    @Setter(onMethod_ = @Autowired)
    private BoardAttachMapper attachMapper;

    // 게시물의 등록 작업은 tbl_board 테이블과 tbl_attach 테이블 양쪽 모두 insert가 진행되어야 함
    @Transactional
    @Override
    public void register(BoardVO board) {
        log.info("register......." + board);

        mapper.insertSelectKey(board);

        if(board.getAttachList() == null || board.getAttachList().size() <= 0) {
            return;
        }

        board.getAttachList().forEach(attach -> {
            attach.setBno(board.getBno());
            attachMapper.insert(attach);
        });
    }

    @Override
    public BoardVO get(Long bno) {
        log.info("get........." + bno);

        return mapper.read(bno);
    }

    @Transactional
    @Override
    public boolean modify(BoardVO board) {
        log.info("modify.........." + board);

        attachMapper.deleteAll(board.getBno());

        boolean modifyResult = mapper.update(board) == 1;

        if(modifyResult && board.getAttachList() != null && board.getAttachList().size() > 0) {
            board.getAttachList().forEach(attach -> {

                attach.setBno(board.getBno());
                attachMapper.insert(attach);

            });
        }

        return modifyResult;
    }

    @Transactional
    @Override
    public boolean remove(Long bno) {
        log.info("remove.........." + bno);

        attachMapper.deleteAll(bno);

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

    // 게시물의 번호를 이용해 BoardAttachVO로 변환
    @Override
    public List<BoardAttachVO> getAttachList(Long bno) {
        log.info("get Attach list by bno : " + bno);

        return attachMapper.findByBno(bno);
    }
}
