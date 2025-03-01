<?php

namespace App\Controller;
use Symfony\Component\HttpFoundation\JsonResponse;
use App\Entity\Planification;
use App\Form\PlanificationType;
use App\Repository\PlanificationRepository;
use Doctrine\ORM\EntityManagerInterface;
use Symfony\Bundle\FrameworkBundle\Controller\AbstractController;
use Symfony\Component\HttpFoundation\Request;
use Symfony\Component\HttpFoundation\Response;
use Symfony\Component\Routing\Attribute\Route;

#[Route('/planification')]
final class PlanificationController extends AbstractController
{
    #[Route(name: 'app_planification_index', methods: ['GET'])]
    public function index(PlanificationRepository $planificationRepository): Response
    {
        return $this->render('planification/index.html.twig', [
            'planifications' => $planificationRepository->findAll(),
        ]);
    }

    #[Route('/backPlan',name: 'app_planification_index1', methods: ['GET'])]
    public function index22(PlanificationRepository $planificationRepository): Response
    {
        return $this->render('planification/backPlanning.html.twig', [
            'planifications' => $planificationRepository->findAll(),
        ]);
    }

    #[Route('/new', name: 'app_planification_new', methods: ['GET', 'POST'])]
    public function new(Request $request, EntityManagerInterface $entityManager): Response
    {
        $planification = new Planification();
        $form = $this->createForm(PlanificationType::class, $planification);
        $form->handleRequest($request);

        if ($form->isSubmitted() && $form->isValid()) {
            $entityManager->persist($planification);
            $entityManager->flush();

            return $this->redirectToRoute('app_planification_index', [], Response::HTTP_SEE_OTHER);
        }

        return $this->render('planification/new.html.twig', [
            'planification' => $planification,
            'form' => $form,
        ]);
    }

    #[Route('/{id}', name: 'app_planification_show', methods: ['GET'])]
    public function show(Planification $planification): Response
    {
        return $this->render('planification/show.html.twig', [
            'planification' => $planification,
        ]);
    }

    #[Route('/{id}/edit', name: 'app_planification_edit', methods: ['GET', 'POST'])]
    public function edit(Request $request, Planification $planification, EntityManagerInterface $entityManager): Response
    {
        $form = $this->createForm(PlanificationType::class, $planification);
        $form->handleRequest($request);

        if ($form->isSubmitted() && $form->isValid()) {
            $entityManager->flush();

            return $this->redirectToRoute('doctor_plan_Aff', [], Response::HTTP_SEE_OTHER);
        }

        return $this->render('planification/edit.html.twig', [
            'planification' => $planification,
            'form' => $form,
        ]);
    }

    #[Route('/{id}', name: 'app_planification_delete', methods: ['POST'])]
    public function delete(Request $request, Planification $planification, EntityManagerInterface $entityManager): Response
    {
        if ($this->isCsrfTokenValid('delete'.$planification->getId(), $request->getPayload()->getString('_token'))) {
            $entityManager->remove($planification);
            $entityManager->flush();
        }

        return $this->redirectToRoute('doctor_plan_Aff', [], Response::HTTP_SEE_OTHER);
    }

 

    #[Route('/matching/planifications', name: 'planification_list', methods: ['GET'])]
    public function list(PlanificationRepository $planificationRepository): JsonResponse
    {
        $planifications = $planificationRepository->findAll();
        
        $data = [];
        foreach ($planifications as $planif) {
            $data[] = [
                'id' => $planif->getId(),
                'freelancer' => $planif->getFreelancer()->getPrenom() . ' ' . $planif->getFreelancer()->getNom(),
                'date' => $planif->getDate()->format('Y-m-d H:i'),
                'adresse' => $planif->getAdresse(),
                'mode' => $planif->getMode(),
                'statut' => $planif->getStatut(),
            ];
        }

        return new JsonResponse($data);
    }
    #[Route('/update-status/{id}', name: 'update_planification_status', methods: ['POST'])]
    public function updateStatus($id, Request $request, EntityManagerInterface $entityManager, PlanificationRepository $planificationRepository): JsonResponse
    {
        $data = json_decode($request->getContent(), true);
        $status = $data['status'] ?? null;

        if (!$status) {
            return new JsonResponse(['success' => false, 'message' => 'Invalid status'], 400);
        }

        $planification = $planificationRepository->find($id);

        if (!$planification) {
            return new JsonResponse(['success' => false, 'message' => 'Planification not found'], 404);
        }

        $planification->setStatut($status);
        $entityManager->flush();

        return new JsonResponse(['success' => true, 'message' => 'Status updated successfully']);
    }
}