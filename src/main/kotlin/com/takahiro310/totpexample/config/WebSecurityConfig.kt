package com.takahiro310.totpexample.config

import com.takahiro310.totpexample.controller.AccountController
import com.takahiro310.totpexample.security.JWTAuthenticationFilter
import com.takahiro310.totpexample.security.JWTAuthorizationFilter
import com.takahiro310.totpexample.security.SecurityConstants
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.context.annotation.Bean
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.core.userdetails.UserDetailsService

@EnableWebSecurity
class WebSecurityConfig() : WebSecurityConfigurerAdapter() {

    @Qualifier("userDetailsServiceImpl")
    @Autowired
    private lateinit var userDetailsService: UserDetailsService

    @Throws(Exception::class)
    override fun configure(http: HttpSecurity) {
        val createAccountPath = "${AccountController.ACCOUNT_PATH}${AccountController.CREATE_ACCOUNT_SUFFIX}"
        val verifyEmailPath = "${AccountController.ACCOUNT_PATH}${AccountController.VERIFY_EMAIL_SUFFIX}"
        http
            .cors()
            .and().authorizeRequests()
            .antMatchers(SecurityConstants.LOGIN_PATH, createAccountPath, verifyEmailPath).permitAll()
            .anyRequest().authenticated()
            .and().logout()
            .and().csrf().disable()
            .addFilter(JWTAuthenticationFilter(authenticationManager()))
            .addFilter(JWTAuthorizationFilter(authenticationManager()))
            .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
    }

    @Autowired
    @Throws(Exception::class)
    fun configureAuth(auth: AuthenticationManagerBuilder) {
        auth
            .userDetailsService<UserDetailsService>(userDetailsService)
            .passwordEncoder(bCryptPasswordEncoder())
    }

    @Bean
    fun bCryptPasswordEncoder(): BCryptPasswordEncoder {
        return BCryptPasswordEncoder()
    }
}
