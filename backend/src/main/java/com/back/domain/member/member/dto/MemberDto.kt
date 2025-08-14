package com.back.domain.member.member.dto

import com.back.domain.member.member.entity.Member

import java.time.LocalDateTime


open class MemberDto (
    private val id: Long,
    private val createDate: LocalDateTime,
    private val modifyDate: LocalDateTime,
    private val nickname: String,
    private val profileImgUrl: String
    ) {
    constructor(member: Member) : this(
        id = member.id,
        createDate = member.createDate,
        modifyDate = member.modifyDate,
        nickname = member.Username,
        profileImgUrl = member.profileImgUrlOrDefault
    )

}
