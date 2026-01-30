package it.unicam.cs.mpgc.jtime123063;

import javafx.animation.PauseTransition;
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
import javafx.util.Duration;
import javafx.util.converter.DoubleStringConverter;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

/**
 * Controller per la gestione della vista Task.
 * Implementa la logica di presentazione delegando le operazioni di business a JTimeManager.
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

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        initManagers();
        configureTableColumns();
        setupInitialState();
        refreshTable();
    }

    private void initManagers() {
        ProjectManager projectManager = new SimpleProjectManager(new XmlSimpleProjectRepository());
        TaskManager taskManager = new SimpleTaskManager(new XmlSimpleTaskRepository());
        this.jTimeManager = new JTimeManager(projectManager, taskManager);
    }

    private void configureTableColumns() {
        tasksTable.setEditable(true);
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("title"));
        estimatedTime.setCellValueFactory(new PropertyValueFactory<>("estimatedTime"));
        scheduledDate.setCellValueFactory(new PropertyValueFactory<>("scheduledDate"));
        parentProject.setCellValueFactory(cellData -> {
            Project p = cellData.getValue().getParentProject();
            return new SimpleStringProperty(p != null ? p.getName() : "Task Libera");
        });
        setupEffectiveTimeColumn();
        setupStatusColumn();
        setupDeleteColumn();
    }

    private void setupEffectiveTimeColumn() {
        effectiveTime.setCellValueFactory(new PropertyValueFactory<>("effectiveTime"));
        effectiveTime.setCellFactory(column -> new TextFieldTableCell<>(new DoubleStringConverter()) {
            @Override
            public void updateItem(Double item, boolean empty) {
                super.updateItem(item, empty);
                if (!empty && getTableRow() != null && getTableRow().getItem() != null) {
                    Task task = getTableRow().getItem();
                    // Impedisce l'editing visivo se la task non è completata
                    setEditable(task.isCompleted());
                    setStyle(task.isCompleted() ? "-fx-opacity: 1;" : "-fx-opacity: 0.5;");
                    setStyle(getStyle() + "-fx-alignment: CENTER;");
                }
            }
        });

        effectiveTime.setOnEditCommit(event -> {
            Task task = event.getRowValue();
            if (task != null && task.isCompleted()) {
                // Delega l'aggiornamento e il salvataggio combinato (Task + Progetto) al Facade
                jTimeManager.updateTaskEffectiveTime(task, event.getNewValue());
                showStatus("Tempo aggiornato.", false);
            } else {
                refreshTable();
                showStatus("Completa la task prima di inserire le ore!", true);
            }
        });
    }

    private void setupStatusColumn() {
        statusColumn.setText("Stato");
        statusColumn.setMinWidth(90);
        statusColumn.setResizable(false);
        statusColumn.setCellFactory(param -> new TableCell<>() {
            private final CheckBox checkBox = new CheckBox();
            {
                setStyle("-fx-alignment: CENTER;");
                checkBox.setOnAction(event -> {
                    Task task = getTableRow().getItem();
                    if (task != null) {
                        // JTimeManager si occuperà di resettare le ore se la spunta viene tolta
                        jTimeManager.updateTaskStatus(task, checkBox.isSelected());
                        refreshTable();
                        showStatus("Stato aggiornato.", false);
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
        });
    }

    private void setupDeleteColumn() {
        deleteColumn.setMinWidth(80);
        deleteColumn.setResizable(false);
        deleteColumn.setCellFactory(param -> new TableCell<>() {
            private final Button btnDelete = new Button("🗑");
            {
                btnDelete.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white; -fx-cursor: hand;");
                btnDelete.setOnAction(event -> {
                    jTimeManager.removeTaskPermanently(getTableRow().getItem());
                    refreshTable();
                    showStatus("Task eliminata.", false);
                });
            }
            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : btnDelete);
                setStyle("-fx-alignment: CENTER;");
            }
        });
    }

    private void setupInitialState() {
        dueDatePicker.setValue(LocalDate.now());
        setupComboBoxes();
    }

    private void setupComboBoxes() {
        List<Project> allProjects = jTimeManager.getAllProjects();
        projectComboBox.setItems(FXCollections.observableArrayList(allProjects));

        projectFilter.getItems().clear();
        projectFilter.getItems().add("Tutti");
        projectFilter.getItems().addAll(allProjects.stream()
                .map(Project::getName)
                .collect(Collectors.toList()));

        if (projectFilter.getSelectionModel().isEmpty()) {
            projectFilter.getSelectionModel().selectFirst();
        }
    }

    @FXML
    private void handleAddTask() {
        try {
            if (!validateInput()) return;

            Task newTask = new SimpleTask(taskNameField.getText().trim(),
                    Double.parseDouble(durationField.getText().replace(",", ".")));
            newTask.setScheduledDate(dueDatePicker.getValue());

            jTimeManager.addNewTaskToProject(projectComboBox.getValue(), newTask);

            handleClearForm();

            showStatus("Task salvata!", false);

            refreshTable();
        } catch (NumberFormatException e) {
            showStatus("Errore: Durata non valida.", true);
        } catch (Exception e) {
            showStatus("Errore durante il salvataggio.", true);
        }
    }

    @FXML
    private void applyFilters() {
        // La View chiede i dati già sincronizzati al Facade
        List<Task> allTasks = jTimeManager.getAllSyncedTasks();

        String filterProject = projectFilter.getValue();
        LocalDate start = startDatePicker.getValue();
        LocalDate end = endDatePicker.getValue();

        List<Task> filtered = allTasks.stream()
                .filter(t -> filterProject == null || filterProject.equals("Tutti") ||
                        (t.getParentProject() != null && t.getParentProject().getName().equals(filterProject)))
                .filter(t -> {
                    LocalDate d = t.getScheduledDate();
                    if (d == null) return (start == null && end == null);
                    return (start == null || !d.isBefore(start)) && (end == null || !d.isAfter(end));
                })
                .collect(Collectors.toList());

        tasksTable.setItems(FXCollections.observableArrayList(filtered));
    }

    @FXML private void resetFilters() {
        startDatePicker.setValue(null);
        endDatePicker.setValue(null);
        projectFilter.getSelectionModel().selectFirst();
        refreshTable();
    }

    private void refreshTable() {
        setupComboBoxes();
        applyFilters();
        tasksTable.refresh();
    }

    // --- Metodi Utility ---

    @FXML private void handleClearForm() {
        taskNameField.clear();
        durationField.clear();
        dueDatePicker.setValue(LocalDate.now());
        projectComboBox.getSelectionModel().clearSelection();
        messageLabel.setText("");
    }

    private boolean validateInput() {
        if (taskNameField.getText().trim().isEmpty() || durationField.getText().trim().isEmpty() || dueDatePicker.getValue() == null) {
            showStatus("Errore: Campi obbligatori mancanti.", true);
            return false;
        }
        return true;
    }

    private void showStatus(String msg, boolean isError) {
        messageLabel.setText(msg);
        messageLabel.setStyle(isError ? "-fx-text-fill: #e74c3c;" : "-fx-text-fill: #27ae60;");
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