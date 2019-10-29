package com.takahiro310.totpexample.security

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import javax.servlet.http.HttpServletRequest
import org.springframework.security.core.context.SecurityContextHolder
import javax.servlet.ServletException
import java.io.IOException
import javax.servlet.FilterChain
import javax.servlet.http.HttpServletResponse
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter
import java.lang.IllegalStateException
import kotlin.collections.ArrayList

class JWTAuthorizationFilter(
    authenticationManager: AuthenticationManager
) : BasicAuthenticationFilter(authenticationManager) {

    @Throws(IOException::class, ServletException::class)
    override fun doFilterInternal(
        req: HttpServletRequest,
        res: HttpServletResponse,
        chain: FilterChain
    ) {
        val header = req.getHeader(SecurityConstants.HEADER_STRING)
        if (header == null || !header.startsWith(SecurityConstants.TOKEN_PREFIX)) {
            chain.doFilter(req, res)
            return
        }

        val authentication = getAuthentication(req)
        SecurityContextHolder.getContext().authentication = authentication
        chain.doFilter(req, res)
    }

    private fun getAuthentication(request: HttpServletRequest): UsernamePasswordAuthenticationToken? {
        val jwt = getToken(request)
        try {
            // TODO JWTの検証をする
            return UsernamePasswordAuthenticationToken("test", jwt, ArrayList<GrantedAuthority>())
        } catch (e: IllegalStateException) {
            logger.debug(e.message, e)
        }
        return null
    }

    private fun getToken(request: HttpServletRequest): String {
        val token = request.getHeader(SecurityConstants.HEADER_STRING)
        return token?.replaceFirst(SecurityConstants.TOKEN_PREFIX, "") ?: ""
    }
}
