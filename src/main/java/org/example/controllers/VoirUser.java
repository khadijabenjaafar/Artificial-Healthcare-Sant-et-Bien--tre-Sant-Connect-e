
package org.example.controllers;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.example.entities.*;
import org.example.services.ServiceUtilisateur;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
public class VoirUser {
    @FXML
    private ListView<Utilisateur> UsersList;

    @FXML
    private TextField searchField;

    @FXML
    private Button AddAdmin;

    public ObservableList<Utilisateur> userList;
    private ServiceUtilisateur su= new ServiceUtilisateur();

    @FXML
    public void initialize() throws SQLException {
        // Simuler des utilisateurs pour test
        userList = FXCollections.observableArrayList(su.afficher());
        // Appliquer la liste à la ListView
        UsersList.setItems(userList);
        UsersList.setCellFactory(lv -> new ListCell<Utilisateur>() {
            @Override
            protected void updateItem(Utilisateur user, boolean empty) {
                super.updateItem(user, empty);

                if (empty || user == null) {
                    setText(null);
                    setGraphic(null);
                } else {

                    Label nomLabel = new Label(user.getNom());
                    nomLabel.setPrefWidth(100);

                    Label prenomLabel = new Label(user.getPrenom());
                    prenomLabel.setPrefWidth(100);

                    Label emailLabel = new Label(user.getEmail());
                    emailLabel.setPrefWidth(150);

                    Label telLabel = new Label(user.getnumTel());
                    telLabel.setPrefWidth(100);

                    Label statusLabel = new Label(user.getStatus() != null ? user.getStatus().toString() : "Aucun");
                    statusLabel.setPrefWidth(100);
                    Label genreLabel = new Label(user.getGenre());
                    statusLabel.setPrefWidth(100);
                    Label roleLabel = new Label(user.getRole()!= null ? user.getRole().toString() : "Aucun");
                    roleLabel.setPrefWidth(100);
                    Button deleteButton = new Button("Supprimer");
                    deleteButton.setStyle("-fx-background-color: red; -fx-text-fill: white;");
                    deleteButton.setPrefWidth(100);
                    deleteButton.setOnAction(event -> {
                        try {
                            su.supprimer(user.getId());
                            userList.remove(user);
                        } catch (SQLException e) {
                            throw new RuntimeException(e);
                        }
                    });
                    Button banButton = new Button();
                    banButton.setPrefWidth(100);

                    if ("ACTIVE".equals(user.getStatus().toString())) {
                        banButton.setText("Bannir");
                        banButton.setStyle("-fx-background-color: orange; -fx-text-fill: white;");
                    } else {
                        banButton.setText("Débannir");
                        banButton.setStyle("-fx-background-color: green; -fx-text-fill: white;");
                    }

                    banButton.setOnAction(event -> {
                        try {
                            // Appelle une méthode du service pour changer le statut
                            if ("ACTIVE".equals(user.getStatus().toString())) {
                                su.bannir(user.getId()); // exemple : mettre à jour en "banni"
                                user.setStatus(Status.BANNED);
                                banButton.setText("Débannir");
                                banButton.setStyle("-fx-background-color: green; -fx-text-fill: white;");
                            } else {
                                su.debannir(user.getId()); // exemple : remettre à "actif"
                                user.setStatus(Status.ACTIVE);
                                banButton.setText("Bannir");
                                banButton.setStyle("-fx-background-color: orange; -fx-text-fill: white;");
                            }
                            // juste après setStatus
                            int index = userList.indexOf(user);
                            if (index >= 0) {
                                userList.set(index, null);
                                userList.set(index, user);
                            }

                        } catch (SQLException e) {
                            throw new RuntimeException(e);
                        }
                    });


                    HBox row = new HBox(15, nomLabel, prenomLabel, emailLabel, telLabel, statusLabel,genreLabel, roleLabel, deleteButton,banButton);
                    row.setPadding(new Insets(5));
                    row.setStyle("-fx-border-color: #ddd; -fx-border-width: 0 0 1 0;");

                    setGraphic(row);
                }
            }
        });



        // (Optionnel) Action pour le bouton Ajouter Admin
        AddAdmin.setOnAction(e -> NavigateToAddAdmin());
    }

    @FXML
    private void NavigateToAddAdmin() {
        System.out.println("Navigation vers l'ajout d'un admin.");
        // Logique de navigation à écrire selon ton système
    }


}
