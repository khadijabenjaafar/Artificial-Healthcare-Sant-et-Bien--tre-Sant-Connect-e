<?php

namespace App\Controller;

use Symfony\Bundle\FrameworkBundle\Controller\AbstractController;
use Symfony\Component\Routing\Annotation\Route;
use Symfony\Component\HttpFoundation\Response;
use Symfony\Component\HttpFoundation\JsonResponse;
use App\Repository\RendezVousRepository;

class CalendarController extends AbstractController
{
    #[Route('/calendar', name: 'app_calendar')]
    public function index(): Response
    {

        return $this->render('calendar/index.html.twig', [
           
        ]);
    }

    #[Route('/calendar/events', name: 'app_calendar_events', methods: ['GET'])]
    public function getEvents(RendezVousRepository $rendezVousRepository): JsonResponse
    {
        $rendezvous = $rendezVousRepository->findAll();

        $events = [];

        foreach ($rendezvous as $rdv) {
            $events[] = [
                'title' => $rdv->getMotif(),
                'start' => $rdv->getDateHeure()->format('Y-m-d H:i:s'),
                'description' => 'Statut : ' . $rdv->getStatut()->value
            ];
        }

        return new JsonResponse($events);
    }

    
}
