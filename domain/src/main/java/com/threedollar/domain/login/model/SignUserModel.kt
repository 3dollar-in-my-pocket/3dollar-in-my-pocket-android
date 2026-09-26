package com.threedollar.domain.login.model

/**
 * 로그인·회원가입 성공 시 서버가 돌려주는 사용자 인증 정보.
 *
 * [token] 은 서버가 명시적으로 `null` 을 내려줄 수 있어 nullable 로 둔다.
 * 기존 네트워크 DTO(`SignUser`)를 Gson 이 채우던 동작과 같게 맞춘 것이다.
 */
data class SignUserModel(
    val token: String?,
    val userId: Int,
)
