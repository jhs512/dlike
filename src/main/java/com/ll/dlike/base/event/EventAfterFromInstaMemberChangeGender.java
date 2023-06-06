package com.ll.dlike.base.event;

import com.ll.dlike.boundedContext.instaMember.entity.InstaMember;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class EventAfterFromInstaMemberChangeGender extends ApplicationEvent {
    private final InstaMember instaMember;
    private final String oldGender;

    public EventAfterFromInstaMemberChangeGender(Object source, InstaMember instaMember, String oldGender) {
        super(source);
        this.instaMember = instaMember;
        this.oldGender = oldGender;
    }
}
