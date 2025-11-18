package org.example.boardback.common.apis;

public class BoardCommentApi {
    private BoardCommentApi() {}

    // ==================================================
    // Comment
    // ==================================================
    public static final String ROOT = ApiBase.BASE + "/boards/{boardId}/comments";
    public static final String COMMENTS_BY_ID = ROOT + "/{commentId}";
}