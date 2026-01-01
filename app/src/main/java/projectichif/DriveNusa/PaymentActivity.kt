package projectichif.DriveNusa

import android.content.Intent
import android.os.Bundle
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.midtrans.sdk.uikit.external.UiKitApi

class PaymentActivity : AppCompatActivity() {

    private lateinit var paymentLauncher: ActivityResultLauncher<Intent>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        paymentLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
                // Midtrans selesai (success / pending / cancel)
                // STATUS DIHANDLE BACKEND (WEBHOOK)
                setResult(RESULT_OK)
                finish()
            }


        val snapToken = intent.getStringExtra("SNAP_TOKEN")
        if (snapToken.isNullOrEmpty()) {
            finish()
            return
        }

        UiKitApi.getDefaultInstance().startPaymentUiFlow(
            this,
            paymentLauncher,
            snapToken
        )

    }

}
