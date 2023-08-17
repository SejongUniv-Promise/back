package rtsj.sejongPromise.domain.book.model.dto;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import rtsj.sejongPromise.domain.book.model.Book;

@Getter
@EqualsAndHashCode
@RequiredArgsConstructor
public class BookDto {
    private final Long bookId;
    private final String title;
    private final String field;
    private final String writer;
    private final String com;
    private final String imageUrl;
    private final Long code;

    public BookDto(Book book){
        this.bookId = book.getId();
        this.title = book.getTitle();
        this.field = book.getField().getName();
        this.writer = book.getWriter();
        this.com = book.getCom();
        this.imageUrl = book.getImageUrl();
        this.code = book.getCode();
    }
}
