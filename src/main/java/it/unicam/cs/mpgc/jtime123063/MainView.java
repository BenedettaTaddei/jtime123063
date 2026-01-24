package it.unicam.cs.mpgc.jtime123063;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.Objects;
import java.util.ResourceBundle;

public class MainView implements Initializable {

    @FXML private Label homeLabel;

    // I nostri nuovi motori di logica
    private JTimeManager jTimeManager;
    private ProjectManager projectManager;
    private TaskManager taskManager;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // 1. Inizializziamo i Repository con i nomi corretti presenti nel tuo progetto
        ProjectRepository projectRepo = new XmlSimpleProjectRepository();
        TaskRepository taskRepo = new XmlSimpleTaskRepository();

        // 2. Creiamo i Manager
        this.projectManager = new SimpleProjectManager(projectRepo);
        this.taskManager = new SimpleTaskManager(taskRepo);

        // 3. Il coordinatore che gestisce le relazioni
        this.jTimeManager = new JTimeManager(projectManager, taskManager);

        // 4. Aggiorniamo la label (aggiungi una parentesi chiusa alla fine se manca)
        homeLabel.setText("JTime - " + projectManager.getAllProjects().size());
    }

    // Metodo generico per cambiare scena passandogli il manager
    private void changeScene(ActionEvent event, String fxmlPath, String title) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
        Parent root = loader.load();

        // IMPORTANTE: Qui passiamo il manager al controller della nuova vista
        // Immaginiamo che il controller della nuova vista implementi un metodo setManager
        /* Object controller = loader.getController();
        if (controller instanceof BaseController) {
             ((BaseController) controller).setJTimeManager(this.jTimeManager);
        }
        */

        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setTitle(title);
        stage.setScene(new Scene(root));
    }

    @FXML
    private void goToProjectsView(ActionEvent event) throws IOException {
        changeScene(event, "/ProjectsView.fxml", "Gestione Progetti");
    }

    @FXML
    private void goToTasksView(ActionEvent event) throws IOException {
        changeScene(event, "/TasksView.fxml", "Lista Attività");
    }

    @FXML
    public void goToReportView(ActionEvent event) throws IOException {
        changeScene(event, "/ReportView.fxml", "Statistiche e Report");
    }
}