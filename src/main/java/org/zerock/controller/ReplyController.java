package org.zerock.controller;

import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.zerock.domain.Criteria;
import org.zerock.domain.ReplyPageDTO;
import org.zerock.domain.ReplyVO;
import org.zerock.service.ReplyService;

/*
    등록      /replies/new                POST
    조회      /replies/:rno               GET
    삭제      /replies/:rno               DELETE
    수정      /replies/:rno               PUT or PATCH
    페이지     /replies/pages/:bno/:page   GET

    REST방식으로 동작하는 URL을 설계할 때는 PK를 기준으로 작성하는 것이 좋음
    PK만으로 조회, 수정, 삭제가 가능하기 때문
    댓글의 목록은 PK를 사용할 수 없기 때문에 파라미터로 게시물의 번호(bno)와 페이지 번호(page)를 URL에서 표현
 */
@RequestMapping("/replies")
@RestController
@Log4j
@AllArgsConstructor
public class ReplyController {
    private ReplyService service;

    // consumes(Content-Type)와 produces를 이용해서 JSON방식의 데이터만 처리하도록 하고 문자열을 반환하도록 함
    // @RequestBody를 적용해서 JSON 데이터를 ReplyVO 타입으로 변환하도록 지정
    @PreAuthorize("isAuthenticated()")
    @PostMapping(value = "/new", consumes = "application/json", produces = {MediaType.TEXT_PLAIN_VALUE})
    public ResponseEntity<String> create(@RequestBody ReplyVO vo) {
        log.info("ReplyVO : " + vo);

        int insertCount = service.register(vo);

        log.info("Reply INSERT COUNT : " + insertCount);

        return insertCount == 1
                ? new ResponseEntity<>("success", HttpStatus.OK)
                : new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
    }

    /*
    // 특정 게시물의 댓글 목록 확인
    @GetMapping(value = "/pages/{bno}/{page}",
            produces = {MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<List<ReplyVO>> getList(
            @PathVariable("page") int page, @PathVariable("bno") Long bno
    ) {
        log.info("getList...............");

        Criteria cri = new Criteria(page, 10);

        log.info(cri);

        return new ResponseEntity<>(service.getList(cri, bno), HttpStatus.OK);
    }
     */

    // 페이징 처리 된 댓글 목록
    @GetMapping(value = "/pages/{bno}/{page}",
            produces = {MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<ReplyPageDTO> getList(
            @PathVariable("page") int page, @PathVariable("bno") Long bno) {
        Criteria cri = new Criteria(page, 10);

        log.info("get Reply List bno : " + bno);
        log.info("cri : " + cri);

        return new ResponseEntity<>(service.getListPage(cri, bno), HttpStatus.OK);
    }

    // 댓글 조회
    @GetMapping(value = "/{rno}",
            produces = {MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<ReplyVO> get(@PathVariable("rno") Long rno) {
        log.info("get : " + rno);

        return new ResponseEntity<>(service.get(rno), HttpStatus.OK);
    }

    // 댓글 삭제
    @PreAuthorize("principal.username == #vo.replyer")
    @DeleteMapping(value = "/{rno}")
    public ResponseEntity<String> remove(@RequestBody ReplyVO vo, @PathVariable("rno") Long rno) {
        log.info("remove : " + rno);

        return service.remove(rno) == 1
                ? new ResponseEntity<>("success", HttpStatus.OK)
                : new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
    }

    // 댓글 수정
    // 실제 수정되는 데이터는 JSON 포맷이므로 @RequestBody를 이용해서 처리
    //  -> @RequestBody로 처리되는 데이터는 일반 파라미터나 @PathVariable 파라미터를 처리할 수 없으므로 직접 처리해야 함
    @PreAuthorize("principal.username == #vo.replyer")
    @RequestMapping(method = {RequestMethod.PUT, RequestMethod.PATCH},
        value="/{rno}", consumes = "application/json")
    public ResponseEntity<String> modify(
            @RequestBody ReplyVO vo, @PathVariable("rno") Long rno) {

        log.info("rno : " + rno);
        log.info("modify : " + vo);

        return service.modify(vo) == 1
                ? new ResponseEntity<>("success", HttpStatus.OK)
                : new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
