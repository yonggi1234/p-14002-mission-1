package com.back.global.jpa.entity

import jakarta.persistence.*
import java.util.*

@MappedSuperclass
abstract class BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name =  "id")
    protected var _id: Long? = null

    val id: Long
        get() = _id ?: 0



    override fun equals(o: Any?): Boolean {
        if (o === this) return true
        if (o == null || javaClass != o.javaClass) return false
        val that = o as BaseEntity
        return id === that.id
    }

    override fun hashCode(): Int {
        return Objects.hashCode(id)
    }
}