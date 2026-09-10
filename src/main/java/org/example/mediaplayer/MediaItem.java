package org.example.mediaplayer;

import javafx.scene.image.Image;
import java.io.File;

public interface MediaItem {
    File getFile();
    String getTitle();
    String getDuration();
    Image getImage();
    MediaType getType();
}
