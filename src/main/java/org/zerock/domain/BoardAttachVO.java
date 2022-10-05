package org.zerock.domain;

import lombok.Data;

/*
    게시물을 등록할 때 첨부파일 테일블 역시 같이 insert 작업이 진행되어야 하므로 트랜잭션 처리가 필요
 */
@Data
public class BoardAttachVO {

    private String uuid;
    private String uploadPath;
    private String fileName;
    private boolean fileType;

    private Long bno;

}
