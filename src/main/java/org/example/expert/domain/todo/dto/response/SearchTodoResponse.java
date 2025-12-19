package org.example.expert.domain.todo.dto.response;

import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class SearchTodoResponse {
    private final String title;
    private final LocalDateTime createdAt;
    private final Long managerCount;
    private final Integer commentCount;

    public SearchTodoResponse(String title, LocalDateTime createdAt, Long managerCount, Integer commentCount) {
        this.title = title;
        this.createdAt = createdAt;
        this.managerCount = managerCount;
        this.commentCount = commentCount;
    }
}
