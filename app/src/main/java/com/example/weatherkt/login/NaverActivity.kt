package com.example.weatherkt.login

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.weatherkt.MainActivity
import com.example.weatherkt.R
import com.nhn.android.naverlogin.OAuthLogin
import com.nhn.android.naverlogin.OAuthLoginHandler

class NaverActivity : AppCompatActivity() {
    var context: Context? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        context = applicationContext
        Naver() // 네이버 계정 연동
    }
    fun Naver() { //네이버 계정 연동
        val mOAuthLoginModule = OAuthLogin.getInstance()
        mOAuthLoginModule.init(
            context,
            getString(R.string.naver_client_id)
            , getString(R.string.naver_client_secret)
            , getString(R.string.naver_client_name)
        )
        @SuppressLint("HandlerLeak") val mOAuthLoginHandler: OAuthLoginHandler =
            object : OAuthLoginHandler() {
                override fun run(success: Boolean) {
                    if (success) {
                        val accessToken: String = mOAuthLoginModule.getAccessToken(context)
                        val refreshdToken: String =
                            mOAuthLoginModule.getRefreshToken(context)
                        val expiresAt: Long = mOAuthLoginModule.getExpiresAt(context)
                        val tokenType: String = mOAuthLoginModule.getTokenType(context)
                        Log.i("LoginData", "accessToken : $accessToken")
                        Log.i("LoginData", "refreshToken$refreshdToken")
                        Log.i("LoginData", "expiresAt$expiresAt")
                        Log.i("LoginData", "tokenType$tokenType")
                        Moving() //페이지 이동
                    } else {
                        val errorCode: String =
                            mOAuthLoginModule.getLastErrorCode(context).getCode()
                        val errorDesc: String = mOAuthLoginModule.getLastErrorDesc(context)
                        Toast.makeText(
                            context,
                            "errorCode: $errorCode, errorDesc: $errorDesc",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
        mOAuthLoginModule.startOauthLoginActivity(this@NaverActivity, mOAuthLoginHandler)
    }

    fun Moving() { //페이지 이동동
        val intent = Intent(this@NaverActivity, MainActivity::class.java)
        context!!.startActivity(intent)
        finish()
    }
}