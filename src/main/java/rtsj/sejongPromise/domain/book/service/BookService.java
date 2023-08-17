package rtsj.sejongPromise.domain.book.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import rtsj.sejongPromise.domain.book.model.Book;
import rtsj.sejongPromise.domain.book.model.dto.BookDto;
import rtsj.sejongPromise.domain.book.model.field.BookField;
import rtsj.sejongPromise.domain.book.repository.BookMemoryRepository;
import rtsj.sejongPromise.domain.book.repository.BookPersistenceRepository;
import rtsj.sejongPromise.domain.book.repository.spec.BookSpec;
import rtsj.sejongPromise.infra.sejong.model.BookCodeInfo;
import rtsj.sejongPromise.infra.sejong.model.BookInfo;
import rtsj.sejongPromise.infra.sejong.service.SejongBookService;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class BookService {
    private static final String TITLE_PATTERN = "[·\\- /*()＊]";

    private final BookMemoryRepository memoryRepository;
    private final BookPersistenceRepository persistenceRepository;
    private final SejongBookService sejongBookService;



    @Transactional
    public void updateList(){
        memoryRepository.flushAll();

        List<Book> bookList = persistenceRepository.findAll();
        List<String> alreadyTitleList = bookList.stream().map(Book::getTitle).collect(Collectors.toList());
        List<BookInfo> bookInfoList = sejongBookService.getBookInfo();
        List<String> updateTitleList = bookInfoList.stream().map(BookInfo::getTitle).collect(Collectors.toList());
        //#1 기존에 있던 책이 사라진 경우
        bookList.forEach(book -> {
            if(!updateTitleList.contains(book.getTitle())){
                book.deprecated();
            }
        });
        // #2 새로운 책이 생긴 경우
        bookInfoList.forEach(book -> {
            if (!alreadyTitleList.contains(book)) {
                Book newBook = Book.builder()
                        .title(book.getTitle())
                        .field(BookField.of(book.getSection()))
                        .writer(book.getWriter())
                        .com(book.getCom())
                        .imageUrl(book.getImageUrl())
                        .build();
                persistenceRepository.save(newBook);
            }
        });

        // #3 책 정보 업데이트
        Arrays.stream(BookField.values()).map(BookField::getCode).forEach(code -> {
            List<Book> dest = persistenceRepository.findAllByField(BookField.of(code));
            List<BookCodeInfo> src = sejongBookService.getBookCode(code);

            dest.forEach(book -> {
                src.forEach(bookCodeInfo -> {
                    if(bookCodeInfo.getTitle().replaceAll(TITLE_PATTERN, "").contains(book.getTitle().replaceAll(TITLE_PATTERN, "").substring(0,2))){
                        book.updateCode(bookCodeInfo.getCode());
                    }
                });
            });
        });

    }

    @Transactional(readOnly = true)
    public List<BookDto> list(){
        List<BookDto> bookDtoList = memoryRepository.findAll();
        if(!bookDtoList.isEmpty()){
            return bookDtoList;
        }
        List<Book> bookList = persistenceRepository.findAll();
        return memoryRepository.saveAll(bookList);
    }

    @Transactional(readOnly = true)
    public List<BookDto> list(String field, String keyword){
        if(field == null && keyword == null) return list();
        Specification<Book> spec = BookSpec.withField(field).and(BookSpec.withKeyword(keyword));
        return persistenceRepository.findAll(spec).stream()
                .map(BookDto::new)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public BookDto findOne(Long bookId){
        return memoryRepository.findById(bookId)
                .orElseGet(() -> {
                    Book book = persistenceRepository.findById(bookId).orElseThrow(RuntimeException::new);
                    return memoryRepository.save(book);
                });
    }


}
