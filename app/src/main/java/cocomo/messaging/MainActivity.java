package cocomo.messaging;

import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

import cocomo.messaging.adapters.ContactAdapter;
import cocomo.messaging.models.Contact;
import cocomo.messaging.models.Sms;

public class MainActivity extends AppCompatActivity {

    private RecyclerView contactRV;
    private EditText searchET;
    private ImageView clearIV;
    private Cursor c;
    private ContactAdapter adapter;
    private ArrayList<Contact> searchContactArrayList;
    private Map<String, Contact> contactMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getWindow().setStatusBarColor(getResources().getColor(R.color.orange));

        contactRV = findViewById(R.id.contactRV);
        searchET = findViewById(R.id.searchET);
        clearIV = findViewById(R.id.clearIV);

        new FetchSms().execute();

        searchET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                searchMessage(editable.toString().toLowerCase());

                adapter = new ContactAdapter(getApplicationContext(), searchContactArrayList);
                contactRV.setAdapter(adapter);
                adapter.notifyDataSetChanged();
            }
        });

        clearIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                searchET.setText("");
            }
        });
    }

    public void searchMessage(String text) {
        text = text.toLowerCase();
        searchContactArrayList = new ArrayList<>();

        for (Map.Entry<String, Contact> entry : contactMap.entrySet()) {
            Contact contact = entry.getValue();

            if (entry.getKey().toLowerCase().contains(text)) {
                searchContactArrayList.add(contact);
                continue;
            }

            Contact newContact = new Contact(contact);
            for (Sms sms : contact.getSmsArrayList()) {
                if (sms.getMessage().toLowerCase().contains(text))
                    newContact.getSmsArrayList().add(sms);
            }
            if (!newContact.getSmsArrayList().isEmpty())
                searchContactArrayList.add(newContact);
        }

        if (text.isEmpty())
            clearIV.setVisibility(View.GONE);
        else
            clearIV.setVisibility(View.VISIBLE);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        c.close();
    }

    public class FetchSms extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            contactMap = new LinkedHashMap<>();
            c = getContentResolver().query(Uri.parse("content://sms/"), null, null, null, null);

            if (c != null && c.moveToFirst()) {
                for (int i = 0; i < c.getCount(); i++) {
                    final String address = c.getString(c.getColumnIndexOrThrow("address"));
                    Contact contact = findContact(address);
                    Sms sms = new Sms();

                    sms.setId(c.getString(c.getColumnIndexOrThrow("_id")));
                    sms.setMessage(c.getString(c.getColumnIndexOrThrow("body")));
                    sms.setRead(c.getString(c.getColumnIndex("read")).equals("1"));
                    sms.setTime(c.getString(c.getColumnIndexOrThrow("date")));
                    sms.setReceived(c.getString(c.getColumnIndexOrThrow("type")).contains("1"));

                    contact.getSmsArrayList().add(sms);
                    c.moveToNext();
                }
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            adapter = new ContactAdapter(getApplicationContext(), new ArrayList<>(contactMap.values()));
            LinearLayoutManager layoutManager = new LinearLayoutManager(getApplicationContext(), RecyclerView.VERTICAL, false);
            contactRV.setLayoutManager(layoutManager);
            contactRV.setAdapter(adapter);
        }

        public Contact findContact(String originalAddress) {
            String address = originalAddress.toLowerCase();

            if (address.contains("hdfcbk") || address.contains("hdfcbn"))
                address = "HDFC Bank";
            else if (address.contains("dbsbnk"))
                address = "DBS Bank";
            else if (address.contains("payzap"))
                address = "PayZapp";
            else if (address.contains("vfcare") || address.contains("vicare") || address.contains("vfplay") || address.contains("vodafone"))
                address = "Vodafone";
            else
                address = originalAddress;

            Contact contact = contactMap.get(address);
            if (contact == null) {
                contact = new Contact();
                contact.setAddress(address);
                contactMap.put(address, contact);
            }
            return contact;
        }
    }
}
