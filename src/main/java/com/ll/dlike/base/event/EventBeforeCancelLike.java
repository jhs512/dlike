package com.ll.dlike.base.event;

import com.ll.dlike.boundedContext.likeablePerson.entity.LikeablePerson;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class EventBeforeCancelLike extends ApplicationEvent {
    private final LikeablePerson likeablePerson;

    public EventBeforeCancelLike(Object source, LikeablePerson likeablePerson) {
        super(source);
        this.likeablePerson = likeablePerson;
    }
}
