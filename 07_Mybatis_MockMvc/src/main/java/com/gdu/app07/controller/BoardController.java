package com.gdu.app07.controller;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.gdu.app07.service.BoardService;

// DispatcherServlet으로부터 Controller로 스캔되어 Bean 객체에 저장된다.
@Controller
// 특정 URI로 요청을 보내면 Controller에서 어떠한 방식으로 처리할지 정의하는 애너테이션.
// 모든 요청에 대한 Mapping에 /board가 prefix(접두사)로 추가된다.
@RequestMapping("/board")
public class BoardController {
	
	// 클래스간의 의존관계를 Spring Container가 자동으로 연결해주는 애너테이션.
	@Autowired
	private BoardService boardService;
	
	/*
		데이터(속성) 저장 방법
		1. forward  : Model에 attribute로 저장한다.
		2. redirect : RedirectAttributes에 flashAttribute로 저장한다.
	*/
	
	// getBoardList() 서비스가 반환한 List<BoardDTO>를 /WEB-INF/views/board/list.jsp로 전달한다.
	@GetMapping("/list.do")
	// Model : Controller에서 생성된 데이터를 담아 View로 전달할 때 사용하는 객체이다.
	public String list(Model model) {
		// value(boardService.getBoardList()) 객체를 name(boardList) 이름으로 추가한다.(View 코드에서는 name으로 지정한 이름을 통해서 value를 사용한다.)
		model.addAttribute("boardList", boardService.getBoardList());
		return "board/list";
	}
	
	// getBoardByNo() 서비스가 반환한 BoardDTO를 /WEB-INF/views/board/detail.jsp로 전달한다.
	@GetMapping("/detail.do")
	public String detail(HttpServletRequest request, Model model) {
		model.addAttribute("b", boardService.getBoardByNo(request));
		return "board/detail";
	}
	
	@GetMapping("/write.do")
	public String write() {
		return "board/write";
	}
	
	// addBoard() 서비스가 반환한 0 또는 1을 가지고 /board/list.do으로 이동(redirect)한다.
	// addBoard() 서비스가 반환한 0 또는 1은 /WEB-INF/views/board/list.jsp에서 확인한다.
	@PostMapping("/add.do")
	public String add(HttpServletRequest request, RedirectAttributes redirectAttributes) {
		// addAttribute()는 값을 지속적으로 사용해야 할 때, addFlashAttribute()는 일회성으로 사용해야 할 때 사용해야 한다.
		redirectAttributes.addFlashAttribute("addResult", boardService.addBoard(request));
		return "redirect:/board/list.do";
	}
	
	// modifyBoard() 서비스가 반환한 0 또는 1을 가지고 /board/detail.do으로 이동(redirect)한다.
	// modifyBoard() 서비스가 반환한 0 또는 1은 /WEB-INF/views/board/detail.jsp에서 확인한다. 
	@PostMapping("/modify.do")
	public String modify(HttpServletRequest request, RedirectAttributes redirectAttributes) {
		redirectAttributes.addFlashAttribute("modifyResult", boardService.modifyBoard(request));
		return "redirect:/board/detail.do?boardNo=" + request.getParameter("boardNo");
	}
	
	// removeBoard() 서비스가 반환한 0 또는 1을 가지고 /board/list.do으로 이동(redirect)한다.
	// removeBoard() 서비스가 반환한 0 또는 1은 /WEB-INF/views/board/list.jsp에서 확인한다.
	@PostMapping("/remove.do")
	public String remove(HttpServletRequest request, RedirectAttributes redirectAttributes) {
		redirectAttributes.addFlashAttribute("removeResult", boardService.removeBoard(request));
		return "redirect:/board/list.do";
	}

}