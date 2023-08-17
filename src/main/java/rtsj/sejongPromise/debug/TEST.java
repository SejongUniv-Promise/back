package rtsj.sejongPromise.debug;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import rtsj.sejongPromise.domain.book.model.dto.BookDto;
import rtsj.sejongPromise.domain.book.service.BookService;
import rtsj.sejongPromise.global.config.redis.RedisKeys;
import rtsj.sejongPromise.infra.sejong.model.BookInfo;
import rtsj.sejongPromise.infra.sejong.model.SejongAuth;
import rtsj.sejongPromise.infra.sejong.model.StudentInfo;
import rtsj.sejongPromise.infra.sejong.service.SejongAuthenticationService;
import rtsj.sejongPromise.infra.sejong.service.SejongBookService;
import rtsj.sejongPromise.infra.sejong.service.SejongStudentService;

import java.util.Arrays;
import java.util.List;
import java.util.OptionalInt;

@RestController
@RequestMapping("/test")
@Slf4j
@RequiredArgsConstructor
public class TEST {

    private final SejongBookService sejongBookService;
    private final BookService bookService;
    private final SejongStudentService studentService;
    private final SejongAuthenticationService authService;
    private final StringRedisTemplate redisTemplate;
    private final ObjectMapper objectMapper;

    @GetMapping("/book-info")
    public List<BookInfo> getBookInfo() {

        return sejongBookService.getBookInfo();
    }

    @GetMapping("/redis/book-info")
    public List<BookDto> getBookInfoDto() {
        List<BookDto> list = bookService.list();
        return list;
    }

    @GetMapping("/student-info")
    public StudentInfo getStudentInfo() throws JsonProcessingException {
        SejongAuth auth = authService.getSejongAuth("학번", "비번");
        StudentInfo studentInfo = studentService.crawlStudentInfo(auth);
        return studentInfo;
    }

}
