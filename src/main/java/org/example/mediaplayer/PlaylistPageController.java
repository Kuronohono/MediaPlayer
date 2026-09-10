package org.example.mediaplayer;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import javax.print.attribute.standard.Media;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class PlaylistPageController extends GlobalMethods implements Initializable {

    @FXML
    private VBox media_Vbox,credits_VBox,lyrics_vBox,playlists_vbox,info_Vbox;

    @FXML
    private HBox options_hbox,playlistHbox_options;

    @FXML
    private ImageView timeIV,editHeaderIV,detailsIV,playlistImageView,play_button_IV;

    @FXML
    private Label title_label;

    @FXML
    private Button play_button;

    @FXML
    private StackPane backdrop_SP;

    private Playlist<? extends MediaItem> selectedPlaylist;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
    }

    public void initializeData()
    {
        setOptions_hbox();
        setPlaylistHbox_options();
        loadPlaylists();
        timeIV.setEffect(imageColorBlend(timeIV,c,c));
        editHeaderIV.setEffect(imageColorBlend(timeIV,c,c));
        playlists_vbox.setVisible(MainController.visiblePlaylistBox);
        info_Vbox.setVisible(MainController.visibleInfoBox);
        try {
            changePlaylist(MainController.playlists.get(defaultPlaylistPos));
        }catch (IndexOutOfBoundsException e)
        {
            changePlaylist(MainController.favoritePlaylist);
        }

    }

    public static int defaultPlaylistPos = 0;

    public static void setDefaultPlaylistPos(int pos)
    {
        defaultPlaylistPos = pos;
    }

    private void loadPlaylists()
    {
        playlists_vbox.getChildren().clear();
        fillPlaylists(MainController.favoritePlaylist);
        for(Playlist<Music> playlist : MainController.playlists)
        {
            fillPlaylists(playlist);
        }
    }

    ComboBox<String> comboBox;

    private void setOptions_hbox()
    {
        Button shuffle_button = setUpButton("src/main/resources/images/shuffle.png",22,c,c);
        Button settings_button = setUpButton("src/main/resources/images/settings.png",22,c,c);

        HBox hBox = new HBox();
        hBox.setAlignment(Pos.CENTER_RIGHT);

        Button search_button = setUpButton("src/main/resources/images/search.png",22,c,c);

       comboBox = sortingComboBox();
        Button sorting_button = setUpButton("src/main/resources/images/sort.png",22,c,c);
        hBox.getChildren().addAll(search_button,comboBox,sorting_button);

        HBox.setHgrow(hBox, Priority.ALWAYS);
        options_hbox.getChildren().addAll(shuffle_button,settings_button,hBox);
    }

    private void setPlaylistHbox_options()
    {
        Button createPlaylistButton = setUpButton("src/main/resources/images/add-list.png",22,c,c);

        createPlaylistButton.setOnMouseClicked(event -> {
            try {
                createPlaylist();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
        Button changePanel = setUpButton("src/main/resources/images/refresh.png",18,c,c);

        changePanel.setOnMouseClicked(event -> swapVBoxes());

        playlistHbox_options.getChildren().addAll(createPlaylistButton,changePanel);
    }


    private void fillPlaylists(Playlist<? extends MediaItem> playlist)
    {
        HBox hBox = new HBox();
        hBox.setAlignment(Pos.CENTER_LEFT);
        hBox.setStyle("-fx-background-color: #151515; -fx-background-radius: 10px");
        hBox.setPadding(new Insets(10,10,10,15));
        hBox.setSpacing(10);
        hBox.setPrefSize(200,90);

        ImageView imageView = setUpImageView(playlist.getImage(),70,70,10,10);

        VBox vBox = new VBox();
        vBox.setAlignment(Pos.TOP_LEFT);
        vBox.setSpacing(25);

        Label playlistTitle = new Label(playlist.getName());
        playlistTitle.setStyle("-fx-font-size: 20px; -fx-text-fill: white;");

        Label contentAmount = new Label("Songs: "+playlist.getMedia().size());
        contentAmount.setStyle("-fx-font-size: 14px; -fx-text-fill: white;");

        vBox.getChildren().addAll(playlistTitle,contentAmount);

        hBox.getChildren().addAll(imageView,vBox);

        hBox.setOnMouseEntered(event -> {
            hBox.setStyle("-fx-background-color: #303030; -fx-background-radius: 10px");
        });

        hBox.setOnMouseExited(event -> {
            hBox.setStyle("-fx-background-color: #151515; -fx-background-radius: 10px");
        });

        hBox.setOnMousePressed(event -> hBox.setStyle("-fx-background-color: #353535; -fx-background-radius: 10px"));
        hBox.setOnMouseReleased(event -> hBox.setStyle("-fx-background-color: #303030; -fx-background-radius: 10px"));

        hBox.setOnMouseClicked(event -> {
            changePlaylist(playlist);
        });

        playlists_vbox.getChildren().add(hBox);
    }

    private void changePlaylist(Playlist<? extends MediaItem> playlist)
    {
        media_Vbox.getChildren().clear();
        initByPlaylist(playlist);
        selectedPlaylist = playlist;
    }



    private void initByPlaylist(Playlist<? extends MediaItem> playlist)
    {
        playlistImageView.setImage(playlist.getImage());
        ArrayList<? extends MediaItem> items = playlist.getMedia();

        if(playlist.getType() == MediaType.Music){
            @SuppressWarnings("unchecked")
            ArrayList<Music> musicList = (ArrayList<Music>) items;
            fillFX(musicList,mainController,media_Vbox, credits_VBox, lyrics_vBox,detailsIV, title_label);
            musicList = checkComboBoxItem(comboBox.getValue(), musicList);
            for(Music m : musicList) fillMusicList(m);
        }else{
            @SuppressWarnings("unchecked")
            ArrayList<Video> videoList = (ArrayList<Video>) items;
            //for(Video v : videoList) ;
        }
    }

    private MainController mainController;

    public void setMainController(MainController mainController)
    {
        this.mainController = mainController;
    }

    private void createPlaylist() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("add_playlist.fxml"));
        Parent parent1 = (Parent) fxmlLoader.load();
        AddPlayListController controller = fxmlLoader.getController();

        Stage stage = new Stage();
        stage.setTitle("Create Playlist");
        stage.setScene(new Scene(parent1));
        stage.initModality(Modality.APPLICATION_MODAL);
        controller.setParentStage(stage);
        stage.showAndWait();
        loadPlaylists();

    }

    public void swapVBoxes()
    {
        MainController.visibleInfoBox = ! MainController.visibleInfoBox;
        MainController.visiblePlaylistBox = !MainController.visiblePlaylistBox;

        playlists_vbox.setVisible(MainController.visiblePlaylistBox);
        info_Vbox.setVisible(MainController.visibleInfoBox);
    }

    public void playPlaylist()
    {
        if(selectedPlaylist == null) return;
        if (selectedPlaylist.getType() == MediaType.Music){
            @SuppressWarnings("unchecked")
                    Playlist<Music>  musicPlaylist = (Playlist<Music>) selectedPlaylist;
                    mainController.changePlaylist(musicPlaylist);
        }else{
            @SuppressWarnings("unchecked")
            Playlist<Video> videoPlaylist = (Playlist<Video>) selectedPlaylist;

        }

    }
}