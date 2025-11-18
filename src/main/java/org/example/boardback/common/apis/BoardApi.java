package org.example.boardback.common.apis;

public class BoardApi {
    private BoardApi() {}

    // ==================================================
    // Boards
    // ==================================================

    public static final String ROOT = ApiBase.BASE + "/boards";
    public static final String ID_ONLY = "/{boardId}";
    public static final String BY_ID = ROOT + ID_ONLY;

    // 카테고리
    public static final String CATEGORY = ROOT + "/category/{categoryId}";
    public static final String COUNT_BY_CATEGORY = ROOT + "/category-count";

    // 검색
    public static final String SEARCH = ROOT + "/search";

    // + 페이징 조회
    //   : GET /boards/page?page=0&size=10&sort=createdAt,desc
    //   > 첫 페이지에 10개의 게시물을 최신순(작성순+내림차순)으로 조회
    public static final String PAGE = ROOT + "/page";

    // + 내가 쓴 글
    public static final String MY_BOARDS = ROOT + "/me";

    // + 조회수 증가
    public static final String VIEW = BY_ID + "/view";

    // + 좋아요(LIKE) 기능
    public static final String LIKE = BY_ID + "/like";
    public static final String LIKE_CANCEL = BY_ID + "/like/cancel";
    public static final String LIKE_COUNT = BY_ID + "/like/count";

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