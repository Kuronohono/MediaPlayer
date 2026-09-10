package org.example.mediaplayer;

public class CheckedMusic{

    private Music music;
    private boolean isChecked;

    public CheckedMusic(Music music, boolean isChecked) {
        this.music = music;
        this.isChecked = isChecked;
    }

    public Music getMusic() {
        return music;
    }

    public void setMusic(Music music) {
        this.music = music;
    }

    public boolean getIsChecked() {
        return isChecked;
    }

    public void setChecked(boolean checked) {
        isChecked = checked;
    }
}
