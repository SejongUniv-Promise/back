package rtsj.sejongPromise.domain.book.repository.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;
import rtsj.sejongPromise.domain.book.model.Book;
import rtsj.sejongPromise.domain.book.model.dto.BookDto;
import rtsj.sejongPromise.domain.book.model.field.BookStatus;
import rtsj.sejongPromise.domain.book.repository.BookMemoryRepository;
import rtsj.sejongPromise.global.config.redis.RedisKeys;

import java.util.*;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class BookRedisRepository implements BookMemoryRepository {
    private final StringRedisTemplate redisTemplate;
    private final ObjectMapper objectMapper;
    @Override
    public List<BookDto> findAll() {
        List<BookDto> ret = new ArrayList<>();
        Map<Object, Object> entries = redisTemplate.opsForHash().entries(RedisKeys.BOOK_INFO_KEY);
        for (Map.Entry<Object, Object> entry : entries.entrySet()) {
            try {
                ret.add(objectMapper.readValue((String) entry.getValue(), BookDto.class));
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        return ret;
    }

    @Override
    public Optional<BookDto> findById(Long id)  {
        Object value = redisTemplate.opsForHash().get(RedisKeys.BOOK_INFO_KEY, id.toString());
        if(value == null){
            return Optional.empty();
        }
        try {
            BookDto bookDto = objectMapper.readValue((String) value, BookDto.class);
            return Optional.of(bookDto);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void flushAll() {
        redisTemplate.delete(RedisKeys.BOOK_INFO_KEY);
    }

    @Override
    public BookDto save(Book book) {
        BookDto bookDto = new BookDto(book);
        String value;
        try {
            value = objectMapper.writeValueAsString(bookDto);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        redisTemplate.opsForHash().put(RedisKeys.BOOK_INFO_KEY, bookDto.getBookId().toString(), value);
        return bookDto;
    }

    @Override
    public List<BookDto> saveAll(List<Book> bookList) {
        List<BookDto> list = bookList.stream()
                .map(BookDto::new)
                .collect(Collectors.toList());
        Map<String, String> map = new HashMap<>();
        for(BookDto dto : list) {
            try {
                map.put(dto.getBookId().toString(), objectMapper.writeValueAsString(dto));
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        }
        redisTemplate.opsForHash().putAll(RedisKeys.BOOK_INFO_KEY, map);
        return list;
    }
}
