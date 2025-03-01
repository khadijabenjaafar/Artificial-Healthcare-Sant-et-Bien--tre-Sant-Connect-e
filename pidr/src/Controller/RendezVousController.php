<?php

namespace App\Controller;

use App\Entity\RendezVous;
use App\Form\RendezVousType;
use App\Enum\MotifType;
use App\Enum\StatutType;
use App\Enum\ModeType;
use App\Repository\RendezVousRepository;
use Doctrine\ORM\EntityManagerInterface;
use Symfony\Bundle\FrameworkBundle\Controller\AbstractController;
use Symfony\Component\HttpFoundation\Request;
use Symfony\Component\HttpFoundation\Response;
use Symfony\Component\Routing\Attribute\Route;

#[Route('/rendez/vous')]
final class RendezVousController extends AbstractController
{
    #[Route(name: 'app_rendez_vous_index', methods: ['GET'])]
    public function index(RendezVousRepository $rendezVousRepository): Response
    {
        return $this->render('rendez_vous/index.html.twig', [
            'rendez_vouses' => $rendezVousRepository->findAll(),
        ]);
    }

    #[Route('/new', name: 'app_rendez_vous_new', methods: ['GET', 'POST'])]
    public function new(Request $request, EntityManagerInterface $entityManager): Response
    {
        $rendezVou = new RendezVous();
        $form = $this->createForm(RendezVousType::class, $rendezVou);
        $form->handleRequest($request);

        if ($form->isSubmitted() && $form->isValid()) {

            $rendezVou->setMotif($form->get('motif')->getData());


            $rendezVou->setStatut($form->get('statut')->getData());


            $rendezVou->setMode($form->get('mode')->getData());

            

            $entityManager->persist($rendezVou);
            $entityManager->flush();

            return $this->redirectToRoute('app_rendez_vous_index', [], Response::HTTP_SEE_OTHER);
        }

        return $this->render('rendez_vous/new.html.twig', [
            'rendez_vou' => $rendezVou,
            'form' => $form,
        ]);
    }

    #[Route('/{id}', name: 'app_rendez_vous_show', methods: ['GET'])]
    public function show(RendezVous $rendezVou): Response
    {
        return $this->render('rendez_vous/show.html.twig', [
            'rendez_vou' => $rendezVou,
        ]);
    }

    #[Route('/{id}/edit', name: 'app_rendez_vous_edit', methods: ['GET', 'POST'])]
    public function edit(Request $request, RendezVous $rendezVou, EntityManagerInterface $entityManager): Response
    {
        $originalData = clone $rendezVou;
        $form = $this->createForm(RendezVousType::class, $rendezVou);
        $form->handleRequest($request);

        if ($form->isSubmitted() && $form->isValid()) {
            if (!$rendezVou->getCommantaire()) {
                $this->addFlash('danger', 'Le commentaire est obligatoire.');
                return $this->redirectToRoute('app_rendez_vous_edit', ['id' => $rendezVou->getId()]);
            }
            
           
            /*$rendezVou->setMotif($form->get('motif')->getData());


            $rendezVou->setStatut($form->get('statut')->getData());


            $rendezVou->setMode($form->get('mode')->getData());*/
            if (
                $originalData->getMotif() === $rendezVou->getMotif() &&
                $originalData->getStatut() === $rendezVou->getStatut() &&
                $originalData->getMode() === $rendezVou->getMode() &&
                $originalData->getDateHeure() == $rendezVou->getDateHeure() &&
                $originalData->getCommantaire() === $rendezVou->getCommantaire()
            ) {
                $this->addFlash('warning', 'Aucune modification détectée.');
                return $this->redirectToRoute('app_rendez_vous_edit', ['id' => $rendezVou->getId()]);
            }
            $entityManager->flush();
            $this->addFlash('success', 'Rendez-vous mis à jour avec succès.');

            return $this->redirectToRoute('doctor_index', [], Response::HTTP_SEE_OTHER);
        }

        return $this->render('rendez_vous/edit.html.twig', [
            'rendez_vou' => $rendezVou,
            'form' => $form,
        ]);
    }

    #[Route('/{id}', name: 'app_rendez_vous_delete', methods: ['POST'])]
    public function delete(Request $request, RendezVous $rendezVou, EntityManagerInterface $entityManager): Response
    {
        if ($this->isCsrfTokenValid('delete'.$rendezVou->getId(), $request->getPayload()->getString('_token'))) {
            $entityManager->remove($rendezVou);
            $entityManager->flush();
        }

        return $this->redirectToRoute('doctor_index', [], Response::HTTP_SEE_OTHER);
    }
    #[Route('/patient/{patientId}', name: 'app_rendez_vous_patient', methods: ['GET'])]
public function showPatientAppointments(int $patientId, RendezVousRepository $rendezVousRepository): Response
{
    // Récupérer les rendez-vous associés au patient
    $rendezVouses = $rendezVousRepository->findBy(['patient' => $patientId]);

    // Si aucun rendez-vous n'est trouvé pour ce patient, tu peux ajouter un message flash ou une redirection
    if (!$rendezVouses) {
        $this->addFlash('warning', 'Aucun rendez-vous trouvé pour ce patient.');
        return $this->redirectToRoute('app_rendez_vous_index');
    }

    // Afficher les rendez-vous du patient
    return $this->render('rendez_vous/patient.html.twig', [
        'rendez_vouses' => $rendezVouses,
    ]);
}
}
