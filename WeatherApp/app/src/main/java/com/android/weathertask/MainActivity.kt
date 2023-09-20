package com.android.weathertask

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.android.weathertask.databinding.ActivityMainBinding
import com.android.weathertask.models.Weather
import com.android.weathertask.models.WeatherX
import com.bumptech.glide.Glide
import com.google.gson.Gson
import okhttp3.*
import java.io.IOException
import java.text.DateFormat.getDateInstance
import java.util.*

class MainActivity : AppCompatActivity() {
    lateinit var binding : ActivityMainBinding
    private val client = OkHttpClient()
    val gson = Gson()
    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityMainBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        getWeather("aden")
        binding.searchButton.setOnClickListener {
            if (binding.editTextSearch.text.toString() == ""){
                Toast.makeText(this@MainActivity,"Enter Your City",Toast.LENGTH_LONG).show()
            } else {
                getWeather(binding.editTextSearch.text.toString())
                clearEditText()
            }
        }
    }

    private fun clearEditText() {
        binding.editTextSearch.text?.clear()
    }

    private fun getWeather(cityName : String) {
        val url = "http://api.openweathermap.org/data/2.5/weather?q=$cityName&appid=54371fcb754ea3659322fbe6811fb25e"
        val req = Request.Builder().url(url).build()
        client.newCall(req).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Log.i("Fashel",e.message.toString())
            }

            @SuppressLint("SetTextI18n")
            override fun onResponse(call: Call, response: Response) {
                val result = gson.fromJson(response.body?.string(),Weather::class.java)
                if (response.code == 404) {
                    binding.editTextSearch.text?.clear()
                    runOnUiThread {
                        Toast.makeText(this@MainActivity,"The City is not found",Toast.LENGTH_LONG).show()
                    }
                } else {
                    runOnUiThread {
                        binding.textViewCityName.text = result.name + "," + result.sys.country
                        binding.tempResult.text = kelvinToCel(result.main.temp).toString() + " °C"
                        binding.textViewDate.text = getCurrentDate()
                        binding.textViewCloud.text = result.weather[0].description
                        binding.windResult.text = result.wind.speed.toString() + "km/h"
                        binding.degResult.text = result.wind.deg.toString()
                        binding.feelResult.text = kelvinToCel(result.main.feels_like).toString() + " °C"
                        binding.humidityResult.text = result.main.humidity.toString() + "%"
                        Glide.with(this@MainActivity).load("http://openweathermap.org/img/wn/${result.weather[0].icon}@2x.png").into(binding.iconWeather)
                    }
                }
            }
        })
    }
    private fun kelvinToCel(kelvin : Double) : Double {
        return "%.2f".format(kelvin - 273.15).toDouble()
    }
    fun getCurrentDate() : String {
        val sdf = getDateInstance()
        return sdf.format(Date())
    }
}