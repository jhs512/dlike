package com.ll.dlike.boundedContext.instaMember.entity;

import com.ll.dlike.base.baseEntity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

@Entity
@Getter
@NoArgsConstructor
@SuperBuilder
@ToString(callSuper = true)
public class InstaMember extends BaseEntity {
    @Setter
    @Column(unique = true)
    private String username;

}
