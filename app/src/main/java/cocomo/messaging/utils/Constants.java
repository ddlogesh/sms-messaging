package cocomo.messaging.utils;

import java.util.ArrayList;
import java.util.Arrays;

public class Constants {
    public static ArrayList<String> otpString = new ArrayList<>(Arrays.asList("otp", "verification", "password"));
    public static ArrayList<String> debitString = new ArrayList<>(Arrays.asList("withdrawn", "spent", "debited"));
    public static ArrayList<String> creditString = new ArrayList<>(Arrays.asList("deposited", "credited"));
    public static ArrayList<String> balanceUpdateString = new ArrayList<>(Arrays.asList("update: available bal"));
    public static ArrayList<String> billpayString = new ArrayList<>(Arrays.asList("vodafone bill"));

    public static String otp = "OTP";
    public static String debit = "Debit";
    public static String credit = "Credit";
    public static String balanceUpdate = "Balance Update";
    public static String billpay = "Billpay";
}
