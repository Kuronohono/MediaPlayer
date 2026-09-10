package org.example.mediaplayer;

public class CheckedItem<T extends MediaItem> {
    private T item;
    private boolean isChecked;

    public CheckedItem(T item, boolean isChecked) {
        this.item = item;
        this.isChecked = isChecked;
    }

    public boolean isChecked() {
        return isChecked;
    }

    public void setChecked(boolean checked) {
        isChecked = checked;
    }

    public T getItem() {
        return item;
    }

    public void setItem(T item) {
        this.item = item;
    }
}
