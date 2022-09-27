package org.zerock.domain;

import lombok.Getter;
import lombok.ToString;

/*
    페이징 끝 번호(endPage) 계산
    -> this.endPage = (int)(Math.ceil(페이지번호 / 10.0)) * 10;

    페이징 시작 번호(startPage) 계산
    -> this.startPage = this.endPage - 9;

    total을 이용한 endPage의 재계산
    -> realEnd = (int)(Math.ceil((total * 1.0) / amount));
        if(realEnd < this.endPage) {
            this.endPage = realEnd;
        }

    이전(prev) 계산
    -> this.prev = this.startPage > 1;

    다음(next) 계산
    -> this.next = this.endPage < realEnd;
 */
@Getter
@ToString
public class PageDTO {
    private int startPage;
    private int endPage;
    private boolean prev;
    private boolean next;

    private int total;
    private Criteria cri;

    public PageDTO(Criteria cri, int total) {
        this.cri = cri;
        this.total = total;

        this.endPage = (int)(Math.ceil(cri.getPageNum() / 10.0)) * 10;

        this.startPage = this.endPage - 9;

        int realEnd = (int)(Math.ceil((total * 1.0) / cri.getAmount()));

        if(realEnd < this.endPage) {
            this.endPage = realEnd;
        }

        this.prev = this.startPage > 1;

        this.next = this.endPage < realEnd;
    }
}
