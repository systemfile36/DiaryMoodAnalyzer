package org.diarymoodanalyzer.domain;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDate;
import java.time.LocalDateTime;

/*
생성시간과 수정된 시간을 가지는 추상 엔티티 클래스. 이 객체는 테이블과 매핑되지 않는다.
여러 테이블에 공통적으로 필요한 컬럼을 하나로 관리하기 위함이다.
이 객체를 상속받은 엔티티들은 이 객체에 정의된 컬럼들을 가지게 될 것이다.
 */
@Getter
@MappedSuperclass //부모 클래스가 가지는 컬럼만 매핑 정보로 제공하기 위해 사용
@EntityListeners(AuditingEntityListener.class) //감시자를 지정.
public abstract class BaseEntity {

    @CreatedDate
    @Column(updatable = false)
    protected LocalDateTime createdAt;

    @LastModifiedDate
    protected LocalDateTime updatedAt;
}
