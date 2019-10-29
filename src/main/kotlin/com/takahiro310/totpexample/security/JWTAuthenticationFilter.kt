package com.takahiro310.totpexample.security

import com.fasterxml.jackson.annotation.JsonProperty
import javax.servlet.ServletException
import java.io.IOException
import javax.servlet.FilterChain
import javax.servlet.http.HttpServletResponse
import javax.servlet.http.HttpServletRequest
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import com.fasterxml.jackson.databind.ObjectMapper
import dev.turingcomplete.kotlinonetimepassword.GoogleAuthenticator
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.web.util.matcher.AntPathRequestMatcher
import org.springframework.security.core.Authentication
import org.springframework.security.core.AuthenticationException
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter
import org.springframework.security.authentication.BadCredentialsException
import java.util.*
import kotlin.collections.ArrayList

class JWTAuthenticationFilter(
    private val authManager: AuthenticationManager
) : UsernamePasswordAuthenticationFilter() {

    init {
        setRequiresAuthenticationRequestMatcher(AntPathRequestMatcher(SecurityConstants.LOGIN_PATH, "POST"))
    }

    data class LoginForm(
        @JsonProperty("userId")
        val userId: String,
        @JsonProperty("password")
        val password: String,
        @JsonProperty("token")
        val token: String
    )

    @Throws(AuthenticationException::class)
    override fun attemptAuthentication(
        req: HttpServletRequest,
        res: HttpServletResponse?
    ): Authentication {
        try {
            val loginForm = ObjectMapper().readValue(req.inputStream, LoginForm::class.java)
            val auth = authManager.authenticate(
                UsernamePasswordAuthenticationToken(
                    loginForm.userId,
                    loginForm.password,
                    ArrayList<GrantedAuthority>()
                )
            )

            val account = (auth.principal as AccountDetails).account
            val googleAuthenticator = GoogleAuthenticator(account.secret)
            logger.debug("ワンタイムパスワード：${googleAuthenticator.generate()}")
            if (!googleAuthenticator.isValid(loginForm.token, Date(System.currentTimeMillis()))) {
                throw BadCredentialsException("MFA Code verify failed")
            }
            return auth
        } catch (e: IOException) {
            logger.error(e.message)
            throw RuntimeException(e)
        }
    }

    @Throws(IOException::class, ServletException::class)
    override fun successfulAuthentication(
        req: HttpServletRequest,
        res: HttpServletResponse,
        chain: FilterChain,
        auth: Authentication
    ) {
        // TODO JWTを生成してクライアントに払い出す
        val charPool: List<Char> = ('a'..'z') + ('A'..'Z') + ('0'..'9')
        val jwt = (1..32)
            .map { kotlin.random.Random.nextInt(0, charPool.size) }
            .map(charPool::get)
            .joinToString("")
        res.addHeader(SecurityConstants.HEADER_STRING, SecurityConstants.TOKEN_PREFIX + jwt)
    }
}
