package com.ll.dlike.boundedContext.likeablePerson.repository;

import com.ll.dlike.boundedContext.instaMember.entity.InstaMember;
import com.ll.dlike.boundedContext.likeablePerson.entity.LikeablePerson;

import java.util.List;
import java.util.Optional;

public interface LikeablePersonRepositoryCustom {
    Optional<LikeablePerson> findQslByFromInstaMemberIdAndToInstaMember_username(long fromInstaMemberId, String toInstaMemberUsername);

    List<LikeablePerson> findQslByToInstaMember(InstaMember instaMember, String gender, int attractiveTypeCode, int sortCode);
}
