package org.example.mediaplayer;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.CancellationException;


public class VideoViewController extends GlobalMethods implements Initializable {

    @FXML
    private BorderPane background_borderPane;

    @FXML
    private ComboBox<String> speedBox;

    private MediaView mediaView;

    private Video video;

    private Media media;

    private MediaPlayer mediaPlayer;

    private Slider playslider, volumeSlider;
    private Label fileTitle, currentTime, totalTime;

    private Stage stage;
    Button playPause;
    private boolean isPlaying= false;


    @Override
    public void initialize(URL location, ResourceBundle resources) {}

    public void initializeData()
    {
        initMediaView();
        initControlPanel();
        loadMedia();
    }

    private void initMediaView()
    {
        mediaView = new MediaView();
        mediaView.setPreserveRatio(false);

        BorderPane.setAlignment(mediaView,Pos.CENTER);

        StackPane mediaContainer = new StackPane(mediaView);
        mediaContainer.setStyle("-fx-background-color: black;");

        mediaView.fitWidthProperty().bind(mediaContainer.widthProperty());
        mediaView.fitHeightProperty().bind(mediaContainer.heightProperty());

        background_borderPane.setCenter(mediaContainer);
    }

    private void initControlPanel()
    {
        HBox hBox = new HBox();
        hBox.setAlignment(Pos.CENTER);
        hBox.setPadding(new Insets(10));
        hBox.setSpacing(20);
        hBox.setStyle("-fx-background-color: #181818;");

        fileTitle = new Label(video.getTitle());
        fileTitle.setMinWidth(100);
        fileTitle.setMaxWidth(400);
        fileTitle.setStyle("-fx-text-fill: white; -fx-font-size: 20px;");

        VBox vBox = new VBox();
        vBox.setSpacing(10);
        vBox.setAlignment(Pos.CENTER);
        HBox.setHgrow(vBox, Priority.ALWAYS);

        HBox timersAndSliderHbox = new HBox();
        timersAndSliderHbox.setSpacing(10);
        timersAndSliderHbox.setAlignment(Pos.CENTER);

        currentTime = new Label("00:00");
        currentTime.setMinWidth(50);
        currentTime.setStyle("-fx-text-fill: white; -fx-alignment: center-right;");

        playslider = new Slider();
        playslider.setMinWidth(200); // Set minimum width
        playslider.setMaxWidth(1000); // Allow it to grow
        HBox.setHgrow(playslider, Priority.ALWAYS);

        totalTime = new Label(video.getDuration());
        totalTime.setMinWidth(50);
        totalTime.setStyle("-fx-text-fill: white;");

        timersAndSliderHbox.getChildren().addAll(currentTime,playslider,totalTime);

        HBox buttonsHbox = new HBox();
        buttonsHbox.setSpacing(15);
        buttonsHbox.setAlignment(Pos.CENTER);
        buttonsHbox.getChildren().addAll(setUpButtons());

        vBox.getChildren().addAll(buttonsHbox,timersAndSliderHbox);
        hBox.getChildren().addAll(fileTitle,vBox,setUpVolumeAndSpeedBox());
        background_borderPane.setBottom(hBox);
    }

    private Button[] setUpButtons(){
        playPause = new Button();
        Button next = new Button();
        Button previous = new Button();
        Button restart = new Button();
        Button shuffle = new Button();
        Button[] buttons = { restart,previous,playPause, next, shuffle};
        ImageView[] buttonImageViews = {
                new ImageView(new Image(new File("src/main/resources/images/replay.png").toURI().toString())),
                new ImageView(new Image(new File("src/main/resources/images/rewind-button.png").toURI().toString())),
                new ImageView(new Image(new File("src/main/resources/images/pause.png").toURI().toString())),
                new ImageView(new Image(new File("src/main/resources/images/fast-forward.png").toURI().toString())),
                new ImageView(new Image(new File("src/main/resources/images/shuffle.png").toURI().toString()))
        };
        int i =0;
        for(Button button : buttons){
            button.setStyle("-fx-border-radius: 40; -fx-background-color: transparent;");
            button.setPrefSize(40,40);
            button.setCursor(Cursor.HAND);
            button.setGraphic(assignImageView(buttonImageViews[i]));
            i++;
            button.setOnMouseEntered(event ->
                    button.setStyle("-fx-background-color: rgba(255,255,255,0.15); -fx-border-radius: 20;"));
            button.setOnMouseExited(e ->
                    button.setStyle("-fx-background-color: transparent; -fx-cursor: hand;")
            );
        }
        playPause.setOnAction(event -> PlayMedia());
        next.setOnAction(event -> NextMedia());
        return buttons;
    }

    private ImageView assignImageView(ImageView imageView){
        imageView.setFitWidth(20);
        imageView.setFitHeight(15);
        imageView.setPreserveRatio(true);
        imageView.setEffect(imageColorBlend(imageView,Color.WHITE,Color.BLACK));
        return imageView;
    }

    private void loadMedia()
    {
        Media media = new Media(video.getFile().toURI().toString());
        mediaPlayer = new MediaPlayer(media);
        mediaView.setMediaPlayer(mediaPlayer);
        mediaPlayerListener();
        mediaPlayer.setOnReady(this::PlayMedia);
    }

    public void mediaPlayerListener(){
        mediaPlayer.currentTimeProperty().addListener((observable, oldValue, newValue) -> {
            // Update Slider
            playslider.setValue(newValue.toSeconds());
        });
    }

    public void setVideo(Video video){
        this.video = video;
    }

    public void setStage(Stage stage){
        this.stage = stage;
        stage.setOnHidden(event -> {
            mediaPlayer.stop();
            mediaPlayer.dispose();
        });
    }

    private void PlayMedia()
    {

        if(mediaPlayer != null){
            File file;

            if(!isPlaying){
                file = new File("src/main/resources/images/pause.png");
                isPlaying = true;
                BeginTimer();
                speedBox.setValue(String.valueOf(1));
                mediaPlayer.setVolume(volumeSlider.getValue() * 0.01);
                mediaPlayer.play();
            }else{
                file = new File("src/main/resources/images/play-button-arrowhead.png");
                isPlaying = false;
                PauseMedia();
            }
            playPause.setGraphic(assignImageView(new ImageView(new Image(file.toURI().toString()))));
        }
    }

    private void NextMedia(){

    }


    private void PauseMedia(){
        if(mediaPlayer!= null){
            mediaPlayer.pause();
        }
    }

    private Timer timer;

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
                        currentTime.setText(currentMinutes + ":0" + currentSeconds);
                    } else {
                        currentTime.setText(currentMinutes + ":" + currentSeconds);
                    }
                });


            }
        };

        timer.scheduleAtFixedRate(timerTask, 0, 1000);
    }

    private void CancelTimer()
    {
        isPlaying = false;
        timer.cancel();
    }

    private HBox setUpVolumeAndSpeedBox()
    {
        ImageView speedometer = new ImageView(new Image(new File("src/main/resources/images/speedometer.png").toURI().toString()));
        speedometer.setFitWidth(70);
        speedometer.setFitHeight(40);
        speedometer.setPreserveRatio(true);
        speedometer.setEffect(imageColorBlend(speedometer,Color.WHITE,Color.BLACK));

        speedBox = new ComboBox<>();

        double[] speeds = {0.25, 0.5, 0.75, 1.0, 1.25, 1.5, 1.75, 2.0};

        for(Double speed : speeds)
            speedBox.getItems().add(String.valueOf(speed));

        speedBox.setOnAction(event -> changeSpeed());

        ImageView volumeImage = new ImageView(new Image(new File("src/main/resources/images/speaker-filled-audio-tool.png").toURI().toString()));
        volumeImage.setFitWidth(70);
        volumeImage.setFitHeight(40);
        volumeImage.setPreserveRatio(true);
        volumeImage.setEffect(imageColorBlend(speedometer,Color.WHITE,Color.BLACK));

        volumeSlider = new Slider();

        volumeSlider.valueProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                if(mediaPlayer!= null)  mediaPlayer.setVolume(volumeSlider.getValue() *0.01);
            }
        });


        HBox hbox = new HBox();
        hbox.getChildren().addAll(speedometer,speedBox,volumeImage,volumeSlider);
        hbox.setAlignment(Pos.CENTER);
        hbox.setSpacing(20);
        return hbox;
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

    private int mediaPosition;

    private void nextMedia()
    {
        if(mediaPlayer != null){

            if(mediaPosition < MainController.videos.size() -1)
                mediaPosition++;
            else mediaPosition = 0;

            mediaPlayer.stop();

            if(isPlaying)
                CancelTimer();

            try{
                isPlaying = false;
                video = MainController.videos.get(mediaPosition);
                ChangeVideo();
                PlayMedia();
            }catch (IndexOutOfBoundsException e){
                System.out.println("Playlist is empty.");
            }
        }
    }

    private void ChangeVideo(){
        if(MainController.videos.get(mediaPosition) != null && mediaPlayer != null){
            video= MainController.videos.get(mediaPosition);

        }
    }










}
