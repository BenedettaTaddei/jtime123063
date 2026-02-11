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

/**
 * Controller class for the main application dashboard.
 */
public class MainView implements Initializable {

    @FXML
    private Label homeLabel;

    private JTimeManager jTimeManager;
    private ProjectManager projectManager;
    private TaskManager taskManager;

    /**
     * Initializes the application managers and UI components.
     * @param location  The FXML location.
     * @param resources The resource bundle.
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        ProjectRepository projectRepo = new XmlSimpleProjectRepository();
        TaskRepository taskRepo = new XmlSimpleTaskRepository();

        this.projectManager = new SimpleProjectManager(projectRepo);
        this.taskManager = new SimpleTaskManager(taskRepo);
        this.jTimeManager = new JTimeManager(projectManager, taskManager);

        homeLabel.setText("JTime");
    }

    /**
     * Centralized method to load a new FXML view and switch the current stage.
     * @param event    The action event that triggered the navigation.
     * @param fxmlPath The path to the FXML resource file.
     * @param title    The title to be displayed on the new stage.
     */
    private void changeScene(ActionEvent event, String fxmlPath, String title) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent root = loader.load();

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setTitle(title);
            stage.setScene(new Scene(root));
        } catch (IOException e) {
            System.err.println("Error loading: " + fxmlPath);
            e.printStackTrace();
        }
    }

    /**
     * Navigates to the Projects management view.
     * @param event The action event.
     * @throws IOException If the FXML file cannot be found.
     */
    @FXML
    private void goToProjectsView(ActionEvent event) throws IOException {
        changeScene(event, "/ProjectsView.fxml", "Project Management");
    }

    /**
     * Navigates to the Tasks management view.
     * @param event The action event.
     * @throws IOException If the FXML file cannot be found.
     */
    @FXML
    private void goToTasksView(ActionEvent event) throws IOException {
        changeScene(event, "/TasksView.fxml", "Task Management");
    }

    /**
     * Navigates to the Reports and statistics view.
     * @param event The action event.
     * @throws IOException If the FXML file cannot be found.
     */
    @FXML
    public void goToReportsView(ActionEvent event) throws IOException {
        changeScene(event, "/ReportsView.fxml", "Reports & Statistics");
    }
}