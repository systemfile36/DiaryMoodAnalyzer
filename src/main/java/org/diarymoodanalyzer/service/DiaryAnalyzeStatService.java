package org.diarymoodanalyzer.service;

import lombok.RequiredArgsConstructor;
import org.diarymoodanalyzer.aop.LoggingAspect;
import org.diarymoodanalyzer.domain.User;
import org.diarymoodanalyzer.dto.request.DailyAvgDepressionScoreRequest;
import org.diarymoodanalyzer.dto.response.DailyAvgDepressionScoreResponse;
import org.diarymoodanalyzer.repository.DiaryRepository;
import org.diarymoodanalyzer.repository.UserRepository;
import org.diarymoodanalyzer.util.AuthenticationUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Service class to get statistics of diary analyze.
 */
@RequiredArgsConstructor
@Service
public class DiaryAnalyzeStatService {

    private final DiaryRepository diaryRepository;

    private final UserRepository userRepository;

    /**
     * Get current authenticated user's daily average depression score.
     * @param req Request DTO contain date range
     * @return daily average depression score as DTO.
     */
    public DailyAvgDepressionScoreResponse getDailyAvgDepressionScore(DailyAvgDepressionScoreRequest req) {
        String currentUserEmail = AuthenticationUtils.getCurrentUserEmail()
                .orElseThrow(()->new ResponseStatusException(HttpStatus.FORBIDDEN, "you don't have permission"));

        return getDailyAvgDepressionScore(currentUserEmail, req);
    }

    /**
     * Get target user's daily average depression score.<br/>
     * User specified by <code>targetEmail</code> should have current authenticated user as own {@link org.diarymoodanalyzer.domain.Expert Expert}.
     * @param req Request DTO contain date range
     * @param targetEmail email to specify target user
     * @return daily average depression score as DTO.
     */
    public DailyAvgDepressionScoreResponse getDailyAvgDepressionScore(DailyAvgDepressionScoreRequest req, String targetEmail) {
        String currentUserEmail = AuthenticationUtils.getCurrentUserEmail()
                .orElseThrow(()->new ResponseStatusException(HttpStatus.FORBIDDEN, "you don't have permission"));

        return getDailyAvgDepressionScore(currentUserEmail, targetEmail, req);
    }

    /**
     * Get user's daily average depression score.
     * @param email email to specify user
     * @param req Request DTO contain date range
     * @return daily average depression score as DTO.
     */
    public DailyAvgDepressionScoreResponse getDailyAvgDepressionScore(String email, DailyAvgDepressionScoreRequest req) {
        if(req.getStart() != null && req.getEnd() != null) {
            return getDailyAvgDepressionScore(email, req.getStart(), req.getEnd());
        } else {
            return getDailyAvgDepressionScore(email);
        }
    }

    /**
     * Get target user's daily average depression score.<br/>
     * User specified by <code>targetEmail</code> should have user specified by <code>expertEmail</code> as own {@link org.diarymoodanalyzer.domain.Expert Expert}.
     * @param expertEmail email to specify {@link org.diarymoodanalyzer.domain.Expert Expert}.
     * @param targetEmail email to specify target user
     * @param req Request DTO contain date range
     * @return daily average depression score as DTO.
     */
    public DailyAvgDepressionScoreResponse getDailyAvgDepressionScore(
            String expertEmail, String targetEmail, DailyAvgDepressionScoreRequest req
    ) {
        User user = userRepository.findByEmail(targetEmail)
                .orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND, "not found user " + targetEmail));

        if(expertEmail.equals(user.getExpert().getEmail())) {
            return getDailyAvgDepressionScore(targetEmail, req);
        } else {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You have not permission");
        }
    }

    /**
     * Get user's daily average depression score.
     * @param email email to specify user
     * @param start Start of date range
     * @param end End of date range
     * @return daily average depression score as DTO.
     */
    public DailyAvgDepressionScoreResponse getDailyAvgDepressionScore(String email, LocalDate start, LocalDate end) {
        List<Object[]> result = diaryRepository.findDailyDepressionScoreAvg(email, start, end);

        return convertToDto(result);
    }

    /**
     * Get user's daily average depression score. without range
     * @param email email to specify user\
     * @return daily average depression score as DTO.
     */
    public DailyAvgDepressionScoreResponse getDailyAvgDepressionScore(String email) {
        List<Object[]> result = diaryRepository.findDailyDepressionScoreAvg(email);

        return convertToDto(result);
    }

    /**
     * Convert result of native query result to DTO
     * @param nativeResult result of native query from {@link DiaryRepository DiaryRepository}
     * @return converted DTO from <code>nativeResult</code>
     */
    private DailyAvgDepressionScoreResponse convertToDto(List<Object[]> nativeResult) {
        Map<String, Double> map = nativeResult.stream()
                // Convert LocalDataTime to String
                .collect(Collectors.toMap(value -> value[0].toString(), value -> (Double)value[1]));

        return new DailyAvgDepressionScoreResponse(map);
    }
}
