package it.unicam.cs.mpgc.jtime123063.controller;

import it.unicam.cs.mpgc.jtime123063.manager.*;
import it.unicam.cs.mpgc.jtime123063.model.Project;
import it.unicam.cs.mpgc.jtime123063.model.SimpleTask;
import it.unicam.cs.mpgc.jtime123063.model.Task;
import it.unicam.cs.mpgc.jtime123063.repository.XmlSimpleProjectRepository;
import it.unicam.cs.mpgc.jtime123063.repository.XmlSimpleTaskRepository;
import javafx.beans.property.SimpleStringProperty;
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
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.stage.Stage;
import javafx.util.converter.DoubleStringConverter;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

/**
 * Controller for managing Tasks views and interactions.
 */
public class TasksView implements Initializable {

    @FXML private TextField taskNameField, durationField;
    @FXML private ComboBox<Project> projectComboBox;
    @FXML private ComboBox<String> projectFilter;
    @FXML private DatePicker dueDatePicker, startDatePicker, endDatePicker;
    @FXML private Label messageLabel;

    @FXML private TableView<Task> tasksTable;
    @FXML private TableColumn<Task, String> nameColumn, parentProject;
    @FXML private TableColumn<Task, Double> estimatedTime, effectiveTime;
    @FXML private TableColumn<Task, Boolean> statusColumn;
    @FXML private TableColumn<Task, LocalDate> scheduledDate;
    @FXML private TableColumn<Task, Void> deleteColumn;

    private JTimeManager jTimeManager;

    /**
     * Initializes the controller, managers, and table configurations.
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
     * Sets up the business logic managers.
     */
    private void initManagers() {
        ProjectManager projectManager = new SimpleProjectManager(new XmlSimpleProjectRepository());
        TaskManager taskManager = new SimpleTaskManager(new XmlSimpleTaskRepository());
        this.jTimeManager = new JTimeManager(projectManager, taskManager);
    }

    /**
     * Configures table columns and cell factories.
     */
    private void configureTableColumns() {
        tasksTable.setEditable(true);
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("title"));
        estimatedTime.setCellValueFactory(new PropertyValueFactory<>("estimatedTime"));
        scheduledDate.setCellValueFactory(new PropertyValueFactory<>("scheduledDate"));
        parentProject.setCellValueFactory(cellData -> {
            Project p = cellData.getValue().getParentProject();
            return new SimpleStringProperty(p != null ? p.getName() : "Unassigned Task");
        });
        setupEffectiveTimeColumn();
        setupStatusColumn();
        setupDeleteColumn();
    }

    /**
     * Configures the effective time column with inline editing.
     */
    private void setupEffectiveTimeColumn() {
        effectiveTime.setCellValueFactory(new PropertyValueFactory<>("effectiveTime"));
        effectiveTime.setCellFactory(column -> new TextFieldTableCell<>(new DoubleStringConverter()) {
            @Override
            public void updateItem(Double item, boolean empty) {
                super.updateItem(item, empty);
                if (!empty && getTableRow() != null && getTableRow().getItem() != null) {
                    Task task = getTableRow().getItem();
                    setEditable(task.isCompleted());
                    setStyle(task.isCompleted() ? "-fx-opacity: 1;" : "-fx-opacity: 0.5;");
                    setStyle(getStyle() + "-fx-alignment: CENTER;");
                }
            }
        });

        effectiveTime.setOnEditCommit(event -> {
            Task task = event.getRowValue();
            if (task != null && task.isCompleted()) {
                jTimeManager.updateTaskEffectiveTime(task, event.getNewValue());
                showStatus("Time updated.", false);
            } else {
                refreshTable();
                showStatus("Complete the task before adding hours!", true);
            }
        });
    }

    /**
     * Sets up the status column with interactive checkboxes.
     */
    private void setupStatusColumn() {
        statusColumn.setCellFactory(param -> new StatusCell());
    }

    /**
     * Sets up the delete column with custom buttons.
     */
    private void setupDeleteColumn() {
        deleteColumn.setCellFactory(param -> new DeleteTaskCell());
    }

    /**
     * Configures the initial UI state and project selectors.
     */
    private void setupInitialState() {
        dueDatePicker.setValue(LocalDate.now());
        setupComboBoxes();
    }

    /**
     * Populates project-related combo boxes.
     */
    private void setupComboBoxes() {
        List<Project> allProjects = jTimeManager.getAllProjects();
        projectComboBox.setItems(FXCollections.observableArrayList(allProjects));

        projectFilter.getItems().clear();
        projectFilter.getItems().add("All");
        projectFilter.getItems().addAll(allProjects.stream()
                .map(Project::getName)
                .collect(Collectors.toList()));

        if (projectFilter.getSelectionModel().isEmpty()) {
            projectFilter.getSelectionModel().selectFirst();
        }
    }

    /**
     * Handles the creation of a new task.
     */
    @FXML
    private void handleAddTask() {
        try {
            if (!validateInput()) return;
            Task newTask = new SimpleTask(taskNameField.getText().trim(),
                    Double.parseDouble(durationField.getText().replace(",", ".")));
            newTask.setScheduledDate(dueDatePicker.getValue());
            jTimeManager.addNewTaskToProject(projectComboBox.getValue(), newTask);
            handleClearForm();
            showStatus("Task saved!", false);
            refreshTable();
        } catch (NumberFormatException e) {
            showStatus("Error: Invalid duration.", true);
        } catch (Exception e) {
            showStatus("Error during save operation.", true);
        }
    }

    /**
     * Validates that the task input fields are correctly filled.
     * @return true if valid, false otherwise.
     */
    private boolean validateInput() {
        if (taskNameField.getText().trim().isEmpty() || durationField.getText().trim().isEmpty() || dueDatePicker.getValue() == null) {
            showStatus("Error: Required fields missing.", true);
            return false;
        }
        return true;
    }

    /**
     * Clears the task input form.
     */
    @FXML
    private void handleClearForm() {
        taskNameField.clear();
        durationField.clear();
        dueDatePicker.setValue(LocalDate.now());
        projectComboBox.getSelectionModel().clearSelection();
    }

    /**
     * Filters the tasks table based on project and date range.
     */
    @FXML
    private void applyFilters() {
        List<Task> allTasks = jTimeManager.getAllSyncedTasks();
        String filterProject = projectFilter.getValue();
        LocalDate start = startDatePicker.getValue();
        LocalDate end = endDatePicker.getValue();

        List<Task> filtered = allTasks.stream()
                .filter(t -> filterProject == null || filterProject.equals("All") ||
                        (t.getParentProject() != null && t.getParentProject().getName().equals(filterProject)))
                .filter(t -> {
                    LocalDate d = t.getScheduledDate();
                    if (d == null) return (start == null && end == null);
                    return (start == null || !d.isBefore(start)) && (end == null || !d.isAfter(end));
                })
                .collect(Collectors.toList());

        tasksTable.setItems(FXCollections.observableArrayList(filtered));
    }

    /**
     * Resets filters and reloads the full task list.
     */
    @FXML private void resetFilters() {
        startDatePicker.setValue(null);
        endDatePicker.setValue(null);
        projectFilter.getSelectionModel().selectFirst();
        refreshTable();
    }

    /**
     * Synchronizes projects and refreshes the task table.
     */
    private void refreshTable() {
        setupComboBoxes();
        applyFilters();
    }

    /**
     * Displays a temporary status message to the user.
     * @param msg     The message text.
     * @param isError True if the message is an error.
     */
    private void showStatus(String msg, boolean isError) {
        messageLabel.setText(msg);
        messageLabel.setStyle(isError ? "-fx-text-fill: #e74c3c;" : "-fx-text-fill: #27ae60;");
        javafx.animation.PauseTransition pause = new javafx.animation.PauseTransition(javafx.util.Duration.seconds(3));
        pause.setOnFinished(e -> messageLabel.setText(""));
        pause.play();
    }

    /**
     * Navigates back to the main view.
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

    /**
     * Specialized cell for task status management using a CheckBox.
     */
    private class StatusCell extends TableCell<Task, Boolean> {
        private final CheckBox checkBox = new CheckBox();

        public StatusCell() {
            setStyle("-fx-alignment: CENTER;");
            checkBox.setOnAction(event -> {
                Task task = getTableRow().getItem();
                if (task != null) {
                    jTimeManager.updateTaskStatus(task, checkBox.isSelected());
                    refreshTable();
                    showStatus("Status updated.", false);
                }
            });
        }

        @Override
        protected void updateItem(Boolean item, boolean empty) {
            super.updateItem(item, empty);
            if (empty || getTableRow() == null || getTableRow().getItem() == null) {
                setGraphic(null);
            } else {
                checkBox.setSelected(getTableRow().getItem().isCompleted());
                setGraphic(checkBox);
            }
        }
    }

    /**
     * Specialized cell for task deletion using a Button.
     */
    private class DeleteTaskCell extends TableCell<Task, Void> {
        private final Button btnDelete = new Button("🗑");

        public DeleteTaskCell() {
            btnDelete.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white; -fx-cursor: hand;");
            btnDelete.setOnAction(event -> {
                Task task = getTableRow().getItem();
                if (task != null) {
                    jTimeManager.removeTaskPermanently(task);
                    refreshTable();
                    showStatus("Task deleted.", false);
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
    }
}