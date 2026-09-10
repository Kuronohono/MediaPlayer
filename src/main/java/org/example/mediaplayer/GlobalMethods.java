package org.example.mediaplayer;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.PauseTransition;
import javafx.animation.Timeline;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.geometry.Bounds;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.control.*;
import javafx.scene.effect.Blend;
import javafx.scene.effect.BlendMode;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.effect.ColorInput;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.util.Duration;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;

import static org.example.mediaplayer.MainController.isDarkMode;

public abstract class GlobalMethods {


     Color c = Color.rgb(170, 170, 170, 1.0);
     private static final Duration ANIM_DURATION = Duration.millis(300);
     private static final double OPEN_WIDTH = 200;

    ArrayList<Music> removeObject(ArrayList<Music> arrayList, Music music)
    {
        for(Music thumbnail : arrayList)
        {
            if(thumbnail.getFile().getPath().equals(music.getFile().getPath()))
            {
                arrayList.remove(thumbnail);
                break;
            }
        }
        return arrayList;
    }

    int getMusicObjectPosition(ArrayList<Music> arrayList, Music music)
    {
        int position = -1;
        for(Music music1 : arrayList)
        {
            if(music1.getFile().getPath().equals(music.getFile().getPath())){
                position = arrayList.indexOf(music1);
                break;
            }
        }
        return position;
    }


    ImageView setUpImageView(Image image, int width, int height, int clipHeight, int clipWidth){
        ImageView imageView = new ImageView(image);
        imageView.setFitWidth(width);
        imageView.setFitHeight(height);
        imageView.setPreserveRatio(false);

        Rectangle clip = new Rectangle((int) imageView.getFitWidth(), (int) imageView.getFitHeight());
        clip.setArcHeight(clipHeight);
        clip.setArcWidth(clipWidth);
        imageView.setClip(clip);

        return imageView;
    }

    void setUpImageAndClip(ImageView imageView, Image image, int clipDim){
        imageView.setImage(image);
        Rectangle clip = new Rectangle((int) imageView.getFitWidth(), (int) imageView.getFitHeight());
        clip.setArcHeight(clipDim);
        clip.setArcWidth(clipDim);
        imageView.setClip(clip);
    }



    Button setUpButton(String path, int size, Color darkColor, Color lightColor)
    {
        Button button = new Button();
        button.setStyle("-fx-background-color: transparent;");
        button.setCursor(Cursor.HAND);

        File file = new File(path);
        ImageView imageView = new ImageView(new Image(file.toURI().toString()));
        imageView.setFitHeight(size);
        imageView.setFitWidth(size);
        imageView.setEffect(imageColorBlend(imageView,darkColor,lightColor));
        button.setGraphic(imageView);

        hoverEffect(button,imageView,darkColor,lightColor);
        return button;
    }

    Color hoverColor = Color.rgb(232, 232, 232, 1.0);

    void hoverEffect(Button button, ImageView imageView,Color darkColor, Color lightColor)
    {
        button.setCursor(Cursor.HAND);
        //On Hover Effect
        button.setOnMouseEntered(event -> {
            button.setStyle("-fx-background-color: #303030; -fx-background-radius: 20;");
            imageView.setEffect(imageColorBlend(imageView,hoverColor,darkColor));
        });

        button.setOnMouseExited(event -> {
            button.setStyle("-fx-background-color: transparent; -fx-background-radius: 20;");
            imageView.setEffect(imageColorBlend(imageView,darkColor,lightColor));

        });
    }


     Blend imageColorBlend(ImageView imageView, Color darkmodeColor, Color lightModeColor)
    {
        ColorAdjust monochrome = new ColorAdjust();
        monochrome.setSaturation(-1.0);

        Blend colorBlend = new Blend(
                BlendMode.SRC_ATOP,
                monochrome,
                isDarkMode? new ColorInput(0,0, imageView.getFitWidth(), imageView.getFitHeight(), darkmodeColor) : new ColorInput(0,0, imageView.getFitWidth(), imageView.getFitHeight(), lightModeColor)
        );
        return colorBlend;
    }

    VBox music_vbox, credits_VBox,lyrics_vBox;
    ImageView detailsIV;
    Label title_label;
    ArrayList<Music> musicFiles;
    ArrayList<Video> videoFiles;
    MainController mainController;

    void fillFX(ArrayList<Music> musicFiles, MainController mainController,VBox music_vbox, VBox credits_VBox, VBox lyrics_vBox, ImageView detailsIV, Label title_label)
    {
        this.musicFiles = musicFiles;
        this.videoFiles = videoFiles;
        this.mainController = mainController;
        this.music_vbox = music_vbox;
        this.credits_VBox = credits_VBox;
        this.lyrics_vBox = lyrics_vBox;
        this.detailsIV = detailsIV;
        this.title_label = title_label;
    }


    void fillMusicList(Music music)
    {
        HBox hBox = new HBox();
        hBox.setAlignment(Pos.CENTER_LEFT);
        hBox.setPadding(new Insets(10,10,10,15));
        hBox.setSpacing(40);
        hBox.setStyle("-fx-border-radius: 20");
        hBox.setPrefSize(905,70);
        hBox.setCursor(Cursor.HAND);

        int temp = musicFiles.indexOf(music) + 1;

        Label number = new Label(String.valueOf(temp));
        number.setAlignment(Pos.CENTER);
        number.setStyle("-fx-font-size: 18px; -fx-text-fill: white; -fx-background-color: transparent;");
        number.setPrefWidth(45);
        number.setDisable(true);

        //Adding Image Title and Artist
        HBox hBox1 = new HBox();
        hBox1.setAlignment(Pos.CENTER_LEFT);
        hBox1.setSpacing(15);

        //Music File Image
        ImageView imageView = setUpImageView(music.getImage(),50,50,10,10);

        //VBox that will contain Title and Artist name
        VBox vBox = new VBox();
        vBox.setPrefWidth(290);

        Label title = new Label(music.getTitle());
        title.setStyle("-fx-font-size: 19px; -fx-text-fill: white;");

        Label artist = new Label(music.getArtist());
        artist.setStyle("-fx-font-size: 13px; -fx-text-fill: #d0d0d0;");

        vBox.getChildren().addAll(title,artist);
        hBox1.getChildren().addAll(imageView,vBox);

        //Adding Album Label
        Label album = new Label(music.getAlbum());
        album.setStyle("-fx-font-size: 17px; -fx-text-fill: #d0d0d0;");
        album.setPrefWidth(205);

        //Adding Duration Label
        Label duration = new Label(music.getDuration());
        duration.setStyle("-fx-font-size: 15px; -fx-text-fill: #d0d0d0;");
        duration.setPrefWidth(55);

        ContextMenu contextMenu = new ContextMenu();
        MenuItem option1 = new MenuItem("Delete");
        contextMenu.getItems().add(option1);

        Button options = new Button("x");
        options.setStyle("-fx-text-fill:white; -fx-background-color:transparent; -fx-font-size: 19px;");
        options.setVisible(false);
        options.setOnAction(e -> {
            Bounds bounds = options.localToScreen(options.getBoundsInLocal());
            contextMenu.show(options, bounds.getMaxX(), bounds.getMinY());
        });

        boolean found = false;

        //Adding Favorites Button
        for(Music thumbnail : MainController.favorites)
        {
            if((thumbnail.getFile().getPath().equals(music.getFile().getPath())))
            {
                found = true;
                break;
            }
        }

        AtomicBoolean isfavorite = new AtomicBoolean(found);

        File f = isfavorite.get()
                ? new File("src/main/resources/images/heart_full.png")
                : new File("src/main/resources/images/heart_empty.png");

        ImageView favorite = new ImageView(new Image(f.toURI().toString()));
        favorite.setFitHeight(20);
        favorite.setFitWidth(20);

        if(!isfavorite.get()) favorite.setEffect(imageColorBlend(favorite, Color.WHITE, Color.BLACK));

        Button favorite_button = new Button();
        favorite_button.setCursor(Cursor.HAND);
        favorite_button.setGraphic(favorite);
        favorite_button.setStyle("-fx-background-color: transparent;");

        favorite_button.setOnMouseClicked(event -> {
            File file;

            if(isfavorite.get()){
                file =   new File("src/main/resources/images/heart_empty.png");
                ImageView imageView1 = new ImageView(new Image(file.toURI().toString()));
                imageView1.setFitHeight(20);
                imageView1.setFitWidth(20);
                imageView1.setEffect(imageColorBlend(favorite, Color.WHITE, Color.BLACK));
                MainController.favorites = removeObject(MainController.favorites, music);
                favorite_button.setGraphic(imageView1);
                isfavorite.set(false);
            }
            else{
                file =   new File("src/main/resources/images/heart_full.png");
                ImageView imageView1 = new ImageView(new Image(file.toURI().toString()));
                imageView1.setFitHeight(20);
                imageView1.setFitWidth(20);
                MainController.favorites.add(music);
                favorite_button.setGraphic(imageView1);
                isfavorite.set(true);
            }
        });

        hBox.getChildren().addAll(number,hBox1,album,duration,favorite_button, options);

        music_vbox.getChildren().add(hBox);

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
            fillDetailsTab(music, credits_VBox,  lyrics_vBox, detailsIV,  title_label);
            if(event.getClickCount() == 2)
            {
                mainController.setCurrentMusic(music,musicFiles);
            }
        });
    }

     void fillDetailsTab(Music music, VBox credits_VBox, VBox lyrics_vBox, ImageView detailsIV, Label title_label)
    {
        credits_VBox.getChildren().clear();
        lyrics_vBox.getChildren().clear();

        Label creditsLabel = new Label("Credits");
        creditsLabel.setStyle("-fx-font-size:13px; -fx-text-fill: white; -fx-font-weight: bold;");
        credits_VBox.getChildren().add(creditsLabel);
        creditsLabel.setText("Lyrics");
        lyrics_vBox.getChildren().add(creditsLabel);

        setUpImageAndClip(detailsIV,music.getImage(),20);

        title_label.setText(music.getTitle());

        //Credits Tab
        String[][] info = music.getInfo();
        for(int i=1;i<5;i++){
            for(int j=1;j>=0;j--)
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

        //Lyrics Tab
        Label lyrics = new Label(music.getLyrics());
        lyrics.setWrapText(true);
        lyrics.setStyle("-fx-background-color: transparent; -fx-text-fill:white; -fx-font-size: 16px;");
        lyrics.sceneProperty().addListener((observable, oldValue, newValue) -> {
            if(newValue != null)
            {
                lyrics.applyCss();
                lyrics.layout();
                if(lyrics.lookup(".content") != null){
                    lyrics.lookup(".content").setStyle(
                            "-fx-background-color: transparent;"
                    );
                }
            }
        });
        lyrics.setMaxWidth(Double.MAX_VALUE);
        VBox.setVgrow(lyrics, Priority.ALWAYS);

        lyrics_vBox.getChildren().add(lyrics);

        VBox.setVgrow(lyrics_vBox, Priority.ALWAYS);
    }

     void setOptions_hbox(Consumer<MouseEvent> onAddClick, Consumer<MouseEvent> onShuffleClick, HBox options_hbox, VBox vbox, MediaType media)
    {
        Button shuffle_button;
        if(media == MediaType.Music){
            shuffle_button = setUpButton("src/main/resources/images/shuffle.png",22,c,c);
            shuffle_button.setOnMouseClicked(onShuffleClick::accept);
            options_hbox.getChildren().add(shuffle_button);
        }

        Button add_button = setUpButton("src/main/resources/images/add.png",22,c,c);
        add_button.setOnMouseClicked( onAddClick::accept);

        HBox hBox = new HBox();
        hBox.setAlignment(Pos.CENTER_RIGHT);

        TextField searchField = new TextField();
        searchField.setPromptText("Search...");
        searchField.setPrefWidth(0);
        searchField.setVisible(false);
        searchField.setManaged(false); // Don't take up layout space when hidden
        searchField.setStyle("-fx-background-color: transparent; -fx-border-color: white; -fx-border-radius:20; -fx-text-fill: white;");

        PauseTransition pause = new PauseTransition(Duration.millis(300));
        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            pause.setOnFinished(e -> {
                if(media == MediaType.Music){
                    performMusicSearch(newValue,vbox);
                }else{
                    performVideoSearch(newValue,vbox);
                }
            });
            pause.playFromStart(); // Rest the tie on every keystroke
        });

        Button search_button = setUpButton("src/main/resources/images/search.png",22,c,c);
        search_button.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                ToggleSearch(searchField);
            }
        });
         comboBox = sortingComboBox();

        Button sorting_button = setUpButton("src/main/resources/images/sort.png",22,c,c);
        sorting_button.setOnAction(event -> comboBox.show());

        hBox.getChildren().addAll(searchField,search_button,comboBox,sorting_button);

        HBox.setHgrow(hBox, Priority.ALWAYS);

        options_hbox.getChildren().addAll(add_button,hBox);
    }

    void setShuffleButton(Button shuffle_button){
        // --- shuffle active-state styling ---
        ImageView shuffleIV = (ImageView) shuffle_button.getGraphic();
        Color activeColor = Color.rgb(8, 171, 252); // matches your play_button blue, tweak as you like

        Runnable applyShuffleIdleStyle = () -> {
            if (MainController.randomOrder.get()) {
                shuffle_button.setStyle("-fx-background-color: #303030; -fx-background-radius: 20;");
                shuffleIV.setEffect(imageColorBlend(shuffleIV, activeColor, activeColor));
            } else {
                shuffle_button.setStyle("-fx-background-color: transparent; -fx-background-radius: 20;");
                shuffleIV.setEffect(imageColorBlend(shuffleIV, c, c));
            }
        };

        // override the generic hover handlers so "exit" respects active state
        shuffle_button.setOnMouseEntered(event -> {
            shuffle_button.setStyle("-fx-background-color: #303030; -fx-background-radius: 20;");
            shuffleIV.setEffect(imageColorBlend(shuffleIV, hoverColor, c));
        });
        shuffle_button.setOnMouseExited(event -> applyShuffleIdleStyle.run());

        applyShuffleIdleStyle.run(); // set correct style immediately on load
        MainController.randomOrder.addListener((obs, oldVal, newVal) -> applyShuffleIdleStyle.run());
        // --- end shuffle active-state styling ---
    }


    ComboBox<String> comboBox;

     ComboBox<String> sortingComboBox()
    {
        ComboBox<String> comboBox = new ComboBox<>();
        comboBox.getItems().addAll("Title","Album","Artist");
        comboBox.setValue("Title");

        Color bgColor = Color.rgb(24, 24, 24, 1.0);
        Background bg = new Background(new BackgroundFill(bgColor, new CornerRadii(0), Insets.EMPTY));
        comboBox.setBackground(bg);

        comboBox.valueProperty().addListener((obs, oldValue, newValue) -> {
            music_vbox.getChildren().clear();
            switch (newValue) {
                case "Title" -> musicFiles.sort(Comparator.comparing(Music::getTitle));
                case "Artist" -> musicFiles.sort(Comparator.comparing(Music::getArtist));
                case "Album" -> musicFiles.sort(Comparator.comparing(Music::getAlbum));
            }
            for(Music music1 : musicFiles)
                fillMusicList(music1);
        });

        // Apply same background to button cell (the selected item display)
        comboBox.setButtonCell(new ListCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? "" : item);
                setBackground(bg);
                setFont(Font.font(16));
                setTextFill(c);// same color
            }
        });

        // Apply same background to dropdown cells
        comboBox.setCellFactory(lv -> new ListCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? "" : item);
                setBackground(bg);
                setFont(Font.font(12));
                setTextFill(c);
            }
        });

        // Disable manual dropdown
        comboBox.addEventFilter(MouseEvent.MOUSE_PRESSED, Event::consume);
        return comboBox;
    }

    ArrayList<Music> checkComboBoxItem(String newValue, ArrayList<Music> files)
    {
        switch (newValue) {
            case "Title" -> files.sort(Comparator.comparing(Music::getTitle));
            case "Artist" -> files.sort(Comparator.comparing(Music::getArtist));
            case "Album" -> files.sort(Comparator.comparing(Music::getAlbum));
        }
        return files;
    }

     void setHeaderIV(File f, StackPane backdrop_SP)
    {

        ImageView header = new ImageView();
        header.setImage(new Image(f.toURI().toString()));
        header.setPreserveRatio(false);
        header.setFitWidth(950);
        header.setFitHeight(120);

        Rectangle clip = new Rectangle(950,120);
        clip.setArcHeight(20);
        clip.setArcWidth(20);
        header.setClip(clip);

        Rectangle gradientOverlay = new Rectangle(960,120);
        gradientOverlay.setFill(new LinearGradient(
                0,0,0,1, true, CycleMethod.NO_CYCLE,
                new Stop(0.7, Color.TRANSPARENT),
                new Stop(1.0, Color.BLACK)
        ));
        gradientOverlay.setOpacity(0.3);

        backdrop_SP.getChildren().addAll(header,gradientOverlay);
    }

    void ToggleSearch(TextField textField){
         boolean show = !textField.isVisible();
         textField.setManaged(show);
         textField.setVisible(show);

         if(show){
             textField.setPrefWidth(0);
             Timeline expand = new Timeline(
                     new KeyFrame(ANIM_DURATION,
                             new KeyValue(textField.prefWidthProperty(), OPEN_WIDTH))
             );
             expand.play();
             textField.requestFocus();
         }else{

             Timeline collapse = new Timeline(
                     new KeyFrame(ANIM_DURATION,
                             new KeyValue(textField.prefWidthProperty(), 0))
             );
             collapse.setOnFinished(e -> {
                 textField.setVisible(false);
                 textField.setManaged(false);
                 textField.clear();
             });
             collapse.play();
         }
    }

    void performMusicSearch(String value, VBox vBox){

        List<Music> filtered = MainController.music.stream()
                .filter(item -> item.getTitle().toLowerCase().contains(value.toLowerCase()))
                .toList();

        vBox.getChildren().clear();
        for(Music music : filtered)
            fillMusicList(music);

    }

    List<Video> performVideoSearch(String value, VBox vBox){

        List<Video> filtered = MainController.videos.stream()
                .filter(item -> item.getTitle().toLowerCase().contains(value.toLowerCase()))
                .toList();

        return filtered;

    }

    void changeProfilePic(File file, ImageView imageView, int radius){
        Image image = new Image(file.toURI().toString());
        imageView.setImage(image);

        imageView.setFitWidth(radius * 2);
        imageView.setFitHeight(radius * 2);
        imageView.setPreserveRatio(false); // ensures it fills a perfect circle

        Circle clip = new Circle(radius,radius,radius);
        imageView.setClip(clip);
    }

    public static String getProfilePicPath(File xmlFile) {
        try {
            if (!xmlFile.exists()) return null;

            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(xmlFile);

            NodeList list = doc.getElementsByTagName("profilePic");
            if (list.getLength() > 0) {
                return list.item(0).getTextContent();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


}
