<?php

namespace App\Controller;
use Symfony\Component\HttpFoundation\JsonResponse;
use App\Entity\Planification;
use App\Form\PlanificationType;
use App\Repository\PlanificationRepository;
use Doctrine\ORM\EntityManagerInterface;
use Symfony\Bundle\FrameworkBundle\Controller\AbstractController;
use Symfony\Bundle\SecurityBundle\Security;
use Symfony\Component\HttpFoundation\Request;
use Symfony\Component\HttpFoundation\Response;
use Symfony\Component\Routing\Attribute\Route;
use Symfony\Component\Mailer\MailerInterface;
use Symfony\Component\Mime\Email;
use App\Repository\UtilisateurRepository;

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
    public function new(Request $request, EntityManagerInterface $entityManager,Security $security): Response
    {
        $planification = new Planification();
        $form = $this->createForm(PlanificationType::class, $planification);
        $form->handleRequest($request);
    
        if ($form->isSubmitted() && $form->isValid()) {
            $user = $security->getUser(); // Get logged-in user
            $planification->setUtilisateur( $user); // Assign user as the patient
    
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

            return $this->redirectToRoute('doctor_index', [], Response::HTTP_SEE_OTHER);
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

        return $this->redirectToRoute('doctor_index', [], Response::HTTP_SEE_OTHER);
    }

 

    #[Route('/matching/planifications', name: 'planification_list', methods: ['GET'])]
    public function list(PlanificationRepository $planificationRepository): JsonResponse
    {
        $planifications = $planificationRepository->findAll();
    
        $data = [];
        foreach ($planifications as $planif) {
            $data[] = [
                'id' => $planif->getId(),
                'patient' => $planif->getPatient()->getPrenom() . ' ' . $planif->getPatient()->getNom(),
                'freelancer' => $planif->getFreelancer()->getPrenom() . ' ' . $planif->getFreelancer()->getNom(),
                'date' => $planif->getDate()->format('Y-m-d H:i'),
                'adresse' => $planif->getAdresse(),
                'mode' => $planif->getMode(),
                'statut' => $planif->getStatut(),
            ];
        }
    
        return new JsonResponse($data);
    }
    #[Route('/confirm/{id}', name: 'planification_confirm', methods: ['POST'])]
    public function confirmPlanification(PlanificationRepository $repo, EntityManagerInterface $em, int $id): JsonResponse {
        $planification = $repo->find($id);
        if (!$planification) {
            return new JsonResponse(['success' => false, 'error' => 'Planification not found'], 404);
        }
    
        $planification->setStatut('confirmé');
        $em->persist($planification);
        $em->flush();
    
        return new JsonResponse(['success' => true]);
    }
    
        #[Route('/cancel/{id}', name: 'planification_cancel', methods: ['POST'])]
public function cancelPlanification(PlanificationRepository $repo, Request $request, EntityManagerInterface $em, int $id): JsonResponse {
    $planification = $repo->find($id);
    if (!$planification) {
        return new JsonResponse(['success' => false, 'error' => 'Planification not found'], 404);
    }

    $data = json_decode($request->getContent(), true);
    $responseText = $data['response'] ?? null;

    $planification->setStatut('annulé');
    $planification->setReponse($responseText);
    
    $em->persist($planification);
    $em->flush();

    return new JsonResponse(['success' => true]);
}


#[Route('/confirm-status/{id}', name: 'confirm_status', methods: ['POST'])]
public function confirmStatus($id, MailerInterface $mailer, EntityManagerInterface $entityManager)
{
    $planification = $entityManager->getRepository(Planification::class)->find($id);
    if ($planification) {
        $planification->setStatut('confirmé');
        $entityManager->flush();

        // Send email
        $email = (new Email())
            ->from('hanaharragi555@gmail.com')
            ->to($planification->getUtilisateur()->getEmail())
            ->subject('Planification Confirmée')
            ->text('Votre planification a été confirmée.');

        try {
            $mailer->send($email);
            return new JsonResponse(['success' => true]);
        } catch (\Exception $e) {
            return new JsonResponse(['success' => false, 'error' => $e->getMessage()], 500);
        }
    }

    return new JsonResponse(['success' => false], 400);
}

public function cancelStatus($id, MailerInterface $mailer, EntityManagerInterface $entityManager)
{
    $planification = $entityManager->getRepository(Planification::class)->find($id);
    if ($planification) {
        $planification->setStatut('annulé');
        $entityManager->flush();

        // Send email
        $email = (new Email())
            ->from('hanaharragi555@gmail.com')
            ->to($planification->getUtilisateur()->getEmail())
            ->subject('Planification Annulée')
            ->text('Votre planification a été annulée.');

        $mailer->send($email);

        return new JsonResponse(['success' => true]);
    }

    return new JsonResponse(['success' => false], 400);
}
#[Route('/admin/planification-stats', name: 'admin_planification_stats')]
public function planificationStats(PlanificationRepository $PlanificationRepository): Response
{
    $stats = $PlanificationRepository->countByStatus();

    return $this->render('statistiques/planstat.html.twig', [
        'stats' => $stats
    ]);
}
#[Route('/doctor/plan/Aff', name: 'doctor_plan_Aff')]
public function doctorPlan(UtilisateurRepository $utilisateurRepository,PlanificationRepository $planificationRepository,
 Security $security)
{
   
    $user = $security->getUser();
    if (!$user) {
        throw $this->createAccessDeniedException("Vous devez être connecté pour voir vos rendez-vous.");
    }

           // Fetch data and ensure all referenced entities exist
           $planifications = $planificationRepository->findAll();
           $validPlanifications = [];
           $invalidPlanifications = [];
   
           foreach ($planifications as $planification) {
               if ($planification->getFreelancer() && $planification->getUtilisateur()) {
                   $validPlanifications[] = $planification;
               } else {
                   $invalidPlanifications[] = $planification;
               }
           }
   
           if (!empty($invalidPlanifications)) {
               // Log the invalid planifications or handle them as needed
           }
    return $this->render('planification/AffDoc.html.twig', [
        'planifications' => $planifications
    ]);
}
}
