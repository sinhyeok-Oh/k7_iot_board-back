package org.example.boardback.common.apis;

public class UserApi {
    private UserApi() {}

    // ==================================================
    // Users
    // ==================================================
    public static final String ROOT = ApiBase.BASE + "/users";

    // ※ ID_ONLY ※
    // : path variable 로 특정 사용자 한 명을 식별하는 경로 조각
    //   > 단독으로 쓰이지 않고 다른 경로와 조합됨
    public static final String ID_ONLY = "/{userId}";

    // 특정 사용자 한 명에 대한 CRUD 접근 용도
    public static final String BY_ID = ROOT + ID_ONLY;

    // 현재 로그인한 사용자 자신의 정보 접근 용도 - userId를 path variable로 받지 않음
    public static final String ME = ROOT + "/me";

    // 특정 유저의 비밀번호 변경/초기화 관련 엔드포인트
    // : {userId}/password
    // - 관리자가 사용자 비밀번호 초기화
    // - 비밀번호 완전 재설정
    public static final String PASSWORD = ID_ONLY + "/password";
}