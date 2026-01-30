package it.unicam.cs.mpgc.jtime123063;

import javafx.animation.PauseTransition;
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
import javafx.util.Duration;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.Objects;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class ProjectsView implements Initializable {

    @FXML private TextField projectNameField;
    @FXML private Label messageLabel;

    @FXML private TableView<Project> projectsTable;
    @FXML private TableColumn<Project, String> nameColumn;
    @FXML private TableColumn<Project, Integer> taskCountColumn;
    @FXML private TableColumn<Project, Double> totalEstimatedColumn;
    @FXML private TableColumn<Project, Double> totalEffectiveColumn;
    @FXML private TableColumn<Project, String> completionColumn;
    @FXML private TableColumn<Project, Void> deleteColumn;

    @FXML private ComboBox<String> statusFilter;
    @FXML private TextField searchBar;

    private JTimeManager jTimeManager;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        initManagers();
        configureTableColumns();
        setupInitialState();

        // Ricerca in tempo reale: filtra mentre l'utente scrive
        searchBar.textProperty().addListener((obs, oldVal, newVal) -> applyFilters());

        refreshTable();
    }

    private void initManagers() {
        ProjectManager pMan = new SimpleProjectManager(new XmlSimpleProjectRepository());
        TaskManager tMan = new SimpleTaskManager(new XmlSimpleTaskRepository());
        this.jTimeManager = new JTimeManager(pMan, tMan);
    }

    private void configureTableColumns() {
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));

        taskCountColumn.setCellValueFactory(cellData ->
                new SimpleIntegerProperty(cellData.getValue().getTasks().size()).asObject());

        totalEstimatedColumn.setCellValueFactory(cellData ->
                new SimpleDoubleProperty(cellData.getValue().getTotalEstimatedTime()).asObject());

        totalEffectiveColumn.setCellValueFactory(cellData ->
                new SimpleDoubleProperty(cellData.getValue().getTotalEffectiveTime()).asObject());

        completionColumn.setCellValueFactory(cellData -> {
            double percent = cellData.getValue().getCompletionPercentage();
            return new SimpleStringProperty(String.format("%.1f%%", percent));
        });

        setupDeleteColumn();
    }

    private void setupDeleteColumn() {
        deleteColumn.setCellFactory(param -> new TableCell<>() {
            private final Button btnDelete = new Button("🗑");
            {
                btnDelete.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white; -fx-cursor: hand;");
                btnDelete.setOnAction(event -> {
                    Project project = getTableRow().getItem();
                    if (project != null) {
                        handleDeleteConfirmation(project);
                    }
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || getTableRow() == null || getTableRow().getItem() == null) {
                    setGraphic(null);
                } else {
                    setGraphic(btnDelete);
                    setStyle("-fx-alignment: CENTER;");
                }
            }
        });
    }

    private void handleDeleteConfirmation(Project project) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION,
                "Eliminare il progetto '" + project.getName() + "'? I task associati rimarranno liberi.",
                ButtonType.YES, ButtonType.NO);
        alert.setHeaderText(null);
        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.YES) {
                jTimeManager.removeProjectPermanently(project);
                refreshTable();
                showStatus("Progetto eliminato.", false);
            }
        });
    }

    private void setupInitialState() {
        statusFilter.getItems().setAll("Tutti", "In Corso", "Completati");
        statusFilter.getSelectionModel().selectFirst();
    }

    @FXML
    private void handleAddProject() {
        String name = projectNameField.getText().trim();
        if (name.isEmpty()) {
            showStatus("Il nome è obbligatorio", true);
            return;
        }

        jTimeManager.saveProject(new SimpleProject(name));
        handleClearForm();
        showStatus("Progetto creato con successo!", false);
        refreshTable();
    }

    private void handleClearForm() {
        projectNameField.clear();
    }

    @FXML
    public void applyFilters() {
        String query = searchBar.getText().toLowerCase().trim();
        String status = statusFilter.getValue();

        // Forza ricaricamento dati sincronizzati
        jTimeManager.getAllSyncedTasks();
        List<Project> projects = jTimeManager.getAllProjects();

        List<Project> filtered = projects.stream()
                .filter(p -> p.getName().toLowerCase().contains(query))
                .filter(p -> {
                    if ("Completati".equals(status)) return p.getCompletionPercentage() >= 100.0;
                    if ("In Corso".equals(status)) return p.getCompletionPercentage() < 100.0;
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
        jTimeManager.syncData(jTimeManager.getAllProjects());
        applyFilters();
    }

    private void showStatus(String msg, boolean isError) {
        messageLabel.setText(msg);
        messageLabel.setStyle(isError ? "-fx-text-fill: #e74c3c;" : "-fx-text-fill: #2ecc71;");

        PauseTransition pause = new PauseTransition(Duration.seconds(3));
        pause.setOnFinished(e -> messageLabel.setText(""));
        pause.play();
    }

    @FXML
    public void goToMainView(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/MainView.fxml")));
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(new Scene(root));
    }
}