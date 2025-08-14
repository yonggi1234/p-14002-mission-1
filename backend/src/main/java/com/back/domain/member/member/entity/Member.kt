package com.back.domain.member.member.entity

import com.back.global.jpa.entity.BaseTime
import jakarta.persistence.Column
import jakarta.persistence.Entity
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority

@Entity
class Member : BaseTime {

    @Column(unique = true)
    private var username: String

    private lateinit var _password: String

    private var nickname: String

    @Column(unique = true)
    private lateinit var _apiKey: String

    private var profileImgUrl: String

    constructor(id: Long, username: String, nickname: String) {
        this._id= id
        this.username = username
        this.nickname = nickname
        this.profileImgUrl = ""
    }

    constructor(username: String, password: String, nickname: String, apiKey: String, profileImgUrl: String) {
        this.username = username
        this._password = password
        this.nickname = nickname
        this._apiKey = apiKey
        this.profileImgUrl = profileImgUrl
    }
    val name: String
        get() = nickname

    val Username: String
        get() {
            if (nickname.isNotBlank()) return nickname
            return username
        }

    val Password: String
        get() = _password



    val apiKey: String
        get() = _apiKey

    fun modifyApiKey(apiKey: String) {
        this._apiKey = apiKey
    }

    val isAdmin: Boolean
        get() {
            if ("system" == username) return true
            if ("admin" == username) return true

            return false
        }

    val authorities: Collection<GrantedAuthority?>
        get() = authoritiesAsStringList
            .stream()
            .map { role: String? -> SimpleGrantedAuthority(role) }
            .toList()

    private val authoritiesAsStringList: List<String>
        get() {
            val authorities: MutableList<String> = ArrayList()

            if (isAdmin) authorities.add("ROLE_ADMIN")

            return authorities
        }

    fun modify(nickname: String, profileImgUrl: String) {
        this.nickname = nickname
        this.profileImgUrl = profileImgUrl
    }

    val profileImgUrlOrDefault: String
        get() {
            if (profileImgUrl == null) return "https://placehold.co/600x600?text=U_U"

            return profileImgUrl
        }
}
