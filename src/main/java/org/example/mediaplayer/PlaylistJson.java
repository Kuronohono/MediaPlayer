package org.example.mediaplayer;

import java.util.ArrayList;

public class PlaylistJson {

    public String name;
    public String imagePath;
    public ArrayList<String> mediaPaths;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public ArrayList<String> getMediaPaths() {
        return mediaPaths;
    }

    public void setMediaPaths(ArrayList<String> mediaPaths) {
        this.mediaPaths = mediaPaths;
    }

    public PlaylistJson(String name, String imagePath, ArrayList<String> mediaPaths) {
        this.name = name;
        this.imagePath = imagePath;
        this.mediaPaths = mediaPaths;
    }
}
