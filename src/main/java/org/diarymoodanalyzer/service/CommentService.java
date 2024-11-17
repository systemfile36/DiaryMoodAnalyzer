package org.diarymoodanalyzer.service;

import lombok.RequiredArgsConstructor;
import org.diarymoodanalyzer.domain.*;
import org.diarymoodanalyzer.dto.response.GetCommentByDiaryIdResponse;
import org.diarymoodanalyzer.repository.CommentRepository;
import org.diarymoodanalyzer.repository.DiaryRepository;
import org.diarymoodanalyzer.repository.ExpertRepository;
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

    private final ExpertRepository expertRepository;

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
        String currentUserEmail = AuthenticationUtils.getCurrentUserEmail()
                .orElseThrow(()->new ResponseStatusException(HttpStatus.FORBIDDEN, "You have not permission"));

        //인자의 id로 다이어리 조회
        Diary diary = diaryRepository.findById(id)
                .orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND, "not found diary : " + id));

        if(isOwner(diary, currentUserEmail) || isOwnerManagedBy(diary, currentUserEmail)) {

            //Diary의 List<Comment> comments 를 DTO로 매핑해서 반환한다.
            return diary.getComments().stream()
                    .map((value) -> {
                        GetCommentByDiaryIdResponse res = new GetCommentByDiaryIdResponse();
                        res.setId(value.getId());
                        res.setContent(value.getContent());
                        res.setDiaryId(diary.getId());
                        res.setCreatedAt(value.getCreatedAt());
                        res.setUpdatedAt(value.getUpdatedAt());
                        res.setExpertEmail(value.getExpert().getEmail());
                        return res;
                    })
                    .toList();
        } else {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You have no permission");
        }
    }


    /**
     * 전문가가 작성한 모든 코멘트를 목록으로 반환.
     * @return 코멘트 정보의 List
     */
    public List<GetCommentByDiaryIdResponse> getCommentsByExpert() {
        String currentUserEmail = AuthenticationUtils.getCurrentUserEmail()
                .orElseThrow(()->new ResponseStatusException(HttpStatus.FORBIDDEN, "Invalid Authentication"));


        Expert expert = expertRepository.findByEmail(currentUserEmail)
                .orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND, "no expert : " + currentUserEmail));

        return expert.getComments().stream()
                .map((value) -> {
                    GetCommentByDiaryIdResponse res = new GetCommentByDiaryIdResponse();
                    res.setId(value.getId());
                    res.setContent(value.getContent());
                    //이미 로드한 엔티티 참조
                    res.setExpertEmail(expert.getEmail());
                    res.setDiaryId(value.getDiary().getId());
                    res.setUpdatedAt(value.getUpdatedAt());
                    res.setCreatedAt(value.getCreatedAt());
                    return res;
                })
                .toList();
    }

    /**
     * 지정된 Diary에 코멘트를 추가한다.
     * @param diaryId 코멘트를 추가할 Diary의 id
     * @param content 추가할 코멘트의 본문
     * @throws ResponseStatusException 찾지 못했거나 권한이 없을 때 throw
     */
    public void addCommentToDiary(Long diaryId, String content) throws ResponseStatusException{

        String currentUserEmail = AuthenticationUtils.getCurrentUserEmail()
                .orElseThrow(()->new ResponseStatusException(HttpStatus.FORBIDDEN, "Invalid Authentication"));

        //코멘트가 추가될 다이어리를 불러옴
        Diary diary = diaryRepository.findById(diaryId)
                .orElseThrow(()-> new ResponseStatusException(HttpStatus.NOT_FOUND, "no diary : " + diaryId));

        Expert expert = expertRepository.findByEmail(currentUserEmail)
                .orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND, "no expert : " + currentUserEmail));

        //코멘트가 추가될 다이어리의 소유자가 현재 인증된 유저의 담당 사용자가 맞다면
        //로직 수행
        if(isOwnerManagedBy(diary, currentUserEmail)) {
            Comment comment = new Comment(content);

            //관계 설정
            diary.addComment(comment);
            expert.addComment(comment);

            //저장
            commentRepository.save(comment);
        } else {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You have no permission");
        }
    }

    /**
     * 지정된 코멘트를 삭제한다.
     * @param commentId 삭제할 Comment의 id
     * @throws ResponseStatusException 인증 정보가 바르지 않거나 지정된 엔티티를 찾지 못하거나, 소유자가 아닐 경우 throw
     */
    public void deleteComment(Long commentId) throws ResponseStatusException {

        String currentUserEmail = AuthenticationUtils.getCurrentUserEmail()
                .orElseThrow(()->new ResponseStatusException(HttpStatus.FORBIDDEN, "Invalid Authentication"));

        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND, "no comment : " + commentId));

        //코멘트의 작성 전문가와 현재 인증된 유저가 일치한다면 로직 실행
        if(currentUserEmail.equals(comment.getExpert().getEmail())) {
            //이미 소유 확인을 위해 불러왔으므로 직접 엔티티를 넘겨서 삭제
            commentRepository.delete(comment);
        }
    }

    /**
     * Diary의 소유자인지 여부를 확인하는 메소드.
     * @param diary - 확인할 Diary
     * @param email - 확인할 소유자의 이메일
     * @return Diary 소유자의 email과 인자의 email이 같으면 true, otherwise, false
     */
    private boolean isOwner(Diary diary, String email) {
        return diary.getUser().getEmail().equals(email);
    }

    /**
     * Diary의 소유자 담당 전문가인지 여부를 확인하는 메소드.
     * @param diary - 확인할 Diary
     * @param email - 확인할 전문가의 이메일
     * @return Diary의 소유자 담당 전문가가 email이 맞다면 true, otherwise, false
     */
    private boolean isOwnerManagedBy(Diary diary, String email) {
        return diary.getUser().getExpert().getEmail().equals(email);
    }
}
