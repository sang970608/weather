package com.example.weatherkt.login

import android.content.Context
import android.content.Intent
import android.util.Log
import com.example.weatherkt.MainActivity
import com.kakao.auth.ISessionCallback
import com.kakao.network.ErrorResult
import com.kakao.usermgmt.UserManagement
import com.kakao.usermgmt.callback.MeV2ResponseCallback
import com.kakao.usermgmt.response.MeV2Response
import com.kakao.usermgmt.response.model.UserProfile
import com.kakao.util.exception.KakaoException

class SessionCallback(var mcontext: Context) : ISessionCallback {
    override fun onSessionOpened() {
        requestMe() // 카카오 세션 요청
    }

    override fun onSessionOpenFailed(exception: KakaoException) { // 세션 오픈 실패
        Log.e("SessionCallback :: ", "onSessionOpenFailed : " + exception.message)
    }

    fun requestMe() { //카카오 세션 요청
        UserManagement.getInstance().me(object : MeV2ResponseCallback() {
                override fun onSessionClosed(errorResult: ErrorResult) {
                    Log.e("KAKAO_API", "세션이 닫혀있음: $errorResult")
                }

                override fun onFailure(errorResult: ErrorResult) {
                    Log.e("KAKAO_API", "사용자 정보 요청 실패: $errorResult")
                }

                override fun onSuccess(result: MeV2Response?) {

                    val intent = Intent(mcontext, MainActivity::class.java)
                    mcontext.startActivity(intent)
                }
            })
    }
}

