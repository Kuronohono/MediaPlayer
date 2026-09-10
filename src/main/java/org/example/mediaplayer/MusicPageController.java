package org.example.mediaplayer;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.stage.FileChooser;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.ResourceBundle;

public class MusicPageController extends GlobalMethods implements Initializable {

    @FXML
    private VBox music_vbox,credits_VBox,lyrics_vBox;

    @FXML
    private StackPane backdrop_SP;

    @FXML
    private ImageView detailsIV;

    @FXML
    private Label title_label;

    @FXML
    private ImageView timeIV;

    @FXML
    private HBox options_hbox;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
    }

    public void initializeData()
    {
        fillMusicVbox();
        File f = new File("src/main/resources/images/music_header_backdrop.jpg");
        setHeaderIV(f, backdrop_SP);
        timeIV.setEffect(imageColorBlend(timeIV,c,c));
        setOptions_hbox(this::addNewMusicFile,this::enableRandomOrder,options_hbox,music_vbox, MediaType.Music);
        if( MainController.music != null && !MainController.music.isEmpty())
            fillDetailsTab(musicFiles.getFirst(), credits_VBox,  lyrics_vBox, detailsIV,  title_label);
    }

    private void fillMusicVbox() {
        music_vbox.getChildren().clear();
        fillFX(MainController.music, mainController, music_vbox, credits_VBox, lyrics_vBox, detailsIV, title_label);
        for (Music ct : MainController.music)
            fillMusicList(ct);
    }

    private MainController mainController;

    public void setMainController(MainController mainController)
    {
        this.mainController = mainController;
    }


    private void addNewMusicFile(MouseEvent event){
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select files to load");

        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter(
                        "Audio Files", "*.mp3", "*.wav", "*.m4a", "*.aac", "*.flac", "*.ogg", "*.oga"));

        List<File> selectedFiles = fileChooser.showOpenMultipleDialog(null);

        if(selectedFiles != null && !selectedFiles.isEmpty()){

            File destFolder = new File("music");
            if(!destFolder.exists()){
                destFolder.mkdirs();
            }

            for(File selectedFile : selectedFiles) {

                Path destinationPath = destFolder.toPath().resolve(selectedFile.getName());
                boolean replaced = Files.exists(destinationPath);

                try {
                    Files.copy(selectedFile.toPath(), destinationPath, StandardCopyOption.REPLACE_EXISTING);
                    File file = new File(destinationPath.toUri());
                    Music music = new Music(file);

                    if (!replaced) {
                        MainController.music.add(music);
                        fillMusicVbox();
                    }

                    if (MainController.music.size() == 1) {
                        fillDataIfEmpty(MainController.music.getFirst());
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

    private void enableRandomOrder(MouseEvent event){
        MainController.randomOrder.set(!MainController.randomOrder.get());
    }

    private void fillDataIfEmpty(Music music)
    {
        mainController.setCurrentMusic(music,MainController.music);
        MainController.mediaPlayer = new MediaPlayer(new Media(MainController.music.getFirst().getFile().toURI().toString()));
    }

    public void playPlaylist()
    {
        mainController.changePlaylist(MainController.allSongsPlaylist);
    }

}
