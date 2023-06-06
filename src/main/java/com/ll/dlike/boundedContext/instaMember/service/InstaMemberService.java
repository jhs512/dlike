package com.ll.dlike.boundedContext.instaMember.service;

import com.ll.dlike.base.rsData.RsData;
import com.ll.dlike.boundedContext.instaMember.entity.InstaMember;
import com.ll.dlike.boundedContext.instaMember.entity.InstaMemberSnapshot;
import com.ll.dlike.boundedContext.instaMember.repository.InstaMemberRepository;
import com.ll.dlike.boundedContext.instaMember.repository.InstaMemberSnapshotRepository;
import com.ll.dlike.boundedContext.likeablePerson.entity.LikeablePerson;
import com.ll.dlike.boundedContext.member.entity.Member;
import com.ll.dlike.boundedContext.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class InstaMemberService {
    private final InstaMemberRepository instaMemberRepository;
    private final MemberService memberService;
    private final InstaMemberSnapshotRepository instaMemberSnapshotRepository;

    public Optional<InstaMember> findByUsername(String username) {
        return instaMemberRepository.findByUsername(username);
    }

    @Transactional
    // member : 현재 로그인한 회원
    // username : 입력한 본인 인스타 username
    // gender : 입력한 본인의 성별
    public RsData<InstaMember> connect(Member member, String username, String gender) {
        Optional<InstaMember> opInstaMember = findByUsername(username); // 혹시 다른 회원이 이미 입력하신 인스타 ID와 연결되었는지

        // 등록이 되어있고, 성별이 U가 아니라
        if (opInstaMember.isPresent() && !opInstaMember.get().getGender().equals("U")) {
            gender = opInstaMember.get().getGender();
        }

        //
        RsData<InstaMember> instaMemberRsData = findByUsernameOrCreate(username, gender);

        memberService.updateInstaMember(member, instaMemberRsData.getData());

        return instaMemberRsData;
    }

    // InstaMember 생성
    private RsData<InstaMember> create(String username, String gender) {
        InstaMember instaMember = InstaMember
                .builder()
                .username(username)
                .gender(gender)
                .build();

        instaMemberRepository.save(instaMember);

        return RsData.of("S-1", "인스타계정이 등록되었습니다.", instaMember);
    }

    @Transactional
    public RsData<InstaMember> findByUsernameOrCreate(String username) {
        Optional<InstaMember> opInstaMember = findByUsername(username);

        return opInstaMember
                .map(instaMember -> RsData.of("S-2", "인스타계정이 등록되었습니다.", instaMember))
                .orElseGet(() -> create(username, "U"));
    }

    @Transactional
    public RsData<InstaMember> findByUsernameOrCreate(String username, String gender) {
        Optional<InstaMember> opInstaMember = findByUsername(username);

        // 찾았다면
        if (opInstaMember.isPresent()) {
            InstaMember instaMember = opInstaMember.get();
            instaMember.updateGender(gender); // 성별세팅
            instaMemberRepository.save(instaMember); // 저장

            // 기존 인스타회원이랑 연결
            return RsData.of("S-2", "인스타계정이 등록되었습니다.", instaMember);
        }

        // 생성
        return create(username, gender);
    }

    private void saveSnapshot(InstaMemberSnapshot snapshot) {
        instaMemberSnapshotRepository.save(snapshot);
    }

    public void whenAfterModifyAttractiveType(LikeablePerson likeablePerson, int oldAttractiveTypeCode) {
        InstaMember fromInstaMember = likeablePerson.getFromInstaMember();
        InstaMember toInstaMember = likeablePerson.getToInstaMember();

        toInstaMember.decreaseLikesCount(fromInstaMember.getGender(), oldAttractiveTypeCode);
        toInstaMember.increaseLikesCount(fromInstaMember.getGender(), likeablePerson.getAttractiveTypeCode());

        InstaMemberSnapshot snapshot = toInstaMember.snapshot("ModifyAttractiveType");

        saveSnapshot(snapshot);
    }

    public void whenAfterLike(LikeablePerson likeablePerson) {
        InstaMember fromInstaMember = likeablePerson.getFromInstaMember();
        InstaMember toInstaMember = likeablePerson.getToInstaMember();

        toInstaMember.increaseLikesCount(fromInstaMember.getGender(), likeablePerson.getAttractiveTypeCode());

        InstaMemberSnapshot snapshot = toInstaMember.snapshot("Like");

        saveSnapshot(snapshot);

        // 알림
    }

    public void whenBeforeCancelLike(LikeablePerson likeablePerson) {
        InstaMember fromInstaMember = likeablePerson.getFromInstaMember();
        InstaMember toInstaMember = likeablePerson.getToInstaMember();

        toInstaMember.decreaseLikesCount(fromInstaMember.getGender(), likeablePerson.getAttractiveTypeCode());

        InstaMemberSnapshot snapshot = toInstaMember.snapshot("CancelLike");

        saveSnapshot(snapshot);
    }

    public void whenAfterFromInstaMemberChangeGender(InstaMember instaMember, String oldGender) {
        final Map<Long, Boolean> alrearyProcessed = new HashMap<>();

        instaMember
                .getFromLikeablePeople(null)
                .forEach(likeablePerson -> {
                    InstaMember toInstaMember = likeablePerson.getToInstaMember();

                    if (alrearyProcessed.containsKey(toInstaMember.getId())) {
                        return;
                    }

                    alrearyProcessed.put(toInstaMember.getId(), true);

                    toInstaMember.decreaseLikesCount(oldGender, likeablePerson.getAttractiveTypeCode());
                    toInstaMember.increaseLikesCount(instaMember.getGender(), likeablePerson.getAttractiveTypeCode());

                    InstaMemberSnapshot snapshot = toInstaMember.snapshot("FromInstaMemberChangeGender");

                    saveSnapshot(snapshot);
                });
    }

    public RsData<InstaMember> connect(Member actor, String gender, String oauthId, String username, String accessToken) {
        Optional<InstaMember> opInstaMember = instaMemberRepository.findByOauthId(oauthId);

        if (opInstaMember.isPresent()) {
            InstaMember instaMember = opInstaMember.get();
            instaMember.setUsername(username);
            instaMember.setAccessToken(accessToken);
            instaMember.setGender(gender);
            instaMemberRepository.save(instaMember);

            actor.setInstaMember(instaMember);

            return RsData.of("S-3", "인스타계정이 연결되었습니다.", instaMember);
        }

        opInstaMember = findByUsername(username);

        if (opInstaMember.isPresent()) {
            InstaMember instaMember = opInstaMember.get();
            instaMember.setOauthId(oauthId);
            instaMember.setAccessToken(accessToken);
            instaMember.setGender(gender);
            instaMemberRepository.save(instaMember);

            actor.setInstaMember(instaMember);

            return RsData.of("S-4", "인스타계정이 연결되었습니다.", instaMember);
        }

        InstaMember instaMember = connect(actor, username, gender).getData();

        instaMember.setOauthId(oauthId);
        instaMember.setAccessToken(accessToken);

        return RsData.of("S-5", "인스타계정이 연결되었습니다.", instaMember);
    }
}
