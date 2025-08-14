package com.back.domain.post.post.service

import com.back.domain.member.member.entity.Member
import com.back.domain.post.post.entity.Post
import com.back.domain.post.post.repository.PostRepository
import com.back.domain.post.postComment.entity.PostComment
import org.springframework.stereotype.Service
import java.util.*

@Service
class PostService (
    private val postRepository: PostRepository
    ) {
    fun count(): Long {
        return postRepository.count()
    }

    fun write(author: Member, title: String, content: String, published: Boolean, listed: Boolean): Post {
        val post = Post(
            author,
            title,
            content,
            published,
            listed
        )
        return postRepository.save(post)
    }

    fun findById(id: Int): Optional<Post> {
        return postRepository.findById(id)
    }

    fun findAll(): List<Post> {
        return postRepository.findAll()
    }

    fun modify(post: Post, title: String, content: String) {
        post.modify(title, content)
    }

    fun writeComment(author: Member, post: Post, content: String): PostComment {
        return post.addComment(author, content)
    }

    fun deleteComment(post: Post, postComment: PostComment): Boolean {
        return post.deleteComment(postComment)
    }

    fun modifyComment(postComment: PostComment, content: String?) {
        postComment.modify(content)
    }

    fun delete(post: Post) {
        postRepository.delete(post)
    }

    fun findLatest(): Optional<Post> {
        return postRepository.findFirstByOrderByIdDesc()
    }

    fun flush() {
        postRepository.flush()
    }
}