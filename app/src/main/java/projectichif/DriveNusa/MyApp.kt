package projectichif.DriveNusa

import android.app.Application
import android.content.Context
import com.midtrans.sdk.uikit.SdkUIFlowBuilder

class MyApp : Application() {

    companion object {
        lateinit var appContext: Context
            private set
    }

    override fun onCreate() {
        super.onCreate()

        appContext = applicationContext

        SdkUIFlowBuilder.init()
            .setContext(this)
            .setClientKey("SB-Mid-client-fuWmq4VZw_-zFpm0 ")
            .setMerchantBaseUrl("http://192.168.1.7:8000/")
            .enableLog(true)
            .buildSDK()
    }
}
