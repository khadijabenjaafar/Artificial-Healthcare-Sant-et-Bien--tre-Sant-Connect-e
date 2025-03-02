<?php

namespace App\Controller;

use App\Repository\RendezVousRepository;
use Symfony\Bundle\FrameworkBundle\Controller\AbstractController;
use Symfony\Component\HttpFoundation\JsonResponse;
use Symfony\Component\HttpFoundation\Request;
use Symfony\Component\Routing\Annotation\Route;
use App\Service\MailerService;

class NotificationController extends AbstractController
{
    #[Route('/notifications', name: 'app_notifications', methods: ['GET'])]
    public function getNotifications(RendezVousRepository $rendezVousRepository): JsonResponse
    {
        $dernierRdv = $rendezVousRepository->findOneBy([], ['id' => 'DESC']);
        if (!$dernierRdv) {
            return $this->json(['message' => null]);
        }


        return $this->json([
            'message' => 'Un nouveau rendez-vous a été ajouté à ' . $dernierRdv->getDateHeure()->format('Y-m-d H:i:s'),
            'date' => $dernierRdv->getDateHeure()->format('Y-m-d H:i:s'),
            'id' => $dernierRdv->getId() 
        ]);
    }

    #[Route('/confirm-rdv/{id}', name: 'confirm_rdv')]
    public function confirmRdv(int $id, RendezVousRepository $rdvRepository, MailerService $mailerService): JsonResponse
    {
        $rdv = $rdvRepository->find($id);

        if (!$rdv) {
            return $this->json(['success' => false, 'message' => 'Rendez-vous non trouvé.'], 404);
        }

        $patient = $rdv->getIdPatient();
        if (!$patient) {
            return $this->json(['success' => false, 'message' => 'Aucun patient associé.'], 400);
        }

        if ($rdv->getMode()=="en ligne")
        {
            $email = $patient->getEmail();
            $mailerService->sendEmail($email);
            }
        else
        {
            $email = $patient->getEmail();
            $mailerService->sendEmailSurPlace($email);
            }

        return $this->json(['success' => true, 'message' => 'Email de confirmation envoyé à ' . $email]);
    }

    #[Route('/cancel-rdv/{id}', name: 'cancel_rdv')]
    public function cancelRdv(int $id, RendezVousRepository $rdvRepository, MailerService $mailerService): JsonResponse
    {
        $rdv = $rdvRepository->find($id);

        if (!$rdv) {
            return $this->json(['success' => false, 'message' => 'Rendez-vous non trouvé.'], 404);
        }

        $patient = $rdv->getIdPatient();
        if (!$patient) {
            return $this->json(['success' => false, 'message' => 'Aucun patient associé.'], 400);
        }
            $email = $patient->getEmail();
            $mailerService->sendCancellationEmail($email);
    
            return $this->json(['success' => true, 'message' => 'Email d’annulation envoyé à ' . $email]);
        
    }

}
