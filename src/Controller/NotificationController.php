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
            'date' => $dernierRdv->getDateHeure()->format('Y-m-d H:i:s')
        ]);
    }

    #[Route('/confirm-rdv', name: 'confirm_rdv', methods: ['POST'])]
    public function confirmRdv(MailerService $mailerService): JsonResponse
    {
        // Envoi d'un email de confirmation
        $mailerService->sendEmail('khadija.benjaafar@esprit.tn');

        return $this->json(['success' => true, 'message' => 'Email de confirmation envoyé.']);
    }

    #[Route('/cancel-rdv', name: 'cancel_rdv', methods: ['POST'])]
    public function cancelRdv(MailerService $mailerService): JsonResponse
    {
        $mailerService->sendCancellationEmail('khadija.benjaafar@esprit.tn');
        return $this->json(['success' => true, 'message' => 'Email d’annulation envoyé.']);
    }

}
