package org.example.expert.domain.auth.service;

import com.zaxxer.hikari.HikariDataSource;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.expert.config.JwtUtil;
import org.example.expert.config.PasswordEncoder;
import org.example.expert.domain.auth.dto.request.SigninRequest;
import org.example.expert.domain.auth.dto.request.SignupRequest;
import org.example.expert.domain.auth.dto.response.SigninResponse;
import org.example.expert.domain.auth.dto.response.SignupResponse;
import org.example.expert.domain.auth.exception.AuthException;
import org.example.expert.domain.common.exception.InvalidRequestException;
import org.example.expert.domain.user.entity.User;
import org.example.expert.domain.user.enums.UserRole;
import org.example.expert.domain.user.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final HikariDataSource dataSource;

    @Transactional
    public SignupResponse signup(SignupRequest signupRequest) {

        if (userRepository.existsByEmail(signupRequest.getEmail())) {
            throw new InvalidRequestException("이미 존재하는 이메일입니다.");
        }

        String encodedPassword = passwordEncoder.encode(signupRequest.getPassword());

        UserRole userRole = UserRole.of(signupRequest.getUserRole());

        User newUser = new User(
                signupRequest.getEmail(),
                signupRequest.getNickname(),
                encodedPassword,
                userRole
        );
        User savedUser = userRepository.save(newUser);

        String bearerToken = jwtUtil.createToken(savedUser.getId(), savedUser.getEmail(), savedUser.getNickname(), userRole);

        return new SignupResponse(bearerToken);
    }

    public SigninResponse signin(SigninRequest signinRequest) {
        User user = userRepository.findByEmail(signinRequest.getEmail()).orElseThrow(
                () -> new InvalidRequestException("가입되지 않은 유저입니다."));

        // 로그인 시 이메일과 비밀번호가 일치하지 않을 경우 401을 반환합니다.
        if (!passwordEncoder.matches(signinRequest.getPassword(), user.getPassword())) {
            throw new AuthException("잘못된 비밀번호입니다.");
        }

        String bearerToken = jwtUtil.createToken(user.getId(), user.getEmail(), user.getNickname(), user.getUserRole());

        return new SigninResponse(bearerToken);
    }

    public void testBulk() throws SQLException {

        int total = 5_000_000;
        int batchSize = 20_000;

        long startTime = System.currentTimeMillis();
        long stopTime;
        long tempTime = System.currentTimeMillis();
        long totalTime;

        dataSource.setUsername("test");
        dataSource.setPassword("");
        String sql = "INSERT INTO users(email,nickname,password,user_role,created_at,modified_at) VALUES(?,?,?,?,?,?)";
        Connection connection = dataSource.getConnection();
        connection.setAutoCommit(false);
        PreparedStatement statement = connection.prepareStatement(sql);

        for (int i = 0; i < total; i++) {
            String email = "a" + i + "@gmail.com";
            String nickname = UUID.randomUUID() + "_" + i;
            String password = "Aasdfzxcv" + i;

            statement.setString(1, email);
            statement.setString(2, nickname);
            statement.setString(3, password);
            statement.setString(4, UserRole.USER.toString());
            statement.setString(5, LocalDateTime.now().toString());
            statement.setString(6, LocalDateTime.now().toString());
            statement.addBatch();
            if (i % batchSize == (batchSize - 1)) {
                statement.executeBatch();
                connection.commit();
                statement.clearBatch();

                stopTime = System.currentTimeMillis();
                tempTime = stopTime - tempTime;
                totalTime = stopTime - startTime;
                log.info("{}번째 완료\t걸린 시간 : {}ms\t 총 시간 : {}ms", i + 1, tempTime, totalTime);
                tempTime = stopTime;
            }
        }
        statement.executeBatch();
        connection.commit();
        Statement select = connection.createStatement();
        ResultSet res = select.executeQuery("SELECT COUNT(*) FROM users");

        if (res.next())
            log.info("등록된 유저 수 : {}", res.getInt(1));
    }
}
