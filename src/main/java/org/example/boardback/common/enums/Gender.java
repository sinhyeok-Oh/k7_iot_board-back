package org.example.boardback.common.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.Getter;

@Getter
public enum Gender {

    MALE("M", "남성"),
    FEMALE("F", "여성"),
    OTHER("O", "기타"),
    UNKNOWN("U", "미지정");

    private final String code;
    private final String description;

    Gender(String code, String description) {
        this.code = code;
        this.description = description;
    }

    /**
     * 문자열 code → Gender enum 변환
     * 잘못된 입력 시 UNKNOWN 반환 (Null-safe)
     */
    @JsonCreator
    public static Gender fromCode(String code) {
        if (code == null) return UNKNOWN;

        for (Gender g : values()) {
            if (g.code.equalsIgnoreCase(code)) {
                return g;
            }
        }
        return UNKNOWN;
    }
}