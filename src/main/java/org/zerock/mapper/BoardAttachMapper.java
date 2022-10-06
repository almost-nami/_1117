package org.zerock.mapper;

import org.zerock.domain.BoardAttachVO;

import java.util.List;

public interface BoardAttachMapper {

    public void insert(BoardAttachVO vo);

    public void delete(String uuid);

    public List<BoardAttachVO> findByBno(Long bno);

    // 게시글 삭제 시 첨부파일도 함께 샂게
    public void deleteAll(Long bno);

    public List<BoardAttachVO> getOldFiles();

}
