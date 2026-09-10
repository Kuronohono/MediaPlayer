package org.example.mediaplayer;

public class CheckedVideo {

    private Video video;
    private boolean isChecked;

    public CheckedVideo(Video video, boolean isChecked) {
        this.video = video;
        this.isChecked = isChecked;
    }

    public Video getVideo() {
        return video;
    }

    public void setVideo(Video video) {
        this.video = video;
    }

    public boolean isChecked() {
        return isChecked;
    }

    public void setChecked(boolean checked) {
        isChecked = checked;
    }
}
