package hu.mobilalk.turafoglalas.notification;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;

import androidx.core.app.NotificationCompat;

import hu.mobilalk.turafoglalas.R;

public class NotificationController
{
    private final NotificationManager notificationManager;
    private final Context context;


    public NotificationController(Context context)
    {
        this.context = context;
        this.notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        createChannel();
    }

    private void createChannel()
    {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O)
            return;

        NotificationChannel channel = new NotificationChannel("tour", "Jelentkezés", NotificationManager.IMPORTANCE_DEFAULT);

        channel.enableVibration(true);
        channel.setDescription("Túrafoglalás alkalmazás értesítés");

        notificationManager.createNotificationChannel(channel);
    }

    public void sendNotification(String message)
    {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "tour")
                .setContentTitle("Túrafoglalás értesítés")
                .setSmallIcon(R.drawable.tree)
                .setContentText(message);

        notificationManager.notify(0, builder.build());
    }
}
