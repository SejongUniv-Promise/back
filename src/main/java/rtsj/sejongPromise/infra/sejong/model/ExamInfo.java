package rtsj.sejongPromise.infra.sejong.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Objects;

@Getter
@RequiredArgsConstructor
public class ExamInfo {
    private final String year;
    private final String semester;
    private final String field;
    private final String title;
    private final boolean isPass;
    private final boolean isTest;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ExamInfo examInfo = (ExamInfo) o;
        return Objects.equals(field, examInfo.field) && Objects.equals(title, examInfo.title);
    }

    @Override
    public int hashCode() {
        return Objects.hash(field, title);
    }
}
