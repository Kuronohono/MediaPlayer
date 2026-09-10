package org.example.mediaplayer;

import javafx.scene.image.Image;
import org.jaudiotagger.audio.AudioFile;
import org.jaudiotagger.audio.AudioFileIO;
import org.jaudiotagger.audio.AudioHeader;
import org.jaudiotagger.audio.exceptions.CannotReadException;
import org.jaudiotagger.audio.exceptions.InvalidAudioFrameException;
import org.jaudiotagger.audio.exceptions.ReadOnlyFileException;
import org.jaudiotagger.tag.FieldKey;
import org.jaudiotagger.tag.Tag;
import org.jaudiotagger.tag.TagException;
import org.jaudiotagger.tag.images.Artwork;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.time.Instant;

public class Music implements MediaItem {

    private File file;
    private String title;
    private String artist;
    private String album;
    private String genre;
    private String year;
    private String duration;
    private Image image;
    private String lyrics;
    private final Instant dateAdded;
    private final MediaType mediaType = MediaType.Music;


    public Music(File file) {
        this.file = file;
        this.dateAdded = Instant.now();
        try {
            setFields();
        } catch (CannotReadException | TagException | InvalidAudioFrameException | ReadOnlyFileException | IOException e) {
            throw new RuntimeException(e);
        }
    }

    public Image getDisplayImage() throws CannotReadException, TagException, InvalidAudioFrameException, ReadOnlyFileException, IOException {
        AudioFile audioFile = AudioFileIO.read(file);
        Tag tag = audioFile.getTag();
        Image image;
        if(tag != null && tag.getFirstArtwork() != null)
        {
            Artwork artwork = tag.getFirstArtwork();

            //Convert byte[] to JavaFX Image
            ByteArrayInputStream bis = new ByteArrayInputStream(artwork.getBinaryData());
            image = new Image(bis);

        }else{
            File f = new File("src/main/resources/images/music_backdrop.jpg");
            image = new Image(f.toURI().toString());

        }
        return image;
    }


    private void setFields() throws CannotReadException, TagException, InvalidAudioFrameException, ReadOnlyFileException, IOException {
        AudioFile audioFile = AudioFileIO.read(file);
        Tag tag = audioFile.getTag();

        title = tag.getFirst(FieldKey.TITLE);
        artist = tag.getFirst(FieldKey.ARTIST);
        album = tag.getFirst(FieldKey.ALBUM);
        genre = tag.getFirst(FieldKey.GENRE);
        year = tag.getFirst(FieldKey.YEAR);
        lyrics = tag.getFirst(FieldKey.LYRICS);


        AudioHeader header = audioFile.getAudioHeader();
        int trackLength = header.getTrackLength();
        calculateTrackLength(trackLength);
        image = getDisplayImage();

        if(title.isEmpty() || lyrics.isEmpty())
      infoNotFound();
    }

    private void calculateTrackLength(int trackLength)
    {
        int s = trackLength % 60; //Calculate the seconds
        int H = trackLength / 60; //Convert total seconds to minutes
        int M = H % 60; //Calculate the remaining minutes
        H = H / 60; //Convert total minutes to hours

        if (H > 0) {
            duration = String.format("%d:%02d:%02d", H, M, s);
        } else {
            duration = String.format("%02d:%02d", M, s);
        }
    }

    public String[][] getInfo()
    {
        String[][] info = {
                {"Title",title},
                {"Artist",artist},
                {"Album",album},
                {"Genre",genre},
                {"Year",year},
                {"Lyrics",lyrics}
        };
        return info;
    }

    private void infoNotFound()
    {
        if(title.isEmpty())
        {
            String name = file.getName();
            if(name.contains(".mp3"))
            {
                String regex = ".mp3";
                name = name.replaceFirst(regex,"");
            }
            title = name;
        }
        if(artist.isEmpty())
        {
            artist = "Unknown";
        }
        if(year.isEmpty())
        {
            year = "Unknown";
        }
        if(album.isEmpty())
        {
            album = "Unknown";
        }
        if(genre.isEmpty())
        {
            genre = "Unknown";
        }
        if(lyrics.isEmpty())
        {
            lyrics = "No Lyrics Available.";
        }
    }

    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public String getAlbum() {
        return album;
    }

    public void setAlbum(String album) {
        this.album = album;
    }

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public Image getImage() {
        return image;
    }

    @Override
    public MediaType getType() {
        return mediaType;
    }

    public void setImage(Image image) {
        this.image = image;
    }

    public String getLyrics() {
        return lyrics;
    }

    public void setLyrics(String lyrics) {
        this.lyrics = lyrics;
    }

    public Instant getDateAdded() {
        return dateAdded;
    }
}
