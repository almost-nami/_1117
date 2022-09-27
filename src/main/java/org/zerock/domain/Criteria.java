package org.zerock.domain;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.web.util.UriComponentsBuilder;

@Getter
@Setter
@ToString
public class Criteria {
    private int pageNum;
    private int amount;

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

    // UriComponentsBuilder는 queryParam() 메서드를 이용해서 파라미터를 URL로 인코딩해줌
    public String getListLink() {
        UriComponentsBuilder builder = UriComponentsBuilder.fromPath("")
                .queryParam("pageNum", this.pageNum)
                .queryParam("amount", this.getAmount())
                .queryParam("type", this.getType())
                .queryParam("keyword", this.getKeyword());

        return builder.toUriString();
    }
}
