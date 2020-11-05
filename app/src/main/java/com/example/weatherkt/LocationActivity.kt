package com.example.weatherkt

import android.app.AlertDialog
import android.content.Intent
import android.location.Address
import android.location.Geocoder
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.FragmentActivity
import com.example.weatherkt.data.LocationInterface
import com.example.weatherkt.data.PostItem
import com.example.weatherkt.databinding.ActivityLocationBinding
import com.example.weatherkt.databinding.ActivityMainBinding
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.*

class LocationActivity : FragmentActivity(), OnMapReadyCallback {
    private var mMap: GoogleMap? = null
    private val geocoder: Geocoder? = null
    private lateinit var Binding: ActivityLocationBinding
    var addressText: String? = null //주소명
    var address: Address? = null
    var lat: Double? = null //위도
    var lon: Double? = null //경도
    var weather: String? = null //날씨명
    var API_KEY = "482ad99bc7af267ac9752c284866539b"
    var baseURI = "http://api.openweathermap.org/"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Binding = DataBindingUtil.setContentView(this, R.layout.activity_location)

        Binding.imgArrow.setOnClickListener(Back) //뒤로가기 버튼 이벤트
        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment? // 맵 프래그먼트 적용
        mapFragment!!.getMapAsync(this)
        Binding.imgSearch.setOnClickListener(View.OnClickListener { // 검색창에서 텍스트를 가져온다
            val searchText = Binding.editSearch.getText().toString()
            val geocoder = Geocoder(baseContext)
            var addresses: List<Address?>? = null
            try {
                addresses = geocoder.getFromLocationName(searchText, 3)
                if (addresses != null && !(addresses?.equals(" ") ?: (" " == null))) {
                    search(addresses)
                }
            } catch (e: Exception) {
            }
        })
        Binding.btnSearch.setOnClickListener(Enter) // 검색 버튼 이벤트
    }

    var Enter = View.OnClickListener { //검색 버튼 이벤트
        val builder = AlertDialog.Builder(this@LocationActivity)
        builder.setTitle("날씨")
            .setMessage(weather)
            .setPositiveButton("확인", null)
            .show()
    }

    protected fun search(addresses: List<Address?>) { //지도에서 좌표 찾아가는 메소드
        address = addresses[0]
        val latLng = LatLng(address!!.latitude, address!!.longitude)
        addressText = String.format(
            "%s, %s",
            if (address!!.maxAddressLineIndex > 0) address!!
                .getAddressLine(0) else " ", address!!.featureName
        )
        val markerOptions = MarkerOptions()
        markerOptions.position(latLng)
        lat = address!!.latitude
        lon = address!!.longitude
        initMyAPI(lat!!, lon!!)
        markerOptions.title(addressText)
        mMap!!.clear()
        mMap!!.addMarker(markerOptions)
        mMap!!.moveCamera(CameraUpdateFactory.newLatLng(latLng))
        mMap!!.animateCamera(CameraUpdateFactory.zoomTo(15f))
    }

    var Back = View.OnClickListener { //뒤로가기 버튼 이벤트
        val intent = Intent(this@LocationActivity, MainActivity::class.java)
        startActivity(intent)
        finish()
    }

    override fun onMapReady(googleMap: GoogleMap) { // 메인에서 가져온 좌표값 지정하는 메소드
        mMap = googleMap
        val intent = intent
        lat = intent.getDoubleExtra("lat", 0.0)
        lon = intent.getDoubleExtra("lon", 0.0)
        initMyAPI(lat!!, lon!!)
        val SEOUL = LatLng(lat!!, lon!!)
        val markerOptions = MarkerOptions()
        markerOptions.position(SEOUL)
        markerOptions.title("서울")
        markerOptions.snippet(addressText)
        mMap!!.addMarker(markerOptions)
        mMap!!.moveCamera(CameraUpdateFactory.newLatLngZoom(SEOUL, 15f))
        mMap!!.setOnMarkerClickListener {
            Toast.makeText(this@LocationActivity, "클릭되었습니다", Toast.LENGTH_SHORT).show()
            false
        }
    }

    private fun initMyAPI(latitude: Double, longitude: Double) { // 날씨 정보 가져오는 메소드
        val retrofit = Retrofit.Builder()
            .baseUrl(baseURI)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        val locationInterface: LocationInterface = retrofit.create<LocationInterface>(
            LocationInterface::class.java
        )
        locationInterface.getLocation(latitude, longitude, API_KEY)
            .enqueue(object : Callback<PostItem> {
                override fun onResponse(call: Call<PostItem>, response: Response<PostItem>) {
                    val weathers: String? = response.body()!!.weather[0].main
                    if (weathers != null){
                        Weather(weathers!!)
                    }
                }

                override fun onFailure(call: Call<PostItem>, t: Throwable) {}
            })
    }

    private fun Weather(weathers: String) {
        weather = weathers
    }
}

private fun <T> Call<T>?.enqueue(callback: Callback<PostItem>) {

}
