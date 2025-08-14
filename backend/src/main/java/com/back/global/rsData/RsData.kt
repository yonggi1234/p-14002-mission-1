package com.back.global.rsData

import com.back.standard.base.Empty
import com.fasterxml.jackson.annotation.JsonIgnore

class RsData<T>(
    val resultCode: String,
    val msg: String,
    val data: T = Empty() as T
) {
    companion object{
        val OK = RsData("200-1", "OK", Empty())
    }

    @JsonIgnore
    fun getStatusCode(): Int = resultCode.split("-")[0].toInt()

    val isSuccess = getStatusCode() < 400
    val isFaild = !isSuccess

    constructor(resultCode: String, msg: String) : this(resultCode, msg, Empty() as T)
}
