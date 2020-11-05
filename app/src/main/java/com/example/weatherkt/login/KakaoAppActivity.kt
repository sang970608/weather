package com.example.weatherkt.login

import android.app.Activity
import android.app.Application
import android.content.Context
import com.kakao.auth.*


class KakaoAppActivity: Application() {
    private var instance: KakaoAppActivity? = null
    fun getKakaoAppActivityContext(): KakaoAppActivity? { //인스턴스정보 가져오기
        checkNotNull(instance) { "이 앱은 카카오에서 정보를 받아올 수 없습니다." }
        return instance
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
        KakaoSDK.init(KakaoSDKAdapter())
    }

    override fun onTerminate() {
        super.onTerminate()
        instance = null
    }

    inner class KakaoSDKAdapter : KakaoAdapter() {
        override fun getSessionConfig(): ISessionConfig {
            return object : ISessionConfig {
                override fun getAuthTypes(): Array<AuthType> {
                    return arrayOf(AuthType.KAKAO_LOGIN_ALL)
                }

                override fun isUsingWebviewTimer(): Boolean {
                    return false
                }

                override fun isSecureMode(): Boolean {
                    return false
                }


                override fun getApprovalType(): ApprovalType? {
                    return ApprovalType.INDIVIDUAL
                }

                override fun isSaveFormData(): Boolean {
                    return true
                }
            }
        }

        override fun getApplicationConfig(): IApplicationConfig { // 어플이 갖고있는 정보를 얻는 메소드
            return object : IApplicationConfig {
                fun getTopActivity(): Activity {
                    TODO("Not yet implemented")
                }

                override fun getApplicationContext(): KakaoAppActivity? {
                    return getKakaoAppActivityContext()
                }
            }
        }
    }
}
