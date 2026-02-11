package it.unicam.cs.mpgc.jtime123063.controller;

import it.unicam.cs.mpgc.jtime123063.manager.*;
import it.unicam.cs.mpgc.jtime123063.model.Project;
import it.unicam.cs.mpgc.jtime123063.model.Task;
import it.unicam.cs.mpgc.jtime123063.repository.XmlSimpleProjectRepository;
import it.unicam.cs.mpgc.jtime123063.repository.XmlSimpleTaskRepository;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

/**
 * Controller for generating and displaying project reports and statistics.
 */
public class ReportsView implements Initializable {

    @FXML private ComboBox<String> projectSelector;
    @FXML private DatePicker startDate, endDate;
    @FXML private Label totalTasksLabel, totalEstimatedLabel, totalEffectiveLabel, varianceLabel;
    @FXML private TextArea reportArea;

    private JTimeManager jTimeManager;
    private ReportManager reportManager;

    /**
     * Initializes the report view, managers, and UI components.
     * @param location  The FXML location.
     * @param resources The resource bundle.
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        ProjectManager pMan = new SimpleProjectManager(new XmlSimpleProjectRepository());
        TaskManager tMan = new SimpleTaskManager(new XmlSimpleTaskRepository());
        this.jTimeManager = new JTimeManager(pMan, tMan);
        this.reportManager = new ReportManager();
        setupComboBox();
    }

    /**
     * Triggers report generation by filtering tasks based on user input.
     */
    @FXML
    private void generateReport() {
        jTimeManager.syncData(jTimeManager.getAllProjects());
        List<Task> filteredTasks = reportManager.getFilteredTasks(
                jTimeManager.getAllSyncedTasks(),
                projectSelector.getValue(),
                startDate.getValue(),
                endDate.getValue()
        );
        updateUI(filteredTasks);
    }

    /**
     * Updates the summary labels and the report area with filtered task data.
     * @param tasks The list of filtered tasks to display.
     */
    private void updateUI(List<Task> tasks) {
        double est = tasks.stream().mapToDouble(Task::getEstimatedTime).sum();
        double eff = tasks.stream().mapToDouble(Task::getEffectiveTime).sum();
        double diff = reportManager.getTotalVariance(tasks);

        totalTasksLabel.setText(String.valueOf(tasks.size()));
        totalEstimatedLabel.setText(String.format("%.1f h", est));
        totalEffectiveLabel.setText(String.format("%.1f h", eff));
        varianceLabel.setText(String.format("%+.1f h", diff));

        applyVarianceColor(diff);
        updateTimeline(tasks);
    }

    /**
     * Populates the project selection combo box with available project names.
     */
    private void setupComboBox() {
        List<String> projectNames = jTimeManager.getAllProjects().stream()
                .map(Project::getName)
                .collect(Collectors.toList());
        projectNames.add(0, "Tutti i Progetti");
        projectSelector.setItems(FXCollections.observableArrayList(projectNames));
        projectSelector.getSelectionModel().selectFirst();
    }

    /**
     * Applies semantic coloring to the variance label based on the numerical value.
     * Red (#e74c3c) = Over-budget, Green (#27ae60) = Saving, Dark Blue (#2c3e50) = Neutral.
     * @param diff The variance value to evaluate.
     */
    private void applyVarianceColor(double diff) {
        String color = (diff > 0) ? "#e74c3c" : (diff < 0) ? "#27ae60" : "#2c3e50";
        varianceLabel.setStyle("-fx-text-fill: " + color + "; -fx-font-size: 20px; -fx-font-weight: bold;");
    }

    /**
     * Delegates the timeline string generation to the ReportManager and updates the UI.
     * @param tasks The list of tasks to format in the timeline.
     */
    private void updateTimeline(List<Task> tasks) {
        // Logica delegata al manager per alleggerire il controller
        reportArea.setText(reportManager.getFormattedTimeline(tasks));
    }

    /**
     * Navigates back to the main dashboard view.
     * @param event The action event triggered by the user.
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