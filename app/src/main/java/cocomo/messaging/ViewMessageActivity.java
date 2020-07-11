package cocomo.messaging;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.telephony.SmsManager;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import cocomo.messaging.adapters.MessageAdapter;
import cocomo.messaging.enums.Titles;
import cocomo.messaging.models.Contact;

public class ViewMessageActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_message);
        getWindow().setStatusBarColor(getResources().getColor(R.color.orange));

        TextView titleTV = findViewById(R.id.titleTV);
        RelativeLayout sendRL = findViewById(R.id.sendRL);
        ImageView backIV = findViewById(R.id.backIV);
        ImageView senderIV = findViewById(R.id.senderIV);
        ImageView callIV = findViewById(R.id.callIV);
        ImageView infoIV = findViewById(R.id.infoIV);
        final EditText sendET = findViewById(R.id.sendET);
        ImageView sendIV = findViewById(R.id.sendIV);
        RecyclerView messageRV = findViewById(R.id.messageRV);

        final Contact contact = getIntent().getParcelableExtra("data");
        titleTV.setText(contact.getAddress());
        senderIV.setImageDrawable(Titles.fetchDrawable(contact.getAddress(), getApplicationContext()));
        if (contact.getNumber() != null) {
            callIV.setVisibility(View.VISIBLE);
            sendRL.setVisibility(View.VISIBLE);
            if (!contact.getNumber().equals(contact.getAddress()))
                infoIV.setVisibility(View.VISIBLE);
        }

        MessageAdapter adapter = new MessageAdapter(getApplicationContext(), contact.getSmsArrayList());
        LinearLayoutManager layoutManager = new LinearLayoutManager(getApplicationContext(), RecyclerView.VERTICAL, true);
        messageRV.setLayoutManager(layoutManager);
        messageRV.setAdapter(adapter);

        sendIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SmsManager smsManager = SmsManager.getDefault();
                smsManager.sendTextMessage(contact.getNumber(), null, sendET.getEditableText().toString(), null, null);
                sendET.setText("");
                sendET.clearFocus();
                InputMethodManager imm = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }
        });

        callIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + contact.getNumber())));
            }
        });

        infoIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.withAppendedPath(ContactsContract.Contacts.CONTENT_URI, contact.getId()));
                startActivity(intent);
            }
        });

        backIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
    }
}
