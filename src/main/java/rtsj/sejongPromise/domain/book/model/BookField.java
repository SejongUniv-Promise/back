package rtsj.sejongPromise.domain.book.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@RequiredArgsConstructor
@Getter
public enum BookField {
    W_HISTORY("서양의 역사와 사상", 1000),
    E_HISTORY("동양의 역사와 사상", 2000),
    EW_CULTURE("동서양의 문학", 3000),
    SCIENCE("과학 사상", 4000);

    private final String name;
    private final Integer code;

    private static final Map<String, BookField> BY_LABEL =
            Stream.of(values()).collect(Collectors.toMap(BookField::getName, e -> e));

    private static final Map<Integer, BookField> BY_CODE_LABEL =
            Stream.of(values()).collect(Collectors.toMap(BookField::getCode, e -> e));

    public static BookField of(String name){
        if(BY_LABEL.get(name) == null){
            throw new RuntimeException("존재하지 않는 영역 입니다.");
        }
        return BY_LABEL.get(name);
    }

    public static BookField of(Integer code){
        if(BY_CODE_LABEL.get(code) == null){
            throw new RuntimeException("존재하지 않는 code 입니다.");
        }
        return BY_CODE_LABEL.get(code);
    }
}
