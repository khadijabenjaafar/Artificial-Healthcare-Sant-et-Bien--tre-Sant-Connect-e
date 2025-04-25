package org.example.api;

import org.example.entities.Utilisateur;
import org.example.services.ServiceUtilisateur;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import java.io.File;
import java.io.UnsupportedEncodingException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Properties;

public class mailler {

    public static void sendResetPasswordEmail(Utilisateur u, ServiceUtilisateur su) {
        final String username = "khadijabenjaafar123@gmail.com";
        final String password = "fqej ryva xzgm ajvp"; // À remplacer par un app password

        Properties props = new Properties();
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.ssl.protocols", "TLSv1.2");
        props.put("mail.smtp.auth", "true");

        Session session = Session.getInstance(props, new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(username, password);
            }
        });
        System.out.println("Valeur actuelle: " + System.getProperty("mail.smtp.host"));
        session.setDebug(true); // Garder activé pour le débogage

// Force les paramètres SMTP
        session.getProperties().put("mail.smtp.host", "smtp.gmail.com");
        session.getProperties().put("mail.smtp.port", "587");
        session.setDebug(true); // Active le mode debug pour voir le flux SMTP

        try {
            String tempPassword = PasswordUtils.generateTempPassword(10); // 👈 mot de passe temporaire
            String hashedPassword = PasswordUtils.hashPassword(tempPassword); // 👈 à stocker dans la BDD
            su.updatePassword(u.getId(), hashedPassword); // 👈 update dans ta base (ajoute cette méthode dans ton ServiceUtilisateur)

            String signature = "\n\n-- \nClinicFlow Application\nTél: +216 11 111 111\nEmail: Clinicflow@gmail.com\nSite: www.cliniclflow.com";

            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(username, "ClinicFlow Application"));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(u.getEmail()));
            message.setSubject("Réinitialisation de votre mot de passe");
            message.setText("Bonjour " + u.getNom() + ",\n\nVotre nouveau mot de passe temporaire est : " + tempPassword +
                    "\nVeuillez vous connecter et le changer immédiatement dans votre profil.\n\nCordialement," + signature);

            Transport.send(message);
            System.out.println("Mail envoyé avec succès");

        } catch (MessagingException | UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }
    public static void sendEmailWithAttachment(String toEmail, String attachmentPath) {
        final String fromEmail = "khadijabenjaafar123@gmail.com";
        final String password = "fqej ryva xzgm ajvp"; // Utilise un mot de passe d'application Google

        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        String formattedDateTime = now.format(formatter);

        Properties props = new Properties();
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.ssl.protocols", "TLSv1.2");
        props.put("mail.smtp.auth", "true");

        Session session = Session.getInstance(props, new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(fromEmail, password);
            }
        });

        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(fromEmail, "ClinicFlow Application"));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toEmail));
            message.setSubject("Tentative de connexion échouée");

            // Corps du message
            MimeBodyPart messageBodyPart = new MimeBodyPart();
            String signature = "\n\n-- \nClinicFlow Application\nTél: +216 11 111 111\nEmail: Clinicflow@gmail.com\nSite: www.cliniclflow.com";
            String body =
                    "Bonjour,\n\n" +
                            "Une tentative de connexion à votre compte a échoué trois fois de suite.\n" +
                            "Date et heure de la tentative : " + formattedDateTime + "\n\n" +
                            "Nous avons pris une photo de la personne ayant tenté d’accéder au compte. Vous trouverez cette image en pièce jointe.\n\n" +
                            "Si vous n'êtes pas à l'origine de ces tentatives, nous vous recommandons de changer votre mot de passe immédiatement.\n\n" +
                            "Cordialement,\n" + signature;

            messageBodyPart.setText(body);

            // Partie de la pièce jointe
            MimeBodyPart attachmentPart = new MimeBodyPart();
            File attachmentFile = new File(attachmentPath);

            // Vérification si le fichier existe et est valide
            if (attachmentFile.exists() && attachmentFile.isFile()) {
                attachmentPart.attachFile(attachmentFile);
            } else {
                System.out.println("Le fichier de pièce jointe n'a pas été trouvé ou est invalide.");
                // Tu peux choisir de ne pas envoyer l'email si la pièce jointe est absente
                return;
            }

            // Création du multipart pour ajouter le message et la pièce jointe
            Multipart multipart = new MimeMultipart();
            multipart.addBodyPart(messageBodyPart);
            multipart.addBodyPart(attachmentPart);

            // Ajout du contenu à l'email
            message.setContent(multipart);

            // Envoi de l'email
            Transport.send(message);

            System.out.println("Email envoyé avec succès !");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }





}
