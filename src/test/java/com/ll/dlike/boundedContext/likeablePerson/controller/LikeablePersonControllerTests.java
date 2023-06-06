package com.ll.dlike.boundedContext.likeablePerson.controller;


import com.ll.dlike.base.appConfig.AppConfig;
import com.ll.dlike.boundedContext.instaMember.entity.InstaMember;
import com.ll.dlike.boundedContext.likeablePerson.entity.LikeablePerson;
import com.ll.dlike.boundedContext.likeablePerson.service.LikeablePersonService;
import com.ll.dlike.boundedContext.member.entity.Member;
import com.ll.dlike.boundedContext.member.service.MemberService;
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
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@ActiveProfiles("test")
@TestMethodOrder(MethodOrderer.MethodName.class)
public class LikeablePersonControllerTests {
    @Autowired
    private MockMvc mvc;
    @Autowired
    private MemberService memberService;
    @Autowired
    private LikeablePersonService likeablePersonService;

    @Test
    @DisplayName("등록 폼(인스타 인증을 안해서 폼 대신 메세지)")
    @WithUserDetails("user1")
    void t001() throws Exception {
        // WHEN
        ResultActions resultActions = mvc
                .perform(get("/usr/likeablePerson/like"))
                .andDo(print());

        // THEN
        resultActions
                .andExpect(handler().handlerType(LikeablePersonController.class))
                .andExpect(handler().methodName("showLike"))
                .andExpect(status().is2xxSuccessful())
                .andExpect(content().string(containsString("""
                        먼저 본인의 인스타 아이디를 입력해주세요.
                        """.stripIndent().trim())))
        ;
    }

    @Test
    @DisplayName("등록 폼")
    @WithUserDetails("user2")
    void t002() throws Exception {
        // WHEN
        ResultActions resultActions = mvc
                .perform(get("/usr/likeablePerson/like"))
                .andDo(print());

        // THEN
        resultActions
                .andExpect(handler().handlerType(LikeablePersonController.class))
                .andExpect(handler().methodName("showLike"))
                .andExpect(status().is2xxSuccessful())
                .andExpect(content().string(containsString("""
                        <input type="text" name="username"
                        """.stripIndent().trim())))
                .andExpect(content().string(containsString("""
                        <input type="radio" name="attractiveTypeCode" value="1"
                        """.stripIndent().trim())))
                .andExpect(content().string(containsString("""
                        <input type="radio" name="attractiveTypeCode" value="2"
                        """.stripIndent().trim())))
                .andExpect(content().string(containsString("""
                        <input type="radio" name="attractiveTypeCode" value="3"
                        """.stripIndent().trim())))
                .andExpect(content().string(containsString("""
                        id="btn-like-1"
                        """.stripIndent().trim())));
    }

    @Test
    @DisplayName("등록 폼 처리(user2가 user3에게 호감표시(외모))")
    @WithUserDetails("user2")
    void t003() throws Exception {
        // WHEN
        ResultActions resultActions = mvc
                .perform(post("/usr/likeablePerson/like")
                        .with(csrf()) // CSRF 키 생성
                        .param("username", "insta_user3")
                        .param("attractiveTypeCode", "1")
                )
                .andDo(print());

        // THEN
        resultActions
                .andExpect(handler().handlerType(LikeablePersonController.class))
                .andExpect(handler().methodName("like"))
                .andExpect(status().is3xxRedirection());
    }

    @Test
    @DisplayName("등록 폼 처리(user2가 abcd에게 호감표시(외모), abcd는 아직 우리 서비스에 가입하지 않은상태)")
    @WithUserDetails("user2")
    void t004() throws Exception {
        // WHEN
        ResultActions resultActions = mvc
                .perform(post("/usr/likeablePerson/like")
                        .with(csrf()) // CSRF 키 생성
                        .param("username", "abcd")
                        .param("attractiveTypeCode", "2")
                )
                .andDo(print());

        // THEN
        resultActions
                .andExpect(handler().handlerType(LikeablePersonController.class))
                .andExpect(handler().methodName("like"))
                .andExpect(status().is3xxRedirection());
    }

    @Test
    @DisplayName("수정 폼")
    @WithUserDetails("user3")
    void t014() throws Exception {
        // WHEN
        ResultActions resultActions = mvc
                .perform(get("/usr/likeablePerson/modify/2"))
                .andDo(print());

        // THEN
        resultActions
                .andExpect(handler().handlerType(LikeablePersonController.class))
                .andExpect(handler().methodName("showModify"))
                .andExpect(status().is2xxSuccessful())
                .andExpect(content().string(containsString("""
                        <input type="radio" name="attractiveTypeCode" value="1"
                        """.stripIndent().trim())))
                .andExpect(content().string(containsString("""
                        <input type="radio" name="attractiveTypeCode" value="2"
                        """.stripIndent().trim())))
                .andExpect(content().string(containsString("""
                        <input type="radio" name="attractiveTypeCode" value="3"
                        """.stripIndent().trim())))
                .andExpect(content().string(containsString("""
                        inputValue__attractiveTypeCode = 2;
                        """.stripIndent().trim())))
                .andExpect(content().string(containsString("""
                        id="btn-modify-like-1"
                        """.stripIndent().trim())));
    }

    @Test
    @DisplayName("수정 폼 처리")
    @WithUserDetails("user3")
    void t015() throws Exception {
        // WHEN
        ResultActions resultActions = mvc
                .perform(post("/usr/likeablePerson/modify/2")
                        .with(csrf()) // CSRF 키 생성
                        .param("attractiveTypeCode", "3")
                )
                .andDo(print());

        // THEN
        resultActions
                .andExpect(handler().handlerType(LikeablePersonController.class))
                .andExpect(handler().methodName("modify"))
                .andExpect(status().is3xxRedirection());
    }

    @Test
    @DisplayName("호감목록")
    @WithUserDetails("user3")
    void t005() throws Exception {
        // WHEN
        ResultActions resultActions = mvc
                .perform(get("/usr/likeablePerson/list"))
                .andDo(print());

        // THEN
        resultActions
                .andExpect(handler().handlerType(LikeablePersonController.class))
                .andExpect(handler().methodName("showList"))
                .andExpect(status().is2xxSuccessful())
                .andExpect(content().string(containsString("""
                        data-test="toInstaMember_username=insta_user4"
                        """.stripIndent().trim())))
                .andExpect(content().string(containsString("""
                        data-test="toInstaMember_attractiveTypeDisplayName=외모"
                        """.stripIndent().trim())))
                .andExpect(content().string(containsString("""
                        data-test="toInstaMember_username=insta_user100"
                        """.stripIndent().trim())))
                .andExpect(content().string(containsString("""
                        data-test="toInstaMember_attractiveTypeDisplayName=성격"
                        """.stripIndent().trim())));
    }

    @Test
    @DisplayName("호감취소")
    @WithUserDetails("user3")
    void t006() throws Exception {
        // WHEN
        ResultActions resultActions = mvc
                .perform(
                        delete("/usr/likeablePerson/1")
                                .with(csrf())
                )
                .andDo(print());

        // THEN
        resultActions
                .andExpect(handler().handlerType(LikeablePersonController.class))
                .andExpect(handler().methodName("cancel"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("/usr/likeablePerson/list**"))
        ;

        assertThat(likeablePersonService.findById(1L).isPresent()).isEqualTo(false);
    }

    @Test
    @DisplayName("호감취소(없는거 취소, 취소가 안되어야 함)")
    @WithUserDetails("user3")
    void t007() throws Exception {
        // WHEN
        ResultActions resultActions = mvc
                .perform(
                        delete("/usr/likeablePerson/100")
                                .with(csrf())
                )
                .andDo(print());

        // THEN
        resultActions
                .andExpect(handler().handlerType(LikeablePersonController.class))
                .andExpect(handler().methodName("cancel"))
                .andExpect(status().is4xxClientError())
        ;
    }

    @Test
    @DisplayName("호감취소(권한이 없는 경우, 취소가 안됨)")
    @WithUserDetails("user2")
    void t008() throws Exception {
        // WHEN
        ResultActions resultActions = mvc
                .perform(
                        delete("/usr/likeablePerson/1")
                                .with(csrf())
                )
                .andDo(print());

        // THEN
        resultActions
                .andExpect(handler().handlerType(LikeablePersonController.class))
                .andExpect(handler().methodName("cancel"))
                .andExpect(status().is4xxClientError())
        ;

        assertThat(likeablePersonService.findById(1L).isPresent()).isEqualTo(true);
    }

    @Test
    @DisplayName("인스타아이디가 없는 회원은 대해서 호감표시를 할 수 없다.")
    @WithUserDetails("user1")
    void t009() throws Exception {
        // WHEN
        ResultActions resultActions = mvc
                .perform(post("/usr/likeablePerson/like")
                        .with(csrf()) // CSRF 키 생성
                        .param("username", "insta_user4")
                        .param("attractiveTypeCode", "1")
                )
                .andDo(print());

        // THEN
        resultActions
                .andExpect(handler().handlerType(LikeablePersonController.class))
                .andExpect(handler().methodName("like"))
                .andExpect(status().is4xxClientError());
    }

    @Test
    @DisplayName("본인이 본인에게 호감표시하면 안된다.")
    @WithUserDetails("user3")
    void t010() throws Exception {
        // WHEN
        ResultActions resultActions = mvc
                .perform(post("/usr/likeablePerson/like")
                        .with(csrf()) // CSRF 키 생성
                        .param("username", "insta_user3")
                        .param("attractiveTypeCode", "1")
                )
                .andDo(print());

        // THEN
        resultActions
                .andExpect(handler().handlerType(LikeablePersonController.class))
                .andExpect(handler().methodName("like"))
                .andExpect(status().is4xxClientError());
    }

    @Test
    @DisplayName("특정인에 대해서 호감표시를 중복으로 시도하면 안된다.")
    @WithUserDetails("user3")
    void t011() throws Exception {
        // WHEN
        ResultActions resultActions = mvc
                .perform(post("/usr/likeablePerson/like")
                        .with(csrf()) // CSRF 키 생성
                        .param("username", "insta_user4")
                        .param("attractiveTypeCode", "1")
                )
                .andDo(print());

        // THEN
        resultActions
                .andExpect(handler().handlerType(LikeablePersonController.class))
                .andExpect(handler().methodName("like"))
                .andExpect(status().is4xxClientError());
    }

    @Test
    @DisplayName("한 회원은 호감표시 할 수 있는 최대 인원이 정해져 있다.")
    @WithUserDetails("user5")
    void t012() throws Exception {
        Member memberUser5 = memberService.findByUsername("user5").get();

        IntStream.range(0, (int) AppConfig.getLikeablePersonFromMax())
                .forEach(index -> {
                    likeablePersonService.like(memberUser5, "insta_user%30d".formatted(index), 1);
                });

        // WHEN
        ResultActions resultActions = mvc
                .perform(post("/usr/likeablePerson/like")
                        .with(csrf()) // CSRF 키 생성
                        .param("username", "insta_user111")
                        .param("attractiveTypeCode", "1")
                )
                .andDo(print());

        // THEN
        resultActions
                .andExpect(handler().handlerType(LikeablePersonController.class))
                .andExpect(handler().methodName("like"))
                .andExpect(status().is4xxClientError());
    }

    @Test
    @DisplayName("기존에 호감을 표시한 유저에게 새로운 사유로 호감을 표시하면 추가가 아니라 수정이 된다.")
    @WithUserDetails("user3")
    void t013() throws Exception {
        // WHEN
        ResultActions resultActions = mvc
                .perform(post("/usr/likeablePerson/like")
                        .with(csrf()) // CSRF 키 생성
                        .param("username", "insta_user4")
                        .param("attractiveTypeCode", "2")
                )
                .andDo(print());

        // THEN
        resultActions
                .andExpect(handler().handlerType(LikeablePersonController.class))
                .andExpect(handler().methodName("like"))
                .andExpect(status().is3xxRedirection());

        Optional<LikeablePerson> opLikeablePerson = likeablePersonService.findByFromInstaMember_usernameAndToInstaMember_username("insta_user3", "insta_user4");

        int newAttractiveTypeCode = opLikeablePerson
                .map(LikeablePerson::getAttractiveTypeCode)
                .orElse(-1);

        assertThat(newAttractiveTypeCode).isEqualTo(2);
    }

    @Test
    @DisplayName("호감취소는 쿨타임이 지나야 가능하다.")
    @WithUserDetails("user3")
    void t016() throws Exception {
        // WHEN
        ResultActions resultActions = mvc
                .perform(
                        delete("/usr/likeablePerson/3")
                                .with(csrf())
                )
                .andDo(print());

        // THEN
        resultActions
                .andExpect(handler().handlerType(LikeablePersonController.class))
                .andExpect(handler().methodName("cancel"))
                .andExpect(status().is4xxClientError())
        ;

        assertThat(likeablePersonService.findById(3L).isPresent()).isEqualTo(true);
    }

    @Test
    @DisplayName("호감사유변경은 쿨타임이 지나야 가능하다.")
    @WithUserDetails("user3")
    void t017() throws Exception {
        // WHEN
        ResultActions resultActions = mvc
                .perform(post("/usr/likeablePerson/modify/3")
                        .with(csrf()) // CSRF 키 생성
                        .param("attractiveTypeCode", "3")
                )
                .andDo(print());

        // THEN
        resultActions
                .andExpect(handler().handlerType(LikeablePersonController.class))
                .andExpect(handler().methodName("modify"))
                .andExpect(status().is4xxClientError());

        assertThat(likeablePersonService.findById(3L).get().getAttractiveTypeCode()).isEqualTo(2);
    }

    @Test
    @DisplayName("남성 필터링")
    @WithUserDetails("user4")
    void t018() throws Exception {
        // WHEN
        ResultActions resultActions = mvc
                .perform(get("/usr/likeablePerson/toList?gender=M"))
                .andDo(print());

        // THEN
        MvcResult mvcResult = resultActions
                .andExpect(handler().handlerType(LikeablePersonController.class))
                .andExpect(handler().methodName("showToList"))
                .andExpect(status().is2xxSuccessful())
                .andReturn();

        Map<String, Object> model = mvcResult.getModelAndView().getModel();

        List<LikeablePerson> likeablePeople = (List<LikeablePerson>) model.get("likeablePeople");

        Map<String, Long> countings = likeablePeople
                .stream()
                .map(LikeablePerson::getFromInstaMember)
                .map(InstaMember::getGender)
                .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));

        assertThat(countings.get("M")).isEqualTo(likeablePeople.size());
    }

    @Test
    @DisplayName("여성 필터링")
    @WithUserDetails("user4")
    void t019() throws Exception {
        // WHEN
        ResultActions resultActions = mvc
                .perform(get("/usr/likeablePerson/toList?gender=W"))
                .andDo(print());

        // THEN
        MvcResult mvcResult = resultActions
                .andExpect(handler().handlerType(LikeablePersonController.class))
                .andExpect(handler().methodName("showToList"))
                .andExpect(status().is2xxSuccessful())
                .andReturn();

        Map<String, Object> model = mvcResult.getModelAndView().getModel();

        List<LikeablePerson> likeablePeople = (List<LikeablePerson>) model.get("likeablePeople");

        Map<String, Long> countings = likeablePeople
                .stream()
                .map(LikeablePerson::getFromInstaMember)
                .map(InstaMember::getGender)
                .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));

        assertThat(countings.get("W")).isEqualTo(likeablePeople.size());
    }

    @Test
    @DisplayName("외모 필터링")
    @WithUserDetails("user4")
    void t020() throws Exception {
        // WHEN
        ResultActions resultActions = mvc
                .perform(get("/usr/likeablePerson/toList?attractiveTypeCode=1"))
                .andDo(print());

        // THEN
        MvcResult mvcResult = resultActions
                .andExpect(handler().handlerType(LikeablePersonController.class))
                .andExpect(handler().methodName("showToList"))
                .andExpect(status().is2xxSuccessful())
                .andReturn();

        Map<String, Object> model = mvcResult.getModelAndView().getModel();

        List<LikeablePerson> likeablePeople = (List<LikeablePerson>) model.get("likeablePeople");

        Map<Integer, Long> countings = likeablePeople
                .stream()
                .map(LikeablePerson::getAttractiveTypeCode)
                .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));

        assertThat(countings.get(1)).isEqualTo(likeablePeople.size());
    }
}
