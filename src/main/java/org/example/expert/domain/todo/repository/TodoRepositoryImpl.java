package org.example.expert.domain.todo.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.example.expert.domain.todo.entity.QTodo;
import org.example.expert.domain.todo.entity.Todo;
import org.example.expert.domain.user.entity.QUser;

@RequiredArgsConstructor
public class TodoRepositoryImpl implements TodoCustomRepository{
    private final JPAQueryFactory queryFactory;
    QTodo todo = QTodo.todo;
    QUser user = QUser.user;
    @Override
    public Todo findByIdWithUser(Long todoId) {
        return queryFactory
                .selectFrom(todo)
                .leftJoin(todo.user, user).fetchJoin()
                .where(todo.id.eq(todoId))
                .fetchOne();
    }
}
