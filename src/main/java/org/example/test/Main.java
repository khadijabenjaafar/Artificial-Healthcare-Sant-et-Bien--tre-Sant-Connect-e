package org.example.test;

import org.example.entities.Status;
import org.example.entities.Utilisateur;
import org.example.services.ServiceUtilisateur;
import org.example.utils.MyDataBase;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Date;
import java.sql.SQLException;

import static org.example.entities.EnumRole.ROLE_MEDECIN;

// Press Shift twice to open the Search Everywhere dialog and type `show whitespaces`,
// then press Enter. You can now see whitespace characters in your code.
public class Main {
    public static void main(String[] args) {
        MyDataBase.getInstance();
        ServiceUtilisateur su = new ServiceUtilisateur();
            Utilisateur u1 = new Utilisateur(63,"aloo", "aloo", "aloogmailcom", ROLE_MEDECIN , LocalDate.of(2002, 2, 10),"12345678","dddd", "homme", "ddd", "12345678", false, "dddd", false, Status.ACTIVE, "ddd");
            try {
                //su.ajouter(u1);
               // System.out.println(su.afficher());
                //su.supprimer(67);
                su.modifier(u1);
            } catch (SQLException e) {
                System.out.println(e.getMessage());
            }


    }
}