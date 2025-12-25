    package projectichif.DriveNusa

    import okhttp3.OkHttpClient
    import okhttp3.logging.HttpLoggingInterceptor
    import projectichif.DriveNusa.api.AuthApi
    import projectichif.DriveNusa.api.AuthInterceptor
    import retrofit2.Retrofit
    import retrofit2.converter.gson.GsonConverterFactory
    import java.util.concurrent.TimeUnit

    object ApiClient {

        const val BASE_URL = "http://192.168.1.7:8000/api/"
        const val BASE_IMAGE_URL = "http://192.168.1.7:8000/storage"

        private val client by lazy {

            val logging = HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY
            }

            OkHttpClient.Builder()
                .addInterceptor(
                    AuthInterceptor {
                        UserLocal.getToken(MyApp.appContext)
                    }
                )
                .addInterceptor(logging)
                .connectTimeout(60, TimeUnit.SECONDS)
                .readTimeout(60, TimeUnit.SECONDS)
                .writeTimeout(60, TimeUnit.SECONDS)
                .build()
        }

        val authApi: AuthApi by lazy {
            Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(AuthApi::class.java)
        }

    }
