package rtsj.sejongPromise.domain.book.repository;

import com.fasterxml.jackson.core.JsonProcessingException;
import rtsj.sejongPromise.domain.book.model.Book;
import rtsj.sejongPromise.domain.book.model.dto.BookDto;
import rtsj.sejongPromise.domain.book.model.field.BookStatus;

import java.util.List;
import java.util.Optional;

public interface BookMemoryRepository {

    List<BookDto> findAll();
    Optional<BookDto> findById(Long id);

    void flushAll();

    BookDto save(Book book);

    List<BookDto> saveAll(List<Book> bookList);
}
