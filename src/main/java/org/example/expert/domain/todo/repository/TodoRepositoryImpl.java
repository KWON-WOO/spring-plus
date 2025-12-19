package org.example.expert.domain.todo.repository;

import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.example.expert.domain.todo.dto.response.SearchTodoResponse;
import org.example.expert.domain.todo.entity.QTodo;
import org.example.expert.domain.todo.entity.Todo;
import org.example.expert.domain.user.entity.QUser;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

import static org.example.expert.domain.manager.entity.QManager.manager;

@Repository
@RequiredArgsConstructor
public class TodoRepositoryImpl implements TodoCustomRepository {
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


    @Override
    public Page<SearchTodoResponse> searchTodo(String title, String nickname, LocalDateTime startDate, LocalDateTime endDate, Pageable pageable) {


        List<SearchTodoResponse> result = queryFactory
                .select(Projections.constructor(SearchTodoResponse.class,
                        todo.title, todo.createdAt, manager.id.countDistinct(), todo.comments.size())) // 담당자 저장시 중복검사 로직이 없어서 추가한 countDistinct
                .from(todo)
                .leftJoin(todo.managers, manager)
                .leftJoin(manager.user, user)
                .where(
                        containsTitle(title),
                        containsNickname(nickname),
                        afterCreatedAt(startDate),
                        beforeCreatedAt(endDate)
                )
                .groupBy(todo.id)
                .fetch();

        return new PageImpl<>(result, pageable, result.size());
    }

    private BooleanExpression containsTitle(String title) {
        return title.isEmpty() ? null : todo.title.contains(title);
    }

    private BooleanExpression containsNickname(String nickname) {
        return nickname.isEmpty() ? null : user.nickname.contains(nickname);
    }

    private BooleanExpression afterCreatedAt(LocalDateTime startDate) {
        return startDate == null ? null : todo.createdAt.after(startDate);
    }

    private BooleanExpression beforeCreatedAt(LocalDateTime endDate) {
        return endDate == null ? null : todo.createdAt.before(endDate);
    }
}
