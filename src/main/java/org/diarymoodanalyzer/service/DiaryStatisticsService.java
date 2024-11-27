package org.diarymoodanalyzer.service;

import lombok.RequiredArgsConstructor;
import org.diarymoodanalyzer.dto.response.GetAvgDepressionLevel;
import org.diarymoodanalyzer.repository.DiaryRepository;
import org.diarymoodanalyzer.util.AuthenticationUtils;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Diary의 통계 관련 정보를 반환하는 서비스 클래스
 */
@RequiredArgsConstructor
@Service
public class DiaryStatisticsService {

    private final DiaryRepository diaryRepository;

    public GetAvgDepressionLevel getDailyAvgDepressionLevel() {
        String currentUserEmail = AuthenticationUtils.getCurrentUserEmail()
                .orElseThrow(()->new ResponseStatusException(HttpStatus.FORBIDDEN, "you don't have permission"));

        List<Object[]> result = diaryRepository.findDailyDepressionLevelAvg(currentUserEmail);

        

        //컴파일 오류 방지 임시
        return null;
    }

}
