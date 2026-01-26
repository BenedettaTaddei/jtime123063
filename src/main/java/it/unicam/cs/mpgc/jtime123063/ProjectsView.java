package it.unicam.cs.mpgc.jtime123063;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.Objects;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class ProjectsView implements Initializable {

    @FXML private TextField projectNameField;
    @FXML private TextArea descriptionField;
    @FXML private Label messageLabel;

    @FXML private TableView<Project> projectsTable;
    @FXML private TableColumn<Project, String> nameColumn;
    @FXML private TableColumn<Project, Integer> taskCountColumn;
    @FXML private TableColumn<Project, Double> totalEstimatedColumn;
    @FXML private TableColumn<Project, Double> totalEffectiveColumn;
    @FXML private TableColumn<Project, String> completionColumn;

    @FXML private ComboBox<String> statusFilter;
    @FXML private TextField searchBar;

    private ProjectManager projectManager;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        ProjectRepository pRepo = new XmlSimpleProjectRepository();
        this.projectManager = new SimpleProjectManager(pRepo);

        setupTableColumns();
        setupFilters();
        refreshTable();
    }

    private void setupTableColumns() {
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));

        // Calcolo dinamico del numero di task
        taskCountColumn.setCellValueFactory(cellData ->
                new SimpleIntegerProperty(cellData.getValue().getTasks().size()).asObject());

        // Calcolo dinamico del tempo totale stimato
        totalEstimatedColumn.setCellValueFactory(cellData ->
                new SimpleDoubleProperty(cellData.getValue().getTotalEstimatedTime()).asObject());

        // Calcolo dinamico del tempo totale effettivo
        totalEffectiveColumn.setCellValueFactory(cellData ->
                new SimpleDoubleProperty(cellData.getValue().getTotalEffectiveTime()).asObject());

        // Colonna % Avanzamento (Elegante)
        completionColumn.setCellValueFactory(cellData -> {
            double percent = cellData.getValue().getCompletionPercentage();
            return new SimpleStringProperty(String.format("%.1f%%", percent));
        });
    }

    private void setupFilters() {
        statusFilter.getItems().addAll("Tutti", "In Corso", "Completati");
        statusFilter.getSelectionModel().selectFirst();
    }

    @FXML
    private void handleAddProject() {
        String name = projectNameField.getText().trim();
        if (name.isEmpty()) {
            showStatus("Il nome è obbligatorio", true);
            return;
        }

        // Creazione e salvataggio
        Project newProject = new SimpleProject(name); // Assicurati di avere questo costruttore
        projectManager.saveProject(newProject);

        showStatus("Progetto creato con successo!", false);
        projectNameField.clear();
        descriptionField.clear();
        refreshTable();
    }

    @FXML
    private void applyFilters() {
        String query = searchBar.getText().toLowerCase();
        String status = statusFilter.getValue();

        List<Project> filtered = projectManager.getAllProjects().stream()
                .filter(p -> p.getName().toLowerCase().contains(query))
                .filter(p -> {
                    if (status.equals("Completati")) return p.isCompleted();
                    if (status.equals("In Corso")) return !p.isCompleted();
                    return true;
                })
                .collect(Collectors.toList());

        projectsTable.setItems(FXCollections.observableArrayList(filtered));
    }

    @FXML
    private void resetFilters() {
        searchBar.clear();
        statusFilter.getSelectionModel().selectFirst();
        refreshTable();
    }

    private void refreshTable() {
        projectsTable.setItems(FXCollections.observableArrayList(projectManager.getAllProjects()));
    }

    private void showStatus(String msg, boolean isError) {
        messageLabel.setText(msg);
        messageLabel.setStyle(isError ? "-fx-text-fill: #e74c3c;" : "-fx-text-fill: #2ecc71;");
    }

    @FXML
    public void goToMainView(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/MainView.fxml")));
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(new Scene(root));
    }
}