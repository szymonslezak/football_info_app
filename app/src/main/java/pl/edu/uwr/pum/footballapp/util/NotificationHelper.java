package pl.edu.uwr.pum.footballapp.util;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import java.util.List;

import pl.edu.uwr.pum.footballapp.R;
import pl.edu.uwr.pum.footballapp.model.models.match.ModelMatch;
import pl.edu.uwr.pum.footballapp.view.MainActivity;

public class NotificationHelper {
    private static final String CHANNEL_ID = "Football_channel_id";
    private static final int NOTIFICATION_ID = 1;

    private Context context;

    public NotificationHelper(Context context){
        this.context = context;
    }

    public void createNotification(List<ModelMatch> matches){
        createNotificationChannel();
        StringBuilder body = new StringBuilder();
        for(ModelMatch match: matches)
        {
            body.append(match.homeName);
            body.append(" - ");
            body.append(match.awayName);
            body.append(" on ");
            body.append(match.utcDate);
            body.append("\n");
        }

        Intent intent = new Intent(context, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);

        Bitmap icon = BitmapFactory.decodeResource(context.getResources(), R.drawable.physicist);

        Notification notification = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.scientist_icon)
                //.setLargeIcon(icon)
                .setContentTitle("Upcoming Favourite teams matches")

                /*.setStyle(new NotificationCompat
                        .BigPictureStyle()
                        .bigPicture(icon)
                        .bigLargeIcon(null)
                )*/
                .setStyle(new NotificationCompat.BigTextStyle().bigText(body))
                .setContentText(body)
                .setContentIntent(pendingIntent)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .build();

        NotificationManagerCompat.from(context).notify(NOTIFICATION_ID, notification);
    }

    private void createNotificationChannel(){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            String name = CHANNEL_ID;
            String description = "Match notification channel";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.createNotificationChannel(channel);
        }
    }
}
