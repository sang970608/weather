package com.example.weatherkt.login

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.example.weatherkt.MainActivity
import com.example.weatherkt.R
import com.example.weatherkt.databinding.ActivityLoginBinding
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.SignInButton
import com.google.android.gms.common.api.ApiException


class GoogleActivity : AppCompatActivity() {
    private var googleSignInButton: SignInButton? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        googleSignInButton = findViewById<SignInButton>(R.id.google)
        val gso =
            GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN) //구글 이메일 및 기본값 요청
                .requestEmail()
                .build()
        val googleSignInClient = GoogleSignIn.getClient(this, gso) // 옵션정보를 클라이언트에 넣음

        val signInIntent: Intent = googleSignInClient.getSignInIntent()
        startActivityForResult(signInIntent, 101) //101은 요청 성공코드
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) { //요청 이후의 함수
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) //구글 및 파이어베이스에서 통신이 됐다면 -1번으로 돌아옴
            when (requestCode){
                101-> try {
                    val task =
                        GoogleSignIn.getSignedInAccountFromIntent(data)
                    val account =
                        task.getResult(ApiException::class.java)
                    Toast.makeText(this, "코드 일치", Toast.LENGTH_SHORT).show()
                    if (account != null) {
                        onLoggedIn(account)
                    }
                } catch (e: ApiException) {
                    e.printStackTrace()
                }
            }
    }
    private fun onLoggedIn(googleSignInAccount: GoogleSignInAccount) { // 계정에 로그인이 되었으면
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }
    override fun onStart() { // 이미 로그인 기록이 있을시
        super.onStart()
        val alreadyloggedAccount = GoogleSignIn.getLastSignedInAccount(this)
        if (alreadyloggedAccount != null) {
            Toast.makeText(this, "이미 로그인 되었습니다.", Toast.LENGTH_SHORT).show()
            onLoggedIn(alreadyloggedAccount)
        } else {
            Log.d("11", "로그인되지 않았습니다.")
        }
    }
}