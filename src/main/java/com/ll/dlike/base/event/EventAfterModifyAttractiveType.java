package com.ll.dlike.base.event;

import com.ll.dlike.boundedContext.likeablePerson.entity.LikeablePerson;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class EventAfterModifyAttractiveType extends ApplicationEvent {
    private final LikeablePerson likeablePerson;
    private final int oldAttractiveTypeCode;

    public EventAfterModifyAttractiveType(Object source, LikeablePerson likeablePerson, int oldAttractiveTypeCode, int newAttractiveTypeCode) {
        super(source);
        this.likeablePerson = likeablePerson;
        this.oldAttractiveTypeCode = oldAttractiveTypeCode;
    }
}
