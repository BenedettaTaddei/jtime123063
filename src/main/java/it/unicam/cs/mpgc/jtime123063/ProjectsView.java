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

/**
 * Controller per la gestione dei Progetti.
 * Utilizza esclusivamente JTimeManager come Facade per l'accesso ai dati sincronizzati.
 */
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
    @FXML private TableColumn<Project, Void> deleteColumn;

    @FXML private ComboBox<String> statusFilter;
    @FXML private TextField searchBar;

    private JTimeManager jTimeManager;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        initManagers();
        configureTableColumns();
        setupFilters();
        refreshTable();
    }

    private void initManagers() {
        // I manager di basso livello sono creati localmente e passati al Facade
        ProjectManager pMan = new SimpleProjectManager(new XmlSimpleProjectRepository());
        TaskManager tMan = new SimpleTaskManager(new XmlSimpleTaskRepository());
        this.jTimeManager = new JTimeManager(pMan, tMan);
    }

    private void configureTableColumns() {
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        taskCountColumn.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().
                getTasks().size()).asObject());
        totalEstimatedColumn.setCellValueFactory(cellData -> new SimpleDoubleProperty(cellData.getValue().
                getTotalEstimatedTime()).asObject());
        totalEffectiveColumn.setCellValueFactory(cellData -> new SimpleDoubleProperty(cellData.getValue().
                getTotalEffectiveTime()).asObject());
        completionColumn.setCellValueFactory(cellData -> {
            double percent = cellData.getValue().getCompletionPercentage();
            return new SimpleStringProperty(String.format("%.1f%%", percent));
        });
        setupDeleteColumn();
    }

    private void setupDeleteColumn() {
        deleteColumn.setMinWidth(80);
        deleteColumn.setResizable(false);
        deleteColumn.setCellFactory(param -> new TableCell<>() {
            private final Button btnDelete = new Button("🗑");
            {
                btnDelete.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white; -fx-cursor: hand;");
                btnDelete.setOnAction(event -> {
                    Project project =  getTableRow().getItem();
                    if (project != null) {
                        // Alert di conferma
                        Alert alert = new Alert(Alert.AlertType.CONFIRMATION,
                                "Eliminare il progetto '" + project.getName() + "'? I task associati rimarranno come task libere.",
                                ButtonType.YES, ButtonType.NO);

                        alert.showAndWait().ifPresent(response -> {
                            if (response == ButtonType.YES) {
                                jTimeManager.removeProjectPermanently(project);
                                refreshTable();
                                showStatus("Progetto eliminato.", false);
                            }
                        });
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

    private void setupFilters() {
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

        // Creazione di un progetto (operazione semplice, gestita tramite il Facade se necessario)
        // Qui usiamo il costruttore del progetto e lo salviamo tramite jTimeManager
        Project newProject = new SimpleProject(name);
        // Nota: se non hai jTimeManager.saveProject, puoi aggiungerlo come metodo ponte
        jTimeManager.saveProject(newProject);

        showStatus("Progetto creato con successo!", false);
        projectNameField.clear();
        descriptionField.clear();
        refreshTable();
    }

    @FXML
    private void applyFilters() {
        String query = searchBar.getText().toLowerCase();
        String status = statusFilter.getValue();

        // Chiediamo i dati freschi e sincronizzati al Facade
        jTimeManager.getAllSyncedTasks(); // Questa chiamata popola internamente i progetti
        List<Project> allProjects = jTimeManager.getAllProjects();

        List<Project> filtered = allProjects.stream()
                .filter(p -> p.getName().toLowerCase().contains(query))
                .filter(p -> {
                    if ("Completati".equals(status)) return p.getCompletionPercentage() == 100.0;
                    if ("In Corso".equals(status)) return p.getCompletionPercentage() < 100.0;
                    return true;
                })
                .collect(Collectors.toList());

        projectsTable.setItems(FXCollections.observableArrayList(filtered));
        projectsTable.refresh();
    }

    @FXML
    private void resetFilters() {
        searchBar.clear();
        statusFilter.getSelectionModel().selectFirst();
        refreshTable();
    }

    private void refreshTable() {
        // 1. Chiediamo la lista dei progetti al Facade
        List<Project> projects = jTimeManager.getAllProjects();

        // 2. Chiediamo al Facade di sincronizzare tutto internamente.
        // Passiamo la lista dei progetti così il manager può riempirli con i task corretti.
        jTimeManager.syncData(projects);

        // 3. Ora visualizziamo i progetti (che il manager ha appena popolato)
        projectsTable.setItems(FXCollections.observableArrayList(projects));
        projectsTable.refresh();
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