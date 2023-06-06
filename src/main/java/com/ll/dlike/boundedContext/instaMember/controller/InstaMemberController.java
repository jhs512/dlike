package com.ll.dlike.boundedContext.instaMember.controller;

import com.ll.dlike.base.rq.Rq;
import com.ll.dlike.base.rsData.RsData;
import com.ll.dlike.boundedContext.instaMember.entity.InstaMember;
import com.ll.dlike.boundedContext.instaMember.service.InstaMemberService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;


@Controller
@RequestMapping("/usr/instaMember")
@RequiredArgsConstructor
public class InstaMemberController {
    private final Rq rq;
    private final InstaMemberService instaMemberService;

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/connect")
    public String showConnect() {
        return "usr/instaMember/connect";
    }

    @AllArgsConstructor
    @Getter
    public static class ConnectForm {
        @NotBlank
        @Size(min = 3, max = 30)
        private final String username;
        @NotBlank
        @Size(min = 1, max = 1)
        private final String gender;
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/connect")
    public String connect(@Valid ConnectForm connectForm) {
        RsData<InstaMember> rsData = instaMemberService.connect(rq.getMember(), connectForm.getUsername(), connectForm.getGender());

        if (rsData.isFail()) {
            return rq.historyBack(rsData);
        }

        return rq.redirectWithMsg("/usr/likeablePerson/like", "인스타그램 계정이 연결되었습니다.");
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/connectByApi")
    public String showConnectByApi() {
        return "usr/instaMember/connectByApi";
    }

    @AllArgsConstructor
    @Getter
    public static class ConnectByApiForm {
        @NotBlank
        @Size(min = 1, max = 1)
        private final String gender;
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/connectByApi")
    public String connectByApi(@Valid ConnectByApiForm connectForm) {
        rq.setSessionAttr("connectByApi__gender", connectForm.getGender());

        return "redirect:/oauth2/authorization/instagram";
    }
}
