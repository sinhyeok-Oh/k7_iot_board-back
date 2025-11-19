package org.example.boardback.파일업로드시스템;

/*
 * === 파일 업로드 시스템 ===
 * 1) 프로필 이미지: 1개만 업로드 / 교체 가능
 * 2) 게시글 첨부파일: 여러 개 업로드 가능
 *    + 제한된 첨부 개수 지원 (게시글당 최대 N개)
 *
 * 3) 파일은 board-back/server 디스크 저장
 *    board-back (프로젝트 루트 기준)
 *       > src
 *       > upload
 *           > user
 *               > profile
 *           > board
 *               > {boardId}
 * 4) DB에는 파일 메타데이터 저장
 * 5) 기존 엔티티와 코드 구조는 유지하면서 확장 가능한 파일 시스템 작성
 * */

/**
 * 추가 요구 기능
 * 1) 게시글 파일 다운로드 API
 * 2) 게시글 파일 삭제 API
 * 3) 게시글 파일 전체 조회 DTO + 조회 API
 *
 * */

public class 개요 {
}