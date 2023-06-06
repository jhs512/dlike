package com.ll.dlike.boundedContext.likeablePerson.repository;

import com.ll.dlike.boundedContext.instaMember.entity.InstaMember;
import com.ll.dlike.boundedContext.likeablePerson.entity.LikeablePerson;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.NumberExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;

import java.util.List;
import java.util.Optional;

import static com.ll.dlike.boundedContext.likeablePerson.entity.QLikeablePerson.likeablePerson;

@RequiredArgsConstructor
public class LikeablePersonRepositoryImpl implements LikeablePersonRepositoryCustom {
    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public Optional<LikeablePerson> findQslByFromInstaMemberIdAndToInstaMember_username(long fromInstaMemberId, String toInstaMemberUsername) {
        return Optional.ofNullable(
                jpaQueryFactory
                        .selectFrom(likeablePerson)
                        .where(
                                likeablePerson.fromInstaMember.id.eq(fromInstaMemberId)
                                        .and(
                                                likeablePerson.toInstaMember.username.eq(toInstaMemberUsername)
                                        )
                        )
                        .fetchOne()
        );
    }

    @Override
    public List<LikeablePerson> findQslByToInstaMember(InstaMember instaMember, String gender, int attractiveTypeCode, int sortCode) {

        Sort sort = switch (sortCode) {
            case 2 -> Sort.by("id").ascending();
            case 3 -> Sort.by("likes").descending().and(Sort.by("id").descending());
            case 4 -> Sort.by("likes").ascending().and(Sort.by("id").descending());
            case 5 -> Sort.by("gender").descending().and(Sort.by("id").descending());
            case 6 -> Sort.by("attractiveTypeCode").ascending().and(Sort.by("id").descending());
            default -> Sort.by("id").descending();
        };

        JPAQuery<LikeablePerson> contentQuery = jpaQueryFactory
                .select(likeablePerson)
                .from(likeablePerson)
                .where(
                        likeablePerson.toInstaMember.eq(instaMember),
                        eqGender(gender),
                        eqAttractiveTypeCode(attractiveTypeCode)
                )
                .orderBy(getOrderSpecifiers(sort));

        return contentQuery.fetch();
    }

    private static BooleanExpression eqAttractiveTypeCode(int attractiveTypeCode) {
        if (attractiveTypeCode <= 0) return null;

        return likeablePerson.attractiveTypeCode.eq(attractiveTypeCode);
    }

    private static BooleanExpression eqGender(String gender) {
        if (gender == null || gender.isBlank()) return null;

        return likeablePerson.fromInstaMember.gender.eq(gender);
    }

    private OrderSpecifier<?>[] getOrderSpecifiers(@NonNull Sort sort) {
        return sort.stream()
                .map(this::getOrderSpecifier)
                .distinct()
                .toArray(OrderSpecifier[]::new);
    }

    private OrderSpecifier<?> getOrderSpecifier(Sort.Order sortOrder) {
        Order order = sortOrder.getDirection().isAscending() ? Order.ASC : Order.DESC;

        Expression<?> expression = switch (sortOrder.getProperty()) {
            case "likes" -> likesExpression();
            case "createDate" -> likeablePerson.createDate;
            case "gender" -> likeablePerson.fromInstaMember.gender;
            case "attractiveTypeCode" -> likeablePerson.attractiveTypeCode;
            default -> likeablePerson.id;
        };

        return new OrderSpecifier(order, expression);
    }

    private NumberExpression<Long> likesExpression() {
        return likeablePerson.fromInstaMember.likesCountByGenderWomanAndAttractiveTypeCode1
                .add(likeablePerson.fromInstaMember.likesCountByGenderWomanAndAttractiveTypeCode2)
                .add(likeablePerson.fromInstaMember.likesCountByGenderWomanAndAttractiveTypeCode3)
                .add(likeablePerson.fromInstaMember.likesCountByGenderManAndAttractiveTypeCode1)
                .add(likeablePerson.fromInstaMember.likesCountByGenderManAndAttractiveTypeCode2)
                .add(likeablePerson.fromInstaMember.likesCountByGenderManAndAttractiveTypeCode3);
    }
}
