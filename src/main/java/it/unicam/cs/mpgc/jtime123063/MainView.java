package it.unicam.cs.mpgc.jtime123063;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.Button;
import java.net.URL;
import java.util.ResourceBundle;

public class MainView implements Initializable {

    @FXML
    private Label homeLabel;

    @FXML
    private Button testButton;

    /**
     * Inizializzazione automatica quando viene caricato l'FXML
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        homeLabel.setText("Home");
    }
}