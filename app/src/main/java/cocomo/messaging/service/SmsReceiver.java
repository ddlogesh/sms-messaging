package cocomo.messaging.service;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.widget.RemoteViews;

import androidx.core.app.NotificationCompat;

import cocomo.messaging.R;
import cocomo.messaging.utils.CommonUtils;

public class SmsReceiver extends BroadcastReceiver {

    private static final String SMS_RECEIVED = "android.provider.Telephony.SMS_DELIVER";

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(SMS_RECEIVED)) {
            Bundle bundle = intent.getExtras();
            if (bundle != null) {
                Object[] pdus = (Object[]) bundle.get("pdus");
                if (pdus.length == 0) {
                    return;
                }
                SmsMessage[] messages = new SmsMessage[pdus.length];
                StringBuilder sb = new StringBuilder();
                for (int i = 0; i < pdus.length; i++) {
                    messages[i] = SmsMessage.createFromPdu((byte[]) pdus[i]);
                    sb.append(messages[i].getMessageBody());
                }
                String sender = messages[0].getOriginatingAddress();
                String message = sb.toString();
                Long time = messages[0].getTimestampMillis();

                ContentValues values = new ContentValues();
                values.put("address", sender);
                values.put("body", message);
                values.put("read", false);
                values.put("date", time);
                context.getContentResolver().insert(Uri.parse("content://sms/inbox"), values);

                NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
                RemoteViews contentView = new RemoteViews(context.getPackageName(), R.layout.contact_list_item);
                contentView.setTextViewText(R.id.titleTV, sender);
                contentView.setTextViewText(R.id.messageTV, message);
                contentView.setImageViewResource(R.id.senderIV, R.drawable.app_icon);
                contentView.setTextViewText(R.id.timeTV, CommonUtils.getDate(time.toString(), 1));

                NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context).setSmallIcon(R.drawable.app_icon).setContent(contentView);
                Notification notification = mBuilder.build();
                notification.flags |= Notification.FLAG_AUTO_CANCEL;
                notification.defaults |= Notification.DEFAULT_SOUND;
                notification.defaults |= Notification.DEFAULT_VIBRATE;
                notificationManager.notify((int) ((time / 1000L) % Integer.MAX_VALUE), notification);
            }
        }
    }
}
