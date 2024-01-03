package rtsj.sejongPromise.infra.sejong.service;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import rtsj.sejongPromise.global.webclient.ChromeAgentWebclient;
import rtsj.sejongPromise.infra.sejong.model.BookCodeInfo;
import rtsj.sejongPromise.infra.sejong.model.BookInfo;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
public class SejongBookService extends SejongScrapper{
    private final String BOOK_INFO_URI;
    private final String BOOK_CODE_URI;
    private final String BASE_URL = "https://classic.sejong.ac.kr";

    public SejongBookService(@ChromeAgentWebclient WebClient webClient,
                             @Value("${sejong.book.info}") String bookInfoUri,
                             @Value("${sejong.book.code}") String bookCodeUri) {
        super(webClient);
        this.BOOK_INFO_URI = bookInfoUri;
        this.BOOK_CODE_URI = bookCodeUri;
    }

    public List<BookInfo> getBookInfo(){
        String html = requestWebInfo(BOOK_INFO_URI);
        return parseBookInfo(html);
    }

    /**
     * 책 정보를 가져옵니다.
     * @param html
     * @return  책 정보 리스트 반환.
     */
    private List<BookInfo> parseBookInfo(String html) {
        List<BookInfo> bookInfoList = new ArrayList<>();

        Document doc = Jsoup.parse(html);
        Elements sections = doc.select("div.listTab li");
        sections.forEach(data -> {
            String section = data.text();
            String id = data.id();
            Elements select = doc.select("#" + id);
            select.forEach(bookInfo ->{
                Elements list = bookInfo.select("ul.book_list li");
                list.forEach(bookData -> {
                    String title = bookData.select("span.book_tit").text();
                    String writer = bookData.select("span.book_wr").text();
                    String com = bookData.select("span.book_com").text();
                    String image = bookData.select("span.book_img img").attr("src");
                    BookInfo dto = new BookInfo(section, title, writer, com, BASE_URL + image);
                    bookInfoList.add(dto);
                });
            });
        });

        return bookInfoList;
    }

    /**
     * 책 코드 정보를 가져옵니다.
     * @param fieldCode
     * @return
     */
    public List<BookCodeInfo> getBookCode(Integer fieldCode){
        List<BookCodeInfo> bookCodeInfoList = new ArrayList<>();
        try{
            Document doc = Jsoup.connect(String.format(BOOK_CODE_URI, fieldCode.toString())).ignoreContentType(true).get();
            JSONParser parser = new JSONParser();
            Object obj = parser.parse(doc.text());
            JSONObject json = (JSONObject) obj;
            JSONArray resultArr = (JSONArray) json.get("results");
            for (Object o : resultArr) {
                JSONObject bookObj = (JSONObject) o;
                BookCodeInfo bookCodeInfo = new BookCodeInfo((String) bookObj.get("bkName"), (Long) bookObj.get("bkCode"));
                bookCodeInfoList.add(bookCodeInfo);
            }
        } catch (IOException | ParseException e ) {
            throw new RuntimeException("책 코드 정보를 가져올 수 없습니다.");
        }
        return bookCodeInfoList;
    }


}
