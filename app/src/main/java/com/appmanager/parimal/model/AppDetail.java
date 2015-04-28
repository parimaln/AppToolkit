package com.appmanager.parimal.model;
import android.graphics.drawable.Drawable;

public class AppDetail {
        private CharSequence label;
        private CharSequence name;
        private Drawable icon;
        private int id;

    public CharSequence getLabel() {
        return label;
    }

    public CharSequence getName() {
        return name;
    }

    public Drawable getIcon() {
        return icon;
    }

    public void setLabel(CharSequence label) {
        this.label = label;
    }

    public void setName(CharSequence name) {
        this.name = name;
    }

    public void setIcon(Drawable icon) {
        this.icon = icon;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }
}
