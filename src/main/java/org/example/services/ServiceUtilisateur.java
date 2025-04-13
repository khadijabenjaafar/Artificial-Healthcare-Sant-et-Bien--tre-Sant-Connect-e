package org.example.services;
import org.example.entities.EnumRole;
import org.example.entities.Status;
import org.example.entities.Utilisateur;
import org.example.utils.MyDataBase;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
public class ServiceUtilisateur implements IService <Utilisateur> {
    private static Connection connection = MyDataBase.getInstance().getMyConnection();



    public ServiceUtilisateur(){
        connection = MyDataBase.getInstance().getMyConnection();
    }
    @Override
    public void ajouter(Utilisateur utilisateur) throws SQLException {
            String sql = "INSERT INTO `utilisateur` (`nom`, `prenom`, `email`, `password`, `date_naissance`, `role`, `adresse`, `genre`, `image`, `is_verified`, `reset_token`, `num_tel`, `tel_verified`, `status`, `image1`) " +
                    "VALUES ('" + utilisateur.getNom() + "', " +
                    "'" + utilisateur.getPrenom() + "', " +
                    "'" + utilisateur.getEmail() + "', " +
                    "'" + utilisateur.getPassword() + "', " +
                    (utilisateur.getDate_naissance() != null ? "'" + utilisateur.getDate_naissance() + "'" : "NULL") + ", " +
                    "'" + utilisateur.getRole() + "', " +
                    "'" + utilisateur.getAdresse() + "', " +
                    "'" + utilisateur.getGenre() + "', " +
                    "'" + utilisateur.getImage() + "', " +
                    utilisateur.isIs_verified() + ", " +
                    (utilisateur.getResetToken() != null ? "'" + utilisateur.getResetToken() + "'" : "NULL") + ", " +
                    "'" + utilisateur.getnumTel() + "', " +
                    utilisateur.isTel_verified() + ", " +
                    "'ACTIVE', " +
                    "'" + utilisateur.getImage1() + "')";

            System.out.println(sql); // Pour voir la requête générée (utile pour debug)
            Statement stm = connection.createStatement();
            stm.executeUpdate(sql);
    }

    @Override
    public void modifier(Utilisateur utilisateur) throws SQLException {
        String sql ="UPDATE `utilisateur` SET `nom`=? ,`prenom`=? ,`email`=?  ,`date_naissance`=? ,`role`=? ,`adresse`=? ,`genre`=? ,`num_tel`=?  ,`status`=? ,`image1`=?  WHERE id = ?";
        PreparedStatement pst = connection.prepareStatement(sql);
        pst.setString(1, utilisateur.getNom());
        pst.setString(2, utilisateur.getPrenom());
        pst.setString(3,utilisateur.getEmail());
        if (utilisateur.getDate_naissance() != null) {
            java.sql.Date sqlDate = java.sql.Date.valueOf(utilisateur.getDate_naissance());
            pst.setDate(4, sqlDate);
        } else {
            pst.setNull(4, java.sql.Types.DATE);
        }
        pst.setString(5, utilisateur.getRole().name());
        pst.setString(6,utilisateur.getAdresse());
        pst.setString(7,utilisateur.getGenre());
        pst.setString(8,utilisateur.getnumTel());
        if (utilisateur.getStatus() != null) {
            pst.setString(9, utilisateur.getStatus().toString());
        } else {
            pst.setNull(9, java.sql.Types.VARCHAR); // ou tu mets "INACTIF", "BANNED", etc.
        }
        pst.setString(10,utilisateur.getImage1());
        pst.setInt(11,utilisateur.getId());
        pst.executeUpdate();

    }

    @Override
    public void supprimer(int id) throws SQLException {
        String sql = "DELETE FROM `utilisateur` WHERE id = ?";
        PreparedStatement ps = connection.prepareStatement(sql);
        ps.setInt(1,id);
        ps.executeUpdate();

    }

    @Override
    public List<Utilisateur> afficher() throws SQLException {
        List<Utilisateur> utilisateurs = new ArrayList<>();
        try {
            String sql = "Select * from utilisateur";
            Statement statement = connection.createStatement();
            ResultSet rs =statement.executeQuery(sql);
                while (rs.next()){
                    EnumRole role = EnumRole.valueOf(rs.getString("role"));
                    Status status =Status.valueOf(rs.getString("status"));
                    java.sql.Date sqlDate = rs.getDate(6);
                    LocalDate dateNaissance = (sqlDate != null) ? sqlDate.toLocalDate() : null;
                    Utilisateur u = new Utilisateur(
                    rs.getInt("id"),
                    rs.getString(2),
                    rs.getString(3),
                    rs.getString(4),
                    role,
                    dateNaissance,
                    rs.getString(5),
                    rs.getString(8),
                    rs.getString(9),
                    rs.getString(11),
                            status
                    );

                    utilisateurs.add(u);
                }
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        }
        return utilisateurs;
    }
    public static Utilisateur getUserByEmailAndPass(String email, String password) {
        Utilisateur user = null;
        try {
            String req = "SELECT * FROM utilisateur WHERE email = ? AND password = ?";
            PreparedStatement psmt = connection.prepareStatement(req);
            psmt.setString(1, email);
            psmt.setString(2, password);
            ResultSet rs = psmt.executeQuery();

            if (rs.next()) {
                user = new Utilisateur();
                user.setId(rs.getInt("Id"));
                user.setNom(rs.getString("nom"));
                user.setPrenom(rs.getString("Prenom"));
                user.setEmail(rs.getString("Email"));
                user.setPassword(rs.getString("Password"));
                user.setnumTel(rs.getString("num_tel"));
                user.setGenre(rs.getString("genre"));
                user.setAdresse(rs.getString("adresse"));
                // Convert String to Status enum
                String statusStr = rs.getString("status");
                user.setImage1(rs.getString("image1"));
                user.setDate_naissance(LocalDate.parse(rs.getString("date_naissance")));


                Status status = Status.valueOf(statusStr);
                user.setStatus(status);

                // Convert String to Role enum
                String roleStr = rs.getString("Role");
                EnumRole role = EnumRole.valueOf(roleStr); // Convert String to Role enum
                user.setRole(role);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            // Handle case where the database value does not match any enum constant
            e.printStackTrace();
        }

        return user;
    }

    public void bannir(int id) throws SQLException {
        String query = "UPDATE utilisateur SET status = 'BANNED' WHERE id = ?";
        PreparedStatement pst = connection.prepareStatement(query);
        pst.setInt(1, id);
        pst.executeUpdate();
    }

    public void debannir(int id) throws SQLException {
        String query = "UPDATE utilisateur SET status = 'ACTIVE' WHERE id = ?";
        PreparedStatement pst = connection.prepareStatement(query);
        pst.setInt(1, id);
        pst.executeUpdate();
    }


}
