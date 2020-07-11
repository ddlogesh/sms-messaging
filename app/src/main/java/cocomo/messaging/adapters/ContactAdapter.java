package cocomo.messaging.adapters;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import cocomo.messaging.R;
import cocomo.messaging.ViewMessageActivity;
import cocomo.messaging.enums.Titles;
import cocomo.messaging.models.Contact;
import cocomo.messaging.models.Sms;
import cocomo.messaging.utils.CommonUtils;
import cocomo.messaging.utils.Constants;

import static android.content.Context.CLIPBOARD_SERVICE;

public class ContactAdapter extends RecyclerView.Adapter<ContactAdapter.ContactViewHolder> {
    private ArrayList<Contact> contactArrayList;
    private Context context;

    public ContactAdapter(Context context, ArrayList<Contact> contactArrayList) {
        this.context = context;
        this.contactArrayList = contactArrayList;
    }

    @NonNull
    @Override
    public ContactAdapter.ContactViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View listItem = layoutInflater.inflate(R.layout.contact_list_item, parent, false);
        return new ContactAdapter.ContactViewHolder(listItem);
    }

    @Override
    public void onBindViewHolder(@NonNull ContactAdapter.ContactViewHolder holder, int position) {
        final Contact contact = CommonUtils.getContactName(context, contactArrayList.get(position));
        final Sms sms = contact.getSmsArrayList().get(0);

        holder.titleTV.setText(contact.getAddress());
        holder.messageTV.setText(sms.getMessage());
        holder.timeTV.setText(CommonUtils.getDate(sms.getTime(), 1));
        holder.senderIV.setImageDrawable(Titles.fetchDrawable(contact.getAddress(), context));

        if (sms.getRead()) {
            holder.layoutLL.setBackgroundResource(R.drawable.ic_contact_read_list_bg);
            Typeface opensansSemiBold = ResourcesCompat.getFont(context, R.font.opensans_semibold);
            Typeface opensans = ResourcesCompat.getFont(context, R.font.opensans);
            holder.titleTV.setTypeface(opensansSemiBold);
            holder.messageTV.setTypeface(opensans);
            holder.timeTV.setTypeface(opensans);
        } else {
            holder.layoutLL.setBackgroundResource(R.drawable.ic_contact_unread_list_bg);
            Typeface opensansBold = ResourcesCompat.getFont(context, R.font.opensans_bold);
            holder.titleTV.setTypeface(opensansBold);
            holder.messageTV.setTypeface(opensansBold);
            holder.timeTV.setTypeface(opensansBold);
        }

        final ArrayList<String> pattern = CommonUtils.patternMatch(sms.getMessage());
        if (pattern != null && pattern.size() > 1) {
            holder.priceTV.setVisibility(View.VISIBLE);
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
                    holder.priceTV.setVisibility(View.GONE);

                holder.layoutLL.setOnLongClickListener(null);
                holder.priceTV.setTextColor(context.getResources().getColor(R.color.white));
            }
        } else
            holder.priceTV.setVisibility(View.GONE);

        holder.layoutLL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, ViewMessageActivity.class);
                intent.putExtra("data", contact);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);

                if (!sms.getRead()) {
                    sms.setRead(true);
                    notifyDataSetChanged();
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return contactArrayList.size();
    }

    public class ContactViewHolder extends RecyclerView.ViewHolder {
        LinearLayout layoutLL;
        TextView titleTV, messageTV, timeTV, priceTV;
        ImageView senderIV;

        public ContactViewHolder(View convertView) {
            super(convertView);

            layoutLL = convertView.findViewById(R.id.layoutLL);
            titleTV = convertView.findViewById(R.id.titleTV);
            messageTV = convertView.findViewById(R.id.messageTV);
            timeTV = convertView.findViewById(R.id.timeTV);
            senderIV = convertView.findViewById(R.id.senderIV);
            priceTV = convertView.findViewById(R.id.priceTV);
        }
    }
}
