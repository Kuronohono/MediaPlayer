package org.example.mediaplayer;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Cursor;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.paint.Color;
import javafx.util.Duration;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.EOFException;
import java.io.FileReader;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Type;
import java.util.Random;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.*;

public class MainController  extends GlobalMethods implements Initializable {

    @FXML
    private Button home_button,music_button,videos_button,playlists_button,settings_button,about_button;

    @FXML//Images on the menu items
    private ImageView homeIV,musicIV,videosIV,playlistsIV,settingsIV,aboutIV,speedIV,audioIV,replayIV,previousIV,play_pauseIV,nextIV,shuffleIV;
    @FXML
    private StackPane contentArea;

    @FXML
    private ImageView ProfilePicIV;

    public static ArrayList<Music> favorites;

    public static boolean isDarkMode = true;

    public static ArrayList<Music> music;

    public static ArrayList<Video> videos;

    public static ArrayList<Playlist<Music>> playlists;

    public static ArrayList<Playlist<Video>> videoPlaylists;

    public static Playlist<Music> favoritePlaylist,allSongsPlaylist;

    public static BooleanProperty randomOrder = new SimpleBooleanProperty(false);

    public static ArrayList<SettingsFields> settingsFields;


    @Override
    public void initialize(URL arg0, ResourceBundle arg1)
    {

        favorites = new ArrayList<>();
        playlists = new ArrayList<>();
        settingsFields = new ArrayList<>();

        reColorIcons();
        parseFiles();
        parseFavorites();
        ButtonUX();

        setProfilePic();

        if(music.isEmpty())
        {
            favorites = new ArrayList<>();
            playlists = new ArrayList<>();
        }

        checkSettingsFields();

        //Make the current playlist be all the music loaded in the app
        runningPlaylist = music;

        //Make the currentmusic the first element of the running playlist, if the playlist is not empty
        if(!runningPlaylist.isEmpty())
            currentmusic = runningPlaylist.getFirst();

        if(!music.isEmpty()){
            //Set up the first item inside the music array list as the first media to play
            mediaPlayer = new MediaPlayer(new Media(music.getFirst().getFile().toURI().toString()));
        }

        setUpVolumeAndSpeedBox();


        File f = new File("src/main/resources/images/music_header_backdrop.jpg");
        Image image = new Image(f.toURI().toString());
        favoritePlaylist = new Playlist<>("Favorites",favorites,image,MediaType.Music);

        allSongsPlaylist = new Playlist<>("All Songs",music,null,MediaType.Music);

        //Load the Home Page
        try {
            HomePageChange();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        if(currentmusic!=null)
            ChangeMedia();

    }

    private void setProfilePic(){
        String path = getProfilePicPath(new File("UserData/userData.xml"));
        if(path!=null && !path.isEmpty()){
            File file = new File(path);
            changeProfilePic(file,ProfilePicIV,75);
        }
    }

    private void checkSettingsFields()
    {
        //Check if empty, if true the user is starting new session
        if(settingsFields ==null  || settingsFields.isEmpty())
        {
            initializeSettingsFields();
        }
    }


    public void updateProfilePic(File file) {
        changeProfilePic(file, ProfilePicIV, 75);
    }

    private void initializeSettingsFields()
    {
        SettingsFields profilePicture = new SettingsFields("Profile Picture","src/main/resources/images/profile-user.png");
        SettingsFields username = new SettingsFields("Username","User");
        settingsFields.add(profilePicture);
        settingsFields.add(username);

    }

    public void parseFiles()
    {
        parseMusic();
        try {
            ParsePlaylistsJson();
        }catch (Exception e)
        {
            System.out.println("Empty Json caught");
        }
    }

    public void changePlaylist(Playlist<Music> playlist)
    {
        setCurrentMusic(runningPlaylist.getFirst(),playlist.getMedia());
    }

    private void parseMusic()
    {
        //Parse music from music File
        music = new ArrayList<>();
        File directory = new File("music");
        File[] files = directory.listFiles();
        if(files != null)
        {
            for(File file : files)
            {
                Music ct = new Music(file);
                music.add(ct);
            }
        }
        else{
            System.out.println("Null");
        }

        //Parse videos from videos File
        videos = new ArrayList<>();
        File VideoDirectory = new File("videos");
        File[] videoFiles = VideoDirectory.listFiles();
        if(videoFiles != null){
            for(File file : videoFiles)
            {
                Video video = new Video(file);
                videos.add(video);
            }
        }else{
            System.out.println("Video files are null");
        }
    }


     private void parseFavorites()
    {
        //Parse Favorites from favorites.xml File and cross validate with music
        File file = new File("src/main/resources/favorites.xml");
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder documentBuilder = factory.newDocumentBuilder();
            Document doc = documentBuilder.parse(file);
            doc.getDocumentElement().normalize();

            NodeList list =  doc.getElementsByTagName("favorite");
            for(int i =0; i< list.getLength(); i++){
                Node node = list.item(i);
                if(node.getNodeType() == Node.ELEMENT_NODE){
                    File f = new File(node.getTextContent().trim());
                    Music ct = new Music(f);
                    if (ct != null)
                        favorites.add(ct);

                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }


    private void ParsePlaylistsJson()
    {
        //Parse Playlists from playlists.json
        File playlistFile = new File("src/main/resources/playlists.json");

        Gson gson = new GsonBuilder().create();

        try {
            Type playlistType = new TypeToken<List<PlaylistJson>>() {
            }.getType();

            List<PlaylistJson> playlistJsons = new ArrayList<>();
            try (FileReader reader = new FileReader(playlistFile.getPath())) {
                playlistJsons = gson.fromJson(reader, playlistType);
            } catch (IOException e) {
                e.printStackTrace();
            }

            for (PlaylistJson playlistJson : playlistJsons) {
                loadPlaylist(playlistJson);
            }
        }catch (Exception e)
        {
            try (FileReader reader = new FileReader(playlistFile.getPath())) {
                PlaylistJson pj = gson.fromJson(reader, PlaylistJson.class);
                loadPlaylist(pj);
            } catch (IOException exception) {
                exception.printStackTrace();
            }
        }
    }


    private void loadPlaylist(PlaylistJson playlistJson)
    {
        //Create a common arraylist for the music objects with common path fields inside the other two lists
        ArrayList<Music> common = new ArrayList<>();
        //The music objects loaded inside the application
        ArrayList<Music> list1 = MainController.music;
        //the music objects loaded from the playlist json
        ArrayList<String> list2 = playlistJson.getMediaPaths();
        //Start parsing and checking the common fields
        for (Music music1 : list1) {
            for (String s : list2) {
                if (music1.getFile().getPath().equals(s))
                    common.add(music1);
            }
        }
        Image image = new Image(playlistJson.getImagePath());
        Playlist<Music> playlist = new Playlist<>(playlistJson.getName(), common, image, MediaType.Music);
        MainController.playlists.add(playlist);
    }


    //Recoloring the imageViews on the menu
    @FXML
    private void reColorIcons()
    {
        ImageView[] imageViews = { homeIV,musicIV,videosIV,playlistsIV,settingsIV,aboutIV,speedIV,audioIV,replayIV,previousIV,play_pauseIV,nextIV,shuffleIV};
        for(ImageView imageView : imageViews)
        {
            imageView.setEffect(imageColorBlend(imageView,Color.WHITE,Color.BLACK));
        }
    }

    @FXML
    private void ButtonUX()
    {
        Button[] buttons = {home_button,music_button,videos_button,playlists_button,settings_button,about_button};

        for(Button button : buttons)
        {
            button.setCursor(Cursor.HAND);
            button.setOnMouseEntered(event -> {
                if (isDarkMode) {
                    button.setStyle("-fx-background-color:#2E2E2E");
                } else {
                    button.setStyle("-fx-background-color:#2E2E2E");
                }
            });

            button.setOnMouseExited(event -> {
                if (isDarkMode) {
                    button.setStyle("-fx-background-color:#222222");
                } else {
                    button.setStyle("-fx-background-color:#2E2E2E");
                }
            });

            button.setOnMousePressed(event -> {
                button.setStyle("-fx-background-color:#414141");
            });

            button.setOnMouseReleased(event->{
                button.setStyle("-fx-background-color:#2E2E2E");
            });

        }
    }

    //Check if the player is active
    public static MediaPlayer mediaPlayer;

    @FXML
    private Label playingMediaDuration,playingMediaLabel, mediaCurrentLabel;

    @FXML
    private ImageView playingMediaIV;

    @FXML
    private Slider volumeSlider;

    @FXML
    private Button playPause_button;

    @FXML
    private Slider musicSlider;

    @FXML
    private ComboBox<String> speedBox;

    /////////////////////Music Player Handling Methods///////////////////////////

    private Timer timer;
    public static boolean isPlaying= false;

    public static boolean visibleInfoBox, visiblePlaylistBox = true;


    private void setUpMusicSlider()
    {
        if(mediaPlayer!=null)
        {
            mediaPlayer.currentTimeProperty().addListener((observable, oldValue, newValue) -> {
                musicSlider.setValue(newValue.toSeconds());
            });

            mediaPlayer.setOnReady( () ->{
                double total = mediaPlayer.getMedia().getDuration().toSeconds();
                musicSlider.setMax(total);
            });
        }
    }

    @FXML
    private void sliderPressed()
    {
       if(mediaPlayer!=null)
           mediaPlayer.seek(Duration.seconds(musicSlider.getValue()));
    }

    private void changeSpeed()
    {
        if(mediaPlayer!=null)
        {
            if(speedBox.getValue()  == null)
                mediaPlayer.setRate(1);
            else
                mediaPlayer.setRate(Double.parseDouble(speedBox.getValue()));
        }
    }


    private ArrayList<Music> runningPlaylist;
    private int mediaPosition;

    private Music currentmusic;

    public void setCurrentMusic(Music music, ArrayList<Music> playlist){
        currentmusic = music;
        runningPlaylist = playlist;
        mediaPosition = getMusicObjectPosition(runningPlaylist,music);
        if(isPlaying){
            isPlaying= false;
            PauseMedia();
        }
        ChangeMedia();
        PlayMedia();
    }

    public void ChangeMedia()
    {
        if(runningPlaylist.get(mediaPosition) != null && mediaPlayer!=null){
            currentmusic = runningPlaylist.get(mediaPosition);
            mediaPlayer = new MediaPlayer(new Media(currentmusic.getFile().toURI().toString()));
            mediaPlayer.setOnEndOfMedia(() -> {
                CancelTimer();
                if(randomOrder.get()){
                    randomMedia();
                }else{
                    nextMedia();
                }
            });
            playingMediaDuration.setText(currentmusic.getDuration());
            playingMediaLabel.setText(currentmusic.getTitle());
            setUpImageAndClip(playingMediaIV,currentmusic.getImage(),10);
            setUpMusicSlider();
        }
    }

    public void PlayMedia()
    {
        if(mediaPlayer!=null) {

            File file;
            if (!isPlaying) {
                isPlaying = true;
                file = new File("src/main/resources/images/pause.png");

                BeginTimer();
                speedBox.setValue(String.valueOf(1));
                mediaPlayer.setVolume(volumeSlider.getValue() * 0.01);
                mediaPlayer.play();
            } else {
                isPlaying = false;
                file = new File("src/main/resources/images/play-button-arrowhead.png");
                PauseMedia();
            }
            play_pauseIV.setImage(new Image(file.toURI().toString()));
        }
    }

    private void PauseMedia()
    {
        if(mediaPlayer!=null) {
            CancelTimer();
            mediaPlayer.pause();
        }
    }

    public void ResetMedia()
    {
        if(mediaPlayer!=null)
        {
            musicSlider.setValue(0);
            mediaPlayer.seek(Duration.seconds(0));
        }
    }

    public void nextMedia()
    {
        if(mediaPlayer!=null)
        {

            if(randomOrder.get()){
                randomMedia();
            }
            //If the current media position is smaller than the last index of the playlist set the next index as value
            if(mediaPosition < runningPlaylist.size() -1)
                mediaPosition++;
                //Else if it is the same value as the last index or bigger, reset tha value to 0.
            else
                mediaPosition = 0;

            mediaPlayer.stop();

            //If there is a media playing stop it
            if(isPlaying)
                CancelTimer();

            try {
                isPlaying = false; //Nothing is playing now
                currentmusic = runningPlaylist.get(mediaPosition); //Set new Media
                ChangeMedia();
                PlayMedia(); //Play the new media
            }catch (IndexOutOfBoundsException e)
            {
                System.out.println("Playlist is empty. Please add music or change playlist.");
            }
        }
    }

    public void previousMedia()
    {
        if(mediaPlayer!=null){

            if(randomOrder.get()){
                randomMedia();
            }

            if(mediaPosition >0){
                mediaPosition--;
            }else{
                mediaPosition = runningPlaylist.size() -1;
            }
            mediaPlayer.stop();
            if(isPlaying) CancelTimer();
            try{
                isPlaying = false;
                currentmusic = runningPlaylist.get(mediaPosition);
                ChangeMedia();
                PlayMedia(); //Play new Media
            }catch (IndexOutOfBoundsException e){
                System.out.println("Playlist is empty. Please add music or change playlist.");
            }
        }
    }

    public void randomMedia()
    {
        if(mediaPlayer!=null)
        {
            int newMediaPos = 0;
            mediaPlayer.stop();
            if(isPlaying) CancelTimer();

            try{
                if(runningPlaylist.size() != 1){
                    do{
                        newMediaPos = new Random().nextInt(runningPlaylist.size());
                    }while (newMediaPos == mediaPosition);
                    mediaPosition = newMediaPos;
                    isPlaying = false;
                    ChangeMedia();
                    PlayMedia();
                }
            }catch (IndexOutOfBoundsException | IllegalArgumentException e){
                System.out.println("Playlist is empty. Please add music or change playlist.");
            }
        }
    }


    private void BeginTimer()
    {
        timer = new Timer();

        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                double current = mediaPlayer.getCurrentTime().toSeconds();
                int seconds = (int) Math.round(current);
                int currentMinutes = seconds / 60;
                int currentSeconds = seconds % 60;

                Platform.runLater(() -> {
                    if (currentSeconds < 10) {
                        mediaCurrentLabel.setText(currentMinutes + ":0" + currentSeconds);
                    } else {
                        mediaCurrentLabel.setText(currentMinutes + ":" + currentSeconds);
                    }
                });
            }
        };
        timer.scheduleAtFixedRate(timerTask, 0, 1000);
    }

    void setUpVolumeAndSpeedBox()
    {
        volumeSlider.valueProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                if(mediaPlayer!= null)  mediaPlayer.setVolume(volumeSlider.getValue() *0.01);
            }
        });

        double[] speeds = {0.25, 0.5, 0.75, 1.0, 1.25, 1.5, 1.75, 2.0};

        for(Double speed : speeds)
            speedBox.getItems().add(String.valueOf(speed));

        speedBox.setOnAction(event -> changeSpeed());
    }

    private void CancelTimer()
    {
        isPlaying = false;
        timer.cancel();
    }

    ///////////////////////////////Page Changing Methods//////////////////////////

    @FXML
    private void HomePageChange() throws IOException
    {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("home_page.fxml"));
        Parent fxml = loader.load();
        HomePageController homePageController = loader.getController();
        homePageController.setMainController(this);

        contentArea.getChildren().clear();;
        contentArea.getChildren().addAll(fxml);
    }

    @FXML
    private void MusicPageChange() throws IOException
    {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("music_page.fxml"));
        Parent fxml = loader.load();
        MusicPageController musicPageController = loader.getController();

        musicPageController.setMainController(this);

        musicPageController.initializeData();

        contentArea.getChildren().clear();;
        contentArea.getChildren().addAll(fxml);
    }

    static int playlistPagePos = 0;

    public static void setPlaylistPagePos(int pos)
    {
        playlistPagePos = pos;
    }



    @FXML
    public void PlayListPageChange() throws IOException
    {
        FXMLLoader loader = new  FXMLLoader(getClass().getResource("playlist_page.fxml"));
        Parent fxml = loader.load();
        PlaylistPageController playlistPageController = loader.getController();

        playlistPageController.setMainController(this);
        PlaylistPageController.setDefaultPlaylistPos(playlistPagePos);

        playlistPageController.initializeData();

        contentArea.getChildren().clear();;
        contentArea.getChildren().addAll(fxml);
    }

    @FXML
    private void VideoPageChange() throws IOException
    {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("video_page.fxml"));
        Parent fxml = loader.load();

        contentArea.getChildren().clear();
        contentArea.getChildren().add(fxml);
    }

    @FXML
    private void SettingsPageChange() throws IOException
    {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("settings_page.fxml"));
        Parent fxml = loader.load();
        SettingsPageController controller = loader.getController();

        controller.setMainController(this);
        controller.initializeData();

        contentArea.getChildren().clear();
        contentArea.getChildren().add(fxml);
    }

}