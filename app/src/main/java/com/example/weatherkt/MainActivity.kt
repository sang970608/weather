package com.example.weatherkt

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.*
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import com.example.weatherkt.data.LocationInterface
import com.example.weatherkt.data.PostItem
import com.example.weatherkt.databinding.ActivityMainBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity() {
    var API_KEY = "482ad99bc7af267ac9752c284866539b"
    var baseURI = "http://api.openweathermap.org/"
    var latitude = 0.0 //위도
    var longitude = 0.0 //경도
    var address: Address? = null //주소
    lateinit var Binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        Binding.progressBar.visibility = View.VISIBLE
        GetLoc() //위치 권한 받기
        Binding.imgLoc.setOnClickListener(Loc) //구글맵 보기로 이동
    }

    var Loc =
        View.OnClickListener { getLatLon(latitude, longitude) }

    private fun initMyAPI(latitude: Double, longitude: Double) { //위도, 경도로 api정보 가져오기
        var retrofit = Retrofit.Builder()
            .baseUrl(baseURI)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        val locationInterface = retrofit.create(LocationInterface::class.java).also {
            it.getLocation(latitude, longitude, API_KEY)
            .enqueue(object : Callback<PostItem> {
                override fun onResponse(
                    call: Call<PostItem>,
                    response: Response<PostItem>
                ) {
                    if (response.isSuccessful()) {
                        val Temp: Double =
                            response.body()!!.main!!.temp!!.toDouble().minus(273) //기온
                        val feels_like: Double =
                            response.body()!!.main!!.feels_like!!.toDouble() - 273 //체감온도
                        val sunRise: Long =
                            response.body()!!.sys!!.sunrise!!.toLong() * 1000 //일출
                        val sunSet: Long =
                            response.body()!!.sys!!.sunset!!.toLong() * 1000 //일몰
                        val weather: String? =
                            response.body()!!.weather[0].main //날씨
                        if (weather != null){
                            Weather(weather)
                        }
                        Binding.textTemp.setText(
                            """현재 온도는 ${Math.round(Temp * 10) / 10.0}도 입니다.
 체감온도 : ${Math.round(feels_like * 10) / 10.0}"""
                        )
                        SunTime(sunRise, sunSet)
                        Binding.progressBar.visibility = View.GONE
                        val geocoder = Geocoder(this@MainActivity, Locale.getDefault())
                        var addresses: List<Address>
                        try {
                            addresses = geocoder.getFromLocation(
                                latitude, longitude, 1
                            )
                            address = addresses[0]
                            Binding.textLoc.setText(address!!.getAddressLine(0).toString())
                        } catch (ioException: IOException) {
                        }
                    } else Toast("응답이 실패하였습니다.")
                }

                override fun onFailure(
                    call: Call<PostItem>,
                    t: Throwable
                ) {
                    Toast("응답에 실패하였습니다.")
                }
            })
        }
    }

    fun GetLoc() { //위치 권한 받기
        val lm =
            getSystemService(Context.LOCATION_SERVICE) as LocationManager
        if (Build.VERSION.SDK_INT >= 23 && ContextCompat.checkSelfPermission(
                applicationContext,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) { // 권한을 못받았을시
            ActivityCompat.requestPermissions(
                this@MainActivity, arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION
                ), 0
            )
        } else {
            val location =
                lm.getLastKnownLocation(LocationManager.GPS_PROVIDER)
            longitude = location.longitude
            latitude = location.latitude
            Binding.textLatLong.setText("위도 : $latitude \n 경도 : $longitude")
            initMyAPI(latitude, longitude)//위도, 경도로 api정보 가져오기
            lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 1f, gpsLocationListener)
            lm.requestLocationUpdates(
                LocationManager.NETWORK_PROVIDER,
                1000,
                1f,
                gpsLocationListener
            )
        }
    }

    fun getLatLon(lat: Double, lon: Double) { // 구글맵에 좌표값 전달
        val intent = Intent(this@MainActivity, LocationActivity::class.java)
        intent.putExtra("lat", lat)
        intent.putExtra("lon", lon)
        startActivity(intent)
        finish()
    }

    val gpsLocationListener: LocationListener =
        object : LocationListener {
            override fun onLocationChanged(location: Location) { //위치 일정 주기마다 새로고침
                longitude = location.longitude
                latitude = location.latitude
                Binding.textLatLong.setText("위도 : $latitude \n 경도 : $longitude")
                initMyAPI(latitude, longitude)//위도, 경도로 api정보 가져오기
            }

            override fun onStatusChanged(
                provider: String,
                status: Int,
                extras: Bundle
            ) {
            }

            override fun onProviderEnabled(provider: String) {}
            override fun onProviderDisabled(provider: String) {}
        }

    private fun Weather(weather: String) { //날씨를 한글로 번역
        when (weather) {
            "Thunderstorm" -> {
                Binding.textText.setText("천둥번개 치는날")
                Binding.imgWeather.setImageResource(R.drawable.thunderstorm)
            }
            "Drizzle" -> {
                Binding.textText.setText("이슬비 내리는 날")
                Binding.imgWeather.setImageResource(R.drawable.drizzle)
            }
            "Rain" -> {
                Binding.textText.setText("비 내리는 날")
                Binding.imgWeather.setImageResource(R.drawable.rain)
            }
            "Snow" -> {
                Binding.textText.setText("눈 내리는 날")
                Binding.imgWeather.setImageResource(R.drawable.snow)
            }
            "Atmosphere" -> {
                Binding.textText.setText("안개끼는 날")
                Binding.imgWeather.setImageResource(R.drawable.mist)
            }
            "Clear" -> {
                Binding.textText.setText("맑은 날")
                Binding.imgWeather.setImageResource(R.drawable.clear)
            }
            "Clouds" -> {
                Binding.textText.setText("구름낀 날")
                Binding.imgWeather.setImageResource(R.drawable.cloud)
            }
            else -> Binding.textText.setText("위치 정보를 켜주세요")
        }
    }

    private fun SunTime(sunRise: Long, sunSet: Long) { //일출, 일몰시간
        var AM = ""
        var PM = ""
        val sunRiseTime = Date(sunRise)
        val secondFormat1 =
            SimpleDateFormat("ss", Locale.getDefault())
        val minuteFormat1 =
            SimpleDateFormat("mm", Locale.getDefault())
        val hourFormat1 =
            SimpleDateFormat("HH", Locale.getDefault())
        var fullHour = hourFormat1.format(sunRiseTime).toInt()
        val hour1 = (fullHour % 12).toString()
        val minute1 = minuteFormat1.format(sunRiseTime)
        val second1 = secondFormat1.format(sunRiseTime)
        AM = if (fullHour / 12 == 0) "오전" else "오후"
        val sunSetTime = Date(sunSet)
        val secondFormat2 =
            SimpleDateFormat("ss", Locale.getDefault())
        val minuteFormat2 =
            SimpleDateFormat("mm", Locale.getDefault())
        val hourFormat2 =
            SimpleDateFormat("HH", Locale.getDefault())
        fullHour = hourFormat2.format(sunSetTime).toInt()
        val hour2 = (fullHour % 12).toString()
        val minute2 = minuteFormat2.format(sunSetTime)
        val second2 = secondFormat2.format(sunSetTime)
        PM = if (fullHour / 12 == 0) "오전" else "오후"
        Binding.textSun.setText(
            """일출 : $AM ${hour1}시${minute1}분${second1}초 
 일몰 : $PM ${hour2}시${minute2}분${second2}초"""
        )
    }

    fun Toast(text: String?) {
        Toast.makeText(this, text, Toast.LENGTH_SHORT).show()
    }
}

private fun <T> Call<T>?.enqueue(callback: Callback<PostItem>) {

}
