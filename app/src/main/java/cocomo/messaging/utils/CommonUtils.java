package cocomo.messaging.utils;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import android.text.format.DateUtils;
import android.util.Log;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cocomo.messaging.models.Contact;

public class CommonUtils {
    public static String getDate(String time, int flag) {
        Date date = new Date(Long.parseLong(time));
        Date now = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat(" hh:mm a", Locale.US);

        if (DateUtils.isToday(Long.parseLong(time))) {
            if (flag == 1)
                return sdf.format(date);
            else
                return "Today" + sdf.format(date);
        }

        if (date.getDate() + 1 == now.getDate() &&
                date.getMonth() == now.getMonth() &&
                date.getYear() == now.getYear()) {
            if (flag == 1)
                return "Yesterday";
            else
                return "Yesterday" + sdf.format(date);
        }

        if (flag == 1)
            sdf = new SimpleDateFormat("dd/MM", Locale.US);
        else
            sdf = new SimpleDateFormat("EEE, dd/MM/yy hh:mm a", Locale.US);
        return sdf.format(date);
    }

    public static ArrayList<String> patternMatch(String message) {
        message = message.toLowerCase();

        Pattern pricePattern = Pattern.compile("[Rs.]*[Rs]*([0-9]+[.][0-9]{1,2})|(([0-9]*[,]*)*[.][0-9]{1,2})");
        Matcher priceMatcher = pricePattern.matcher(message);

        for (String pattern : Constants.billpayString) {
            if (message.contains(pattern) && priceMatcher.find() && priceMatcher.group(0) != null) {
                String price = priceMatcher.group(1) != null ? priceMatcher.group(1) : priceMatcher.group(2);
                try {
                    price = NumberFormat.getCurrencyInstance(new Locale("en", "IN")).format(Double.parseDouble(price));
                    price = price.substring(4);
                } catch (Exception e) {
                    Log.e("error", e.getMessage());
                }
                return new ArrayList<>(Arrays.asList(Constants.billpay, price));
            }
        }

        Pattern otpPattern = Pattern.compile("\\b\\d{4,6}\\b");
        Matcher otpMatcher = otpPattern.matcher(message);

        for (String pattern : Constants.otpString) {
            if (message.contains(pattern) && otpMatcher.find() && otpMatcher.group(0) != null)
                return new ArrayList<>(Arrays.asList(Constants.otp, otpMatcher.group(0)));
        }

        for (String pattern : Constants.debitString) {
            if (message.contains(pattern) && priceMatcher.find() && priceMatcher.group(0) != null) {
                String price = priceMatcher.group(1) != null ? priceMatcher.group(1) : priceMatcher.group(2);
                try {
                    price = NumberFormat.getCurrencyInstance(new Locale("en", "IN")).format(Double.parseDouble(price));
                    price = price.substring(4);
                } catch (Exception e) {
                    Log.e("error", e.getMessage());
                }
                return new ArrayList<>(Arrays.asList(Constants.debit, price));
            }
        }

        for (String pattern : Constants.creditString) {
            if (message.contains(pattern) && priceMatcher.find() && priceMatcher.group(0) != null) {
                String price = priceMatcher.group(1) != null ? priceMatcher.group(1) : priceMatcher.group(2);
                try {
                    price = NumberFormat.getCurrencyInstance(new Locale("en", "IN")).format(Double.parseDouble(price));
                    price = price.substring(4);
                } catch (Exception e) {
                    Log.e("error", e.getMessage());
                }
                return new ArrayList<>(Arrays.asList(Constants.credit, price));
            }
        }

        for (String pattern : Constants.balanceUpdateString) {
            if (message.startsWith(pattern) && priceMatcher.find() && priceMatcher.group(0) != null) {
                String price = priceMatcher.group(1) != null ? priceMatcher.group(1) : priceMatcher.group(2);
                try {
                    price = NumberFormat.getCurrencyInstance(new Locale("en", "IN")).format(Double.parseDouble(price));
                    price = price.substring(4);
                } catch (Exception e) {
                    Log.e("error", e.getMessage());
                }
                return new ArrayList<>(Arrays.asList(Constants.balanceUpdate, price));
            }
        }

        return null;
    }

    public static Contact getContactName(Context context, Contact contact) {
        String phoneNumber = contact.getAddress();
        if (!phoneNumber.matches("^[+]*[0-9]+$"))
            return contact;
        else
            contact.setNumber(phoneNumber);

        Cursor phones = context.getContentResolver().query(Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri.encode(phoneNumber)), null, null, null, null);
        if (phones != null && phones.moveToNext()) {
            contact.setId(phones.getString(phones.getColumnIndex(ContactsContract.Contacts._ID)));
            contact.setAddress(phones.getString(phones.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME)));
            phones.close();
            return contact;
        }

        return contact;
    }
}
