package org.zerock.controller;

import lombok.extern.log4j.Log4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.zerock.domain.SampleVO;
import org.zerock.domain.Ticket;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

// @RestController : 메서드의 리턴타입으로 사용자가 정의한 클래스 타입을 사용할 수 있고, 이를 JSON이나 XML로 자동으로 처리 가능ㄴ
@RestController
@RequestMapping("/sample")
@Log4j
public class SampleController {
    // produces 속성은 필수가 아니라 생략하는 것도 가능
    @GetMapping(value = "/getText", produces = "text/plain; charset=UTF-8")
    public String getText() {
        log.info("MIME TYPE : " + MediaType.TEXT_PLAIN_VALUE);

        // @Controller의 경우 문자열을 반환하면 JSP 파일의 이름으로 처리됨
        // produces의 속성값으로 지정된 "text/plain" 결과가 나오게 됨
        return "Hi!";
    }

    @GetMapping(value = "/getSample", produces = { MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public SampleVO getSample() {

        return new SampleVO(112, "star", "roard");
    }

    // produces 속성은 필수가 아니라 생략하는 것도 가능
    @GetMapping(value = "/getSample2")
    public SampleVO getSample2() {

        return new SampleVO(113, "rocket", "raccoon");
    }

    @GetMapping(value = "/getList")
    public List<SampleVO> getList() {

        // 1부터 10미만까지 루프를 처리하면서 SampleVO객체를 만들어서 List<SampleVO>로 만들어 냄
        return IntStream.range(1, 10).mapToObj(i -> new SampleVO(i, i + "First", i + "Last"))
                .collect(Collectors.toList());
    }

    @GetMapping(value = "/getMap")
    public Map<String, SampleVO> getMap() {

        Map<String, SampleVO> map = new HashMap<>();
        // key에 속하는 "First"는 XML로 변환되는 경우에 태그의 이름이 됨
        map.put("First", new SampleVO(111, "groot", "junior"));

        return map;
    }

    // ResponseEntity는 데이터와 함께 HTTP 헤더의 상태메시지를 같이 전달하는 용도로 사용
    @GetMapping(value = "/check", params = {"height", "weight"})
    public ResponseEntity<SampleVO> check(Double height, Double weight) {

        SampleVO vo = new SampleVO(0, "" + height, "" + weight);

        ResponseEntity<SampleVO> result = null;

        if(height < 150) {
            result = ResponseEntity.status(HttpStatus.BAD_GATEWAY).body(vo);
        } else {
            result = ResponseEntity.status(HttpStatus.OK).body(vo);
        }

        return result;
    }

    // {}로 처리된 부분은 컨트롤러 메소드에서 변수로 처리 가능
    // @PathVariable은 {}의 이름을 처리할 때 사용 -> 값을 가져 올때 기본데이터형 사용할 수 없음
    @GetMapping("/product/{cat}/{pid}")
    public String[] getPath(
            @PathVariable("cat") String cat,
            @PathVariable("pid") Integer pid) {

        return new String[] {"category : " + cat, "productid : " + pid};
    }

    // JSON으로 전달되는 데이터를 받아서 Ticket 타입으로 변환
    @PostMapping("/ticket")
    public Ticket convert(@RequestBody Ticket ticket) {

        log.info("convert......... ticket" + ticket);

        return ticket;
    }
}
