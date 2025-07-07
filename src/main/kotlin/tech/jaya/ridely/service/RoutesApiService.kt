package tech.jaya.ridely.service

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST
import tech.jaya.ridely.dto.ComputeRoutesRequest
import tech.jaya.ridely.dto.ComputeRoutesResponse

interface GoogleRoutesApiService {
    @POST("directions/v2:computeRoutes")
    suspend fun computeRoutes(
        @Header("X-Goog-Api-Key") apiKey: String,
        @Header("X-Goog-FieldMask") fieldMask: String,
        @Header("Content-Type") contentType: String,
        @Body request: ComputeRoutesRequest
    ): ComputeRoutesResponse
}

object RetrofitClient {
    private const val BASE_URL = "https://routes.googleapis.com/"

    val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        })
        .build()

    val instance: GoogleRoutesApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(okHttpClient)
            .build()
            .create(GoogleRoutesApiService::class.java)
    }
}