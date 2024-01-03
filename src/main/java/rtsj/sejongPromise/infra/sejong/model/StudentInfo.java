package rtsj.sejongPromise.infra.sejong.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Getter
@RequiredArgsConstructor
public class StudentInfo {
    private final String major;
    private final String studentId;
    private final String name;
    private final String semester;
    private final boolean isPass;
    private final List<ExamInfo> examInfoList;

}
