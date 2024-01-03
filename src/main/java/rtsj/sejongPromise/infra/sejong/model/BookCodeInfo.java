package rtsj.sejongPromise.infra.sejong.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class BookCodeInfo {
    private final String title;
    private final Long code;
}

