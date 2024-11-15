package org.diarymoodanalyzer.service;

import lombok.RequiredArgsConstructor;
import org.apache.coyote.Response;
import org.diarymoodanalyzer.domain.Diary;
import org.diarymoodanalyzer.domain.User;
import org.diarymoodanalyzer.domain.UserAuthority;
import org.diarymoodanalyzer.dto.request.GetCommentByDiaryIdRequest;
import org.diarymoodanalyzer.dto.response.GetCommentByDiaryIdResponse;
import org.diarymoodanalyzer.repository.CommentRepository;
import org.diarymoodanalyzer.repository.DiaryRepository;
import org.diarymoodanalyzer.repository.UserRepository;
import org.diarymoodanalyzer.util.AuthenticationUtils;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RequiredArgsConstructor
@Service
public class CommentService {

    private final DiaryRepository diaryRepository;

    private final UserRepository userRepository;

    private final CommentRepository commentRepository;

    /**
     * Diary의 id와 소유자 이메일을 받아서 Comment 목록을 반환한다. <br>
     * 조회를 위해선 해당 Diary가 자신의 소유이거나, 자신의 담당 사용자 소유일 필요가 있다.
     * @param id - 조회할 Comment가 속한 Diary의 id
     * @return Comment의 List
     * @throws ResponseStatusException 권한이 없거나, 찾지 못했을 때 throw
     */
    public List<GetCommentByDiaryIdResponse> getCommentsByDiaryId(Long id)
            throws ResponseStatusException {
        //현재 인증된 유저의 email 로드
        String currentUserEmail = AuthenticationUtils.getCurrentUserEmail();

        if(currentUserEmail == null) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "There is no Authentication");
        }

        //인자의 id로 다이어리 조회
        Diary diary = diaryRepository.findById(id)
                .orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND, "not found diary : " + id));

        //소유자 조회
        User owner = diary.getUser();

        //현재 인증된 유저가 소유자 인가
        boolean isOwner = owner.getEmail().equals(currentUserEmail);

        //현재 인증된 유저가 소유자의 담당자와 일치하는 전문가 인가
        boolean isOwnerManagedBy = (AuthenticationUtils.hasAuthority(UserAuthority.EXPERT.getAuthority())
                && owner.getExpert().getEmail().equals(currentUserEmail));

        if(isOwner || isOwnerManagedBy) {

            //Diary의 List<Comment> comments 를 DTO로 매핑해서 반환한다. 
            return diary.getComments().stream()
                    .map((value) -> {
                        GetCommentByDiaryIdResponse res = new GetCommentByDiaryIdResponse();
                        res.setContent(value.getContent());
                        res.setDiaryId(diary.getId());
                        res.setCreatedAt(value.getCreatedAt());
                        res.setUpdatedAt(value.getUpdatedAt());
                        res.setExpertEmail(currentUserEmail);
                        return res;
                    })
                    .toList();
        } else {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You have no permission");
        }


    }
}
