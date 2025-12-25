package org.example.expert.domain.user.repository;

import org.example.expert.domain.user.dto.response.UserResponse;

import java.util.List;

public interface UserCustomRepository {
    List<UserResponse> findAllByNickname(String nickname);
}
