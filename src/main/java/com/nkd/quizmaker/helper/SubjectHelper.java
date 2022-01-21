package com.nkd.quizmaker.helper;

import com.nkd.quizmaker.mapper.SubjectMapper;
import com.nkd.quizmaker.model.Subject;
import com.nkd.quizmaker.request.SubjectRequest;
import com.nkd.quizmaker.response.BaseResponse;
import com.nkd.quizmaker.service.SubjectService;
import com.nkd.quizmaker.utils.ValidatorUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Component("SubjectHelper")
public class SubjectHelper {

    private final SubjectService subjectService;

    public ResponseEntity<?> getSubjects(Map<String, String> params) {
        //title
        String title = null;
        String paramTitle = params.get("title");
        if (!ValidatorUtils.isNullOrBlank(paramTitle)) {
            title = "%" + paramTitle + "%";
        }
        //orderBy
        String sortBy = "create_date";
        String sortByParam = params.get("sortBy");
        if (!ValidatorUtils.isNullOrBlank(sortByParam)) {
            sortBy = sortByParam;
        }
        //direction
        String direction = "ASC";
        String directionParam = params.get("direction");
        if (!ValidatorUtils.isNullOrBlank(directionParam)) {
            direction = directionParam;
        }
        //page size
        int pageSize = 15;
        String paramPageSize = params.get("pageSize");
        if (ValidatorUtils.isNumeric(paramPageSize)) {
            pageSize = Integer.parseInt(paramPageSize);
        }
        //page count
        int page = 0;
        String pageParam = params.get("page");
        if (ValidatorUtils.isNumeric(pageParam)) {
            page = Integer.parseInt(pageParam);
        }
        PageRequest pageable =
                PageRequest.of(page, pageSize, Sort.by(Sort.Direction.fromString(direction), sortBy));

        List<Subject> subjects = subjectService.getSubjects(title, pageable);
        return ResponseEntity.ok(
                BaseResponse.successData(subjects.stream().map(SubjectMapper::toDto).collect(Collectors.toList()), "Get subjects success!")
        );
    }

    public ResponseEntity<?> saveSubject(SubjectRequest subjectRequest) {
        String name = subjectRequest.getTitle();
        Subject subject = subjectService.getByName(name);
        if (subject != null)
            return ResponseEntity.badRequest().body(BaseResponse.badRequest("Subject has already exist!"));
        Subject s = new Subject();
        s.setTitle(name);
        subjectService.save(s);
        return ResponseEntity.ok("Save subject success!");
    }
}
