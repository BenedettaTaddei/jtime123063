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
    @FXML private DatePicker dueDatePicker;
    @FXML private Label messageLabel;

    // --- Tabella ---
    @FXML private TableView<Task> tasksTable;
    @FXML private TableColumn<Task, String> nameColumn;
    @FXML private TableColumn<Task, String> parentProject;
    @FXML private TableColumn<Task, Double> estimatedTime;
    @FXML private TableColumn<Task, Double> effectiveTime;
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
        // 1. Inizializzazione Logica (Manager e Repository)
        ProjectRepository pRepo = new XmlSimpleProjectRepository();
        TaskRepository tRepo = new XmlSimpleTaskRepository();
        this.projectManager = new SimpleProjectManager(pRepo);
        this.taskManager = new SimpleTaskManager(tRepo);
        this.jTimeManager = new JTimeManager(projectManager, taskManager);

        // 2. Configurazione Colonne Tabella
        // Usiamo "title" perché il getter nella tua classe SimpleTask è getTitle()
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("title"));
        estimatedTime.setCellValueFactory(new PropertyValueFactory<>("estimatedTime"));
        effectiveTime.setCellValueFactory(new PropertyValueFactory<>("effectiveTime"));
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("completed"));
        scheduledDate.setCellValueFactory(new PropertyValueFactory<>("scheduledDate"));

        // Gestione dinamica del nome progetto (Libera vs Nome Progetto)
        parentProject.setCellValueFactory(cellData -> {
            Project p = cellData.getValue().getParentProject();
            return new SimpleStringProperty(p != null ? p.getName() : "Libera");
        });

        // 3. Setup Iniziale UI
        dueDatePicker.setValue(LocalDate.now());
        setupComboBoxes();

        // 4. Caricamento iniziale dei dati
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
            LocalDate date = dueDatePicker.getValue();

            // Creazione Task
            Task newTask = new SimpleTask(title, duration);
            newTask.setScheduledDate(date);

            // Salvataggio tramite Manager
            if (selectedProject != null) {
                // Il Manager si occupa di settare il Parent e salvare entrambi
                jTimeManager.addNewTaskToProject(selectedProject, newTask);
            } else {
                taskManager.saveTask(newTask);
            }

            showStatus("Task salvata con successo!", false);
            handleClearForm();
            refreshTable(); // Refresh automatico come in jBudget

        } catch (NumberFormatException e) {
            showStatus("Errore: Durata non valida (usa i numeri).", true);
        } catch (Exception e) {
            showStatus("Errore nel salvataggio della task.", true);
            e.printStackTrace();
        }
    }

    @FXML
    private void applyFilters() {
        // Recuperiamo i criteri dai filtri
        String selectedProjectName = projectFilter.getValue();
        LocalDate start = startDatePicker.getValue();
        LocalDate end = endDatePicker.getValue();

        // Carichiamo i dati applicando i filtri
        loadTasksTable(selectedProjectName, start, end);
    }

    private void loadTasksTable(String filterProject, LocalDate start, LocalDate end) {
        // 1. PRENDIAMO la lista (qui le task sono ancora [Libero])
        List<Task> allTasks = taskManager.getAllTasks();

        jTimeManager.syncData(allTasks);

        // 3. Applichiamo lo stream di filtraggio
        List<Task> filtered = allTasks.stream()
                .filter(t -> {
                    // Filtro Progetto
                    if (filterProject == null || filterProject.equals("Tutti")) return true;
                    return t.getParentProject() != null && t.getParentProject().getName().equals(filterProject);
                })
                .filter(t -> {
                    // Filtro Range Date
                    LocalDate taskDate = t.getScheduledDate();
                    if (taskDate == null) return (start == null && end == null);
                    if (start != null && taskDate.isBefore(start)) return false;
                    if (end != null && taskDate.isAfter(end)) return false;
                    return true;
                })
                .collect(Collectors.toList());

        // 4. Inviamo alla Tabella
        tasksTable.setItems(FXCollections.observableArrayList(filtered));
        tasksTable.refresh();
    }

    @FXML
    private void resetFilters() {
        startDatePicker.setValue(null);
        endDatePicker.setValue(null);
        projectFilter.getSelectionModel().selectFirst();
        refreshTable();
    }

    private void refreshTable() {
        setupComboBoxes(); // Aggiorna le liste nelle ComboBox
        applyFilters();    // Sincronizza e ricarica la tabella
    }

    @FXML
    private void handleClearProjectSelection() {
        projectComboBox.getSelectionModel().clearSelection();
    }

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
            showStatus("Errore: Titolo, Durata e Scadenza sono obbligatori.", true);
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