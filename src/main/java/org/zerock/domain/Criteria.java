package org.zerock.domain;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.web.util.UriComponentsBuilder;


/*
    Criteria : 검색의 기준
 */
@Getter
@Setter
@ToString
public class Criteria {

    // 페이징 처리
    private int pageNum;
    private int amount;

    // 검색 처리
    private String type;
    private String keyword;

    public Criteria() {
        // 1 페이지, 페이지당 10개
        this(1,10);
    }

    public Criteria(int pageNum, int amount) {
        this.pageNum = pageNum;
        this.amount = amount;
    }

    public String[] getTypeArr(){
        // 검색 조건이 T,W,C로 구성되어 있으므로 검색조건 구분을 위해 split("") 사용
        return type == null ? new String[] {} : type.split("");
    }

    // redirect에 필요한 파라미터들을 한번에 처리
    // UriComponentsBuilder는 여러 개의 파라미터들을 연결해서 URL의 형태로 만들어줌
    // -> queryParam() 메서드를 이용해서 파라미터를 URL로 인코딩
    public String getListLink() {
        UriComponentsBuilder builder = UriComponentsBuilder.fromPath("")
                .queryParam("pageNum", this.pageNum)
                .queryParam("amount", this.getAmount())
                .queryParam("type", this.getType())
                .queryParam("keyword", this.getKeyword());

        return builder.toUriString();
    }
}
