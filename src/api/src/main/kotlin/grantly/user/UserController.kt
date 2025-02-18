package grantly

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class UserController(
    private val repository: UserRepository,
) {
    @GetMapping("/users")
    fun findAll() =
        listOf(
            User(1, "jack@grantly.work", "password", "Jack Bauer"),
            User(2, "chloe@grantly.work", "password", "Chloe O'Brian"),
            User(3, "kim@grantly.work", "password", "Kim Bauer"),
            User(4, "david@grantly.work", "password", "David Palmer"),
            User(5, "michelle@grantly.work", "password", "Michelle Dessler"),
        )
}
