package org.example.mediaplayer;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

public class VideoPageController extends GlobalMethods implements Initializable {

    @FXML
    private HBox options_hbox;

    @FXML
    private VBox video_vBox,credits_VBox;

    @FXML
    private ImageView detailsIV,timeIV;

    @FXML
    private Label title_label;

    @FXML
    private StackPane backdrop_SP;

    Color backColor = Color.rgb(85,85,85,1.0);

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setOptions_hbox(this::addNewVideoFiles,null,options_hbox, video_vBox, MediaType.Video);
        fillVideoVBox();
        File f = new File("src/main/resources/images/video_backdrop.jpg");
        setHeaderIV(f,backdrop_SP);
        timeIV.setEffect(imageColorBlend(timeIV,c,c));
        if(MainController.videos != null && !MainController.videos.isEmpty()){
            fillVideoDetailsTab(MainController.videos.getFirst());
        }
    }

    private void addNewVideoFiles(MouseEvent event)
    {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select a file to load");

        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Video Files","*.mp4","*.mov","*.avi"));

        List<File> selectedFiles = fileChooser.showOpenMultipleDialog(null);

        if(selectedFiles!=null && !selectedFiles.isEmpty()){
            File destFolder = new File("videos");
            if(!destFolder.exists()){
                destFolder.mkdirs();
            }

            for(File selectedFile : selectedFiles) {

                Path destinationPath = destFolder.toPath().resolve(selectedFile.getName());
                boolean replaced = Files.exists(destinationPath);

                try {
                    Files.copy(selectedFile.toPath(), destinationPath, StandardCopyOption.REPLACE_EXISTING);
                    File file = new File(destinationPath.toUri());
                    Video video = new Video(file);

                    if (!replaced) {
                        MainController.videos.add(video);
                    }

                } catch (IOException e) {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Error");
                    alert.setHeaderText("File Loading Failed");
                    alert.setContentText(e.getMessage());
                    alert.showAndWait();
                }
            }
        }
    }



    private ArrayList<Video> videos;

    private void fillVideoVBox()
    {
        video_vBox.getChildren().clear();
        videos = MainController.videos;
        for(Video video : videos)
            fillVideoList(video);
    }

    private Stage videoStage;
    private VideoViewController videoViewController;

    private void loadVideoView(Video video) throws IOException {

        if(videoStage != null && videoStage.isShowing()){
            videoViewController.setVideo(video);
            videoViewController.initializeData();
            videoStage.toFront();
            videoStage.requestFocus();
            return;
        }

        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("video_view.fxml"));
        Parent parent1 = (Parent) fxmlLoader.load();
        videoViewController = fxmlLoader.getController();

        videoStage = new Stage();
        videoStage.setTitle("Video Player");
        videoStage.setScene(new Scene(parent1));

        videoStage.setOnHidden(e -> {
            videoStage = null;
            videoViewController = null;
        });

        videoViewController.setVideo(video);
        videoViewController.setStage(videoStage);
        videoViewController.initializeData();
        videoStage.show();

    }

    private void fillVideoList(Video video)
    {
        HBox hBox = new HBox();
        hBox.setAlignment(Pos.CENTER_LEFT);
        hBox.setPadding(new Insets(10,10,10,15));
        hBox.setSpacing(40);
        hBox.setStyle("-fx-border-radius: 20");
        hBox.setPrefSize(905,70);

        int temp = videos.indexOf(video) + 1;

        Label number = new Label(String.valueOf(temp));
        number.setAlignment(Pos.CENTER);
        number.setStyle("-fx-font-size: 18px; -fx-text-fill: white; -fx-background-color: transparent;");
        number.setPrefWidth(45);
        number.setDisable(true);

        //Adding Image StackPane and the Title,Info

        HBox hBox1 = new HBox();
        hBox1.setAlignment(Pos.CENTER_LEFT);
        hBox1.setSpacing(15);

        StackPane stackPane = videoIndicator(video.getImage());

        VBox infoVbox = new VBox();

        //Video Name
        Label videoName = new Label(video.getTitle());
        videoName.setStyle("-fx-font-size: 19px; -fx-text-fill: white;");
        videoName.setPrefWidth(500);

        //Video resolution
        Label resolution = new Label(video.getResolution());
        resolution.setStyle("-fx-font-size: 15px; -fx-text-fill: #A8A8A8;");

        infoVbox.getChildren().addAll(videoName,resolution);

        Label duration = new Label(video.getDuration());
        duration.setStyle("-fx-font-size: 15px; -fx-text-fill: #d0d0d0;");
        duration.setPrefWidth(55);

        Button options = new Button("...");
        options.setStyle("-fx-text-fill:white; -fx-background-color:transparent; -fx-font-size: 19px;");
        options.setVisible(false);


        hBox.getChildren().addAll(number,stackPane,infoVbox, duration, options);

        video_vBox.getChildren().add(hBox);

        hBox.setOnMouseEntered(e ->{
            hBox.setStyle("-fx-background-color: #303030; -fx-background-radius: 20;");
            options.setVisible(true);
            number.setText("▶");
        });

        hBox.setOnMouseExited(e ->{
            hBox.setStyle("-fx-background-color: transparent; -fx-background-radius: 20;");
            options.setVisible(false);
            number.setText(String.valueOf(temp));
        });

        hBox.setOnMouseClicked(event -> {
            fillVideoDetailsTab(video);
            if(event.getClickCount() == 2)
            {
                try {
                    loadVideoView(video);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }

    private void fillVideoDetailsTab(Video video){
        credits_VBox.getChildren().clear();

        Label infoLabel = new Label("Information");
        infoLabel.setStyle("-fx-font-size:13px; -fx-text-fill: white; -fx-font-weight: bold;");
        credits_VBox.getChildren().add(infoLabel);

        setUpImageAndClip(detailsIV,video.getImage(),20);

        title_label.setText(video.getTitle());

        //Information Tab
        String[][] info = video.getInformation();
        for(int i=1;i< info.length;i++)
            for(int j=1; j>=0;j--)
            {
                VBox vBox = new VBox();
                Label field = new Label();
                field.setStyle("-fx-font-size: 12px; -fx-text-fill: white; -fx-font-weight: bold;");
                Label role = new Label();
                role.setStyle("-fx-font-size:10px; -fx-text-fill: #d0d0d0");
                field.setText(info[i][j]);
                role.setText(info[i][j]);
                if(j==1)
                    vBox.getChildren().add(field);
                else
                    vBox.getChildren().add(role);
                credits_VBox.getChildren().add(vBox);
            }
    }

    private StackPane videoIndicator(Image image)
    {
        //Set up Stack Pane for video indicator and Image
        StackPane stackPane = new StackPane();
        stackPane.setPrefSize(70,60);

        //Set up Video Indicator Image View
        File videoIndicator = new File("src/main/resources/images/film.png");
        ImageView film = new ImageView(new Image(videoIndicator.toURI().toString()));
        film.setFitHeight(60);
        film.setFitWidth(60);
        film.setEffect(imageColorBlend(film,backColor,backColor));

        //Set up Video Thumbnail
        ImageView videoThumbnail = new ImageView(image);
        videoThumbnail.setFitHeight(35);
        videoThumbnail.setFitWidth(55);

        //Put Video Indicator below Video Thumbnail
        stackPane.getChildren().addAll(film, videoThumbnail);

        return stackPane;
    }

}
