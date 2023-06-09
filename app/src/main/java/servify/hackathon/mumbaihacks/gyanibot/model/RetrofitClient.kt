package servify.hackathon.mumbaihacks.gyanibot.model

import retrofit2.Retrofit
import retrofit2.adapter.rxjava3.RxJava3CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import servify.hackathon.mumbaihacks.gyanibot.network.ApiService


class RetrofitClient private constructor() {

    private var apiRepository: ApiService

    init {
        val retrofit: Retrofit = Retrofit.Builder().baseUrl(ApiService.QUERY_GPT_BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(RxJava3CallAdapterFactory.create())
            .build()
        apiRepository = retrofit.create(ApiService::class.java)
    }

    fun getRepository(): ApiService {
        return apiRepository
    }

    companion object {

        @get:Synchronized
        var instance: RetrofitClient? = null
            get() {
                if (field == null) {
                    field = RetrofitClient()
                }
                return field
            }
            private set
    }
}