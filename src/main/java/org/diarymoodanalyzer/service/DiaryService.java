package org.diarymoodanalyzer.service;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.diarymoodanalyzer.domain.Diary;
import org.diarymoodanalyzer.domain.Expert;
import org.diarymoodanalyzer.domain.User;
import org.diarymoodanalyzer.domain.UserAuthority;
import org.diarymoodanalyzer.dto.request.*;
import org.diarymoodanalyzer.dto.response.AddDiaryResponse;
import org.diarymoodanalyzer.dto.response.GetDiaryByIdResponse;
import org.diarymoodanalyzer.dto.response.GetDiaryByPageResponse;
import org.diarymoodanalyzer.dto.response.GetDiaryTitleByPageResponse;
import org.diarymoodanalyzer.repository.DiaryRepository;
import org.diarymoodanalyzer.repository.ExpertRepository;
import org.diarymoodanalyzer.repository.UserRepository;
import org.diarymoodanalyzer.util.AuthenticationUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class DiaryService {

    private final DiaryRepository diaryRepository;

    private final UserRepository userRepository;

    private final ExpertRepository expertRepository;

//    private final DiaryEmotionService diaryEmotionService;

    private final DiaryAnalyzeService diaryAnalyzeService;

    private final NotificationService notificationService;

    @PersistenceContext
    private final EntityManager entityManager;

    //트랜잭션으로 관리
    @Transactional
    public AddDiaryResponse addDiary(AddDiaryRequest dto) throws ResponseStatusException {

        //현재 인증된 사용자의 정보를 가져옴
        String currentUserEmail = AuthenticationUtils.getCurrentUserEmail()
                .orElseThrow(()->new ResponseStatusException(HttpStatus.FORBIDDEN, "There is no Authentication"));

        //Get User Entity by email
        User user = userRepository.findByEmail(currentUserEmail)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "not found user with email : " + currentUserEmail));

        //빌더로 엔티티의 인스턴스 생성
        Diary diary = Diary.builder()
                .user(user)
                .title(dto.getTitle())
                .content(dto.getContent())
                .build();

        //리포지토리에 저장하며 저장된 엔티티 받아옴
        //AUTOINCREMENT id 참조를 위해서는 저장하고 나서 받아와야 함
        Diary savedDiary = diaryRepository.save(diary);


        //AI 서버에 분석을 요청한다.
        //비동기로 실행되며, 분석이 완료되면 DB에 반영 될것이다.
//        diaryEmotionService.submitTask(new DiaryEmotionTask(savedDiary));

        // Send analyze request to AI server
        // Will be applied when task completed
        diaryAnalyzeService.submitTask(new DiaryAnalyzeTask(savedDiary));

        Expert expert = user.getExpert();

        // Send notification when expert is not null
        if(expert != null) {
            //알림 전송
            notificationService.sendNotification(currentUserEmail, NotificationRequest.builder()
                    .notificationTypeName("NEW_DIARY")
                    .content("")
                    .values(currentUserEmail)
                    .refLink("/diaries/" + savedDiary.getId())
                    .targetEmail(user.getExpert() != null ? user.getExpert().getEmail() : "") // Set target
                    .build());
        }

        return new AddDiaryResponse(savedDiary.getId(), currentUserEmail, dto.getTitle());

    }

    /**
     * `id`에 해당하는 Diary를 조회해서 DTO로 반환한다.
     * 소유자 User, 또는 소유자 User의 담당 Expert만 접근할 수 있다.
     * @param id 조회할 Diary 엔티티의 id
     * @return 조회된 Diary 엔티티의 DTO
     */
    public GetDiaryByIdResponse getDiaryById(long id) throws ResponseStatusException {

        //diaryId로 소유자 userId 조회
        Long ownerId = diaryRepository.findUserIdById(id); //Query

        //Null check
        if(ownerId == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "not found with diary id : " + id);
        }

        //현재 인증된 유저의 엔티티 가져옴
        User currentUser = AuthenticationUtils.getCurrentUser(); //Query

        //Null check
        if(currentUser == null) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You have not permission");
        }

        // Check Authority and Permission
        if(currentUser.getAuthority() == UserAuthority.USER) {
            //소유자가 아니면 예외
            if(!currentUser.getId().equals(ownerId)) {
                throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You have not permission");
            }
        } else if(currentUser.getAuthority() == UserAuthority.EXPERT) {
            //소유자의 담당 Expert 엔티티 조회
            Expert expert = expertRepository.findExpertByUserId(ownerId) //Query
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "not found with expert id : " + ownerId));

            //현재 인증된 유저와 소유자의 담당 Expert 비교
            if(!currentUser.getId().equals(expert.getId())) {
                throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You have not permission");
            }
        }

        return new GetDiaryByIdResponse(diaryRepository.findById(id).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND,"not found with diary id : " + id)
        ));

        //Query count : [2, 3]
    }

    /**
     * @deprecated this function is deprecated. use `getDiaryById` instead of this
     */
    @Deprecated //로직 통합에 따른 Deprecated
    public GetDiaryByIdResponse getDiaryByIdForExpert(GetDiaryForExpertRequest req) throws ResponseStatusException {

        Diary diary = diaryRepository.findById(req.getId())
                .orElseThrow(()-> new ResponseStatusException(HttpStatus.NOT_FOUND, "not found width diary id : " + req.getId()));


        //조회한 다이어리의 소유자의 담당자와 현재 인증된 유저가 일치하지 않는다면 예외를 던진다.
        if(!validateAuthByEmail(diary.getUser().getExpert().getEmail())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You have not permission");
        } else {
            return new GetDiaryByIdResponse(diary);
        }
    }

    public Page<GetDiaryByPageResponse> getDiariesByEmail(GetDiaryByPageRequest req) throws ResponseStatusException {

        Pageable pageable = req.getPageable();

        //현재 인증된 유저 정보 불러옴
        String currentUserEmail = AuthenticationUtils.getCurrentUserEmail()
                .orElseThrow(()-> new ResponseStatusException(HttpStatus.FORBIDDEN, "You have not permission"));

        Page<Diary> diaries = diaryRepository.findByUserEmail(currentUserEmail, pageable);

        //리포지토리를 통해 페이지로 받아서 컨트롤러로 반환
        //DTO의 생성자로 매핑하여, 원본 엔티티가 아닌 DTO의 페이지로 리턴한다.
        return diaries.map(GetDiaryByPageResponse::new);
    }

    /**
     * Expert가 자신이 관리하는 유저의 Diary를 페이지로 조회하기 위한 서비스 메소드
     * Expert만 접근 가능해야 함 (권한 체크는 컨트롤러 단에서 수행함)
     * @param req 페이징 관련 변수와 조회할 Diary의 주인의 이메일이 포함된 요청 DTO
     * @return 요청한 유저의 다이어리 목록
     * @throws ResponseStatusException - 인증 정보가 잘못되었거나, 매개 변수가 잘못되거나, 권한이 없을 때 발생
     */
    public Page<GetDiaryByPageResponse> getDiariesByEmailForExpert(GetDiaryByPageForExpertRequest req)
            throws ResponseStatusException {

        Pageable pageable = req.getPageable();

        //Get current authenticated user
        User user = AuthenticationUtils.getCurrentUser(); //Query

        if(user == null || user.getAuthority() != UserAuthority.EXPERT) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You have not permission");
        }

        //Cast to Expert. Already check authority.
        Expert expert = (Expert) user;

        //조회할 다이어리의 소유자 이메일
        String ownerEmail = req.getOwnerEmail();

        // 현재 인증된 사용자(전문가)가 다이어리의 소유자를 가지고 있다면 페이지를 구성해서 반환한다.
        if(expertRepository.hasUserByIdAndEmail(expert.getId(), ownerEmail)) { //Query
            //모든 정보가 일치하면 페이지를 구성해서 반환한다.

            Page<Diary> diaries = diaryRepository.findByUserEmail(ownerEmail, pageable); //Query

            //DTO의 생성자로 매핑해서 반환
            return diaries.map(GetDiaryByPageResponse::new);
        } else {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You have not permission");
        }

    }



    public Page<GetDiaryTitleByPageResponse> getDiariesTitleByEmail(GetDiaryByPageRequest req)
            throws ResponseStatusException {

        Pageable pageable = req.getPageable();

        //현재 인증된 유저 정보 불러옴
        String currentUserEmail = AuthenticationUtils.getCurrentUserEmail()
                .orElseThrow(()->new ResponseStatusException(HttpStatus.FORBIDDEN, "You have not permission"));

        return diaryRepository.findByUserEmailOnlyTitle(currentUserEmail, pageable);
    }

    public GetDiaryByIdResponse updateDiaryById(long id, AddDiaryRequest req) {
        // 기존 다이어리 항목 찾기
        Diary diary = diaryRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        // 다이어리 필드 업데이트
        diary.setTitle(req.getTitle());
        diary.setContent(req.getContent());
        // 필요한 경우 추가 필드 업데이트

        // 업데이트된 다이어리 저장
        diaryRepository.save(diary);

        // 응답 DTO로 변환
        return new GetDiaryByIdResponse(diary);
    }

    public void deleteDiaryById(long id) throws ResponseStatusException {

        Long ownerId = diaryRepository.findUserIdById(id);

        if(ownerId == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "not found with diary id : " + id);
        }

        if(isInvalidAuthByUserId(ownerId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You have not permission");
        }

        diaryRepository.deleteById(id);
    }

    /**
     * 현재 인증된 유저의 이메일이, 인자로 받은 이메일과 같은지 여부
     * @param email - 현재 인증된 유저와 비교할 이메일
     * @return 인증 정보의 이메일이 인자와 같으면 true, otherwise, false
     */
    private boolean validateAuthByEmail(String email) {
        String currentUserEmail = AuthenticationUtils.getCurrentUserEmail()
                .orElse("");
        return currentUserEmail.equals(email);
    }

    /*
    현재 인증된 유저의 아이디 값이, 인자로 받은 아이디값과 같으면 true.
    otherwise, false
     */
    private boolean validateAuthByUserId(Long id) {
        Long userId = AuthenticationUtils.getCurrentUserId();
        return userId != null && userId.equals(id);
    }

    /*
    inverted method of validateAuthByUserId(Long)
     */
    private boolean isInvalidAuthByUserId(Long id) {
        return !validateAuthByUserId(id);
    }
}
