package rtsj.sejongPromise.domain.book.repository.spec;

import org.springframework.data.jpa.domain.Specification;
import rtsj.sejongPromise.domain.book.model.Book;
import rtsj.sejongPromise.domain.book.model.field.BookField;

public class BookSpec {
    public static Specification<Book> withKeyword(String keyword) {
        if (keyword == null) {
            return Specification.where(null);
        }

        String pattern = "%" + keyword + "%";
        return (root, query, builder) ->
                builder.like(root.get("title"), pattern);
    }

    public static Specification<Book> withField(String field) {
        if (field == null) {
            return Specification.where(null);
        }
        return (root, query, builder) ->
                builder.equal(root.get("field"), BookField.of(field));
    }
}
