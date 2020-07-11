package cocomo.messaging.adapters;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.hendraanggrian.appcompat.widget.SocialTextView;
import com.hendraanggrian.appcompat.widget.SocialView;

import java.util.ArrayList;

import cocomo.messaging.R;
import cocomo.messaging.models.Sms;
import cocomo.messaging.utils.CommonUtils;
import cocomo.messaging.utils.Constants;

import static android.content.Context.CLIPBOARD_SERVICE;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.SmsViewHolder> {
    private ArrayList<Sms> smsArrayList;
    private Context context;

    public MessageAdapter(Context context, ArrayList<Sms> smsArrayList) {
        this.context = context;
        this.smsArrayList = smsArrayList;
    }

    @NonNull
    @Override
    public MessageAdapter.SmsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View listItem = layoutInflater.inflate(R.layout.message_list_item, parent, false);
        return new MessageAdapter.SmsViewHolder(listItem);
    }

    @Override
    public void onBindViewHolder(@NonNull final MessageAdapter.SmsViewHolder holder, int position) {
        final Sms sms = smsArrayList.get(position);
        String message = sms.getMessage();
        holder.messageTV.setText(message);
        holder.messageTV.setOnClickListener(null);

        holder.timeTV.setText(CommonUtils.getDate(sms.getTime(), 0));

        if (sms.getRead()) {
            if (sms.getReceived()) {
                LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) holder.layoutLL.getLayoutParams();
                params.leftMargin = 0;
                params.rightMargin = 120;
                holder.layoutLL.setLayoutParams(params);

                holder.layoutLL.setBackgroundResource(R.drawable.ic_message_read_left_list_bg);
            } else {
                LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) holder.layoutLL.getLayoutParams();
                params.leftMargin = 120;
                params.rightMargin = 0;
                holder.layoutLL.setLayoutParams(params);

                holder.layoutLL.setBackgroundResource(R.drawable.ic_message_right_list_bg);
            }

            Typeface opensans = ResourcesCompat.getFont(context, R.font.opensans);
            holder.messageTV.setTypeface(opensans);
        } else {
            LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) holder.layoutLL.getLayoutParams();
            params.leftMargin = 0;
            params.rightMargin = 120;
            holder.layoutLL.setLayoutParams(params);

            holder.layoutLL.setBackgroundResource(R.drawable.ic_message_unread_left_list_bg);
            Typeface opensansBold = ResourcesCompat.getFont(context, R.font.opensans_bold);
            holder.messageTV.setTypeface(opensansBold);

            ContentValues values = new ContentValues();
            values.put("read", true);
            context.getContentResolver().update(Uri.parse("content://sms/"), values, "_id=" + sms.getId(), null);
            sms.setRead(true);

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    notifyDataSetChanged();
                }
            }, 2000);
        }

        final ArrayList<String> pattern = CommonUtils.patternMatch(sms.getMessage());
        if (sms.getReceived() && pattern != null && pattern.size() > 1) {
            if (message.length() > 100) {
                holder.messageTV.setText(message.substring(0, 100) + "...");
                holder.messageTV.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        holder.messageTV.setText(sms.getMessage());
                    }
                });
            }

            holder.titleLL.setVisibility(View.VISIBLE);
            holder.titleTV.setText(pattern.get(0));
            holder.priceTV.setText(pattern.get(1));

            if (pattern.get(0).equals(Constants.otp)) {
                holder.priceTV.setBackgroundResource(R.drawable.ic_white_bg);
                holder.priceTV.setTextColor(context.getResources().getColor(R.color.black));

                holder.layoutLL.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View view) {
                        ClipboardManager clipboard = (ClipboardManager) context.getSystemService(CLIPBOARD_SERVICE);
                        ClipData clip = ClipData.newPlainText("label", pattern.get(1));
                        clipboard.setPrimaryClip(clip);
                        Toast.makeText(context, "OTP copied", Toast.LENGTH_SHORT).show();
                        return false;
                    }
                });
            } else {
                if (pattern.get(0).equals(Constants.debit))
                    holder.priceTV.setBackgroundResource(R.drawable.ic_red_bg);
                else if (pattern.get(0).equals(Constants.credit))
                    holder.priceTV.setBackgroundResource(R.drawable.ic_green_bg);
                else if (pattern.get(0).equals(Constants.balanceUpdate) || pattern.get(0).equals(Constants.billpay))
                    holder.priceTV.setBackgroundResource(R.drawable.ic_orange_bg);
                else
                    holder.titleLL.setVisibility(View.GONE);

                holder.layoutLL.setOnLongClickListener(null);
                holder.priceTV.setTextColor(context.getResources().getColor(R.color.white));
            }
        } else
            holder.titleLL.setVisibility(View.GONE);

        holder.messageTV.setOnHyperlinkClickListener(new SocialView.OnClickListener() {
            @Override
            public void onClick(@NonNull SocialView view, @NonNull CharSequence text) {
                try {
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(text.toString()));
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(intent);
                } catch (Exception e) {
                    Log.e("error", e.getMessage());
                }
                Log.d("loggesh", sms.getMessage());
            }
        });
    }

    @Override
    public int getItemCount() {
        return smsArrayList.size();
    }

    public class SmsViewHolder extends RecyclerView.ViewHolder {
        LinearLayout titleLL, layoutLL;
        SocialTextView messageTV;
        TextView timeTV, titleTV, priceTV;

        public SmsViewHolder(View convertView) {
            super(convertView);

            titleLL = convertView.findViewById(R.id.titleLL);
            layoutLL = convertView.findViewById(R.id.layoutLL);
            messageTV = convertView.findViewById(R.id.messageTV);
            timeTV = convertView.findViewById(R.id.timeTV);
            titleTV = convertView.findViewById(R.id.titleTV);
            priceTV = convertView.findViewById(R.id.priceTV);
        }
    }
}
