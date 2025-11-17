package org.example.boardback.common.constants;

/*
* ApiMappingPattern
* --------------------------------------
* - 실무 RESTful Api URL 패턴을 한 곳에서 관리하는 상수 클래스
* - 도메인 단위 그룹 구성
* - 확장성 / 유지보수성 / 일관성 강화
*
* [ 권장 규칙 ]
* 1) BASE = /api/v1
* 2) ROOT = BASE + "/resource"
* 3) ID_ONLY = "/{id}"
* 4) 중첩 리소스는 ROOT + ID_ONLY + "sub-resource"
* */

public class ApiMappingPattern {
    private ApiMappingPattern() {}
    //====================================================
    // 공통 Prefix
    //====================================================
    public static final String API = "/api";
    public static final String V1 = "/v1";
    public static final String BASE = API + V1;

    //====================================================
    // 1. Auth (인증)
    //====================================================
    public static final class Auth {
        private Auth() {}

        public static final String ROOT = BASE + "/auth";

        public static final String LOGIN = ROOT + "/login";
        public static final String LOGOUT= ROOT + "/logout";
        public static final String REFRESH= ROOT + "/refresh";
        public static final String SIGNUP= ROOT + "/signup";

        // 비밀번호 초기화(재설정)를 실제로 수행하는 API
        // : 사용자가 이메일로 받은 토큰을 넣고, 새 비밀번호를 설정하는 완성 단계
        public static final String PASSWORD_RESET = ROOT + "/password/reset";

        // 비밀번호 재설정 토큰이 유효한지 확인하는 API
        // : 사용자가 이메일 링크 클릭
        //  -> 프론트엔드에서 먼저 해당 API로 토큰 유효성 검사
        //  -> 유효하면 비밀번호 변경 화면 열기
        public static final String PASSWORD_VERITY = ROOT + "/password/verify";

        //====================================================
        // 1. Users
        //====================================================
        public static final class Users {
            private Users() {}

            public static final String ROOT = BASE + "/users";

            // ID_ONLY
            // : path variable 로 특정 사용자 한 명을 식별하는 경로 조각
            //  > 단독으로 쓰이지 않고 다른 경로와 조합됨
            public static final String ID_ONLY = "/{userId}";

            // 특정 사용자 한 명에 대한 CRUD 접근 용도
            public static final String BY_ID = ROOT + ID_ONLY;

            // 현재 로그인한 사용자 자신의 정보 접근 용도 - userId를 path variable로 받지 않음
            public static final String ME = ROOT + "/me";

            // 특정 유저의 비밀번호 변경/초기화 관련 엔드포인트
            // : {userId}/password
            // - 관리자가 사용자 비밀번호 초기화
            // - 비밀번호 완전 재설정
            public  static final String PASSWORD = ID_ONLY + "/password";

            //====================================================
            // 3. Boards
            //====================================================
            public static final class Boards {
                private Boards() {}

                public static final String ROOT = BASE + "/boards";
                public static final String ID_ONLY = "/{boardID}";
                public static final String BY_ID = ROOT + ID_ONLY;

                // 카테고리
                public static final String CATEGORY = ROOT + "/category/{categoryId}";
                public static final String COUNT_BY_CATEGORY = ROOT + "/category-count";

                // 검색
                public static final String SEARCH = ROOT + "/search";

                // + 페이징 조회
                //    : GET /boards/page?page=0&size=10&sort=createAt,desc
                //    > 첫 페이지에 10개의 게시물을 최신순(작성순+내림차순)으로 조회
                public static final String PAGE = ROOT + "/page";

                // + 내가 쓴 글
                public static final String MY_BOARDS = ROOT + "/me";

                // + 조회수 증가
                public static final String VIEW = BY_ID + "/view";

                // + 좋아요(LIKE) 기능
                public static final String LIKE = BY_ID + "/like";
                public static final String LIKE_CANCEL = BY_ID + "/like/cancel";
                public static final String LIKE_COUNT = BY_ID + "/like/count";

                // + 댓글 기능
                public static final String COMMENTS = BY_ID + "/comments";
                public static final String COMMENTS_BY_ID = COMMENTS + "/{commentId}";

                // + 게시글 고정 (Pin / Notice 기능)
                public static final String PIN = BY_ID + "/pin";
                public static final String UNPIN = BY_ID + "/unpin";
                public static final String PINNED_LIST = ROOT + "/pinned";

                // + 게시글 신고 (Report / Abuse)
                public static final String REPORT = BY_ID + "/report";

                // + 임시 저장
                public static final String DRAFT = ROOT + "/draft";
                public static final String DRAFT_BY_ID = DRAFT + "/{draftId}";

                // + 통계
                public static final String STATS = ROOT + "/stats";
                public static final String STATS_DAIRY = STATS + "/daily";
                public static final String STATS_MONTHLY = STATS + "/monthly";
                public static final String STATS_GENDER = STATS + "/gender";


            }

        }

    }

}
