<?php

namespace App\Controller;
use Symfony\Component\HttpFoundation\JsonResponse;

use App\Entity\Ordonnance;
use App\Form\OrdonnanceType;
use App\Repository\OrdonnanceRepository;
use Doctrine\ORM\EntityManagerInterface;
use Symfony\Bundle\FrameworkBundle\Controller\AbstractController;
use Symfony\Component\HttpFoundation\Request;
use Symfony\Component\HttpFoundation\Response;
use Knp\Snappy\Pdf;
use Dompdf\Dompdf;
use Dompdf\Options;
use Symfony\Bundle\SecurityBundle\Security;
use App\Repository\UtilisateurRepository;

use App\Entity\Facturation;
use Doctrine\ORM\Query\Expr\OrderBy;
use Symfony\Component\Routing\Attribute\Route;
use DateTime;
use App\Service\SendMailService;

#[Route('/ordonnance')]
final class OrdonnanceController extends AbstractController
{
    #[Route(name: 'app_ordonnance_index', methods: ['GET'])]
    public function index(OrdonnanceRepository $ordonnanceRepository): Response
    {
        return $this->render('ordonnance/index.html.twig', [
            'ordonnances' => $ordonnanceRepository->findAll(),
        ]);
    }
    #[Route('/afficher',name: 'app_ordonnance_index1', methods: ['GET'])]
    public function index2(OrdonnanceRepository $ordonnanceRepository): Response
    {
        return $this->render('ordonnance/Affordonnances.html.twig', [
            'ordonnances' => $ordonnanceRepository->findAll(),
        ]);
    }

    #[Route('/new', name: 'app_ordonnance_new', methods: ['GET', 'POST'])]
    public function new(Request $request, EntityManagerInterface $entityManager,SendMailService $mail): Response
    {
        $ordonnance = new Ordonnance();
        $form = $this->createForm(OrdonnanceType::class, $ordonnance);
        $form->handleRequest($request);

        if ($form->isSubmitted() && $form->isValid()) {
            $entityManager->persist($ordonnance);
            $entityManager->flush();
            $patient = $ordonnance->getIdConsultation()->getIdRendezVous()->getIdPatient()->getEmail();

            $mail->sendOr('noreplay_clinicflow@gmail.com',$patient, 'Nouvelle ordonnance', 'Vous avez reçu une nouvelle ordonnance','mailPdf');

            return $this->redirectToRoute('app_ordonnance_index', [], Response::HTTP_SEE_OTHER);
        }

        return $this->render('ordonnance/new.html.twig', [
            'ordonnance' => $ordonnance,
            'form' => $form,
        ]);
    }


    #[Route('/{id}/edit', name: 'app_ordonnance_edit', methods: ['GET', 'POST'])]
    public function edit(Request $request, Ordonnance $ordonnance, EntityManagerInterface $entityManager): Response
    {
        $form = $this->createForm(OrdonnanceType::class, $ordonnance);
        $form->handleRequest($request);

        if ($form->isSubmitted() && $form->isValid()) {
            $entityManager->flush();
            return $this->redirectToRoute('doctor_ordonnance_Aff', [], Response::HTTP_SEE_OTHER);

        }

        return $this->render('ordonnance/edit.html.twig', [
            'ordonnance' => $ordonnance,
            'form' => $form,
        ]);
    }

    #[Route('/{id}', name: 'app_ordonnance_delete', methods: ['POST'])]
    public function delete(Request $request, Ordonnance $ordonnance, EntityManagerInterface $entityManager): Response
    {
        if ($this->isCsrfTokenValid('delete'.$ordonnance->getId(), $request->getPayload()->getString('_token'))) {
            $entityManager->remove($ordonnance);
            $entityManager->flush();
        }

        return $this->redirectToRoute('doctor_ordonnance_Aff', [], Response::HTTP_SEE_OTHER);

    }
/*
    public function downloadOrdonnance($id, Pdf $pdf,OrdonnanceRepository $ordonnanceRepository)
    {
        // Récupérer l'ordonnance depuis la base de données par ID
        $ordonnance = $ordonnanceRepository->find($id);

        if (!$ordonnance) {
            throw $this->createNotFoundException('Ordonnance non trouvée');
        }

        // Créer le contenu HTML pour le PDF
        $htmlContent = $this->renderView('ordonnance/pdf_template.html.twig', [
            'ordonnance' => $ordonnance,
        ]);

        // Générer le PDF à partir du contenu HTML
        $pdfContent = $pdf->getOutputFromHtml($htmlContent);

        // Créer la réponse avec le fichier PDF en tant que pièce jointe
        $response = new Response($pdfContent);
        $response->headers->set('Content-Type', 'application/pdf');
        $response->headers->set('Content-Disposition', 'attachment; filename="ordonnance.pdf"');

        return $response;
    }*/
 
    #[Route('/ordonnance/historique', name: 'historique_ordonnances', methods: ['GET'])]
    public function historiqueOrdonnances(OrdonnanceRepository $ordonnanceRepository): JsonResponse {
        $ordonnances = $ordonnanceRepository->findAll();
        
        $data = array_map(fn($ord) => [
            'id' => $ord->getId(),
            'date' => $ord->getDate()->format('Y-m-d'),
        ], $ordonnances);
    
        return $this->json($data);
    }
    #[Route('/ordonnance/delete-expired', name: 'delete_expired_ordonnances', methods: ['POST'])]
    public function deleteExpiredOrdonnances(Request $request, OrdonnanceRepository $ordonnanceRepository, EntityManagerInterface $entityManager): JsonResponse
    {
        $data = json_decode($request->getContent(), true);
        
        if (isset($data['ids']) && is_array($data['ids'])) {
            foreach ($data['ids'] as $id) {
                $ordonnance = $ordonnanceRepository->find($id);
                if ($ordonnance) {
                    $entityManager->remove($ordonnance);
                }
            }
            $entityManager->flush();
            return $this->json(['success' => true]);
        }
    
        return $this->json(['success' => false], 400);
    }
    
    
    #[Route('/check-ordonnance', name: 'check_ordonnance', methods: ['GET'])]
     
    public function checkOrdonnances(OrdonnanceRepository $ordonnanceRepository, EntityManagerInterface $entityManager): JsonResponse
    {
        $now = new DateTime();
        $expiredOrdonnances = [];
    
        // Trouver toutes les ordonnances
        $ordonnances = $ordonnanceRepository->findAll();
    
        foreach ($ordonnances as $ordonnance) {
            $dateOrdonnance = $ordonnance->getDate();
    
            if ($dateOrdonnance && $dateOrdonnance < $now) { // Vérifier si la date est passée
                $dateDiff = $now->diff($dateOrdonnance)->days;
    
                if ($dateDiff > 3) { // Vérifier si l'ordonnance a plus de 3 jours
                    $expiredOrdonnances[] = [
                        'id' => $ordonnance->getId(),
                        'date' => $dateOrdonnance->format('Y-m-d'),
                    ];
                }
            }
        }
    
        return $this->json($expiredOrdonnances);
    }
    #[Route('/ordonnance/{id}', name: 'ordonnance_show')]
    public function show(Ordonnance $ordonnance, int $id,OrdonnanceRepository $ordonnanceRepository): Response
    {
        $ordonnance = $ordonnanceRepository->find($id);
        if (!$ordonnance || !$ordonnance->getId()) {
            throw $this->createNotFoundException('Ordonnance non trouvée ou ID invalide');
        }
    
        return $this->render('ordonnance/show.html.twig', [
            'ordonnance' => $ordonnance,
        ]);
    }
    #[Route('/ordonnance/{id}/download', name: 'ordonnance_download')]
     
     
    public function downloadOrdonnance($id, EntityManagerInterface $entityManager, Pdf $pdf)
    {
        // Récupérer l'ordonnance depuis la base de données par ID
        $ordonnance = $entityManager->getRepository(Ordonnance::class)->find($id);

        if (!$ordonnance) {
            throw $this->createNotFoundException('Ordonnance non trouvée');
        }

        // Créer le contenu HTML pour le PDF
        $htmlContent = $this->renderView('ordonnance/pdf_template.html.twig', [
            'ordonnance' => $ordonnance,
        ]);

        // Générer le PDF à partir du contenu HTML
        $pdfContent = $pdf->getOutputFromHtml($htmlContent);

        // Retourner le PDF comme réponse de téléchargement
        return new Response(
            $pdfContent,
            200,
            [
                'Content-Type' => 'application/pdf',
                'Content-Disposition' => 'attachment; filename="ordonnance.pdf"'
            ]
        );
    }

    #[Route('/doctor/ordonnance/Ajout', name: 'doctor_ordonnance_Ajout')]
    public function doctorOrdonnanceAjout(UtilisateurRepository $utilisateurRepository,Security $security,Request $request,
      EntityManagerInterface $entityManager) {
          $user = $security->getUser();
        if (!$user) {
            throw $this->createAccessDeniedException("Vous devez être connecté pour voir vos rendez-vous.");
        }
        $ordonnance = new Ordonnance();
        $form = $this->createForm(OrdonnanceType::class, $ordonnance);
        $form->handleRequest($request);
        if ($form->isSubmitted() && $form->isValid()) {
            $entityManager->persist($ordonnance);
            $entityManager->flush();

            return $this->redirectToRoute('doctor_ordonnance_Aff', [], Response::HTTP_SEE_OTHER);
        }
         return $this->render('ordonnance/docOrdonnance.html.twig', [
            'form' => $form
        ]);
    }
    #[Route('/doctor/ordonnance/Aff', name: 'doctor_ordonnance_Aff')]
    public function doctorOrdonnanceAff(Security $security,OrdonnanceRepository $ordonnanceRepository) {
        $user = $security->getUser();
        if (!$user) {
            throw $this->createAccessDeniedException("Vous devez être connecté pour voir vos ordonnances.");
        }
        // Requête pour récupérer les ordonnances liées au patient connecté
        $ordonnances = $ordonnanceRepository->createQueryBuilder('o')
            ->join('o.id_consultation', 'c')
            ->join('c.id_rendez_vous', 'r')
            ->where('r.id_patient = :user OR r.id_medecin = :user') 
            ->setParameter('user', $user)
            ->getQuery()
            ->getResult();
    

        return $this->render('ordonnance/AFFdoc.html.twig', [
            'ordonnances' => $ordonnances,
        ]);
    }
    #[Route('/doctor/ordonnance/Aff/phar', name: 'doctor_ordonnance_Aff_phar')]
    public function doctorOrdonnanceAffPhar(OrdonnanceRepository $ordonnanceRepository) {
        $ordonnances = $ordonnanceRepository->findAll();
        return $this->render('ordonnance/AFFdoc.html.twig', [
            'ordonnances' => $ordonnances,
        ]);
    }
}