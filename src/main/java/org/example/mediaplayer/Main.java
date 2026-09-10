package org.example.mediaplayer;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public class Main extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("main_menu.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 1440, 800);
        stage.setTitle("Media Player");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }


    @Override
    public void stop()
    {
       initFavoritesXML();
       clearJson();
       initJsonPlaylist();
    }

    private void initFavoritesXML()
    {
        ArrayList<Music> favorites = MainController.favorites;
        File xmlFile = new File("src/main/resources/favorites.xml");
        try{
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();

            Document doc = dBuilder.parse(xmlFile);
            doc.getDocumentElement().normalize();


            //Get root <favorites>
            Element root = doc.getDocumentElement();

            //Remove all old elements
            while (root.hasChildNodes()){
                root.removeChild(root.getFirstChild());
            }

            //Add new <favorite> elements
            for(Music music : favorites)
            {
                Element favorite = doc.createElement("favorite");
                favorite.setTextContent(music.getFile().getPath());
                root.appendChild(favorite);
            }

            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT,"yes");
            transformer.transform(new DOMSource(doc), new StreamResult(xmlFile));

        }catch (Exception e){
            e.printStackTrace();
        }
    }


    private void initJsonPlaylist()
    {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        ArrayList<PlaylistJson> playlistJsons = new ArrayList<>();

        for(Playlist<Music> playlist : MainController.playlists)
        {
            ArrayList<String> paths = new ArrayList<>();
            for(MediaItem media : playlist.getMedia())
            {
                paths.add(media.getFile().getPath());
            }

            PlaylistJson playlistJson = new PlaylistJson(playlist.getName(),playlist.getImage().getUrl(), paths);
            playlistJsons.add(playlistJson);
        }

            try(FileWriter writer = new FileWriter("src/main/resources/playlists.json"))
            {
                gson.toJson(playlistJsons,writer);

            }catch (IOException e)
            {
                e.printStackTrace();
            }


    }

    private void clearJson()
    {
        String path = "src/main/resources/playlists.json";

        try (FileWriter writer = new FileWriter(path, false)) { // 'false' = overwrite mode
            System.out.println("JSON file cleared!");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}