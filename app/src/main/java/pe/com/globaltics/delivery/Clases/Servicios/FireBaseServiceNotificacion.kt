package pe.com.globaltics.delivery.Clases.Servicios

import android.annotation.SuppressLint
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.support.v4.app.NotificationCompat
import android.support.v4.content.LocalBroadcastManager
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import pe.com.globaltics.delivery.Activitys.CarritoActivity
import pe.com.globaltics.delivery.Activitys.ControlActivity
import pe.com.globaltics.delivery.Clases.EndPoints.NOTIFICACION
import pe.com.globaltics.delivery.R
import java.util.*

@SuppressLint("Registered")
class FireBaseServiceNotificacion : FirebaseMessagingService() {

    override fun onMessageReceived(remoteMessage: RemoteMessage?) {
        super.onMessageReceived(remoteMessage)
        val titulo = remoteMessage!!.data["titulo"]
        val fecha = remoteMessage.data["fecha"]
        val mensaje = remoteMessage.data["mensaje"]
        val activity = remoteMessage.data["activity"]
        Notificacion(mensaje)
        ShowNotificacion(titulo, fecha, mensaje, activity)
    }

    private fun ShowNotificacion(titulo: String?, fecha: String?, mensaje: String?, activity: String?) {
        if (Objects.equals(activity, "control")) {
            val intent = Intent(this, ControlActivity::class.java)
            intent.putExtra("key_control", "trab")
            val pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT)

            val uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)

            val canalid = "canal01"
            val builder = NotificationCompat.Builder(this, canalid)
            builder.setAutoCancel(true)
            builder.setContentTitle(titulo)
            builder.setContentText(mensaje)
            builder.setSubText(fecha)
            builder.setSound(uri)
            //builder.setTicker("ticker");
            builder.setSmallIcon(R.drawable.carrito)
            builder.setContentIntent(pendingIntent)

            val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            val random = Random()

            notificationManager.notify(random.nextInt(), builder.build())
        } else if (Objects.equals(activity, "pedidos")) {
            val intent = Intent(this, CarritoActivity::class.java)
            intent.putExtra("key", "fr_mis_pedidos")
            val pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT)

            val uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)

            val canalid = "canal01"
            val builder = NotificationCompat.Builder(this, canalid)
            builder.setAutoCancel(true)
            builder.setContentTitle(titulo)
            builder.setContentText(mensaje)
            builder.setSubText(fecha)
            builder.setSound(uri)
            //builder.setTicker("ticker");
            builder.setSmallIcon(R.drawable.carrito)
            builder.setContentIntent(pendingIntent)

            val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            val random = Random()

            notificationManager.notify(random.nextInt(), builder.build())
        }

    }

    private fun Notificacion(mensaje: String?) {
        val intent = Intent(NOTIFICACION)
        intent.putExtra("mensaje", mensaje)
        LocalBroadcastManager.getInstance(applicationContext).sendBroadcast(intent)
    }
}