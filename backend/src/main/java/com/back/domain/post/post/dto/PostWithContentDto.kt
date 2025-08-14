package com.back.domain.post.post.dto

import com.back.domain.post.post.entity.Post
import lombok.Getter

@Getter
class PostWithContentDto(post: Post) : PostDto(post) {
    private val content: String = post.content
}