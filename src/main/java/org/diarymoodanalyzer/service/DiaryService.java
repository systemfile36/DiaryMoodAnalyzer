package org.diarymoodanalyzer.service;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.diarymoodanalyzer.domain.Diary;
import org.diarymoodanalyzer.domain.User;
import org.diarymoodanalyzer.dto.request.AddDiaryRequest;
import org.diarymoodanalyzer.dto.request.GetDiaryByPageRequest;
import org.diarymoodanalyzer.dto.response.AddDiaryResponse;
import org.diarymoodanalyzer.dto.response.GetDiaryByIdResponse;
import org.diarymoodanalyzer.dto.response.GetDiaryByPageResponse;
import org.diarymoodanalyzer.dto.response.GetDiaryTitleByPageResponse;
import org.diarymoodanalyzer.repository.DiaryRepository;
import org.diarymoodanalyzer.repository.UserRepository;
import org.diarymoodanalyzer.util.AuthenticationUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@RequiredArgsConstructor
@Service
public class DiaryService {

    private final DiaryRepository diaryRepository;

    private final UserRepository userRepository;

    @PersistenceContext
    private final EntityManager entityManager;

    //트랜잭션으로 관리
    @Transactional
    public AddDiaryResponse addDiary(AddDiaryRequest dto) throws ResponseStatusException {

        //현재 인증된 사용자의 정보를 가져옴
        String currentUserEmail = AuthenticationUtils.getCurrentUserEmail();

        if(currentUserEmail == null) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "There is no Authentication");
        }

        //이메일을 통해 User의 아이디 값만 받아온다.
        Long userId = userRepository.findIdByEmail(currentUserEmail);

        //못 찾으면 예외
        if(userId == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "not found user with email : " + currentUserEmail);
        }

        //프록시 객체로 받아온다. 실제로 엔티티에 접근하기 전에는 DB에 쿼리하지 않는다.
        User userRef = entityManager.getReference(User.class, userId);

        //빌더로 엔티티의 인스턴스 생성
        Diary diary = Diary.builder()
                .user(userRef)
                .title(dto.getTitle())
                .content(dto.getContent())
                .build();

        //리포지토리에 저장
        Diary savedDiary = diaryRepository.save(diary);

        return new AddDiaryResponse(savedDiary.getId(), currentUserEmail, dto.getTitle());

        /*
        프록시 객체의 속성에 접근하지 않고 단순히 diary에 참조만 넘겼다.
        따라서 실제 DB에서 User 엔티티를 불러오는 쿼리가 실행되지 않는다.
        이를 통해 불필요하게 엔티티 전체를 불러오는 오버헤드를 막을 수 있다.
         */
    }

    public GetDiaryByIdResponse getDiaryById(long id) throws ResponseStatusException {

        //diaryId로 소유자 userId 조회
        Long ownerId = diaryRepository.findUserIdById(id);

        if(ownerId == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "not found with diary id : " + id);
        }

        //현재 인증된 유저의 id가 소유자의 id와 일치하지 않는다면 예외
        if(isInvalidAuthByUserId(ownerId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You have not permission");
        }

        return new GetDiaryByIdResponse(diaryRepository.findById(id).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND,"not found with diary id : " + id)
        ));
    }

    public Page<GetDiaryByPageResponse> getDiariesByEmail(GetDiaryByPageRequest req) throws ResponseStatusException {

        //정렬 기준을 DTO에서 받아와서 할당함.
        Sort sortBy = req.isAscending() ? Sort.by(req.getSortBy()).ascending() : Sort.by(req.getSortBy()).descending();

        //페이지의 번호와 사이즈, 정렬 기준을 지정
        Pageable pageable = PageRequest.of(req.getPage(), req.getSize(), sortBy);

        //현재 인증된 유저 정보 불러옴
        String currentUserEmail = AuthenticationUtils.getCurrentUserEmail();

        if(currentUserEmail == null) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You have not permission");
        }

        Page<Diary> diaries = diaryRepository.findByUserEmail(currentUserEmail, pageable);

        //리포지토리를 통해 페이지로 받아서 컨트롤러로 반환
        //DTO의 생성자로 매핑하여, 원본 엔티티가 아닌 DTO의 페이지로 리턴한다.
        return diaries.map(GetDiaryByPageResponse::new);
    }

    public Page<GetDiaryTitleByPageResponse> getDiariesTitleByEmail(GetDiaryByPageRequest req) throws ResponseStatusException {
        //정렬 기준을 DTO에서 받아와서 할당함.
        Sort sortBy = req.isAscending() ? Sort.by(req.getSortBy()) : Sort.by(req.getSortBy()).descending();

        //페이지의 번호와 사이즈, 정렬 기준을 지정
        Pageable pageable = PageRequest.of(req.getPage(), req.getSize(), sortBy);

        //현재 인증된 유저 정보 불러옴
        String currentUserEmail = AuthenticationUtils.getCurrentUserEmail();

        if(currentUserEmail == null) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You have not permission");
        }

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

    /*
    현재 인증된 유저의 이메일이, 인자로 받은 이메일과 같으면 true.
    otherwise, false
     */
    private boolean validateAuthByEmail(String email) {
        String currentUserEmail = AuthenticationUtils.getCurrentUserEmail();
        return currentUserEmail != null && currentUserEmail.equals(email);
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
