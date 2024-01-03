package rtsj.sejongPromise.domain.book.model;

import lombok.*;
import org.hibernate.annotations.Where;
import rtsj.sejongPromise.domain.book.model.field.BookField;
import rtsj.sejongPromise.domain.book.model.field.BookStatus;
import rtsj.sejongPromise.global.model.BaseEntity;

import javax.persistence.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Where(clause = "status = 'ACTIVE'")
public class Book extends BaseEntity {
    @Id @GeneratedValue
    @Column(name = "book_id")
    private Long id;

    private String title;

    @Enumerated(EnumType.STRING)
    private BookField field;

    private String writer;

    private String com;

    private String imageUrl;

    private Long code;

    @Enumerated(EnumType.STRING)
    private BookStatus status;

    @Builder
    private Book(@NonNull String title,
                 @NonNull BookField field,
                 @NonNull String writer,
                 @NonNull String com,
                 @NonNull String imageUrl){
        this.title = title;
        this.field = field;
        this.writer = writer;
        this.com = com;
        this.imageUrl = imageUrl;
        this.status = BookStatus.ACTIVE;
    }

    public void deprecated() {
        this.status = BookStatus.DEPRECATED;
    }

    public void updateCode(Long code) {
        this.code = code;
    }
}
