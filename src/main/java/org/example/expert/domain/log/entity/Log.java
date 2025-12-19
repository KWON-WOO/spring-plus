package org.example.expert.domain.log.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Getter
@Entity
@Table(name="log")
@NoArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class Log {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name="login_id")
    private Long loginId;
    @Column(name="target_id")
    private Long targetId;

    @CreatedDate
    @Column(updatable = false)
    private LocalDateTime createdAt;

    public Log(Long loginId, Long targetId){
        this.loginId = loginId;
        this.targetId = targetId;
    }
}
