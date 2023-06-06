package com.ll.dlike.boundedContext.notification.controller;


import com.ll.dlike.boundedContext.notification.entity.Notification;
import com.ll.dlike.boundedContext.notification.service.NotificationService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.not;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest // 스프링부트 관련 컴포넌트 테스트할 때 붙여야 함, Ioc 컨테이너 작동시킴
@AutoConfigureMockMvc // http 요청, 응답 테스트
@Transactional // 실제로 테스트에서 발생한 DB 작업이 영구적으로 적용되지 않도록, test + 트랜잭션 => 자동롤백
@ActiveProfiles("test") // application-test.yml 을 활성화 시킨다.
@TestMethodOrder(MethodOrderer.MethodName.class)
public class NotificationControllerTests {
    @Autowired
    private MockMvc mvc;
    @Autowired
    private NotificationService notificationService;

    @Test
    @DisplayName("알림목록에 접속했을 때 아직 읽음처리되지 않은 것들을 읽음처리 한다.")
    @WithUserDetails("user5")
    void t001() throws Exception {
        List<Notification> notifications = notificationService.findByToInstaMember_username("insta_user5");

        long unreadCount = notifications
                .stream()
                .filter(notification -> !notification.isRead())
                .count();

        assertThat(unreadCount).isEqualTo(1);

        // WHEN
        ResultActions resultActions = mvc
                .perform(get("/usr/notification/list"))
                .andDo(print());

        unreadCount = notifications
                .stream()
                .filter(notification -> !notification.isRead())
                .count();

        assertThat(unreadCount).isEqualTo(0);
    }

    @Test
    @DisplayName("아직 읽지 않은 알림이 있을 때 상단바에 인디케이터 표시")
    @WithUserDetails("user4")
    void t002() throws Exception {
        // WHEN
        ResultActions resultActions = mvc
                .perform(get("/usr/home/about"))
                .andDo(print());

        // THEN
        resultActions
                .andExpect(status().is2xxSuccessful())
                .andExpect(content().string(containsString("""
                        data-test="hasUnreadNotifications"
                        """.stripIndent().trim())));
    }

    @Test
    @DisplayName("아직 읽지 않은 알림이 있을 때, 알림리스트에 접속하면 상단바에 인디케이터 표시가 더 이상 안됨")
    @WithUserDetails("user4")
    void t003() throws Exception {
        // WHEN
        ResultActions resultActions = mvc
                .perform(get("/usr/notification/list"))
                .andDo(print());

        // THEN
        resultActions
                .andExpect(status().is2xxSuccessful())
                .andExpect(content().string(not(containsString("""
                        data-test="hasUnreadNotifications"
                        """.stripIndent().trim()))));
    }
}
