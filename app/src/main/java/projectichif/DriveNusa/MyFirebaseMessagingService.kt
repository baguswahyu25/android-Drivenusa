//package projectichif.DriveNusa
//
//import android.content.Intent
//import android.util.Log
//import com.google.firebase.messaging.FirebaseMessagingService
//import com.google.firebase.messaging.RemoteMessage
//import kotlinx.coroutines.CoroutineScope
//import kotlinx.coroutines.Dispatchers
//import kotlinx.coroutines.launch
//import projectichif.DriveNusa.api.AuthRepository
//
//class MyFirebaseMessagingService : FirebaseMessagingService() {
//
//    override fun onNewToken(token: String) {
//        super.onNewToken(token)
//        Log.d("FCM", "New token: $token")
//
//        CoroutineScope(Dispatchers.IO).launch {
//            AuthRepository.sendFcmToken(applicationContext, token)
//        }
//    }
//
//    override fun onMessageReceived(message: RemoteMessage) {
//        super.onMessageReceived(message)
//
//        val title = message.notification?.title ?: "Notifikasi"
//        val body = message.notification?.body ?: ""
//
//        val intent = Intent(this, HomeActivity::class.java).apply {
//            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
//        }
//
//        NotificationUtil.show(
//            this,
//            title,
//            body,
//            intent
//        )
//    }
//}
