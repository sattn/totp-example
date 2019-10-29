package com.takahiro310.totpexample.controller

import com.takahiro310.totpexample.exception.ValidException
import com.takahiro310.totpexample.service.AccountService
import org.springframework.http.MediaType
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.validation.BindingResult
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import javax.validation.constraints.Email
import javax.validation.constraints.NotBlank
import javax.validation.constraints.Size

/**
 * アカウント用のコントローラー
 *
 * @property accountService
 */
@Validated
@RestController
@RequestMapping(value = [AccountController.ACCOUNT_PATH])
class AccountController(
    private val accountService: AccountService
) {

    companion object {
        const val ACCOUNT_PATH = "/account"
        const val CREATE_ACCOUNT_SUFFIX = "/create"
        const val VERIFY_EMAIL_SUFFIX = "/verify-email"
    }

    /**
     * アカウントを作成する
     *
     * @param req リクエストパラメータ
     * @param result バリデーション結果
     */
    @PostMapping(
        value = [CREATE_ACCOUNT_SUFFIX],
        consumes = [MediaType.ALL_VALUE],
        produces = [MediaType.APPLICATION_JSON_VALUE]
    )
    fun create(
        @RequestBody(required = true) @Validated req: RequestCreateAccoun,
        result: BindingResult
    ): ResponseCreateAccount {
        if (result.hasErrors()) {
            throw ValidException(result.fieldErrors, result.globalErrors)
        }
        val token = accountService.createAccount(req.userId, req.email, req.password)
        return ResponseCreateAccount(token)
    }

    data class RequestCreateAccoun(
        @field:Size(min = 1, max = 32)
        val userId: String,
        @field:NotBlank
        @field:Email
        val email: String,
        @field:Size(min = 8, max = 64)
        val password: String
    )

    data class ResponseCreateAccount(
        val token: String
    )

    /**
     * アカウントのアクティベートを実施する
     *
     * @param req リクエストパラメータ
     * @param result バリデーション結果
     */
    @PostMapping(
        value = [VERIFY_EMAIL_SUFFIX],
        consumes = [MediaType.ALL_VALUE],
        produces = [MediaType.APPLICATION_JSON_VALUE]
    )
    fun verifyEmail(
        @RequestBody(required = true) @Validated req: RequestVerifyEmail,
        result: BindingResult
    ): ResponseVerifyEmail {
        if (result.hasErrors()) {
            throw ValidException(result.fieldErrors, result.globalErrors)
        }
        val account = accountService.verifyEmail(req.token)
        val qrCode = accountService.getQRCode(account)
        return ResponseVerifyEmail(qrCode)
    }

    data class RequestVerifyEmail(
        @field:NotBlank
        val token: String
    )

    data class ResponseVerifyEmail(
        val qrCode: String
    )

    /**
     * JWTAuthorizationFilterの確認用エンドポイント
     *
     * @return メッセージ
     */
    @PostMapping(
        value = ["/test"],
        consumes = [MediaType.ALL_VALUE],
        produces = [MediaType.APPLICATION_JSON_VALUE]
    )
    fun test(): String {
        val jwt = SecurityContextHolder.getContext().authentication.credentials as String
        return "認可されているよ jwt:$jwt"
    }
}
