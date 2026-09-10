package org.example.mediaplayer;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import org.jaudiotagger.audio.exceptions.CannotReadException;
import org.jaudiotagger.audio.exceptions.InvalidAudioFrameException;
import org.jaudiotagger.audio.exceptions.ReadOnlyFileException;
import org.jaudiotagger.tag.TagException;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.ResourceBundle;

public class HomePageController extends GlobalMethods implements Initializable {

    @FXML
    private HBox recentlyMusic;

    @FXML
    private VBox favorites_vbox;

    @FXML
    private TilePane playlists_tilePane;

    @FXML
    private ScrollPane recent_scrollPane,favorites_scrollPane;

    private ArrayList<Music> musicFiles;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        musicFiles = new ArrayList<>(MainController.music);
        NodeStyleChange();
        try {
            List<Music> recentlyAdded = MainController.music.stream()
                    .sorted(Comparator.comparing(Music::getDateAdded).reversed())
                    .limit(10)
                    .toList();
            for(Music music : recentlyAdded)
                fillRecentlyAdded(music);

            if(MainController.favorites.isEmpty()){
                favorites_vbox.getChildren().add(emptyList());
            } else {
                for(Music music : MainController.favorites)
                    fillFavorites(music);
            }


            if(MainController.playlists.isEmpty()){
                playlists_tilePane.getChildren().add(emptyList());
            }
            else{
                for(int i = 0; i< MainController.playlists.size(); i++)
                {
                    if(i<4)
                        fillPlaylists(MainController.playlists.get(i));
                }
            }

        } catch (IOException | CannotReadException | TagException | InvalidAudioFrameException | ReadOnlyFileException e) {
            throw new RuntimeException(e);
        }
    }

    private Label emptyList(){
        Label label = new Label("Your list is empty.");
        label.setStyle("-fx-font-size:13px; -fx-text-fill: #DCDCDC; -fx-font-weight: bold;");
        return label;
    }



    private void NodeStyleChange()
    {
        ScrollPane[] scrollPanes = {recent_scrollPane,favorites_scrollPane};
        for(ScrollPane scrollPane : scrollPanes){
            scrollPane.setStyle("-fx-background: transparent; -fx-background-color:transparent;");
            scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
            scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
            scrollPane.setPannable(true);
        }

        //Adjust Hbox to make the child object scrollable and not adjustable to fit inside it
        recentlyMusic.setSpacing(20);
        recentlyMusic.setFillHeight(false); //Do not stretch vertically


    }

    private void fillRecentlyAdded(Music music) throws IOException, CannotReadException, TagException, InvalidAudioFrameException, ReadOnlyFileException {

        StackPane stackPane = new StackPane();
        stackPane.setAlignment(Pos.BOTTOM_RIGHT);
        stackPane.setPadding(new Insets(0,10,10,10));
        stackPane.setPrefSize(200,250);

        Button button = setUpButton("src/main/resources/images/play-button.png", 22,c,c);
        button.setVisible(false);

        stackPane.setOnMouseEntered(e ->{
            stackPane.setStyle("-fx-background-color: #303030; -fx-background-radius: 10;");
            button.setVisible(true);
        });

        stackPane.setOnMouseExited(e ->{
            stackPane.setStyle("-fx-background-color: transparent; -fx-background-radius: 10;");
            button.setVisible(false);
        });

        button.setOnMouseClicked(event -> {
            mainController.setCurrentMusic(music,MainController.music);
        });

        VBox vBox = new VBox();
        vBox.setPrefSize(190,240);
        vBox.setSpacing(5);
        vBox.setAlignment(Pos.BASELINE_LEFT);
        vBox.setStyle("-fx-background-radius: 10");
        vBox.setPadding(new Insets(10,0,10,0));

        ImageView imageView = new ImageView(music.getImage());
        imageView.setFitHeight(170);
        imageView.setFitWidth(170);
        imageView.setPreserveRatio(false);

        Rectangle clip = new Rectangle((int) imageView.getFitWidth(), (int) imageView.getFitHeight());
        clip.setArcHeight(30);
        clip.setArcWidth(30);
        imageView.setClip(clip);

        Label title =new Label(music.getTitle());
        title.setStyle("-fx-font-size: 16;");

        Label year = new Label(music.getYear());
        year.setStyle("-fx-font-size: 12;");

        vBox.getChildren().addAll(imageView,title,year);

        stackPane.getChildren().add(vBox);

        stackPane.getChildren().add(button);
        recentlyMusic.getChildren().add(stackPane);

    }

    private void fillFavorites(Music ct) throws CannotReadException, TagException, InvalidAudioFrameException, ReadOnlyFileException, IOException {
        Pane pane = new Pane();
        pane.setPrefSize(450,65);

        HBox hBox= new HBox();
        hBox.setPrefSize(430,60);
        hBox.setPadding(new Insets(5,15,5,15));
        hBox.setAlignment(Pos.CENTER_LEFT);
        hBox.setSpacing(15);
        hBox.setCursor(Cursor.HAND);

        hBox.setOnMouseEntered(e ->
                hBox.setStyle("-fx-background-color: #303030; -fx-background-radius: 17;")
        );

        hBox.setOnMouseExited(e ->
                hBox.setStyle("-fx-background-color: transparent; -fx-background-radius: 17;")
        );

        ImageView imageView = new ImageView(ct.getImage());
        imageView.setPreserveRatio(false);
        imageView.setFitWidth(45);
        imageView.setFitHeight(45);

        Rectangle clip = new Rectangle((int) imageView.getFitWidth(), (int) imageView.getFitHeight());
        clip.setArcHeight(15);
        clip.setArcWidth(15);
        imageView.setClip(clip);

        VBox vBox = new VBox();
        vBox.setAlignment(Pos.CENTER_LEFT);
        vBox.setSpacing(2);
        vBox.setPrefSize(270,50);


        Label title = new Label();
        title.setText(ct.getTitle());
        title.setStyle("-fx-font-size: 16px;");

        Label artist = new Label();
        artist.setText(ct.getArtist());
        artist.setStyle("-fx-text-fill: grey;");

        vBox.getChildren().addAll(title,artist);

        Label duration = new Label(ct.getDuration());

        Button play_add = new Button();
        play_add.setText("▶");
        play_add.setStyle("-fx-font-size: 17px; -fx-text-fill: white; -fx-background-color: transparent; -fx-border-fill:white; -fx-border-width: 2");
        play_add.setVisible(false);

        hBox.setOnMouseEntered(event -> play_add.setVisible(true));
        hBox.setOnMouseExited(event -> play_add.setVisible(false));

        hBox.getChildren().addAll(imageView,vBox,duration,play_add);

        hBox.setOnMouseClicked(event -> {
            MainController.setPlaylistPagePos(0);
            mainController.setCurrentMusic(ct, MainController.favorites);
            try{
                mainController.PlayListPageChange();
            }catch (IOException e){
                throw new RuntimeException(e);
            }
        });

        favorites_vbox.getChildren().add(hBox);
    }

    private void fillPlaylists(Playlist playlist) throws CannotReadException, TagException, InvalidAudioFrameException, ReadOnlyFileException, IOException {

            HBox hbox = new HBox();
            hbox.setSpacing(25);
                VBox vBox = new VBox();
                vBox.setAlignment(Pos.CENTER);
                vBox.setPrefSize(200,100);
                vBox.setPadding(new Insets(10,10,10,10));
                vBox.setCursor(Cursor.HAND);

                vBox.setOnMouseEntered(e ->
                        vBox.setStyle("-fx-background-color: #303030; -fx-background-radius: 10;")
                );

                vBox.setOnMouseExited(e ->
                        vBox.setStyle("-fx-background-color: transparent; -fx-background-radius: 10;")
                );

                StackPane stackPane = new StackPane();
                stackPane.setPrefSize(190,150);
                stackPane.setAlignment(Pos.BOTTOM_LEFT);

                ImageView imageView = new ImageView(playlist.getImage());
                imageView.setPreserveRatio(false);
                imageView.setFitWidth(190);
                imageView.setFitHeight(90);
                imageView.setOpacity(0.8);

                Rectangle clip = new Rectangle((int) imageView.getFitWidth(), (int) imageView.getFitHeight());
                clip.setArcHeight(15);
                clip.setArcWidth(15);
                imageView.setClip(clip);

                Label title = new Label(playlist.getName());
                title.setPadding(new Insets(10,10,7,15));
                title.setStyle("-fx-font-size: 15; -fx-text-fill: white; -fx-font-weight: bold;");

                stackPane.getChildren().add(imageView);
                stackPane.getChildren().add(title);

                stackPane.setOnMouseClicked(event -> {
                    MainController.setPlaylistPagePos(MainController.playlists.indexOf(playlist));
                    try {
                        mainController.PlayListPageChange();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                });

                vBox.getChildren().add(stackPane);
                hbox.getChildren().add(vBox);

                playlists_tilePane.getChildren().add(hbox);
    }

    MainController mainController;

    public void setMainController(MainController mainController)
    {
        this.mainController = mainController;
    }

}
