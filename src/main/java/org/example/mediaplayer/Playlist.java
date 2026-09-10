package org.example.mediaplayer;

import javafx.scene.image.Image;
import org.controlsfx.control.cell.MediaImageCell;

import java.util.ArrayList;

public class Playlist<T extends MediaItem>{

    private String name;
    private ArrayList<T> media;
    private Image image;
    private final MediaType type;

    public Playlist(String name, ArrayList<T> media, Image image, MediaType type) {
        this.name = name;
        this.media = media;
        this.image = image;
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ArrayList<T> getMedia() {
        return media;
    }

    public void setMedia(ArrayList<T> media) {
        this.media = media;
    }

    public MediaType getType() {
        return type;
    }

    public Image getImage() {
        return image;
    }

    public void setImage(Image image) {
        this.image = image;
    }

}
