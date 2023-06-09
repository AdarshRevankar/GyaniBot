package servify.hackathon.mumbaihacks.gyanibot.model

import com.google.gson.GsonBuilder
import retrofit2.Retrofit
import retrofit2.adapter.rxjava3.RxJava3CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import servify.hackathon.mumbaihacks.gyanibot.network.ApiService


class RetrofitClient_v2 private constructor() {

    private var apiRepository: ApiService


    init {
        val gson = GsonBuilder()
            .setLenient()
            .create()

        val retrofit: Retrofit = Retrofit.Builder().baseUrl(ApiService.QUERY_STABLE_DIFFUSION_BASE_URL)
            .addCallAdapterFactory(RxJava3CallAdapterFactory.create())
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
        apiRepository = retrofit.create(ApiService::class.java)
    }

    fun getRepository(): ApiService {
        return apiRepository
    }

    companion object {

        @get:Synchronized
        var instance: RetrofitClient_v2? = null
            get() {
                if (field == null) {
                    field = RetrofitClient_v2()
                }
                return field
            }
            private set
    }
}