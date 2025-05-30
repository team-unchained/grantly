package grantly.member.adapter.`in`.dto

import jakarta.validation.Validation
import jakarta.validation.Validator
import jakarta.validation.ValidatorFactory
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
internal class SignUpRequestTest {
    private lateinit var validator: Validator

    @BeforeAll
    fun setUp() {
        val factory: ValidatorFactory = Validation.buildDefaultValidatorFactory()
        validator = factory.validator
    }

    @Test
    @DisplayName("유효하지 않은 형식의 이메일 주소")
    fun `invalid email`() {
        val dto = SignUpRequest("invalid.email.com", "name", "password123!")
        val violations = validator.validate<Any>(dto)

        assertThat(violations).isNotEmpty()
        assertThat(violations).hasSize(1)
        assertThat(violations.first().propertyPath.toString()).isEqualTo("email")
    }

    @Test
    @DisplayName("이메일 주소가 빈 문자열")
    fun `blank email`() {
        val dto = SignUpRequest("", "name", "password123!")
        val violations = validator.validate<Any>(dto)

        assertThat(violations).isNotEmpty()
        assertThat(violations.first().propertyPath.toString()).isEqualTo("email")
    }

    @Test
    @DisplayName("비밀번호 길이가 8자 미만")
    fun `password length less than 8`() {
        val dto = SignUpRequest("valid@email.com", "name", "pass12!")
        val violations = validator.validate<Any>(dto)

        assertThat(violations).isNotEmpty()
        assertThat(violations.first().propertyPath.toString()).isEqualTo("password")
    }

    @ParameterizedTest
    @DisplayName("유효하지 않은 비밀번호 형식")
    @ValueSource(strings = ["password", "password!", "password12]", "password1234"])
    fun `invalid password format`(password: String) {
        val dto = SignUpRequest("valid@email.com", "name", password)
        val violations = validator.validate<Any>(dto)

        assertThat(violations).isNotEmpty()
        assertThat(violations).hasSize(1)
        assertThat(violations.first().propertyPath.toString()).isEqualTo("password")
    }
}
