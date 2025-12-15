package org.example.demo_ssr_v1_1.board;

import lombok.RequiredArgsConstructor;
import org.example.demo_ssr_v1_1._core.errors.exception.Exception403;
import org.example.demo_ssr_v1_1._core.errors.exception.Exception404;
import org.example.demo_ssr_v1_1.user.User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service // IoC 대상 @Component 의 특수 형태
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BoardService {

    private final BoardRepository boardRepository;

    /**
     * 게시글 목록 조회
     * 트랜잭션
     *  - 읽기 전용 트랜잭션 - 성능 최적화
     * @return 게시글 목록 (생성일 기준으로 내림차순)
     */
    public List<Board> 게시글목록조회() {
        return boardRepository.findAllByOrderByCreatedAtDesc();
    }

    public Board 게시글상세조회(Long boardId) {
        return boardRepository.findById(boardId)
                .orElseThrow(() -> new Exception404("게시글을 찾을 수 없습니다."));
    }

    // 1. 트랜잭션 처리
    // 2. repository 저장 처리
    @Transactional
    public Board 게시글작성(BoardRequest.SaveDTO saveDTO, User sessionUser) {
        // DTO 에서 직접 new 해서 생성한 Board 객체일 뿐 아직 영속화 된 객체는 아니다.
        Board board = saveDTO.toEntity(sessionUser);

        return boardRepository.save(board);
    }
    // 1. 게시글 조회
    // 2. 인가 처리
    public Board 게시글수정화면(Long boardId, Long sessionUserId) {
        // 1.
        Board boardEntity = boardRepository.findById(boardId)
                .orElseThrow(() -> new Exception404("게시글을 찾을 수 없습니다."));

        // 2.
        if (!boardEntity.isOwner(sessionUserId)) {
            throw new Exception403("게시글 수정 권한이 없습니다.");
        }

        return boardEntity;
    }

    // 1. 트랜잭션 처리
    // 2. 더티 체킹 -> DB 에서 조회
    // 3. 인가 처리
    // 4. 조회된 board에 상태값 변경 (더티 체킹)
    @Transactional
    public void 게시글수정(BoardRequest.UpdateDTO updateDTO, Long boardId, Long sessionUserId) {
        // 2.
        Board boardEntity = boardRepository.findById(boardId)
                .orElseThrow(() -> new Exception404("게시글이 존재하지 않습니다."));
        // 3.
        if (!boardEntity.isOwner(sessionUserId)) {
            throw new Exception403("게시글 수정 권한이 없습니다.");
        }
        // 4.
        boardEntity.update(updateDTO);

    }
    // 1. 트랜잭션 처리
    // 2. 게시글 조회
    // 3. 인가 처리
    // 4. Repository 에게 삭제 요청
    @Transactional
    public void 게시글삭제(Long boardId, Long sessionUserId) {
        // 2.
        Board boardEntity = boardRepository.findById(boardId)
                .orElseThrow(() -> new Exception404("게시글을 찾을 수 없습니다."));
        // 3.
        if (!boardEntity.isOwner(sessionUserId)) {
            throw new Exception403("게시글 삭제할 권한이 없습니다.");
        }
        // 4.
        boardRepository.deleteById(boardId);
    }
}
