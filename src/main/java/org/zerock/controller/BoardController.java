package org.zerock.controller;

import lombok.Setter;
import lombok.extern.log4j.Log4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.zerock.domain.BoardVO;
import org.zerock.domain.Criteria;
import org.zerock.domain.PageDTO;
import org.zerock.service.BoardService;

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

        int total = service.getTotal(cri);

        log.info("total : " + total);

        model.addAttribute("pageMaker", new PageDTO(cri, total));
    }

    @GetMapping("/register")
    public void register() {

    }

    /*
        RedirectAttributes는 파라미터로 선언해서 사용하고, addFlashAttribute(이름, 값)
        메서드를 이용해서 화면에 한 번만 사용하고 다음에는 사용되지 않는 데이터를 전달하기 위해 사용
     */
    @PostMapping("/register")
    public String register(BoardVO board, RedirectAttributes rttr) {
        // RedirectAttributes addFlashAttribute() -> 일회성으로만 데이터를 전달

        log.info("register : " + board);

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
    @PostMapping("/remove")
    public String remove(@RequestParam("bno") Long bno,
                         @ModelAttribute("cri") Criteria cri, RedirectAttributes rttr) {
        log.info("remove..." + bno);

        if(service.remove(bno)) {
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
}
