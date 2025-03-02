<?php

namespace App\Controller;

use App\Entity\Consultation;
use App\Form\ConsultationType;
use App\Repository\ConsultationRepository;
use Doctrine\ORM\EntityManagerInterface;
use Symfony\Bundle\FrameworkBundle\Controller\AbstractController;
use Symfony\Component\HttpFoundation\Request;
use Symfony\Component\HttpFoundation\Response;
use Symfony\Component\Routing\Attribute\Route;
use Symfony\Component\HttpFoundation\JsonResponse;

#[Route('/consultation')]
final class ConsultationController extends AbstractController
{
    #[Route(name: 'app_consultation_index', methods: ['GET'])]
    public function index(ConsultationRepository $consultationRepository): Response
    {
        return $this->render('consultation/index.html.twig', [
            'consultations' => $consultationRepository->findAll(),
        ]);
    }

    #[Route('/back', name: 'app_consultation_index21', methods: ['GET'])]
    /*public function index12(ConsultationRepository $consultationRepository): Response
    {
        return $this->render('consultation/AffConsultation.html.twig', [
            'consultations' => $consultationRepository->findAll(),
        ]);
    }*/
    public function index12(Request $request, ConsultationRepository $consultationRepository): Response
{
    $searchTerm = $request->query->get('search'); // Récupérer le texte de recherche

    if ($searchTerm) {
        // Recherche par diagnostic, traitement ou observation
        $consultations = $consultationRepository->searchConsultation($searchTerm);
    } else {
        $consultations = $consultationRepository->findAll();
    }

    return $this->render('consultation/AffConsultation.html.twig', [
        'consultations' => $consultations,
        'searchTerm' => $searchTerm, // Garde la recherche affichée
    ]);
}

    #[Route('/new', name: 'app_consultation_new', methods: ['GET', 'POST'])]
    public function new(Request $request, EntityManagerInterface $entityManager): Response
    {
        $consultation = new Consultation();
        $form = $this->createForm(ConsultationType::class, $consultation);
        $form->handleRequest($request);

        if ($form->isSubmitted() && $form->isValid()) {
            
            $entityManager->persist($consultation);
            $entityManager->flush();

            return $this->redirectToRoute('app_consultation_index', [], Response::HTTP_SEE_OTHER);
        }

        return $this->render('consultation/new.html.twig', [
            'consultation' => $consultation,
            'form' => $form,
        ]);
    }

    #[Route('/{id}', name: 'app_consultation_show', methods: ['GET'])]
    public function show(Consultation $consultation): Response
    {
        return $this->render('consultation/show.html.twig', [
            'consultation' => $consultation,
        ]);
    }

    #[Route('/{id}/edit', name: 'app_consultation_edit', methods: ['GET', 'POST'])]
    public function edit(Request $request, Consultation $consultation, EntityManagerInterface $entityManager): Response
    {
        $originalData = clone $consultation;
        $form = $this->createForm(ConsultationType::class, $consultation);
        $form->handleRequest($request);

        if ($form->isSubmitted() && $form->isValid()) {
            if (
                $originalData->getDiagnostic() === $consultation->getDiagnostic() &&
                $originalData->getTraitement() === $consultation->getTraitement() &&
                $originalData->getObservation() === $consultation->getObservation() &&
                $originalData->getPrix() == $consultation->getPrix() &&
                $originalData->getProchainRdv() == $consultation->getProchainRdv() &&
                $originalData->getDuree() == $consultation->getDuree()
            ) {
                $this->addFlash('warning', 'Aucune modification détectée.');
                return $this->redirectToRoute('app_consultation_edit', ['id' => $consultation->getId()]);
            }
            $entityManager->flush();
            $this->addFlash('success', 'Consultation mise à jour avec succès.');
            return $this->redirectToRoute('doctor_consul_Aff',['data-target' => "Consultation"], Response::HTTP_SEE_OTHER);
        }

        return $this->render('consultation/edit.html.twig', [
            'consultation' => $consultation,
            'form' => $form,
        ]);
    }

    #[Route('/{id}', name: 'app_consultation_delete', methods: ['POST'])]
    public function delete(Request $request, Consultation $consultation, EntityManagerInterface $entityManager): Response
    {
        if ($this->isCsrfTokenValid('delete'.$consultation->getId(), $request->getPayload()->getString('_token'))) {
            $entityManager->remove($consultation);
            $entityManager->flush();
        }

        return $this->redirectToRoute('doctor_consul_Aff', [], Response::HTTP_SEE_OTHER);
    }

    #[Route('/api/consultations', name: 'api_consultations', methods: ['GET'])]
    public function getConsultations(ConsultationRepository $repository): JsonResponse
    {
        $consultations = $repository->findAll();
        $events = [];
    
        foreach ($consultations as $consultation) {
            $events[] = [
                'title' => 'Consultation',
                'start' => $consultation->getProchainRdv()->format('Y-m-d H:i:s'),
            ];
        }
    
        return $this->json($events);
    }

    #[Route('/clean-expired-consultations', name: 'clean_expired_consultations')]
public function cleanExpiredConsultations(ConsultationRepository $consultationRepository, EntityManagerInterface $entityManager): Response
{
    $now = new \DateTime();

    // Récupérer toutes les consultations expirées
    $expiredConsultations = $consultationRepository->findExpiredConsultations($now);

    foreach ($expiredConsultations as $consultation) {
        $entityManager->remove($consultation);
    }

    // Enregistrer les modifications dans la base de données
    $entityManager->flush();

    return new Response('Expired consultations cleaned successfully.');
}

    
}