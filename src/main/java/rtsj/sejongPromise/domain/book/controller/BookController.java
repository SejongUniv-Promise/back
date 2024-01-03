package rtsj.sejongPromise.domain.book.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import rtsj.sejongPromise.domain.book.model.dto.BookDto;
import rtsj.sejongPromise.domain.book.service.BookService;

import java.util.List;

@RestController
@RequestMapping("/api/book")
@RequiredArgsConstructor
public class BookController {
    private final BookService service;

    @GetMapping
    public List<BookDto> list(@RequestParam(required = false) String field,
                              @RequestParam(required = false) String keyword){
        return service.list(field, keyword);
    }

    @GetMapping("/{bookId}")
    public BookDto findOne(@PathVariable Long bookId){
        return service.findOne(bookId);
    }
}
