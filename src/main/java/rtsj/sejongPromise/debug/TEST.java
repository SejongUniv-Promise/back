package rtsj.sejongPromise.debug;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import rtsj.sejongPromise.infra.sejong.model.dto.BookInfo;
import rtsj.sejongPromise.infra.sejong.service.SejongBookService;

import java.util.List;

@RestController
@RequestMapping("/test")
@Slf4j
@RequiredArgsConstructor
public class TEST {

    private final SejongBookService bookService;

    @GetMapping("/book-info")
    public List<BookInfo> getBookInfo() {
        return bookService.getBookInfo();
    }
}
