package com.back.domain.member.member.service

import com.back.domain.member.member.entity.Member
import com.back.domain.member.member.repository.MemberRepository
import com.back.global.exception.ServiceException
import com.back.global.rsData.RsData
import com.back.standard.search.MemberSearchKeywordTypeV1
import lombok.RequiredArgsConstructor
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import java.util.*

@Service
@RequiredArgsConstructor
class MemberService {
    private lateinit var authTokenService: AuthTokenService
    private lateinit var memberRepository: MemberRepository
    private lateinit var passwordEncoder: PasswordEncoder

    fun count(): Long {
        return memberRepository!!.count()
    }

    @JvmOverloads
    fun join(username: String, password: String, nickname: String, profileImgUrl: String): Member {
        memberRepository
            .findByUsername(username)
            .ifPresent { _member: Member ->
                throw ServiceException("409-1", "이미 존재하는 아이디입니다.")
            }

        val member = Member(
            username,
            if (password.isNotBlank()) passwordEncoder.encode(password) else "",
            nickname,
            UUID.randomUUID().toString(),
            profileImgUrl
        )

        return memberRepository.save(member)
    }

    fun findByUsername(username: String): Optional<Member> {
        return memberRepository.findByUsername(username)
    }

    fun findByApiKey(apiKey: String): Optional<Member> {
        return memberRepository.findByApiKey(apiKey)
    }

    fun genAccessToken(member: Member): String {
        return authTokenService.genAccessToken(member)
    }

    fun payload(accessToken: String): Map<String, Any> {
        return authTokenService.payload(accessToken)
    }

    fun findById(id: Long): Optional<Member> {
        return memberRepository.findById(id)
    }

    fun findAll(): List<Member> {
        return memberRepository.findAll()
    }

    fun checkPassword(member: Member, password: String) {
        if (!passwordEncoder.matches(password, member.Password)) throw ServiceException("401-1", "비밀번호가 일치하지 않습니다.")
    }

    fun modifyOrJoin(username: String, password: String, nickname: String, profileImgUrl: String): RsData<Member> {
        var member = findByUsername(username).orElse(null)

        if (member == null) {
            member = join(username, password, nickname, profileImgUrl)
            return RsData("201-1", "회원가입이 완료되었습니다.", member)
        }

        modify(member, nickname, profileImgUrl)

        return RsData("200-1", "회원 정보가 수정되었습니다.", member)
    }

    private fun modify(member: Member, nickname: String, profileImgUrl: String) {
        member.modify(nickname, profileImgUrl)
    }

    fun findByPaged(
        searchKeywordType: MemberSearchKeywordTypeV1,
        searchKeyword: String,
        page: Int,
        pageSize: Int
    ): Page<Member> {
        val pageable: Pageable = PageRequest.of(page - 1, pageSize, Sort.by(Sort.Order.desc("id")))

        return memberRepository.findByKw(searchKeywordType, searchKeyword, pageable)
    }


}