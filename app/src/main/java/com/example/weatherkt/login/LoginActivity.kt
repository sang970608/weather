package com.example.weatherkt.login

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.example.weatherkt.R
import com.example.weatherkt.databinding.ActivityLoginBinding

class LoginActivity : AppCompatActivity() {
    private lateinit var lBinding: ActivityLoginBinding //바인딩
    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        lBinding = DataBindingUtil.setContentView(this, R.layout.activity_login)

        lBinding.google.setOnClickListener{ //구글 로그인 리스너
            val intent = Intent(this, GoogleActivity::class.java)
            startActivity(intent)
            finish()
        }
        lBinding.naver.setOnClickListener{ // 네이버 로그인 리스너
            val intent = Intent(this, NaverActivity::class.java)
            startActivity(intent)
            finish()
        }
        lBinding.kakao.setOnClickListener{ // 카카오 로그인 리스너
            val intent = Intent(this, KakaoActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

}