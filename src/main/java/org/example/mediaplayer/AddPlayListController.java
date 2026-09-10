package org.example.mediaplayer;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class AddPlayListController extends GlobalMethods implements Initializable {

    @FXML
    private ToggleButton type_button;

    @FXML
    private Button select_image_button, create_playlistButton;

    @FXML
    private TextField searchTF, search_imageTF, nameTF;

    @FXML
    private VBox media_vbox;

    @FXML
    private ComboBox<String> filterComboBox;

    @FXML
    private ImageView timeIV;

    @FXML
    private Label message_label,extraDetailLabel;

    @FXML
    private Stage parentStage;

    private File selectedImage;

    Color backColor = Color.rgb(85,85,85,1.0);

    private ArrayList<CheckedItem<Music>> checked_music;
    private ArrayList<CheckedItem<Video>> checked_videos;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        initArrays();
        initTypeButton();
        initSelectImageButton();
        initComboBoxSearch();
    }

    //Initialize the music and video checked objects
    private void initArrays()
    {
        checked_music = new ArrayList<>();
        checked_videos = new ArrayList<>();
        for(Music music : MainController.music){
            CheckedItem<Music> checkedItem = new CheckedItem<>(music,false);
            checked_music.add(checkedItem);
        }
        for(Video video: MainController.videos){
            CheckedItem<Video> checkedItem = new CheckedItem<>(video, false);
            checked_videos.add(checkedItem);
        }
    }

    //Initialize the properties of type button
    private void initTypeButton()
    {
        media_vbox.getChildren().clear();
        if(type_button.isSelected()){
            type_button.setText("Video");
            extraDetailLabel.setVisible(false);
            fillVideos(checked_videos);
            filterComboBox.getItems().clear();
            filterComboBox.getItems().addAll("Title");
        }else{
            type_button.setText("Music");
            fillAudio(checked_music);
            extraDetailLabel.setVisible(true);
            filterComboBox.getItems().clear();
            filterComboBox.getItems().addAll("Title","Artist","Album");
        }
        filterComboBox.setValue("Title");
        type_button.setStyle("-fx-text-fill: white; -fx-background-color: transparent; -fx-border-radius: 15px; -fx-border-width: 1px; -fx-border-color: white; -fx-font-size: 16px");
        type_button.setOnMouseClicked(event -> initTypeButton());
    }

    private void initSelectImageButton()
    {
        ImageView imageView = (ImageView) select_image_button.getGraphic();
        select_image_button.setEffect(imageColorBlend( imageView,c,c ));
        select_image_button.setOnMouseEntered(event -> {
            imageView.setEffect(imageColorBlend(imageView,hoverColor,c));
            select_image_button.setGraphic(imageView);
        });
        select_image_button.setOnMouseExited(event -> {
            imageView.setEffect(imageColorBlend(imageView,c,c));
            select_image_button.setGraphic(imageView);
        });

        select_image_button.setOnMouseClicked(event -> {
            select_PlaylistImage();
        });
    }

    private void select_PlaylistImage()
    {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select an image for playlist.");

        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Image Files","*.jpg", "*.jpeg", "*.jpe","*.png"));

        selectedImage = fileChooser.showOpenDialog(null);

        if(selectedImage != null)
        {
            search_imageTF.setText(selectedImage.getPath());
        }

    }

    private void initComboBoxSearch(){
       // filterComboBox.valueProperty().addListener((observable, oldValue, newValue) -> searchMedia(filterComboBox.getValue()));
        filterComboBox.setValue("Title");

        searchTF.textProperty().addListener((observable, oldValue, newValue) -> {
           // searchMedia(filterComboBox.getValue());
        });

        timeIV.setEffect(imageColorBlend(timeIV,c,c));

        //create_playlistButton.setOnMouseClicked(event -> createPlaylist());
    }

    private void fillVideos(ArrayList<CheckedItem<Video>> results){
        for(CheckedItem<Video> checkedItem: results){
            media_vbox.getChildren().addAll(fillVBox(checkedItem,MediaType.Video, checkedItem.getItem().getResolution(), null));
            //fillDetailsBox()
        }
    }

    private void fillAudio(ArrayList<CheckedItem<Music>> results){
        for(CheckedItem<Music> checkedItem: results){
            media_vbox.getChildren().addAll(fillVBox(checkedItem,MediaType.Music,checkedItem.getItem().getArtist(),checkedItem.getItem().getAlbum()));
        }
    }



    private HBox fillVBox(CheckedItem<?> checkedItem, MediaType mediaType, String detail, String AudioAlbum){

            HBox hBox = new HBox();
            hBox.setAlignment(Pos.CENTER_LEFT);
            hBox.setPadding(new Insets(10,10,10,15));
            hBox.setStyle("-fx-border-radius: 20");
            hBox.setPrefSize(750,65);

            Label number = new Label();
            number.setAlignment(Pos.CENTER);
            number.setStyle("-fx-font-size: 16px; -fx-text-fill: white; -fx-background-color: transparent;");
            number.setPrefWidth(30);
            number.setDisable(true);

            //Adding Image Title and Detail
            HBox hBox1 = new HBox();
            hBox1.setAlignment(Pos.CENTER_LEFT);
            hBox1.setSpacing(15);

            //VBox that will contain Title and Artist name
            VBox vBox = new VBox();

            Label title = new Label(checkedItem.getItem().getTitle());
            title.setStyle("-fx-font-size: 17px; -fx-text-fill: white;");

            Label extraDetail = new Label(detail);
            extraDetail.setStyle("-fx-font-size: 12px; -fx-text-fill: #d0d0d0;");

            vBox.getChildren().addAll(title,extraDetail);

            //Adding Duration Label
            Label durationLabel = new Label(checkedItem.getItem().getDuration());
            durationLabel.setStyle("-fx-font-size: 15px; -fx-text-fill: #d0d0d0;");
            durationLabel.setPrefWidth(50);

            CheckBox checkBox = new CheckBox();

            checkBox.setSelected(checkedItem.isChecked());

                checkBox.selectedProperty().addListener(new ChangeListener<Boolean>() {
                    @Override
                    public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                        checkedItem.setChecked(checkBox.isSelected());
                    }
                });

            ImageView imageView;

            if(mediaType == MediaType.Video){
                hBox.setSpacing(30);
                number.setText(Integer.toString(MainController.videos.indexOf(checkedItem.getItem()) + 1));
                vBox.setPrefWidth(405);
                hBox1.getChildren().addAll(videoIndicator(checkedItem.getItem().getImage()),vBox);
                hBox.getChildren().addAll(number,hBox1,durationLabel,checkBox);
            }else{
                hBox.setSpacing(40);
                number.setText(Integer.toString(MainController.music.indexOf(checkedItem.getItem()) + 1));
                vBox.setPrefWidth(200);
                // File Image
                imageView = setUpImageView(checkedItem.getItem().getImage(), 50,50,10,10);
                hBox1.getChildren().addAll(imageView,vBox);

                Label album = new Label(AudioAlbum);
                album.setStyle("-fx-font-size: 15px; -fx-text-fill: #d0d0d0;");
                album.setPrefWidth(160);

                hBox.getChildren().addAll(number,hBox1,album,durationLabel,checkBox);
            }

            return hBox;
    }

    private StackPane videoIndicator(Image image)
    {
        //Set up Stack Pane for video indicator and Image
        StackPane stackPane = new StackPane();
        stackPane.setPrefSize(60,60);

        //Set up Video Indicator Image View
        File videoIndicator = new File("src/main/resources/images/film.png");
        ImageView film = new ImageView(new Image(videoIndicator.toURI().toString()));
        film.setFitHeight(60);
        film.setFitWidth(55);
        film.setEffect(imageColorBlend(film,backColor,backColor));

        //Set up Video Thumbnail
        ImageView videoThumbnail = new ImageView(image);
        videoThumbnail.setFitHeight(35);
        videoThumbnail.setFitWidth(50);

        //Put Video Indicator below Video Thumbnail
        stackPane.getChildren().addAll(film, videoThumbnail);

        return stackPane;
    }


    //Set the stage
    public void setParentStage(Stage s) {
        parentStage = s;
    }



}

