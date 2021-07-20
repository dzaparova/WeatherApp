package kg.tutorialapp.weather.ui


import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.messaging.FirebaseMessaging
import kg.tutorialapp.weather.R
import kg.tutorialapp.weather.format
import kg.tutorialapp.weather.models.Constants
import kg.tutorialapp.weather.models.ForeCast
import kg.tutorialapp.weather.ui.rv.DailyForeCastAdapter
import kg.tutorialapp.weather.ui.rv.HourlyForeCastAdapter
import org.koin.android.viewmodel.ext.android.getViewModel
import kotlin.math.roundToInt

 class MainActivity() : AppCompatActivity() {
    private lateinit var vm: MainViewModel

    private lateinit var dailyForeCastAdapter: DailyForeCastAdapter
    private lateinit var hourlyForeCastAdapter: HourlyForeCastAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        vm=getViewModel(MainViewModel::class)

        setupViews()
        setupRecyclerView()
        subscribeToLiveData()

        FirebaseMessaging.getInstance().token.addOnSuccessListener {
            Log.i("TOKEN", it)
        }
        intent.getStringExtra("EXTRA")?.let {
            Toast.makeText(this,it,Toast.LENGTH_LONG).show()
        }


    }

    private fun setupViews() {
        val tv_refresh=findViewById<TextView>(R.id.tv_refresh)
        tv_refresh.setOnClickListener {
            vm.showLoading()
            vm.getWeatherFromApi()

        }
    }
    private fun setupRecyclerView() {
        val rv_daily_forecast=findViewById<RecyclerView>(R.id.rv_daily_forecast)
       dailyForeCastAdapter= DailyForeCastAdapter()
        rv_daily_forecast.adapter=dailyForeCastAdapter
        val rv_hourly_forecast=findViewById<RecyclerView>(R.id.rv_hourly_forecast)
        hourlyForeCastAdapter = HourlyForeCastAdapter()
        rv_hourly_forecast.adapter = hourlyForeCastAdapter
    }

    private fun subscribeToLiveData() {
        vm.getForeCastAsLive().observe(this, Observer {
            it?.let {
                setValuesToViews(it)
                loadWeatherIcon(it)
                getDataToRecyclerView(it)
            }
        })
        vm._isLoading.observe(this, Observer {
            when(it){
                true->showLoading()
                false->hideLoading()
            }
        })
    }

    private fun getDataToRecyclerView(it: ForeCast) {
        it.daily?.let { dailyList ->
            dailyForeCastAdapter.setItem(dailyList)
        }

        it.hourly?.let { hourlyList ->
            hourlyForeCastAdapter.setItemsHourly(hourlyList)
        }
    }

    private fun showLoading() {
        val progress=findViewById<ProgressBar>(R.id.progress)
        progress.post {
            progress.visibility= View.VISIBLE
        }

    }
    private fun hideLoading(){

        val progress=findViewById<ProgressBar>(R.id.progress)
        progress.postDelayed({
            progress.visibility= View.INVISIBLE
        }, 2000)


    }

    private fun setValuesToViews(it:ForeCast) {
        val tv_temperature=findViewById<TextView>(R.id.tv_temperature)
        val tv_date=findViewById<TextView>(R.id.tv_date)
        val tv_temp_max=findViewById<TextView>(R.id.tv_temp_max)
        val tv_temp_min=findViewById<TextView>(R.id.tv_temp_min)
        val tv_feels_like=findViewById<TextView>(R.id.tv_feels_like)
        val tv_weather=findViewById<TextView>(R.id.tv_weather)
        val tv_sunsrise=findViewById<TextView>(R.id.tv_sunrise)
        val tv_sunset=findViewById<TextView>(R.id.tv_sunset)
        val tv_humidity=findViewById<TextView>(R.id.tv_humidity)


        tv_temperature.text = it.current?.temp?.roundToInt().toString()
        tv_date.text = it.current?.date.format()
        tv_temp_max.text = it.daily?.get(0)?.temp?.max?.roundToInt()?.toString()
        tv_temp_min.text = it.daily?.get(0)?.temp?.min?.roundToInt()?.toString()
        tv_feels_like.text = it.current?.feels_like?.roundToInt()?.toString()
        tv_weather.text = it.current?.weather?.get(0)?.description
        tv_sunsrise.text = it.current?.sunrise?.format("HH:mm")
        tv_sunset.text = it.current?.sunset?.format("HH:mm")
        tv_humidity.text = "${it.current?.humidity?.toString()} %"
    }

    private fun loadWeatherIcon(it: ForeCast) {
        val iv_weather_icon =findViewById<ImageView>(R.id.iv_weather_icon)
        it.current?.weather?.get(0)?.icon?.let { icon ->

            Glide.with(this)
                    .load("${Constants.iconUri}${icon}${Constants.iconFormat}")
                    .into(iv_weather_icon)
        }
    }

}