package rtsj.sejongPromise.domain.book.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import rtsj.sejongPromise.domain.book.model.Book;
import rtsj.sejongPromise.domain.book.model.field.BookField;

import java.util.List;


public interface BookPersistenceRepository extends JpaRepository<Book, Long>, JpaSpecificationExecutor<Book> {

    List<Book> findAllByField(BookField of);
}
