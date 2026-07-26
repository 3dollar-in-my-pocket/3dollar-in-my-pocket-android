package com.zion830.threedollars.utils

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.threedollar.common.utils.SharedPrefUtils
import com.threedollar.domain.login.repository.LoginRepository
import com.zion830.threedollars.DynamicLinkActivity
import com.zion830.threedollars.R
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject


@AndroidEntryPoint
class ThreedollarsMessagingService : FirebaseMessagingService() {

    @Inject
    lateinit var loginRepository: LoginRepository

    @Inject
    lateinit var sharedPrefUtils: SharedPrefUtils

    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    /**
     * 재설치, 앱 데이터 복원, 토큰 만료 등으로 FCM 토큰이 회전되면 호출된다.
     * 갱신된 토큰을 서버에 다시 등록하지 않으면 해당 기기는 푸시 발송 대상에서 빠진다.
     */
    override fun onNewToken(token: String) {
        super.onNewToken(token)
        if (sharedPrefUtils.getAccessToken().isNullOrBlank()) return

        serviceScope.launch {
            loginRepository.putPushInformation(token).collect()
        }
    }

    override fun onDestroy() {
        serviceScope.cancel()
        super.onDestroy()
    }

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)
        message.notification?.apply {
            sendNotification(title.toString(), this.body.toString(), message.data["link"].toString())
        }
    }

    private fun sendNotification(title: String, messageBody: String, link: String) {
        val pendingIntent = PendingIntent.getActivity(
            this,
            0,
            Intent(this, DynamicLinkActivity::class.java).apply {
                putExtra("link", link)
                addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            },
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT else PendingIntent.FLAG_ONE_SHOT or PendingIntent.FLAG_UPDATE_CURRENT
        )

        // 알림 채널 설정
        val channelId = "threedollars"
        val notificationBuilder: NotificationCompat.Builder = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle(title)
            .setContentText(messageBody)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // Android Oreo 버전 이상을 위한 알림 채널 생성
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "가슴속삼천원",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            notificationManager.createNotificationChannel(channel)
        }
        notificationManager.notify(0, notificationBuilder.build())
    }

}