package it.unicam.cs.mpgc.jtime123063;

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

public class ReportsView implements Initializable {

    @FXML private ComboBox<String> projectSelector;
    @FXML private DatePicker startDate, endDate;
    @FXML private Label totalTasksLabel, totalEstimatedLabel, totalEffectiveLabel, varianceLabel;
    @FXML private TextArea reportArea;

    private JTimeManager jTimeManager;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        ProjectManager pMan = new SimpleProjectManager(new XmlSimpleProjectRepository());
        TaskManager tMan = new SimpleTaskManager(new XmlSimpleTaskRepository());
        this.jTimeManager = new JTimeManager(pMan, tMan);

        setupComboBox();
    }

    private void setupComboBox() {
        List<String> projectNames = jTimeManager.getAllProjects().stream()
                .map(Project::getName)
                .collect(Collectors.toList());
        projectNames.add(0, "Tutti i Progetti");
        projectSelector.setItems(FXCollections.observableArrayList(projectNames));
        projectSelector.getSelectionModel().selectFirst();
    }

    @FXML
    private void generateReport() {
        String selected = projectSelector.getValue();
        LocalDate start = startDate.getValue();
        LocalDate end = endDate.getValue();

        jTimeManager.syncData(jTimeManager.getAllProjects());

        List<Task> filteredTasks = jTimeManager.getAllSyncedTasks().stream()
                .filter(t -> selected.equals("Tutti i Progetti") ||
                        (t.getParentProject() != null && t.getParentProject().getName().equals(selected)))
                .filter(t -> {
                    LocalDate d = t.getScheduledDate();
                    if (d == null) return (start == null && end == null);
                    return (start == null || !d.isBefore(start)) && (end == null || !d.isAfter(end));
                })
                .collect(Collectors.toList());

        updateUI(filteredTasks);
    }

    private void updateUI(List<Task> tasks) {
        double est = tasks.stream().mapToDouble(Task::getEstimatedTime).sum();
        double eff = tasks.stream().mapToDouble(Task::getEffectiveTime).sum();
        double diff = eff - est;

        totalTasksLabel.setText(String.valueOf(tasks.size()));
        totalEstimatedLabel.setText(String.format("%.1f h", est));
        totalEffectiveLabel.setText(String.format("%.1f h", eff));
        varianceLabel.setText(String.format("%+.1f h", diff));

        // Abbiamo rimosso 'baseStyle' perché lo passiamo solo una volta
        applyVarianceColor(diff);
        updateTimeline(tasks);
    }

    private void applyVarianceColor(double diff) {
        // Applichiamo solo il cambio colore, mantenendo i 20px e il bold
        String color = (diff > 0) ? "#e74c3c" : (diff < 0) ? "#27ae60" : "#2c3e50";
        varianceLabel.setStyle("-fx-text-fill: " + color + "; -fx-font-size: 20px; -fx-font-weight: bold;");
    }

    private void updateTimeline(List<Task> tasks) {
        if (tasks.isEmpty()) {
            reportArea.setText("\nNessuna attività trovata.");
            return;
        }

        StringBuilder sb = new StringBuilder();
        LocalDate lastDate = null;

        List<Task> sorted = tasks.stream()
                .sorted((t1, t2) -> {
                    if (t1.getScheduledDate() == null) return 1;
                    if (t2.getScheduledDate() == null) return -1;
                    return t1.getScheduledDate().compareTo(t2.getScheduledDate());
                }).collect(Collectors.toList());

        for (Task t : sorted) {
            LocalDate d = t.getScheduledDate();
            if (d != null && !d.equals(lastDate)) {
                sb.append(String.format("\n ---------- %s ---------- \n", d));
                lastDate = d;
            } else if (d == null && lastDate != null) {
                sb.append("\n ---------- DA ASSEGNARE ----------\n");
                lastDate = null;
            }

            String pInfo = (t.getParentProject() != null) ? "[" + t.getParentProject().getName() + "]" : "[Singola]";
            sb.append(String.format(" %-22s %-28s | Est: %4.1fh | Eff: %4.1fh\n",
                    truncate(pInfo, 20), truncate(t.getTitle(), 25), t.getEstimatedTime(), t.getEffectiveTime()));
        }
        reportArea.setText(sb.toString());
    }

    private String truncate(String text, int max) {
        if (text == null) return "";
        return (text.length() <= max) ? text : text.substring(0, max - 3) + "...";
    }

    @FXML
    public void goToMainView(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/MainView.fxml")));
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(new Scene(root));
    }
}