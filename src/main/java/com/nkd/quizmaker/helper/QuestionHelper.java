package com.nkd.quizmaker.helper;

import com.nkd.quizmaker.dto.QuestionDto;
import com.nkd.quizmaker.mapper.QuestionMapper;
import com.nkd.quizmaker.model.Question;
import com.nkd.quizmaker.response.BaseResponse;
import com.nkd.quizmaker.service.QuestionService;
import com.nkd.quizmaker.utils.ValidatorUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Slf4j
public class QuestionHelper {

    private final QuestionService questionService;

    /**
     * get questions
     *
     * @param params
     * @return questions with full explain answers
     */
    public ResponseEntity<?> getQuestions(Map<String, String> params) {
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

        List<Question> questions = questionService.getQuestions(title, pageable);
        List<QuestionDto> rs = questions.stream()
                .map(QuestionMapper::toDto).collect(Collectors.toList());

        return ResponseEntity.ok(BaseResponse.successData(rs, "Get questions success!"));
    }

}
