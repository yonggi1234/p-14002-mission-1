package com.back.global.security;

import com.back.domain.member.member.entity.Member;
import com.back.domain.member.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class CustomOAuth2UserService extends DefaultOAuth2UserService {
    private final MemberService memberService;

    // 카카오톡 로그인이 성공할 때 마다 이 함수가 실행된다.
    @Override
    @Transactional
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {

        OAuth2User oAuth2User = super.loadUser(userRequest);

        String oauthUserId = "";
        String providerTypeCode = userRequest.getClientRegistration().getRegistrationId().toUpperCase();

        String nickname = "";
        String profileImgUrl = "";
        String username = "";

        switch (providerTypeCode) {
            case "KAKAO" -> {
                Map<String, Object> attributes = oAuth2User.getAttributes();
                Map<String, Object> attributesProperties = (Map<String, Object>) attributes.get("properties");

                oauthUserId = oAuth2User.getName();
                nickname = (String) attributesProperties.get("nickname");
                profileImgUrl = (String) attributesProperties.get("profile_image");
            }
            case "GOOGLE" -> {
                oauthUserId = oAuth2User.getName();
                nickname = (String) oAuth2User.getAttributes().get("name");
                profileImgUrl = (String) oAuth2User.getAttributes().get("picture");
            }
            case "NAVER" -> {
                Map<String, Object> attributes = oAuth2User.getAttributes();
                Map<String, Object> attributesProperties = (Map<String, Object>) attributes.get("response");

                oauthUserId = (String) attributesProperties.get("id");
                nickname = (String) attributesProperties.get("nickname");
                profileImgUrl = (String) attributesProperties.get("profile_image");
            }
        }

        username = providerTypeCode + "__%s".formatted(oauthUserId);
        String password = "";

        Member member = memberService.modifyOrJoin(username, password, nickname, profileImgUrl).data();

        return new SecurityUser(
                member.getId(),
                member.getUsername(),
                member.getPassword(),
                member.getName(),
                member.getAuthorities()
        );
    }
}