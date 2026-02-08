package net.spexity.backend.endpoint

import jakarta.validation.Valid
import jakarta.validation.constraints.NotBlank
import net.spexity.backend.service.CreatePostRequest
import net.spexity.backend.service.PostService
import net.spexity.spec.Post
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController


@RestController
@RequestMapping("/api/posts")
class PostController(
    private val postService: PostService,
) {

    @PostMapping
    fun create(
        @Valid @RequestBody request: CreatePostRestRequest
    ): CreatePostRestResponse {
        val result = postService.createPost(
            CreatePostRequest(
                body = request.body,
            )
        )
        return CreatePostRestResponse(
            post = Post(
                id = result.id,
                body = result.body,
            )
        )
    }

    data class CreatePostRestRequest(
        @field:NotBlank val body: String,
    )

    data class CreatePostRestResponse(
        val post: Post,
    )

}