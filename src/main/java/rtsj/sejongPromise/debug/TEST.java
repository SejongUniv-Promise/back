package rtsj.sejongPromise.debug;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import rtsj.sejongPromise.infra.sejong.model.BookInfo;
import rtsj.sejongPromise.infra.sejong.model.SejongAuth;
import rtsj.sejongPromise.infra.sejong.model.StudentInfo;
import rtsj.sejongPromise.infra.sejong.service.SejongAuthenticationService;
import rtsj.sejongPromise.infra.sejong.service.SejongBookService;
import rtsj.sejongPromise.infra.sejong.service.SejongStudentService;

import java.util.List;

@RestController
@RequestMapping("/test")
@Slf4j
@RequiredArgsConstructor
public class TEST {

    private final SejongBookService bookService;
    private final SejongStudentService studentService;
    private final SejongAuthenticationService authService;

    @GetMapping("/book-info")
    public List<BookInfo> getBookInfo() {
        return bookService.getBookInfo();
    }


    @GetMapping("/student-info")
    public StudentInfo getStudentInfo(){
        SejongAuth auth = authService.getSejongAuth("학번", "비번");
        return studentService.crawlStudentInfo(auth);
    }
}
