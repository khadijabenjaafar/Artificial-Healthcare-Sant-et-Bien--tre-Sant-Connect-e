<?php
namespace App\Controller;

use App\Repository\FacturationRepository;
use Symfony\Bundle\FrameworkBundle\Controller\AbstractController;
use Symfony\Component\HttpFoundation\Response;
use Symfony\Component\Routing\Annotation\Route;

class StatistiqueControllerFacturation extends AbstractController
{
    #[Route('/statistique/facturation', name: 'app_statistique_facturation')]
public function facturationStats(FacturationRepository $facturationRepo): Response
{
    $stats = $facturationRepo->countByPaymentMethod();

    return $this->render('statistiques/index.html.twig', [
        'stats' => $stats
    ]);
}
}

