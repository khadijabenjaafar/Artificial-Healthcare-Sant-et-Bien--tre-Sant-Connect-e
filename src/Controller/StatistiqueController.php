<?php

namespace App\Controller;

use App\Repository\RendezVousRepository;
use App\Repository\ConsultationRepository;
use Symfony\Bundle\FrameworkBundle\Controller\AbstractController;
use Symfony\Component\HttpFoundation\Response;
use Symfony\Component\Routing\Annotation\Route;

class StatistiqueController extends AbstractController
{
    #[Route('/statistique', name: 'app_statistique')]
    public function index(RendezVousRepository $rdvRepo, ConsultationRepository $consultRepo): Response
    {
        // Récupérer les statistiques des rendez-vous par jour
        $rendezVousStats = $rdvRepo->countByDay();
        $consultationStats = $consultRepo->countByDay();

        return $this->render('statistiques/index.html.twig', [
            'rendezVous' => $rendezVousStats,
            'consultations' => $consultationStats
        ]);
    }
}
