package cocomo.messaging.models;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

public class Contact implements Parcelable {
    public static final Creator<Contact> CREATOR = new Creator<Contact>() {
        @Override
        public Contact createFromParcel(Parcel in) {
            return new Contact(in);
        }

        @Override
        public Contact[] newArray(int size) {
            return new Contact[size];
        }
    };
    private String id;
    private String address;
    private String number;
    private ArrayList<Sms> smsArrayList;

    public Contact() {
        smsArrayList = new ArrayList<>();
    }

    public Contact(Contact c) {
        id = c.getId();
        address = c.getAddress();
        number = c.getNumber();
        smsArrayList = new ArrayList<>();
    }

    protected Contact(Parcel in) {
        id = in.readString();
        address = in.readString();
        number = in.readString();
        smsArrayList = new ArrayList<>();
        in.readList(smsArrayList, Sms.class.getClassLoader());
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(address);
        dest.writeString(number);
        dest.writeList(smsArrayList);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public ArrayList<Sms> getSmsArrayList() {
        return smsArrayList;
    }

    public void setSmsArrayList(ArrayList<Sms> smsArrayList) {
        this.smsArrayList = smsArrayList;
    }

    @Override
    public String toString() {
        return "Contact{" +
                "id='" + id + '\'' +
                ", address='" + address + '\'' +
                ", number='" + number + '\'' +
                ", smsArrayList=" + smsArrayList +
                '}';
    }
}
