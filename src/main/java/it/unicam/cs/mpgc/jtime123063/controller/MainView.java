package it.unicam.cs.mpgc.jtime123063.controller;

import it.unicam.cs.mpgc.jtime123063.manager.*;
import it.unicam.cs.mpgc.jtime123063.repository.ProjectRepository;
import it.unicam.cs.mpgc.jtime123063.repository.TaskRepository;
import it.unicam.cs.mpgc.jtime123063.repository.XmlSimpleProjectRepository;
import it.unicam.cs.mpgc.jtime123063.repository.XmlSimpleTaskRepository;
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
import java.util.ResourceBundle;

public class MainView implements Initializable {

    @FXML private Label homeLabel;

    private JTimeManager jTimeManager;
    private ProjectManager projectManager;
    private TaskManager taskManager;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        ProjectRepository projectRepo = new XmlSimpleProjectRepository();
        TaskRepository taskRepo = new XmlSimpleTaskRepository();

        this.projectManager = new SimpleProjectManager(projectRepo);
        this.taskManager = new SimpleTaskManager(taskRepo);

        this.jTimeManager = new JTimeManager(projectManager, taskManager);

        homeLabel.setText("JTime");
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
    public void goToReportsView(ActionEvent event) throws IOException {
        changeScene(event, "/ReportsView.fxml", "Statistiche e Report");
    }
}