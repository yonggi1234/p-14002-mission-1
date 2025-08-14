package com.back.domain.member.member.controller

import com.back.domain.member.member.dto.MemberDto
import com.back.domain.member.member.dto.MemberWithUsernameDto
import com.back.domain.member.member.service.MemberService
import com.back.global.exception.ServiceException
import com.back.global.rq.Rq
import com.back.global.rsData.RsData
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size
import lombok.RequiredArgsConstructor
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.bind.annotation.*


@RestController
@RequestMapping("/api/v1/members")
@RequiredArgsConstructor
@Tag(name = "ApiV1MemberController", description = "API 회원 컨트롤러")
@SecurityRequirement(name = "bearerAuth")
class ApiV1MemberController (
    private val memberService: MemberService,
    private val rq: Rq
){

    data class MemeberJoinResBody(
        @field:NotBlank val username: String,
        @field:NotBlank val password: String,
        @field:NotBlank val nickname: String
    )

    @JvmRecord
    data class MemberJoinReqBody(
        val username: @NotBlank @Size(min = 2, max = 30) String,
        val password: @NotBlank @Size(min = 2, max = 30) String,
        val nickname: @NotBlank @Size(min = 2, max = 30) String
    )

    @PostMapping
    @Transactional
    @Operation(summary = "가입")
    fun join(
        @RequestBody @Valid reqBody: MemberJoinReqBody
    ): RsData<MemberDto> {
        val member = memberService.join(
            reqBody.username,
            reqBody.password,
            reqBody.nickname,
            ""
        )

        return RsData(
            "201-1",
            "%s님 환영합니다. 회원가입이 완료되었습니다.".formatted(member.Username),
            MemberDto(member)
        )
    }


    @JvmRecord
    data class MemberLoginReqBody(
        val username: @NotBlank @Size(min = 2, max = 30) String,
        val password: @NotBlank @Size(min = 2, max = 30) String
    )

    @JvmRecord
    data class MemberLoginResBody(
        val item: MemberDto,
        val apiKey: String,
        val accessToken: String
    )

    @PostMapping("/login")
    @Transactional(readOnly = true)
    @Operation(summary = "로그인")
    fun login(
        @RequestBody reqBody: @Valid MemberLoginReqBody
    ): RsData<MemberLoginResBody> {
        val member = memberService.findByUsername(reqBody.username)
            .orElseThrow { ServiceException("401-1", "존재하지 않는 아이디입니다.") }

        memberService.checkPassword(
            member,
            reqBody.password
        )

        val accessToken = memberService.genAccessToken(member)

        rq.setCookie("apiKey", member.apiKey)
        rq.setCookie("accessToken", accessToken)

        return RsData(
            "200-1",
            "${member.Username}님 환영합니다.",
            MemberLoginResBody(
                MemberDto(member),
                member.apiKey,
                accessToken
            )
        )
    }


    @DeleteMapping("/logout")
    @Operation(summary = "로그아웃")
    fun logout(): RsData<Void> {
        rq!!.deleteCookie("apiKey")
        rq.deleteCookie("accessToken")

        return RsData(
            "200-1",
            "로그아웃 되었습니다."
        )
    }


    @GetMapping("/me")
    @Transactional(readOnly = true)
    @Operation(summary = "내 정보")
    fun me(): MemberWithUsernameDto {
        val actor = rq!!.actorFromDb

        return MemberWithUsernameDto(actor)
    }
}
