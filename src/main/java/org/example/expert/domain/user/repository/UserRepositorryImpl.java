package org.example.expert.domain.user.repository;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.example.expert.domain.user.dto.response.UserResponse;
import org.example.expert.domain.user.entity.QUser;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class UserRepositorryImpl implements UserCustomRepository {
    private final JPAQueryFactory queryFactory;
    QUser user = QUser.user;


    @Override
    public List<UserResponse> findAllByNickname(String nickname) {
        BooleanBuilder builder = new BooleanBuilder();

        if (!nickname.isEmpty())
            builder.and(user.nickname.eq(nickname));

        return queryFactory
                .select(Projections.constructor(UserResponse.class,
                        user.id, user.email, user.nickname))
                .from(user)
                .where(builder)
                .fetch();
    }

}
