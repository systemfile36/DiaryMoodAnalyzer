package org.diarymoodanalyzer.domain;

import lombok.Getter;

/**
 * depression_level 을 나타내기 위한 Enum
 */
@Getter
public enum DepressionLevel {
    ERROR((byte) -2),
    NOT_REFLECTED((byte) -1),
    ZERO((byte) 0),
    ONE((byte) 1),
    TWO((byte) 2),
    THREE((byte) 3),
    FOUR((byte) 4),
    FIVE((byte) 5),
    SIX((byte) 6),
    SEVEN((byte) 7),
    EIGHT((byte) 8),
    NINE((byte) 9),
    TEN((byte) 10);

    private final byte value;

    DepressionLevel(byte value) {
        this.value = value;
    }

    /**
     * value 값을 통해 Enum 값을 얻는다.
     * @param value 찾을 enum의 value 값
     * @return 해당 value에 맞는 Enum 값
     * @throws IllegalArgumentException 해당 value 값을 가진 Enum 값이 없을 때 발생
     */
    public static DepressionLevel fromValue(byte value) throws IllegalArgumentException {
        for(DepressionLevel level : values()) {
            if(level.value == value)
                return level;
        }
        throw new IllegalArgumentException("Invalid DepressionLevel value : " + value);
    }
}
