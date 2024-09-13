package org.diarymoodanalyzer.service;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityNotFoundException;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class DiaryService {

    private final DiaryRepository diaryRepository;

    private final UserRepository userRepository;

    @PersistenceContext
    private final EntityManager entityManager;

    //트랜잭션으로 관리
    @Transactional
    public AddDiaryResponse addDiary(AddDiaryRequest dto) {

        /*
        실제로는 토큰이 해당 유저에 대해 유효한지 체크하는 과정도 필요함.
         */

        //이메일을 통해 User의 아이디 값만 받아온다.
        Long userId = userRepository.findIdByEmail(dto.getEmail());

        //못 찾으면 예외
        if(userId == null) {
            throw new EntityNotFoundException("not found user with email : " + dto.getEmail());
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

        return new AddDiaryResponse(savedDiary.getDiaryId(), dto.getEmail(), dto.getTitle());

        /*
        프록시 객체의 속성에 접근하지 않고 단순히 diary에 참조만 넘겼다.
        따라서 실제 DB에서 User 엔티티를 불러오는 쿼리가 실행되지 않는다.
        이를 통해 불필요하게 엔티티 전체를 불러오는 오버헤드를 막을 수 있다.
         */
    }

    public GetDiaryByIdResponse getDiaryById(long id) {
        return new GetDiaryByIdResponse(diaryRepository.findById(id).orElseThrow(
                () -> new IllegalArgumentException("not found with userId : " + id)
        ));
    }

    public Page<GetDiaryByPageResponse> getDiariesByEmail(GetDiaryByPageRequest req) {

        //정렬 기준을 DTO에서 받아와서 할당함.
        Sort sortBy = req.isAscending() ? Sort.by(req.getSortBy()) : Sort.by(req.getSortBy()).descending();

        //페이지의 번호와 사이즈, 정렬 기준을 지정
        Pageable pageable = PageRequest.of(req.getPage(), req.getSize(), sortBy);

        Page<Diary> diaries = diaryRepository.findByUserEmail(req.getEmail(), pageable);

        //리포지토리를 통해 페이지로 받아서 컨트롤러로 반환
        //DTO의 생성자로 매핑하여, 원본 엔티티가 아닌 DTO의 페이지로 리턴한다.
        return diaries.map(GetDiaryByPageResponse::new);
    }

    public Page<GetDiaryTitleByPageResponse> getDiariesTitleByEmail(GetDiaryByPageRequest req) {
        //정렬 기준을 DTO에서 받아와서 할당함.
        Sort sortBy = req.isAscending() ? Sort.by(req.getSortBy()) : Sort.by(req.getSortBy()).descending();

        //페이지의 번호와 사이즈, 정렬 기준을 지정
        Pageable pageable = PageRequest.of(req.getPage(), req.getSize(), sortBy);

        return diaryRepository.findByUserEmailOnlyTitle(req.getEmail(), pageable);
    }

    public void deleteDiaryById(long id) {
        diaryRepository.deleteById(id);
    }


}
