package cocomo.messaging.enums;

import android.content.Context;
import android.graphics.drawable.Drawable;

import com.amulyakhare.textdrawable.TextDrawable;
import com.amulyakhare.textdrawable.util.ColorGenerator;

import cocomo.messaging.R;

public enum Titles {
    VODAFONE("Vodafone", R.drawable.vodafone),
    HDFC("HDFC Bank", R.drawable.hdfc),
    PAYZAPP("PayZapp", R.drawable.hdfc);

    private static TextDrawable.IBuilder builder = TextDrawable.builder().beginConfig()
            .toUpperCase()
            .endConfig()
            .round();
    private String title;
    private int icon;

    Titles(String title, int icon) {
        this.title = title;
        this.icon = icon;
    }

    public static TextDrawable.IBuilder getBuilder() {
        return builder;
    }

    public static void setBuilder(TextDrawable.IBuilder builder) {
        Titles.builder = builder;
    }

    public static Drawable fetchDrawable(String title, Context context) {
        for (Titles titles : Titles.values()) {
            if (title.equals(titles.getTitle()))
                return context.getResources().getDrawable(titles.getIcon());
        }
        return builder.build(String.valueOf(title.charAt(0)), ColorGenerator.MATERIAL.getColor(title));
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getIcon() {
        return icon;
    }

    public void setIcon(int icon) {
        this.icon = icon;
    }
}
