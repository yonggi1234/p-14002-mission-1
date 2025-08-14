package com.back.domain.post.post.entity


import com.back.domain.member.member.entity.Member
import com.back.domain.post.postComment.entity.PostComment
import com.back.global.exception.ServiceException
import com.back.global.jpa.entity.BaseTime
import com.back.global.rsData.RsData
import com.back.standard.base.Empty
import jakarta.persistence.*
import java.util.*

@Entity
class Post : BaseTime {
    @ManyToOne(fetch = FetchType.LAZY)
    var author: Member

    var title: String

    var content: String

    @OneToMany(
        mappedBy = "post",
        fetch = FetchType.LAZY,
        cascade = [CascadeType.PERSIST, CascadeType.REMOVE],
        orphanRemoval = true
    )
    private val comments: MutableList<PostComment> = mutableListOf()

    private var published: Boolean = false

    var listed: Boolean = false

    constructor(author: Member, title: String, content: String, published: Boolean, listed: Boolean) {
        this.author = author
        this.title = title
        this.content = content
        this.published = published
        this.listed = listed
    }

    fun modify(title: String, content: String) {
        this.title = title
        this.content = content
    }

    fun addComment(author: Member?, content: String?): PostComment {
        val postComment = PostComment(author, this, content)
        comments.add(postComment)

        return postComment
    }

    fun findCommentById(id: Int): Optional<PostComment> {
        return comments
            .stream()
            .filter { comment: PostComment -> comment.id == id.toLong() }
            .findFirst()
    }

    fun deleteComment(postComment: PostComment?): Boolean {
        if (postComment == null) return false

        return comments.remove(postComment)
    }

    fun getCheckActorCanModifyRs(actor: Member?): RsData<Empty> {
        if (actor == null) return RsData("401-1", "로그인 후 이용해주세요.")

        if (actor == author) return RsData.OK

        return RsData("403-1", "작성자만 글을 수정할 수 있습니다.")
    }

    fun checkActorCanModify(actor: Member?) {
        Optional.of(
            getCheckActorCanModifyRs(actor)
        )
            .filter { rsData -> rsData.isFaild }
            .ifPresent { rsData ->
                throw ServiceException(rsData.resultCode, rsData.msg)
            }
    }

    fun checkActorCanDelete(actor: Member?) {
        if (!author.equals(actor)) throw ServiceException("403-2", "%d번 글 삭제권한이 없습니다.".formatted(id))
    }
}
