package it.unicam.cs.mpgc.jtime123063;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import java.io.IOException;
import java.util.Optional;

public class ProjectsView {

    @FXML private TableView<Project> projectsTable;
    @FXML private TableColumn<Project, String> nameColumn;

    private JTimeManager jTimeManager;
    private ProjectManager projectManager;

    // Metodo per ricevere i manager dal MainView
    public void setManagers(JTimeManager jTimeManager, ProjectManager projectManager) {
        this.jTimeManager = jTimeManager;
        this.projectManager = projectManager;
        loadData();
    }

    private void loadData() {
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        projectsTable.setItems(FXCollections.observableArrayList(projectManager.getAllProjects()));
    }

    @FXML
    private void handleAddProject() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Nuovo Progetto");
        dialog.setHeaderText("Crea un nuovo progetto di lavoro");
        dialog.setContentText("Nome del progetto:");

        Optional<String> result = dialog.showAndWait();
        result.ifPresent(name -> {
            Project newProject = new SimpleProject(name); // Assicurati di avere questo costruttore
            projectManager.saveProject(newProject);
            loadData();
        });
    }

    @FXML
    private void handleDeleteProject() {
        Project selected = projectsTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Conferma Eliminazione");
            alert.setHeaderText("Stai per eliminare: " + selected.getName());
            alert.setContentText("Questa azione eliminerà anche tutte le task collegate. Procedere?");

            if (alert.showAndWait().get() == ButtonType.OK) {
                // USIAMO IL COORDINATORE PER L'ELIMINAZIONE A CASCATA
                jTimeManager.deleteProjectWithAllTasks(selected);
                loadData();
            }
        }
    }

    @FXML
    private void handleBack(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("/MainView.fxml"));
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(new Scene(root));
    }
}