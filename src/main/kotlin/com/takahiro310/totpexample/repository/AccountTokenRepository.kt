package com.takahiro310.totpexample.repository

import org.apache.ibatis.annotations.Insert
import org.apache.ibatis.annotations.Mapper
import org.apache.ibatis.annotations.Select
import org.springframework.stereotype.Repository

/**
 * メール認証用のリポジトリ
 */
@Mapper
@Repository
interface AccountTokenRepository {

    /**
     * アカウントトークンを作成する
     *
     * @param token
     * @param userId
     */
    @Insert("""
        REPLACE INTO account_token
            (token, user_id, expired_at, created_at)
        VALUES
            (#{token}, #{userId}, CURRENT_TIMESTAMP + INTERVAL 30 MINUTE, CURRENT_TIMESTAMP)
    """)
    fun create(token: String, userId: String)

    /**
     * 有効なトークンを保有しているユーザーIDを取得する
     *
     * @param token
     * @return
     */
    @Select("""
        SELECT
            user_id
        FROM
            account_token
        WHERE
            token = #{token} AND expired_at >= CURRENT_TIMESTAMP
    """)
    fun findUserIdByToken(token: String): String?
}
