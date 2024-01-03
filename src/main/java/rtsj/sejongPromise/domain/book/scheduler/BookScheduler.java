package rtsj.sejongPromise.domain.book.scheduler;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import rtsj.sejongPromise.domain.book.service.BookService;

import javax.annotation.PostConstruct;

@Component
@RequiredArgsConstructor
public class BookScheduler {
    private final BookService bookService;

    @Scheduled(cron = "${book.update-time}")
    public void updateBookList(){
        bookService.updateList();
    }

    @PostConstruct
    public void init(){
        bookService.updateList();
    }

}
