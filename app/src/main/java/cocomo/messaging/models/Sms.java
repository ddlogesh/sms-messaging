package cocomo.messaging.models;

import android.os.Parcel;
import android.os.Parcelable;

public class Sms implements Parcelable {
    public static final Creator<Sms> CREATOR = new Creator<Sms>() {
        @Override
        public Sms createFromParcel(Parcel in) {
            return new Sms(in);
        }

        @Override
        public Sms[] newArray(int size) {
            return new Sms[size];
        }
    };
    private String id;
    private String message;
    private Boolean isRead;
    private String time;
    private Boolean isReceived;

    public Sms() {
    }

    protected Sms(Parcel in) {
        id = in.readString();
        message = in.readString();
        byte tmpIsRead = in.readByte();
        isRead = tmpIsRead == 0 ? null : tmpIsRead == 1;
        time = in.readString();
        byte tmpIsReceived = in.readByte();
        isReceived = tmpIsReceived == 0 ? null : tmpIsReceived == 1;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Boolean getRead() {
        return isRead;
    }

    public void setRead(Boolean read) {
        isRead = read;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public Boolean getReceived() {
        return isReceived;
    }

    public void setReceived(Boolean received) {
        isReceived = received;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(id);
        parcel.writeString(message);
        parcel.writeByte((byte) (isRead == null ? 0 : isRead ? 1 : 2));
        parcel.writeString(time);
        parcel.writeByte((byte) (isReceived == null ? 0 : isReceived ? 1 : 2));
    }

    @Override
    public String toString() {
        return "Sms{" +
                "id='" + id + '\'' +
                ", message='" + message + '\'' +
                ", isRead=" + isRead +
                ", time='" + time + '\'' +
                ", isReceived=" + isReceived +
                '}';
    }
}
