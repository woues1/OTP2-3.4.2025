package org.example;

import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class Controller {

    private static final String DB_URL = "jdbc:mariadb://localhost:3306/translate";
    private static final String user = "root";
    private static final String password = "root";


    @FXML private ComboBox <String> languageComboBox;
    @FXML private Label welcomeText;
    @FXML private ListView<String> listView;
    @FXML private Button insertButton;
    @FXML private TextField keyNameField;
    @FXML TextField translationTextField;

    @FXML
    private void initialize() {
        welcomeText.setText("OTP2 inclass example");
        fetchData("en");
        languageComboBox.getItems().addAll("English", "日本語で", "Français", "Español");
        languageComboBox.setOnAction(event -> {
            String selectedLanguage = languageComboBox.getValue();
            changelanguage();
        });
        insertButton.setOnAction(event -> {
            String keyName = keyNameField.getText();
            String translationText = translationTextField.getText();
            insertData(keyName, translationText, getLanguageCode());
            fetchData(languageComboBox.getValue());
        });
    }

    private void insertData(String keyName, String translationText, String languageCode){
        try (Connection conn = DriverManager.getConnection(DB_URL, user, password)) {
            String query = "INSERT INTO translations (key_name, translation_text, language_code) VALUES (?, ?, ?)";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, keyName);
            stmt.setString(2, translationText);
            stmt.setString(3, languageCode);
            stmt.executeQuery();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String fetchData(String language) {

        try (Connection conn = DriverManager.getConnection(DB_URL, user, password)) {
            String query = "SELECT key_name, translation_text FROM translations WHERE language_code = ?";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, language);
            ResultSet rs = stmt.executeQuery();

            listView.getItems().clear();
            while (rs.next()) {
                String keyName = rs.getString("key_name");
                String translationText = rs.getString("translation_text");
                listView.getItems().add(keyName + ": " + translationText);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    private void changelanguage(){
        String selectedLanguage = languageComboBox.getValue();
        switch (selectedLanguage) {
            case "English":
                fetchData("en");
                break;
            case "日本語で":
                fetchData("ja");
                break;
            case "Français":
                fetchData("fr");
                break;
            case "Español":
                fetchData("es");
                break;
            default:
                fetchData("en");
        }
    }

    private String getLanguageCode(){
        String selectedLanguage = languageComboBox.getValue();
        switch (selectedLanguage){
            case "English":
                return "en";
            case "日本語で":
                return "ja";
            case "Español":
                return "es";
            case "Français":
                return "fr";
            default:
                return "en";

        }
    }
}