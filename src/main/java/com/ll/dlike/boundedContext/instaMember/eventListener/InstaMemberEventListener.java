package com.ll.dlike.boundedContext.instaMember.eventListener;

import com.ll.dlike.base.event.EventAfterFromInstaMemberChangeGender;
import com.ll.dlike.base.event.EventAfterLike;
import com.ll.dlike.base.event.EventAfterModifyAttractiveType;
import com.ll.dlike.base.event.EventBeforeCancelLike;
import com.ll.dlike.boundedContext.instaMember.service.InstaMemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
@Transactional
public class InstaMemberEventListener {
    private final InstaMemberService instaMemberService;

    @EventListener
    public void listen(EventAfterModifyAttractiveType event) {
        instaMemberService.whenAfterModifyAttractiveType(event.getLikeablePerson(), event.getOldAttractiveTypeCode());
    }

    @EventListener
    public void listen(EventAfterLike event) {
        instaMemberService.whenAfterLike(event.getLikeablePerson());
    }

    @EventListener
    public void listen(EventBeforeCancelLike event) {
        instaMemberService.whenBeforeCancelLike(event.getLikeablePerson());
    }

    @EventListener
    public void listen(EventAfterFromInstaMemberChangeGender event) {
        instaMemberService.whenAfterFromInstaMemberChangeGender(event.getInstaMember(), event.getOldGender());
    }
}
