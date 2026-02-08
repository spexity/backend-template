package net.spexity.backend.service

import net.spexity.database.model.tables.references.POST
import org.jooq.DSLContext
import org.springframework.stereotype.Service
import java.util.*

@Service
class PostService(
    private val dslContext: DSLContext,
) {

    fun createPost(request: CreatePostRequest): CreatePostResponse {
        val insertedId = dslContext.insertInto(POST)
            .set(POST.BODY, request.body)
            .returning(POST.ID)
            .fetchOne()!!.id
        return CreatePostResponse(
            id = insertedId!!,
            body = request.body,
        )
    }

}

data class CreatePostResponse(
    val id: UUID,
    val body: String,
)

data class CreatePostRequest(
    val body: String,
)