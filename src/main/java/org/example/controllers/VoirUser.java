package org.example.controllers;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.example.entities.*;
import org.example.services.ServiceUtilisateur;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
public class VoirUser {

    public ObservableList<Utilisateur> userList;
    private ServiceUtilisateur su= new ServiceUtilisateur();
    @FXML private ListView<Utilisateur> UsersList;
    @FXML private TextField searchField;
    @FXML private Button AddAdmin;
    @FXML private ComboBox<String> filtre;
    @FXML private VBox container;

    private ObservableList<Utilisateur> observableUsers;
    private FilteredList<Utilisateur> filteredData;
    private Pagination pagination;
    private static final int ITEMS_PER_PAGE = 10;


    @FXML
    public void initialize() throws SQLException {
        observableUsers = FXCollections.observableArrayList(su.afficher());
        filteredData = new FilteredList<>(observableUsers, p -> true);

        setupListCell();
        setupSearchAndFilters();

        pagination = new Pagination();
        pagination.setPageFactory(this::createPage);
        container.getChildren().addAll(UsersList, pagination);

        updatePagination();

        AddAdmin.setOnAction(e -> NavigateToAddAdmin());
    }

    private void setupListCell() {
        UsersList.setCellFactory(lv -> new ListCell<>() {
            @Override
            protected void updateItem(Utilisateur user, boolean empty) {
                super.updateItem(user, empty);
                if (empty || user == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    Label nom = new Label(user.getNom()); nom.setPrefWidth(100);
                    Label prenom = new Label(user.getPrenom()); prenom.setPrefWidth(100);
                    Label email = new Label(user.getEmail()); email.setPrefWidth(150);
                    Label tel = new Label(user.getnumTel()); tel.setPrefWidth(100);
                    Label status = new Label(user.getStatus() != null ? user.getStatus().toString() : "Aucun"); status.setPrefWidth(100);
                    Label genre = new Label(user.getGenre()); genre.setPrefWidth(100);
                    Label role = new Label(user.getRole() != null ? user.getRole().toString() : "Aucun"); role.setPrefWidth(100);

                    Button delete = new Button("Supprimer");
                    delete.setStyle("-fx-background-color: red; -fx-text-fill: white;");
                    delete.setOnAction(event -> {
                        try {
                            su.supprimer(user.getId());
                            observableUsers.remove(user);
                            updatePagination();
                        } catch (SQLException e) {
                            showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur de suppression : " + e.getMessage());
                        }
                    });

                    Button ban = new Button();
                    if ("ACTIVE".equals(user.getStatus().toString())) {
                        ban.setText("Bannir");
                        ban.setStyle("-fx-background-color: orange; -fx-text-fill: white;");
                    } else {
                        ban.setText("Débannir");
                        ban.setStyle("-fx-background-color: green; -fx-text-fill: white;");
                    }

                    ban.setOnAction(event -> {
                        try {
                            if ("ACTIVE".equals(user.getStatus().toString())) {
                                su.bannir(user.getId());
                                user.setStatus(Status.BANNED);
                            } else {
                                su.debannir(user.getId());
                                user.setStatus(Status.ACTIVE);
                            }
                            UsersList.refresh(); // juste rafraîchir la cellule
                        } catch (SQLException e) {
                            showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur de bannissement : " + e.getMessage());
                        }
                    });

                    HBox row = new HBox(15, nom, prenom, email, tel, status, genre, role, delete, ban);
                    row.setPadding(new Insets(5));
                    row.setStyle("-fx-border-color: #ddd; -fx-border-width: 0 0 1 0;");
                    setGraphic(row);
                }
            }
        });
    }


    private Node createPage(int pageIndex) {
        int from = pageIndex * ITEMS_PER_PAGE;
        int to = Math.min(from + ITEMS_PER_PAGE, filteredData.size());
        UsersList.setItems(FXCollections.observableArrayList(filteredData.subList(from, to)));
        return new VBox();
    }

    private void updatePagination() {
        pagination.setPageCount((int) Math.ceil((double) filteredData.size() / ITEMS_PER_PAGE));
        pagination.setCurrentPageIndex(0);
        pagination.setPageFactory(this::createPage);
    }

    private void setupSearchAndFilters() {
        filtre.getItems().addAll("Tous", "ACTIVE", "BANNED");
        filtre.setValue("Tous");

        searchField.textProperty().addListener((obs, oldVal, newVal) -> applyFilter());
        filtre.valueProperty().addListener((obs, oldVal, newVal) -> applyFilter());
    }

    private void applyFilter() {
        String searchText = searchField.getText().toLowerCase();
        String statusFilter = filtre.getValue();

        filteredData.setPredicate(user -> {
            if (user == null) return false;

            boolean matchStatus = "Tous".equals(statusFilter) || statusFilter.equalsIgnoreCase(user.getStatus().toString());
            boolean matchSearch = searchText == null || searchText.isEmpty() ||
                    (user.getNom() != null && user.getNom().toLowerCase().contains(searchText)) ||
                    (user.getPrenom() != null && user.getPrenom().toLowerCase().contains(searchText)) ||
                    (user.getEmail() != null && user.getEmail().toLowerCase().contains(searchText));

            return matchStatus && matchSearch;
        });

        updatePagination();
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    @FXML
    private void NavigateToAddAdmin() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/CreerAdmin.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) AddAdmin.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Échec du chargement de la page : " + e.getMessage());
        }
    }
}
