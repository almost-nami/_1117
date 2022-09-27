package org.zerock.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

// @NoArgsConstructor : 비어있는 생성자를 만들어 줌
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SampleVO {

    private Integer mno;
    private String firstName;
    private String lastName;

}
