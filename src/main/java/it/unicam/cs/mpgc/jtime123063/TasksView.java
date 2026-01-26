package it.unicam.cs.mpgc.jtime123063;

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
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class TasksView implements Initializable {

    // --- Elementi UI: Inserimento ---
    @FXML private TextField taskNameField;
    @FXML private TextField durationField;
    @FXML private ComboBox<Project> projectComboBox;
    @FXML private DatePicker dueDatePicker; // AGGIUNTO
    @FXML private Label messageLabel;

    @FXML private TableView<Task> tasksTable;
    @FXML private TableColumn<Task, String> nameColumn;
    @FXML private TableColumn<Task, String> ParentProject;
    @FXML private TableColumn<Task, Double> EstimatedTime;
    @FXML private TableColumn<Task, Double> EffectiveTime;
    @FXML private TableColumn<Task, Boolean> statusColumn;
    @FXML private TableColumn<Task, LocalDate> scheduledDate;

    // --- Elementi UI: Filtri ---
    @FXML private ComboBox<String> projectFilter;
    @FXML private DatePicker startDatePicker;
    @FXML private DatePicker endDatePicker;

    private JTimeManager jTimeManager;
    private ProjectManager projectManager;
    private TaskManager taskManager;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        ProjectRepository pRepo = new XmlSimpleProjectRepository();
        TaskRepository tRepo = new XmlSimpleTaskRepository();
        this.projectManager = new SimpleProjectManager(pRepo);
        this.taskManager = new SimpleTaskManager(tRepo);
        this.jTimeManager = new JTimeManager(projectManager, taskManager);

        // Configurazione Colonne
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("title"));
        EstimatedTime.setCellValueFactory(new PropertyValueFactory<>("estimatedTime"));
        EffectiveTime.setCellValueFactory(new PropertyValueFactory<>("effectiveTime"));
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("completed"));
        scheduledDate.setCellValueFactory(new PropertyValueFactory<>("scheduledDate"));

        ParentProject.setCellValueFactory(cellData -> {
            Project p = cellData.getValue().getParentProject();
            return new SimpleStringProperty(p != null ? p.getName() : "Libera");
        });

        // Inizializzazione data a oggi
        dueDatePicker.setValue(LocalDate.now());

        setupComboBoxes();
        refreshTable();
    }

    private void setupComboBoxes() {
        List<Project> allProjects = projectManager.getAllProjects();
        projectComboBox.setItems(FXCollections.observableArrayList(allProjects));

        projectFilter.getItems().clear();
        projectFilter.getItems().add("Tutti");
        projectFilter.getItems().addAll(allProjects.stream().map(Project::getName).collect(Collectors.toList()));
        projectFilter.getSelectionModel().selectFirst();
    }

    @FXML
    private void handleAddTask() {
        try {
            if (!validateInput()) return;

            String title = taskNameField.getText().trim();
            double duration = Double.parseDouble(durationField.getText().replace(",", "."));
            Project selectedProject = projectComboBox.getValue();
            LocalDate date = dueDatePicker.getValue(); // RECUPERO DATA

            Task newTask = new SimpleTask(title, duration);
            newTask.setScheduledDate(date); // SET DELLA DATA NEL MODELLO

            if (selectedProject != null) {
                jTimeManager.addNewTaskToProject(selectedProject, newTask);
            } else {
                taskManager.saveTask(newTask);
            }

            showStatus("Task pianificata per il " + date + " con successo!", false);
            handleClearForm();
            refreshTable();

        } catch (NumberFormatException e) {
            showStatus("Errore: Durata non valida.", true);
        } catch (Exception e) {
            showStatus("Errore nel salvataggio.", true);
        }
    }

    private void loadTasks(String filterProjectName) {
        List<Task> allTasks = taskManager.getAllTasks();
        List<Task> filteredTasks = allTasks.stream()
                .filter(t -> {
                    if (filterProjectName == null || filterProjectName.equals("Tutti")) return true;
                    return t.getParentProject() != null && t.getParentProject().getName().equals(filterProjectName);
                })
                .collect(Collectors.toList());

        tasksTable.setItems(FXCollections.observableArrayList(filteredTasks));
    }

    @FXML
    private void applyFilters() {
        String selectedProject = projectFilter.getValue();
        LocalDate start = startDatePicker.getValue();
        LocalDate end = endDatePicker.getValue();

        List<Task> allTasks = taskManager.getAllTasks();

        List<Task> filteredTasks = allTasks.stream()
                .filter(t -> {
                    // Filtro Progetto
                    boolean matchesProject = (selectedProject == null || selectedProject.equals("Tutti")) ||
                            (t.getParentProject() != null && t.getParentProject().getName().equals(selectedProject));

                    // Filtro Date (Pianificazione)
                    LocalDate taskDate = t.getScheduledDate();
                    boolean matchesDate = true;
                    if (taskDate != null) {
                        if (start != null && taskDate.isBefore(start)) matchesDate = false;
                        if (end != null && taskDate.isAfter(end)) matchesDate = false;
                    } else if (start != null || end != null) {
                        matchesDate = false; // Se cerco per data, escludo i task senza data
                    }

                    return matchesProject && matchesDate;
                })
                .collect(Collectors.toList());

        tasksTable.setItems(FXCollections.observableArrayList(filteredTasks));
    }

    @FXML
    private void resetFilters() {
        startDatePicker.setValue(null);
        endDatePicker.setValue(null);
        projectFilter.getSelectionModel().selectFirst();
        applyFilters();
    }

    private void refreshTable() {
        setupComboBoxes();
        applyFilters();
    }

    @FXML private void handleClearProjectSelection() { projectComboBox.getSelectionModel().clearSelection(); }

    @FXML
    private void handleClearForm() {
        taskNameField.clear();
        durationField.clear();
        dueDatePicker.setValue(LocalDate.now());
        projectComboBox.getSelectionModel().clearSelection();
        messageLabel.setText("");
    }

    private boolean validateInput() {
        if (taskNameField.getText().trim().isEmpty() || durationField.getText().trim().isEmpty() || dueDatePicker.getValue() == null) {
            showStatus("Errore: Nome, Durata e Data sono obbligatori.", true);
            return false;
        }
        return true;
    }

    private void showStatus(String msg, boolean isError) {
        messageLabel.setText(msg);
        messageLabel.setStyle(isError ? "-fx-text-fill: #e74c3c;" : "-fx-text-fill: #27ae60;");
    }

    @FXML
    public void goToMainView(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/MainView.fxml")));
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(new Scene(root));
    }
}