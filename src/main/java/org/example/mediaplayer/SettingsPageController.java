package org.example.mediaplayer;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Cursor;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;

import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Circle;
import javafx.stage.FileChooser;
import javafx.scene.shape.Rectangle;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.awt.*;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.ResourceBundle;
import java.util.Stack;

public class SettingsPageController extends GlobalMethods implements Initializable {

  @FXML
  private Button change_pfpBtn,deleteDataBtn, saveChangesBtn;

  @FXML
  private ImageView profileIV;

  private File newProfilePic;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
    }

    public void initializeData(){
        setUpButtons();
        setProfilePic();
    }

    private void setProfilePic(){
        String path = getProfilePicPath(new File("UserData/userData.xml"));
        if(path!=null && !path.isEmpty()){
            File file = new File(path);
            changeProfilePic(file,profileIV,30);
        }
    }

    private void setUpButtons(){
        change_pfpBtn.setCursor(Cursor.HAND);
        deleteDataBtn.setCursor(Cursor.HAND);
        saveChangesBtn.setCursor(Cursor.HAND);
    }

    public void selectNewPfp()
    {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select new Profile Pic");

        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Image Files","*.jpg", "*.jpeg", "*.jpe","*.png"));

        File selectedFile = fileChooser.showOpenDialog(null);

        if(selectedFile!= null && selectedFile.exists()){
            File destFolder = new File("UserData");
            if(!destFolder.exists()){
                destFolder.mkdirs();
            }

                Path destinationPath = destFolder.toPath().resolve(selectedFile.getName());
                boolean replaced = Files.exists(destinationPath);

                try {
                    Files.copy(selectedFile.toPath(), destinationPath, StandardCopyOption.REPLACE_EXISTING);
                    File file = new File(destinationPath.toUri());

                    changeProfilePic(file,profileIV,30);
                    newProfilePic = new File(file.getAbsolutePath());

                } catch (IOException e) {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Error");
                    alert.setHeaderText("File Loading Failed");
                    alert.setContentText(e.getMessage());
                    alert.showAndWait();
                }
        }
    }

    public static void updateProfilePicPath(File xmlFile, String newPath) {
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();

            Document doc;
            if (xmlFile.exists()) {
                doc = builder.parse(xmlFile);
            } else {
                // create a fresh document with the expected structure if it doesn't exist yet
                doc = builder.newDocument();
                Element root = doc.createElement("UserData");
                doc.appendChild(root);
                root.appendChild(doc.createElement("profilePic"));
            }

            NodeList list = doc.getElementsByTagName("profilePic");
            Element profilePicElement;

            if (list.getLength() > 0) {
                profilePicElement = (Element) list.item(0);
            } else {
                // profilePic tag missing under an existing UserData root, create it
                profilePicElement = doc.createElement("profilePic");
                doc.getDocumentElement().appendChild(profilePicElement);
            }

            profilePicElement.setTextContent(newPath);

            // write back to file
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            transformer.setOutputProperty(javax.xml.transform.OutputKeys.INDENT, "yes");
            transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");

            DOMSource source = new DOMSource(doc);
            StreamResult result = new StreamResult(xmlFile);
            transformer.transform(source, result);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    MainController controller;

    public void setMainController(MainController controller){
        this.controller = controller;
    }

    public void saveChanges(){
        controller.updateProfilePic(newProfilePic);
        File xmlFile = new File("UserData/userData.xml");
        updateProfilePicPath(xmlFile,newProfilePic.getAbsolutePath());
    }






}
