<?php

namespace App\Controller;
use App\Entity\Facturation;
use App\Form\FacturationType;
use App\Enum\MethodePaiement;
use App\Repository\FacturationRepository;
use Doctrine\ORM\EntityManagerInterface;
use Symfony\Bundle\FrameworkBundle\Controller\AbstractController;
use Symfony\Component\HttpFoundation\Request;
use Symfony\Component\HttpFoundation\Response;
use Symfony\Component\Routing\Attribute\Route;
use Dompdf\Dompdf;
use Dompdf\Options;
use App\Repository\UtilisateurRepository;
use Symfony\Bundle\SecurityBundle\Security;

#[Route('/facturation')]
final class FacturationController extends AbstractController
{
    #[Route(name: 'app_facturation_index', methods: ['GET'])]
    public function index(FacturationRepository $facturationRepository): Response
    {
        return $this->render('facturation/index.html.twig', [
            'facturations' => $facturationRepository->findAll(),
        ]);
    }

    #[Route('/new', name: 'app_facturation_new', methods: ['GET', 'POST'])]
    public function new(Request $request, EntityManagerInterface $entityManager): Response
    {
        $facturation = new Facturation();
        $form = $this->createForm(FacturationType::class, $facturation);
        $form->handleRequest($request);

        if ($form->isSubmitted() && $form->isValid()) {

            $facturation->setMethodePaiement($form->get('methode_paiement')->getData());
            
            $entityManager->persist($facturation);
            $entityManager->flush();

            return $this->redirectToRoute('app_facturation_index', [], Response::HTTP_SEE_OTHER);
        }

        return $this->render('facturation/new.html.twig', [
            'facturation' => $facturation,
            'form' => $form,
        ]);
    }

    #[Route('/{id}', name: 'app_facturation_show', methods: ['GET'])]
    public function show(Facturation $facturation): Response
    {
        return $this->render('facturation/show.html.twig', [
            'facturation' => $facturation,
        ]);
    }

    #[Route('/{id}/edit', name: 'app_facturation_edit', methods: ['GET', 'POST'])]
public function edit(Request $request, Facturation $facturation, EntityManagerInterface $entityManager): Response
{
    // Sauvegarder les anciennes données (avant modification)
    $originalData = clone $facturation;

    // Créer et traiter le formulaire
    $form = $this->createForm(FacturationType::class, $facturation);
    $form->handleRequest($request);

    if ($form->isSubmitted() && $form->isValid()) {
        // Vérifier si des modifications ont été faites en comparant les anciennes et nouvelles valeurs
        if (
            $originalData->getMethodePaiement() === $facturation->getMethodePaiement() &&
            $originalData->getStatut() === $facturation->getStatut()
        ) {
            // Si aucune modification n'a été détectée
            $this->addFlash('warning', 'Aucune modification détectée.');
            return $this->redirectToRoute('app_facturation_edit', ['id' => $facturation->getId()]);
        }

        // Si des modifications sont détectées, effectuer l'update dans la base de données
        $entityManager->flush();
        $this->addFlash('success', 'Facturation mise à jour avec succès.');

        // Redirection après mise à jour réussie
        return $this->redirectToRoute('app_facturation_index', [], Response::HTTP_SEE_OTHER);
    }

    // Rendu du formulaire de modification
    return $this->render('facturation/edit.html.twig', [
        'facturation' => $facturation,
        'form' => $form->createView(),
    ]);
}


    #[Route('/{id}', name: 'app_facturation_delete', methods: ['POST'])]
    public function delete(Request $request, Facturation $facturation, EntityManagerInterface $entityManager): Response
    {
        if ($this->isCsrfTokenValid('delete'.$facturation->getId(), $request->getPayload()->getString('_token'))) {
            $entityManager->remove($facturation);
            $entityManager->flush();
        }

        return $this->redirectToRoute('doctor_index', [], Response::HTTP_SEE_OTHER);
    }
    #[Route('/facturation/{id}/toggle-status', name: 'app_facturation_toggle_status')]
    public function toggleStatus(Facturation $facturation, EntityManagerInterface $entityManager): Response
    {
        // Empêcher le changement si la facturation est déjà Payée
        if ($facturation->getStatut() === 'Payée') {
            $this->addFlash('warning', 'Cette facturation est déjà payée et ne peut plus être modifiée.');
            return $this->redirectToRoute('app_facturation_index');
        }
    
        // Changer le statut
        $facturation->setStatut('Payée');
    
        $entityManager->persist($facturation);
        $entityManager->flush();
    
        $this->addFlash('success', 'Le statut de la facturation a été mis à jour.');
    
        return $this->redirectToRoute('app_facturation_index');
    }
    #[Route('/facture/{id}/download', name: 'app_facture_download')]
    public function downloadFacture(Facturation $facturation): Response
    {
        // Vérifier si la facture est payée
        if ($facturation->getStatut() !== 'Payée') {
            $this->addFlash('warning', 'Vous ne pouvez télécharger la facture que si elle est payée.');
            return $this->redirectToRoute('app_facturation_index');
        }
    
        // Options du PDF
        $pdfOptions = new Options();
        $pdfOptions->set('defaultFont', 'Arial');
    
        // Instancier Dompdf
        $dompdf = new Dompdf($pdfOptions);
    
        // Contenu du PDF (modifie selon ton besoin)
        $html = "
            <h1>Facture #{$facturation->getId()}</h1>
            <p><strong>Date :</strong> {$facturation->getDate()->format('d/m/Y')}</p>
            <p><strong>Statut :</strong> {$facturation->getStatut()}</p>
            <p><strong>Montant :</strong> {$facturation->getMontant()} €</p>
            <hr>
            <p>Merci pour votre paiement.</p>
        ";
    
        $dompdf->loadHtml($html);
        $dompdf->setPaper('A4', 'portrait');
        $dompdf->render();
    
        // Réponse PDF
        return new Response($dompdf->output(), 200, [
            'Content-Type' => 'application/pdf',
            'Content-Disposition' => 'attachment; filename="facture_'.$facturation->getId().'.pdf"'
        ]);
    }  
    #[Route('/admin/facturation-stats', name: 'admin_facturation_stats')]
    public function facturationStats(FacturationRepository $facturationRepository): Response
    {
        $stats = $facturationRepository->countByPaymentMethod();

        return $this->render('statistiques/statFacturation.html.twig', [
            'stats' => $stats
        ]);
    }
    #[Route('/doctor/facturation/Aff', name: 'doctor_facturation_Aff')]
    public function doctorFacturationAff(UtilisateurRepository $utilisateurRepository,
     Security $security,FacturationRepository $facturationRepository)
    {
        $user = $security->getUser();
        if (!$user) {
            throw $this->createAccessDeniedException("Vous devez être connecté pour voir vos rendez-vous.");
        }
        return $this->render('facturation/AffDoc.html.twig', [
            'facturations' => $facturationRepository->findAll()
        ]);
    }
    #[Route('/doctor/facturation/Ajout', name: 'doctor_facturation_Ajout')]
    public function doctorFacturationAjout(UtilisateurRepository $utilisateurRepository,
     Security $security,EntityManagerInterface $entityManager,Request $request1)
    {
        $user = $security->getUser();
        if (!$user) {
            throw $this->createAccessDeniedException("Vous devez être connecté pour voir vos rendez-vous.");
        }
        
        $facturation = new Facturation();
        $form1 = $this->createForm(FacturationType::class, $facturation);
        $form1->handleRequest($request1);
        if ($form1->isSubmitted() && $form1->isValid()) {

            $facturation->setMethodePaiement($form1->get('methode_paiement')->getData());
            
            $entityManager->persist($facturation);
            $entityManager->flush();

            return $this->redirectToRoute('doctor_facturation_Aff', [], Response::HTTP_SEE_OTHER);
        }
        return $this->render('facturation/AjoutDoc.html.twig', [
            'facturation' => $facturation,
            'form1' => $form1
        ]);
    }

}
