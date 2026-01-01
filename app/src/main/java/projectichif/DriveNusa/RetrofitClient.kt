        package projectichif.DriveNusa

        import okhttp3.OkHttpClient
        import okhttp3.logging.HttpLoggingInterceptor
        import projectichif.DriveNusa.api.AuthApi
        import projectichif.DriveNusa.api.AuthInterceptor
        import retrofit2.Retrofit
        import retrofit2.converter.gson.GsonConverterFactory
        import java.util.concurrent.TimeUnit

        object ApiClient {

            const val BASE_URL ="https://driveenusa.com/api/"
            const val BASE_IMAGE_URL ="https://driveenusa.com/storage"

            // ========================
            // LOGGING
            // ========================
            private val logging = HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY
            }

            // ========================
            // PUBLIC CLIENT (NO TOKEN)
            // ========================
            private val publicClient: OkHttpClient by lazy {
                OkHttpClient.Builder()
                    .addInterceptor(logging)
                    .connectTimeout(60, TimeUnit.SECONDS)
                    .readTimeout(60, TimeUnit.SECONDS)
                    .writeTimeout(60, TimeUnit.SECONDS)
                    .build()
            }

            // ========================
            // AUTH CLIENT (WITH TOKEN)
            // ========================
            private val authClient: OkHttpClient by lazy {
                OkHttpClient.Builder()
                    .addInterceptor(AuthInterceptor {
                        UserLocal.getToken(MyApp.appContext)
                    })
                    .addInterceptor(logging)
                    .connectTimeout(60, TimeUnit.SECONDS)
                    .readTimeout(60, TimeUnit.SECONDS)
                    .writeTimeout(60, TimeUnit.SECONDS)
                    .build()
            }

            // ========================
            // RETROFIT
            // ========================
            private fun retrofit(client: OkHttpClient): Retrofit =
                Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .client(client)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()

            // ========================
            // API
            // ========================
            val publicAuthApi: AuthApi by lazy {
                retrofit(publicClient).create(AuthApi::class.java)
            }

            val authApi: AuthApi by lazy {
                retrofit(authClient).create(AuthApi::class.java)
            }
        }
