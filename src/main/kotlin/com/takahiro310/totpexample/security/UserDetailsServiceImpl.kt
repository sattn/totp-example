package com.takahiro310.totpexample.security

import com.takahiro310.totpexample.repository.AccountRepository
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.stereotype.Service

@Service
class UserDetailsServiceImpl(
    private val accountRepository: AccountRepository
) : UserDetailsService {

    @Throws(UsernameNotFoundException::class)
    override fun loadUserByUsername(username: String): UserDetails {
        val account = accountRepository.findByUserId(username)
        account ?: throw UsernameNotFoundException(username)
        return AccountDetails(account)
    }
}
