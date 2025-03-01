<?php
namespace App\Controller;


use TCPDF;
use Symfony\Bundle\FrameworkBundle\Controller\AbstractController;
use Symfony\Component\HttpFoundation\Response;
use Symfony\Component\Routing\Annotation\Route;
use Symfony\Component\HttpFoundation\StreamedResponse;
use Symfony\Component\Routing\Generator\UrlGeneratorInterface;
use Symfony\Component\HttpFoundation\JsonResponse;

class PdfController extends AbstractController
{   
    #[Route('/generate-pdf', name: 'generate_pdf')]
    public function generatePdf(): JsonResponse
    {
        // Générer le PDF et l'enregistrer
        $filePath = $this->getParameter('kernel.project_dir') . '/public/pdf/generated_pdf.pdf';
    
        // URL du fichier PDF généré
        $pdfUrl = $this->generateUrl('download_pdf', ['filename' => 'generated_pdf.pdf'], \Symfony\Component\Routing\Generator\UrlGeneratorInterface::ABSOLUTE_URL);
    
        // Vérifier si l'URL a bien été générée
        if (!$pdfUrl) {
            throw $this->createNotFoundException('Le PDF n\'a pas pu être généré');
        }
    
        // Retourner la réponse JSON avec l'URL du PDF
        return new JsonResponse(['pdfUrl' => $pdfUrl]);
    }



    #[Route('/pdf/download/{filename}', name: 'download_pdf')]
    public function downloadPdf($filename)
    {
        $filePath = $this->getParameter('kernel.project_dir') . '/public/pdf/' . $filename;

        if (!file_exists($filePath)) {
            throw $this->createNotFoundException('Fichier PDF non trouvé');
        }

        // Retourner le fichier en tant que téléchargement
        return new StreamedResponse(function() use ($filePath) {
            readfile($filePath);
        }, 200, [
            'Content-Type' => 'application/pdf',
            'Content-Disposition' => 'attachment; filename="' . basename($filePath) . '"',
        ]);
    }
}

