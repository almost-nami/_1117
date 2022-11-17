package org.zerock.controller;

import lombok.Setter;
import lombok.extern.log4j.Log4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.zerock.domain.BoardAttachVO;
import org.zerock.domain.BoardVO;
import org.zerock.domain.Criteria;
import org.zerock.domain.PageDTO;
import org.zerock.service.BoardService;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

/*
    Task        URL             Method  Parameter   From        URL이동
    전체목록    /board/list         GET
    등록처리    /board/register     POST    모든 항목   입력화면 필요     이동
    조회      /board/get          GET     bno=123
    삭제처리    /board/remove       POST    bno      입력화면 필요      이동
    수정처리    /board/modify       POST    모든 항목   입력화면 필요     이동
 */
@Controller
@Log4j
@RequestMapping("/board/*")
public class BoardController {

    @Setter(onMethod_ = {@Autowired})
    private BoardService service;

    /*
    @GetMapping("/list")
    public void list(Model model) {
        log.info("list");

        model.addAttribute("list", service.getList());
    }
     */

    @GetMapping("/list")
    public void list(Criteria cri, Model model) {
        log.info("list : " + cri);

        model.addAttribute("list", service.getList(cri));
//        model.addAttribute("pageMaker", new PageDTO(cri, 123));

        //  글 전체 갯수
        int total = service.getTotal(cri);

        log.info("total : " + total);

        model.addAttribute("pageMaker", new PageDTO(cri, total));
    }

    @GetMapping("/register")
    @PreAuthorize("isAuthenticated()")
    public void register() {

    }

    /*
        RedirectAttributes는 파라미터로 선언해서 사용하고, addFlashAttribute(이름, 값)
        메서드를 이용해서 화면에 한 번만 사용하고 다음에는 사용되지 않는 데이터를 전달하기 위해 사용
     */
    @PostMapping("/register")
    @PreAuthorize("isAuthenticated()")
    public String register(BoardVO board, RedirectAttributes rttr) {
        // RedirectAttributes addFlashAttribute() -> 일회성으로만 데이터를 전달

        log.info("-------------------");
        log.info("register : " + board);

        if(board.getAttachList() != null) {
            board.getAttachList().forEach(attach -> log.info(attach));
        }
        log.info("-------------------");

        service.register(board);

        rttr.addFlashAttribute("result", board.getBno());

        // redirect: -> response.sendRedirect()
        return "redirect:/board/list";
    }

    @GetMapping({"/get", "/modify"})
    public void get(@RequestParam("bno") Long bno, @ModelAttribute("cri") Criteria cri, Model model) {
        log.info("/get or modify");

        model.addAttribute("board", service.get(bno));
    }

    @PreAuthorize("principal.username == #board.writer")
    @PostMapping("/modify")
    public String modify(BoardVO board, @ModelAttribute("cri") Criteria cri, RedirectAttributes rttr) {
        log.info("modify : " + board);

        if(service.modify(board)) {
            rttr.addFlashAttribute("result", "success");
        }

        /*
        rttr.addAttribute("pageNum", cri.getPageNum());
        rttr.addAttribute("amount", cri.getAmount());
        rttr.addAttribute("type", cri.getType());
        rttr.addAttribute("keyword", cri.getKeyword());

        return "redirect:/board/list";
         */
        return "redirect:/board/list" + cri.getListLink();
    }

    // UriComponentsBuilder를 이용하면 파라미터들을 GET방식에 적합한 URL로 만들어줌
    @PreAuthorize("principal.username == #writer")
    @PostMapping("/remove")
    public String remove(@RequestParam("bno") Long bno,
                         @ModelAttribute("cri") Criteria cri, RedirectAttributes rttr) {
        log.info("remove..." + bno);

        List<BoardAttachVO> attachList = service.getAttachList(bno);

        if(service.remove(bno)) {

            // delete Attach Files
            deleteFiles(attachList);

            rttr.addFlashAttribute("result", "success");
        }

        /*
        rttr.addAttribute("pageNum", cri.getPageNum());
        rttr.addAttribute("amount", cri.getAmount());
        rttr.addAttribute("type", cri.getType());
        rttr.addAttribute("keyword", cri.getKeyword());

        return "redirect:/board/list";
         */

        return "redirect:/board/list" + cri.getListLink();
    }

    // 첨부파일과 관련된 데이터를 JSON으로 반환 -> RestController가 아니므로 ResponseBody이용해서 JSON으로 변환
    @GetMapping(value = "/getAttachList", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<List<BoardAttachVO>> getAttachList(Long bno) {
        log.info("getAttachList : " + bno);

        return new ResponseEntity<>(service.getAttachList(bno), HttpStatus.OK);
    }

    // 파일 삭제
    private void deleteFiles(List<BoardAttachVO> attachList) {
        if(attachList == null || attachList.size() == 0) {
            return;
        }

        log.info("delete attach files.....................");
        log.info(attachList);

        attachList.forEach(attach -> {
            try {
                Path file = Paths.get("/Users/nami/Documents/spring/upload/"
                        + attach.getUploadPath() + "/" + attach.getUuid() + "_" + attach.getFileName());

                Files.deleteIfExists(file);

                // 이미지파일의 경우 섬네일 파일도 함께 삭제
                if(Files.probeContentType(file).startsWith("image")) {
                    Path thumbNail = Paths.get("/Users/nami/Documents/spring/upload/"
                            + attach.getUploadPath() + "/s_" + attach.getUuid() + "_" + attach.getFileName());

                    Files.delete(thumbNail);
                }
            } catch (Exception e) {
                log.error("delete file error : " + e.getMessage());
            }   // end catch
        });  // end foreach
    }
}
