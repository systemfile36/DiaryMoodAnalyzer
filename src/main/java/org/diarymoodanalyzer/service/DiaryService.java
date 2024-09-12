package org.diarymoodanalyzer.service;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.diarymoodanalyzer.domain.Diary;
import org.diarymoodanalyzer.domain.User;
import org.diarymoodanalyzer.dto.AddDiaryRequest;
import org.diarymoodanalyzer.dto.AddDiaryResponse;
import org.diarymoodanalyzer.repository.DiaryRepository;
import org.diarymoodanalyzer.repository.UserRepository;
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
        diaryRepository.save(diary);

        return new AddDiaryResponse(dto.getEmail(), dto.getTitle());

        /*
        프록시 객체의 속성에 접근하지 않고 단순히 diary에 참조만 넘겼다.
        따라서 실제 DB에서 User 엔티티를 불러오는 쿼리가 실행되지 않는다.
        이를 통해 불필요하게 엔티티 전체를 불러오는 오버헤드를 막을 수 있다.
         */
    }
}
