package org.diarymoodanalyzer.service;

import lombok.RequiredArgsConstructor;
import org.diarymoodanalyzer.domain.User;
import org.diarymoodanalyzer.dto.response.GetAvgDepressionLevel;
import org.diarymoodanalyzer.repository.DiaryRepository;
import org.diarymoodanalyzer.repository.UserRepository;
import org.diarymoodanalyzer.util.AuthenticationUtils;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
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

    private final UserRepository userRepository;

    /**
     * 현재 인증된 사용자의 일별 평균 depression level을 반환한다.
     * @return 일별 평균 depression level DTO
     */
    public GetAvgDepressionLevel getDailyAvgDepressionLevel() {
        String currentUserEmail = AuthenticationUtils.getCurrentUserEmail()
                .orElseThrow(()->new ResponseStatusException(HttpStatus.FORBIDDEN, "you don't have permission"));

        List<Object[]> result = diaryRepository.findDailyDepressionLevelAvg(currentUserEmail);

        return convertToDto(result);
    }

    /**
     * 현재 인증된 사용자의 일별 평균 depression level을 반환한다.
     * 인자로 받은 날짜 범위 안에 있는 Diary만 계산한다.
     * (start <= Diary.createdAt <= end)
     * @param start 시작 범위 (include)
     * @param end 종료 범위 (include)
     * @return 지정된 범위의 일별 평균 depression level DTO
     */
    public GetAvgDepressionLevel getDailyAvgDepressionLevel(LocalDate start, LocalDate end) {
        String currentUserEmail = AuthenticationUtils.getCurrentUserEmail()
                .orElseThrow(()->new ResponseStatusException(HttpStatus.FORBIDDEN, "you don't have permission"));

        List<Object[]> result = diaryRepository.findDailyDepressionLevelAvg(currentUserEmail, start, end);

        return convertToDto(result);
    }


    /**
     * 인자로 받은 이메일로 특정된 사용자의 일별 평균 depression level을 반환한다.
     * 현재 인증된 사용자가 해당 사용자의 담당 사용자일 필요가 있다.
     * @param start - 시작 범위 (include)
     * @param end 종료 범위 (include)
     * @param userEmail 조회할 대상 사용자
     * @return 지정한 사용자의 일별 평균 depression level DTO
     */
    public GetAvgDepressionLevel getDailyAvgDepressionLevel(LocalDate start, LocalDate end, String userEmail) {
        String currentUserEmail = AuthenticationUtils.getCurrentUserEmail()
                .orElseThrow(()->new ResponseStatusException(HttpStatus.FORBIDDEN, "you don't have permission"));

        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND, "not found user " + userEmail));

        //만약 현재 인증된 사용자가 조회할 대상의 담당자가 맞다면 로직 수행
        if(currentUserEmail.equals(user.getExpert().getEmail())) {
            List<Object[]> result = diaryRepository.findDailyDepressionLevelAvg(user.getEmail(), start, end);

            return convertToDto(result);
        } else {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "you don't have permission");
        }
    }

    /**
     * List&lt;Object[]&gt;로 받아온 쿼리 결과를 DTO로 변환하는 private 메소드
     * @param avgResult - List&lt;Object[]&gt;로 받은 쿼리 결과 
     * @return DTO로 변환한 결과물
     */
    private GetAvgDepressionLevel convertToDto(List<Object[]> avgResult) {

        Map<String, Double> map = avgResult.stream()
                .collect(Collectors.toMap(value -> value[0].toString(), value -> (Double)value[1]));

        return new GetAvgDepressionLevel(map);
    }

    //테스트용 임시 메소드
    public GetAvgDepressionLevel getDailyAvgDepressionLevel(String userEmail) {

        List<Object[]> result = diaryRepository.findDailyDepressionLevelAvg(userEmail);

        Map<String, Double> map = result.stream()
                .collect(Collectors.toMap(value -> value[0].toString(), value -> (Double)value[1]));


        //컴파일 오류 방지 임시
        return new GetAvgDepressionLevel(map);
    }

}
