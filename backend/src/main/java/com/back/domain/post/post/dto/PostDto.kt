package com.back.domain.post.post.dto

import com.back.domain.post.post.entity.Post
import lombok.Getter

@Getter
open class PostDto(post: Post) {
    private val id = post.id.toInt()
    private val createDate = post.createDate
    private val modifyDate = post.modifyDate
    private val authorId = post.author.id.toInt()
    private val authorName = post.author.name
    private val title: String = post.title
}