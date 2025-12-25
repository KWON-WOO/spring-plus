package org.example.expert.domain.auth.service;

import com.zaxxer.hikari.HikariDataSource;
import org.example.expert.config.PasswordEncoder;
import org.example.expert.domain.user.enums.UserRole;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.sql.*;
import java.util.UUID;
@SpringBootTest
class AuthServiceTest {
    @Autowired
    private HikariDataSource dataSource = new HikariDataSource();
    @Autowired
    private PasswordEncoder passwordEncoder;


    @Test
    void signup_BulkInsert_test() throws SQLException {
//        String email, String nickname, String password, UserRole userRole
        dataSource.setUsername("test");
        dataSource.setPassword("");

        String sql = "insert into users(email,nickname,password,user_role) values(?,?,?,?)";
        Connection connection = dataSource.getConnection();
        connection.setAutoCommit(false);
        PreparedStatement statement = connection.prepareStatement(sql);

        for (int i = 0; i < 5000000; i++) {
            String email = "a"+i+"@gmail.com";
            String nickname = UUID.randomUUID() + "_" + i;
            String password = "Aasdfzxcv"+ i;

            statement.setString(1, email);
            statement.setString(2, nickname);
            statement.setString(3, password);
            statement.setString(4, UserRole.USER.toString());
            statement.addBatch();
            if (i % 20000 == 19999) {
                statement.executeBatch();
                connection.commit();
                statement.clearBatch();
                System.out.println((i+1) + "번째 완료");
            }
        }
        statement.executeBatch();
        connection.commit();
        Statement select = connection.createStatement();
        ResultSet res = select.executeQuery("SELECT COUNT(*) FROM users");
        if (res.next())
            System.out.println("등록된 유저 수 : " +  res.getInt(1));
    }
}