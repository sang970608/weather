package com.example.weatherkt.login

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.weatherkt.MainActivity
import com.example.weatherkt.R
import com.kakao.auth.AuthType
import com.kakao.auth.Session

class KakaoActivity: AppCompatActivity() {
    //    private SessionCallback sessionCallback = new SessionCallback();
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        Kakao() //카카오 세션 오픈
    }

    private fun Kakao() { //카카오 세션 오픈
        val session = Session.getCurrentSession()
        session.addCallback(SessionCallback(this))
        session.open(AuthType.KAKAO_LOGIN_ALL, this@KakaoActivity)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (Session.getCurrentSession().handleActivityResult(requestCode, resultCode, data)) {
            return
        }
        val intent = Intent(this@KakaoActivity, MainActivity::class.java) //확인 되었을시 액티비티 이동
        startActivity(intent)
        super.onActivityResult(requestCode, resultCode, data)
    }
}