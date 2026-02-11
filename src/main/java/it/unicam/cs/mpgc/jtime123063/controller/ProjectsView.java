package it.unicam.cs.mpgc.jtime123063.controller;

import it.unicam.cs.mpgc.jtime123063.manager.*;
import it.unicam.cs.mpgc.jtime123063.model.Project;
import it.unicam.cs.mpgc.jtime123063.model.SimpleProject;
import it.unicam.cs.mpgc.jtime123063.repository.XmlSimpleProjectRepository;
import it.unicam.cs.mpgc.jtime123063.repository.XmlSimpleTaskRepository;
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
 * Controller for managing Projects views and interactions.
 */
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

    /**
     * Initializes the controller, managers, and UI components.
     * @param location  The FXML location.
     * @param resources The resource bundle.
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        initManagers();
        configureTableColumns();
        setupInitialState();
        refreshTable();
    }

    /**
     * Initializes the business logic managers with XML repositories.
     */
    private void initManagers() {
        ProjectManager pMan = new SimpleProjectManager(new XmlSimpleProjectRepository());
        TaskManager tMan = new SimpleTaskManager(new XmlSimpleTaskRepository());
        this.jTimeManager = new JTimeManager(pMan, tMan);
    }

    /**
     * Configures the table columns and their data binding logic.
     */
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

    /**
     * Sets up the interactive delete column with custom cell rendering.
     */
    private void setupDeleteColumn() {
        deleteColumn.setCellFactory(param -> new DeleteProjectCell(jTimeManager, this::refreshTable));
    }

    /**
     * Configures the initial state of UI elements.
     */
    private void setupInitialState() {
        setupComboBoxes();
    }

    /**
     * Populates the status filter combo box.
     */
    private void setupComboBoxes() {
        statusFilter.getItems().setAll("Tutti", "In Corso", "Completati");
        if (statusFilter.getSelectionModel().isEmpty()) {
            statusFilter.getSelectionModel().selectFirst();
        }
    }

    /**
     * Handles the creation and saving of a new project.
     */
    @FXML
    private void handleAddProject() {
        if (!validateInput()) return;
        String name = projectNameField.getText().trim();
        try {
            Project newProject = new SimpleProject(name);
            jTimeManager.saveProject(newProject);

            handleClearForm();
            showStatus("Project created successfully!", false);
            refreshTable();
        } catch (Exception e) {
            showStatus("Error during save operation!", true);
        }
    }

    /**
     * Validates that the project input fields are correctly filled.
     * @return true if input is valid, false otherwise.
     */
    private boolean validateInput() {
        if (projectNameField.getText().trim().isEmpty()) {
            showStatus("Error: Project name is mandatory.", true);
            return false;
        }
        return true;
    }

    /**
     * Clears the project input fields.
     */
    @FXML
    private void handleClearForm() {
        projectNameField.clear();
    }

    /**
     * Filters the projects table based on search query and status.
     */
    @FXML
    private void applyFilters() {
        String query = searchBar.getText().toLowerCase();
        String status = statusFilter.getValue();

        List<Project> allProjects = jTimeManager.getAllProjects();
        jTimeManager.syncData(allProjects);

        List<Project> filtered = allProjects.stream()
                .filter(p -> p.getName().toLowerCase().contains(query))
                .filter(p -> {
                    if ("Completati".equals(status)) return p.getCompletionPercentage() == 100.0;
                    if ("In Corso".equals(status)) return p.getCompletionPercentage() < 100.0;
                    return true;
                })
                .collect(Collectors.toList());

        projectsTable.setItems(FXCollections.observableArrayList(filtered));
    }

    /**
     * Resets search filters and reloads all projects.
     */
    @FXML
    private void resetFilters() {
        searchBar.clear();
        statusFilter.getSelectionModel().selectFirst();
        refreshTable();
    }

    /**
     * Refreshes the projects table by fetching synced data from the manager.
     */
    private void refreshTable() {
        List<Project> projects = jTimeManager.getAllProjects();
        jTimeManager.syncData(projects);
        projectsTable.setItems(FXCollections.observableArrayList(projects));
    }

    /**
     * Displays a status message with specific styling based on error state.
     * @param msg The message to display.
     * @param isError True if the message represents an error.
     */
    private void showStatus(String msg, boolean isError) {
        messageLabel.setText(msg);
        messageLabel.setStyle(isError ? "-fx-text-fill: #e74c3c;" : "-fx-text-fill: #2ecc71;");
        javafx.animation.PauseTransition pause = new javafx.animation.PauseTransition(javafx.util.Duration.seconds(3));
        pause.setOnFinished(e -> messageLabel.setText(""));
        pause.play();
    }



    /**
     * Inner class representing a custom table cell with a delete button for projects.
     */
    private class DeleteProjectCell extends TableCell<Project, Void> {
        private final Button btnDelete = new Button("🗑");

        public DeleteProjectCell(JTimeManager manager, Runnable onRefresh) {
            btnDelete.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white; -fx-cursor: hand;");
            btnDelete.setOnAction(event -> {
                Project p = getTableRow().getItem();
                if (p != null) {
                    Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Delete " + p.getName() + "?", ButtonType.YES, ButtonType.NO);
                    alert.showAndWait().ifPresent(response -> {
                        if (response == ButtonType.YES) {
                            manager.removeProjectPermanently(p);
                            onRefresh.run();
                        }
                    });
                }
            });
        }

        /**
         * Updates the cell graphic based on row content.
         */
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
    }
    /**
     * Navigates back to the main dashboard view.
     * @param event The action event.
     */
    @FXML
    public void goToMainView(ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/MainView.fxml")));
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}