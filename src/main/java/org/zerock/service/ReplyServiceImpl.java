package org.zerock.service;

import lombok.Setter;
import lombok.extern.log4j.Log4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.zerock.domain.Criteria;
import org.zerock.domain.ReplyPageDTO;
import org.zerock.domain.ReplyVO;
import org.zerock.mapper.BoardMapper;
import org.zerock.mapper.ReplyMapper;

import java.util.List;

/*
    새로운 댓글이 추가되거나 삭제되는 상황이 되면 BoardMapper와 ReplyMapper를 같이 이용해서 처리하고
    이 작업은 트랜잭션으로 처리되어야 함
        -> 댓글의 수가 변하는 작업 : register, remove
 */
@Service
@Log4j
public class ReplyServiceImpl implements ReplyService {

    @Setter(onMethod_ = @Autowired)
    private ReplyMapper mapper;

    @Setter(onMethod_ = @Autowired)
    private BoardMapper boardMapper;

    @Transactional
    @Override
    public int register(ReplyVO vo) {
        log.info("register........" + vo);

        boardMapper.updateReplyCnt(vo.getBno(), 1);

        return mapper.insert(vo);
    }

    @Override
    public ReplyVO get(Long rno) {
        log.info("get............" + rno);

        return mapper.read(rno);
    }

    @Override
    public int modify(ReplyVO vo) {
        log.info("modify........" + vo);

        return mapper.update(vo);
    }


    @Transactional
    @Override
    public int remove(Long rno) {
        log.info("remove......." + rno);

        ReplyVO vo = mapper.read(rno);

        boardMapper.updateReplyCnt(vo.getBno(), -1);

        return mapper.delete(rno);
    }

    @Override
    public List<ReplyVO> getList(Criteria cri, Long bno) {
        log.info("get Reply List of a Board " + bno);

        return mapper.getListWithPaging(cri, bno);
    }

    @Override
    public ReplyPageDTO getListPage(Criteria cri, Long bno) {

        // 페이징 처리를 위해 댓글 수와 List<ReplyVO>를 같이 전달
        return new ReplyPageDTO(
                mapper.getCountByBno(bno), mapper.getListWithPaging(cri, bno)
        );
    }
}
