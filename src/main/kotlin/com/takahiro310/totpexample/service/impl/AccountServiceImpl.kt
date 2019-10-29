package com.takahiro310.totpexample.service.impl

import com.takahiro310.totpexample.model.Account
import com.takahiro310.totpexample.repository.AccountRepository
import com.takahiro310.totpexample.repository.AccountTokenRepository
import com.takahiro310.totpexample.service.AccountService
import org.slf4j.LoggerFactory
import org.springframework.mail.MailException
import org.springframework.mail.MailSender
import org.springframework.mail.SimpleMailMessage
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.apache.commons.codec.binary.Base32
import java.security.SecureRandom
import java.io.UnsupportedEncodingException
import java.net.URLEncoder
import java.util.*

/**
 * アカウントサービス実装
 *
 * @property accountRepository
 * @property bCryptPasswordEncoder
 * @property mailSender
 */
@Service
class AccountServiceImpl(
    private val accountRepository: AccountRepository,
    private val accountTokenRepository: AccountTokenRepository,
    private val bCryptPasswordEncoder: BCryptPasswordEncoder,
    private val mailSender: MailSender
) : AccountService {

    private val logger = LoggerFactory.getLogger(this::class.java)

    /**
     * アカウントを作成する
     *
     * @param userId ユーザーID
     * @param email メールアドレス
     * @param password パスワード
     * @return トークン
     */
    @Transactional
    override fun createAccount(userId: String, email: String, password: String): String {

        // アカウント認証トークン
        val token = UUID.randomUUID().toString()

        // アカウント作成（無効状態）
        val account = Account(userId, bCryptPasswordEncoder.encode(password), email, createSecret(), false)
        accountRepository.create(account)
        // トークン作成
        accountTokenRepository.create(token, userId)

        // メール送信
        val msg = SimpleMailMessage()
        msg.setFrom("noreply@takahiro310.com")
        msg.setTo(email)
        msg.setSubject("Please verify your email address")
        msg.setText("""
        """.trimIndent())
        logger.debug(msg.text)
        try {
            mailSender.send(msg)
        } catch (e: MailException) {
            logger.error(e.message, e)
        }

        return token
    }

    private fun createSecret(): String {
        val buffer = ByteArray(10)
        SecureRandom().nextBytes(buffer)
        return String(Base32().encode(buffer))
    }

    override fun verifyEmail(token: String): Account {
        val userId = accountTokenRepository.findUserIdByToken(token)
        checkNotNull(userId) { "有効なトークンが見つかりません" }
        val account = accountRepository.findByUserId(userId)
        checkNotNull(account) { "アカウントが存在しません" }
        check(!account.enabled) { "既に認証済みのアカウントです" }
        accountRepository.verified(account)
        return account
    }

    override fun getQRCode(account: Account): String {
        return getQRBarcodeURL(account.userId, "localhost", account.secret)
    }

    private fun getQRBarcodeURL(user: String, host: String, secret: String): String {
        return "https://chart.googleapis.com/chart?" + getQRBarcodeURLQuery(user, host, secret)
    }

    private fun getQRBarcodeURLQuery(user: String, host: String, secret: String): String {
        try {
            return "chs=160x160&chld=M%7C0&cht=qr&chl=" + URLEncoder.encode(
                getQRBarcodeOtpAuthURL(user, host, secret),
                "UTF8"
            )
        } catch (e: UnsupportedEncodingException) {
            throw IllegalStateException(e)
        }
    }

    private fun getQRBarcodeOtpAuthURL(user: String, host: String, secret: String): String {
        return String.format("otpauth://totp/%s@%s?secret=%s", user, host, secret)
    }
}
