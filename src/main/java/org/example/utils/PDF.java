package org.example.utils;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;

import java.io.IOException;

public class PDF {

    // Méthode pour générer un PDF pour une facturation
    public static void generateFacturationPDF(String pdfFilePath, String numeroFacture, String dateFacture, String montant) throws IOException {
        // Créer un document PDF
        PDDocument document = new PDDocument();

        // Créer une page PDF
        PDPage page = new PDPage();
        document.addPage(page);

        // Créer un flux de contenu pour la page
        PDPageContentStream contentStream = new PDPageContentStream(document, page);

        // Ajouter du texte dans le PDF
        contentStream.beginText();
        contentStream.setFont(PDType1Font.HELVETICA_BOLD, 12);
        contentStream.newLineAtOffset(100, 750); // Position du texte

        // Ajouter les détails de la facturation
        contentStream.showText("Facture N°: " + numeroFacture);
        contentStream.newLineAtOffset(0, -20);
        contentStream.showText("Date: " + dateFacture);
        contentStream.newLineAtOffset(0, -20);
        contentStream.showText("Montant: " + montant + " EUR");

        contentStream.endText();

        // Fermer le flux de contenu
        contentStream.close();

        // Sauvegarder le PDF
        document.save(pdfFilePath);
        document.close();

        System.out.println("PDF de facturation généré avec succès !");
    }
}
