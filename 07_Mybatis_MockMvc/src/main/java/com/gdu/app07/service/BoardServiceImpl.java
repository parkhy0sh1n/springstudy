package com.gdu.app07.service;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.gdu.app07.domain.BoardDTO;
import com.gdu.app07.repository.BoardDAO;

// DispatcherServlet으로부터 Service로 스캔되어 Bean 객체에 저장된다.
@Service
public class BoardServiceImpl implements BoardService {

	@Autowired
	private BoardDAO boardDAO;
	
	@Override
	public List<BoardDTO> getBoardList() {
		return boardDAO.selectBoardList();
	}

	@Override
	public BoardDTO getBoardByNo(HttpServletRequest request) {
		// 파라미터 boardNo가 없으면(null, "") 0을 사용한다.
		// getParametor()는 반환타입이 String 타입이다.
		String strBoardNo = request.getParameter("boardNo");
		// null이 아니거나, 공백문자열이 아니면 int 타입으로 변환하여 반환한다.
		int boardNo = 0;
		if(strBoardNo != null && strBoardNo.isEmpty() == false) {
			boardNo = Integer.parseInt(strBoardNo);
		}
		return boardDAO.selectBoardByNo(boardNo);
	}

	@Override
	public int addBoard(HttpServletRequest request) {
		// null 값이 반환될 경우 NullPointException 에러가 발생하기 때문에 예외처리를 해준다.
		try {
			// 파라미터 title, content, writer를 받아온다.
			String title = request.getParameter("title");
			String content = request.getParameter("content");
			String writer = request.getParameter("writer");
			// BoardDAO로 전달할 BoardDTO를 만든다.
			BoardDTO board = new BoardDTO();
			board.setTitle(title);
			board.setContent(content);
			board.setWriter(writer);
			return boardDAO.insertBoard(board);
		} catch (Exception e) {
			return 0;
		}
	}

	@Override
	public int modifyBoard(HttpServletRequest request) {
		try {
			// 파라미터 title, content, boardNo를 받아온다.
			String title = request.getParameter("title");
			String content = request.getParameter("content");
			int boardNo = Integer.parseInt(request.getParameter("boardNo"));
			// BoardDAO로 전달할 BoardDTO를 만든다.
			BoardDTO board = new BoardDTO();
			board.setTitle(title);
			board.setContent(content);
			board.setBoardNo(boardNo);
			return boardDAO.updateBoard(board);
		} catch (Exception e) {
			return 0;
		}
	}

	@Override
	public int removeBoard(HttpServletRequest request) {
		try {
			// 파라미터 boardNo를 받아온다.
			int boardNo = Integer.parseInt(request.getParameter("boardNo"));
			return boardDAO.deleteBoard(boardNo);
		} catch (Exception e) {
			return 0;
		}
	}
	
	// 트랜잭션 확인
	@Override
	// 선언적 트랜잭션 처리를 지원하는 애너테이션이다.
	@Transactional
	public void testTx() {
		boardDAO.insertBoard(new BoardDTO(0, "타이틀", "콘텐트", "작성자", null, null));
		boardDAO.insertBoard(new BoardDTO());
	}
	

}