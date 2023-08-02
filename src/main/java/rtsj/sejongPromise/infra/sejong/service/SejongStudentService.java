package rtsj.sejongPromise.infra.sejong.service;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.reactive.function.client.WebClient;
import rtsj.sejongPromise.domain.book.model.BookField;
import rtsj.sejongPromise.global.webclient.ChromeAgentWebclient;
import rtsj.sejongPromise.infra.sejong.model.ExamInfo;
import rtsj.sejongPromise.infra.sejong.model.SejongAuth;
import rtsj.sejongPromise.infra.sejong.model.StudentInfo;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class SejongStudentService extends SejongScrapper{
    private final String STUDENT_INFO_URI;

    public SejongStudentService(@ChromeAgentWebclient WebClient webClient,
                                @Value("${sejong.student.info}") String studentInfoUri) {
        super(webClient);
        this.STUDENT_INFO_URI = studentInfoUri;
    }

    public StudentInfo createStudentInfo(SejongAuth auth){
        String html = requestWebInfo(auth, STUDENT_INFO_URI);
        return parseStudentInfo(html);
    }

    private StudentInfo parseStudentInfo(String html) {
        List<ExamInfo> examList = new ArrayList<>();

        Document doc = Jsoup.parse(html);

        Elements studentInfo = getStudentInfo(doc);
        String major = studentInfo.get(0).text();
        String studentId = studentInfo.get(1).text();
        String name = studentInfo.get(2).text();
        String semester = studentInfo.get(5).text();
        boolean isPass = studentInfo.get(7).text().contains("인증") | studentInfo.get(7).text().contains("대체이수");

        Elements examInfoList = getExamInfo(doc);
        addExamInfo(examList, examInfoList);
        examList= examList.stream().distinct().collect(Collectors.toList());

        return new StudentInfo(major, studentId, name, semester, isPass, examList);
    }

    private void addExamInfo(List<ExamInfo> examList, Elements examInfoList) {
        for(Element element : examInfoList){
            //filtering -> 도서 인증 영역 텍스트를 가지고 있는 Element.
            List<String> fields = Stream.of(BookField.values()).map(BookField::getName).collect(Collectors.toList());

            for(String field : fields){
                Elements elementsContainingText = element.getElementsContainingText(field);

                if(elementsContainingText.hasText()){
                    Elements examInfo = elementsContainingText.select("td");
                    if(examInfo.text().contains("미응시") || examInfo.text().contains("미이수")){
                        continue;
                    }
                    String[] yearAndSemester = examInfo.get(0).text().split(" ");
                    String examYear = yearAndSemester[0].substring(0, 4);
                    String examSemester = yearAndSemester[1];
                    String title;
                    boolean isTest;
                    if(examInfo.get(1).text().equals(field)) {
                        title = examInfo.get(2).text();
                        isTest = true;
                    }else{
                        title = examInfo.get(3).text();
                        isTest = false;
                    }
                    //no-pass 불합격인 경우.
                    boolean examPass = !examInfo.select("span.no-pass").hasText();

                    ExamInfo exam = new ExamInfo(examYear, examSemester, field, title, examPass, isTest);
                    examList.add(exam);
                }
            }
        }
    }

    private Elements getStudentInfo(Document doc) {
        return doc.select("div.content-section ul.tblA dd");
    }

    private Elements getExamInfo(Document doc) {
        return doc.select("div.content-section div.table_group tbody tr");
    }

}
