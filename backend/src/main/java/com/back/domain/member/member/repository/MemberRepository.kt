package com.back.domain.member.member.repository

import com.back.domain.member.member.entity.Member
import com.back.standard.search.MemberSearchKeywordTypeV1
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import java.util.*

interface MemberRepository : JpaRepository<Member, Long> {
    fun findByUsername(username: String): Optional<Member>

    fun findByApiKey(apiKey: String): Optional<Member>
    fun findByKw(kwType: MemberSearchKeywordTypeV1, kw: String, pageable: Pageable): Page<Member>
}