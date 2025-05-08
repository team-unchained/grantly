package grantly.common.utils

import grantly.common.constants.AuthConstants
import org.springframework.mock.web.MockCookie
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.ResultActions
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder

fun MockMvc.performWithSession(
    requestBuilder: MockHttpServletRequestBuilder,
    token: String,
): ResultActions {
    val sessionCookie = MockCookie(AuthConstants.SESSION_COOKIE_NAME, token)
    return this.perform(requestBuilder.cookie(sessionCookie))
}
