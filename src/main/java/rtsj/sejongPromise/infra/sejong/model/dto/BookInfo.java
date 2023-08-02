package rtsj.sejongPromise.infra.sejong.model.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class BookInfo {
    private final String section;
    private final String title;
    private final String writer;
    private final String com;
    private final String imageUrl;
}
