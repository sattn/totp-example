package com.takahiro310.totpexample.repository

import com.takahiro310.totpexample.model.Account
import org.apache.ibatis.annotations.Insert
import org.apache.ibatis.annotations.Mapper
import org.apache.ibatis.annotations.Select
import org.apache.ibatis.annotations.Update
import org.springframework.stereotype.Repository

/**
 * アカウントリポジトリ
 */
@Mapper
@Repository
interface AccountRepository {

    /**
     * アカウント情報を作成する
     *
     * @param account
     */
    @Insert("""
        INSERT INTO account
            (user_id, password, email, secret, created_at)
        VALUES
            (#{userId}, #{password}, #{email}, #{secret}, now())
    """)
    fun create(account: Account)

    /**
     * アカウント情報を有効化
     *
     * @param account
     */
    @Update("""
        UPDATE account 
        SET 
            enabled = true
            ,email_verified_at = NOW()
        WHERE
            user_id = #{userId}
    """)
    fun verified(account: Account)

    /**
     * ログインIDに該当するアカウント情報を取得する
     *
     * @param userId
     * @return
     */
    @Select("""
        SELECT
            user_id, password, email, secret, enabled
        FROM
            account
        WHERE
            user_id = #{userId}
    """)
    fun findByUserId(userId: String): Account?
}
