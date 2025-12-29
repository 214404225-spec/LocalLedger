package com.example.localledger

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.example.localledger.data.local.AppDatabase
import com.example.localledger.data.remote.ExchangeRateApi
import com.example.localledger.data.repository.TransactionRepository
import com.example.localledger.ui.MainScreen
import com.example.localledger.ui.theme.LocalLedgerTheme
import com.example.localledger.viewmodel.TransactionViewModel
import com.google.gson.Gson
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 初始化数据库
        val database = AppDatabase.getDatabase(this)

        // 初始化 Retrofit
        val okHttpClient = OkHttpClient.Builder()
            .connectTimeout(10, TimeUnit.SECONDS)
            .readTimeout(10, TimeUnit.SECONDS)
            .build()

        // 🔑 替换 YOUR_API_KEY 为你的实际 Key！
        val retrofit = Retrofit.Builder()
            .baseUrl("https://v6.exchangerate-api.com/v6/754b743f7a7295b1a0cd228a/")
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create(Gson()))
            .build()

        val api = retrofit.create(ExchangeRateApi::class.java)

        // 创建依赖
        val repository = TransactionRepository(
            transactionDao = database.transactionDao(),
            exchangeRateDao = database.exchangeRateDao(),
            api = api
        )

        val viewModel = TransactionViewModel(repository)

        setContent {
            LocalLedgerTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    MainScreen(viewModel = viewModel)
                }
            }
        }
    }
}