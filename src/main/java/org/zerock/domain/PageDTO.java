package org.zerock.domain;

import lombok.Getter;
import lombok.ToString;

/*
    페이징 끝 번호(endPage) 계산
    - 1페이지의 경우 : Math.ceil(0.1) * 10 = 10
    - 10페이지의 경우 : Math.ceil(1) * 10 = 10
    - 11페이지의 경우 : Math.ceil(1.1) * 10 = 20
    -> this.endPage = (int)(Math.ceil(페이지번호 / 10.0)) * 10;

    페이징 시작 번호(startPage) 계산
    - 화면에 10개의 페이지씩만 보여준다면 시작번호는 끝번호 - 9
    -> this.startPage = this.endPage - 9;

    total을 이용한 endPage의 재계산
    - 끝 번호는 전체 데이터 수에 영향을 받음
    - 전체 데이터 수가 80개라면 마지막 페이지는 10이 아닌 8이여야 함
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
