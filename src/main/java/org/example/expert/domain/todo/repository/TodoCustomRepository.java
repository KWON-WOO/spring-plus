package org.example.expert.domain.todo.repository;

import org.example.expert.domain.todo.dto.response.SearchTodoResponse;
import org.example.expert.domain.todo.entity.Todo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;

public interface TodoCustomRepository {
    Todo findByIdWithUser(Long todoId);

    Page<SearchTodoResponse> searchTodo(String title, String nickname, LocalDateTime startDate, LocalDateTime endDate, Pageable pageable);
}
