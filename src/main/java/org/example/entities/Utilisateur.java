package org.example.entities;
import java.time.LocalDate;
import java.util.Date;
import java.util.Locale;

public class Utilisateur {
    private Matching matching;

    private int id;
    private String nom = null;
    private String prenom = null;
    private String email = null;
    private String password = null;
    private LocalDate date_naissance;

    private EnumRole role;
    private String adresse = null;
    private String genre = null;
   private String image = null;
    private String numTel ;
    private boolean is_verified = false;
    private String resetToken;
    private boolean Tel_verified = false;
    private Status status;
    private String image1;
    public Utilisateur(){}

    @Override
    public String toString() {
        return "Utilisateur{" +
                "id=" + id +
                ", nom='" + nom + '\'' +
                ", prenom='" + prenom + '\'' +
                ", email='" + email + '\'' +
                ", password='" + password + '\'' +
                ", date_naissance=" + date_naissance +
                ", role=" + role +
                ", adresse='" + adresse + '\'' +
                ", genre='" + genre + '\'' +
                ", numTel='" + numTel + '\'' +
                ", image='" + image1 + '\'' +
                '}';
    }

    public Utilisateur(String nom, String prenom, String email, String password, LocalDate date_naissance, EnumRole role, String adresse, String genre, String numTel,String image1) {
        this.nom = nom;
        this.prenom = prenom;
        this.email = email;
        this.role=role;
        this.date_naissance=date_naissance;
        this.password = password;
        this.adresse = adresse;
        this.genre = genre;
        this.numTel = numTel;
        this.image1=image1;
    }
    public Utilisateur(int id, String nom, String prenom, String email,EnumRole role,LocalDate date_naissance, String password, String adresse, String genre, String numTel) {
        this.id = id;
        this.nom = nom;
        this.prenom = prenom;
        this.email = email;
        this.role=role;
        this.date_naissance=date_naissance;
        this.password = password;
        this.adresse = adresse;
        this.genre = genre;
        this.numTel = numTel;
    }
    public Utilisateur(int id, String nom, String prenom, String email,EnumRole role,LocalDate date_naissance, String password, String adresse, String genre, String image, String numTel, boolean is_verified, String resetToken, boolean tel_verified, Status status, String image1) {
        this.id = id;
        this.nom = nom;
        this.prenom = prenom;
        this.email = email;
        this.role=role;
        this.date_naissance=date_naissance;
        this.password = password;
        this.adresse = adresse;
        this.genre = genre;
        this.image = image;
        this.numTel = numTel;
        this.is_verified = is_verified;
        this.resetToken = resetToken;
        Tel_verified = tel_verified;
        this.status = status;
        this.image1 = image1;
    }
    public Utilisateur(String nom, String prenom, String email,String password,LocalDate date_naissance,EnumRole role, String adresse, String genre, String image, String numTel, boolean is_verified, String resetToken, boolean tel_verified, Status status, String image1) {
        this.nom = nom;
        this.prenom = prenom;
        this.email = email;
        this.role=role;
        this.date_naissance=date_naissance;
        this.password = password;
        this.adresse = adresse;
        this.genre = genre;
        this.image = image;
        this.numTel = numTel;
        this.is_verified = is_verified;
        this.resetToken = resetToken;
        Tel_verified = tel_verified;
        this.status = status;
        this.image1 = image1;
    }


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getPrenom() {
        return prenom;
    }

    public void setPrenom(String prenom) {
        this.prenom = prenom;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
    public LocalDate getDate_naissance() {
        return date_naissance;
    }

    public void setDate_naissance(LocalDate date_naissance) {
        this.date_naissance = date_naissance;
    }
    public EnumRole getRole() {
        return role;
    }

    public void setRole(EnumRole role) {
        this.role = role;
    }
    public String getAdresse() {
        return adresse;
    }

    public void setAdresse(String adresse) {
        this.adresse = adresse;
    }

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getnumTel() {
        return numTel;
    }

    public void setnumTel(String numTel) {
        this.numTel = numTel;
    }

    public boolean isIs_verified() {
        return is_verified;
    }

    public void setIs_verified(boolean is_verified) {
        this.is_verified = is_verified;
    }

    public String getResetToken() {
        return resetToken;
    }

    public void setResetToken(String resetToken) {
        this.resetToken = resetToken;
    }

    public boolean isTel_verified() {
        return Tel_verified;
    }

    public void setTel_verified(boolean tel_verified) {
        Tel_verified = tel_verified;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public String getImage1() {
        return image1;
    }

    public void setImage1(String image1) {
        this.image1 = image1;
    }

    public Matching getMatching() {
        return matching;
    }

    public void setMatching(Matching matching) {
        this.matching = matching;
    }

}
