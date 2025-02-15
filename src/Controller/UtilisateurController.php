<?php

namespace App\Controller;

use Symfony\Bundle\FrameworkBundle\Controller\AbstractController;
use Symfony\Component\HttpFoundation\Response;
use Symfony\Component\Routing\Attribute\Route;
use App\Repository\UtilisateurRepository;


final class UtilisateurController extends AbstractController
{
    #[Route('/utilisateur', name: 'app_utilisateur')]
    public function index(): Response
    {
        return $this->render('utilisateur/index.html.twig', [
            'controller_name' => 'UtilisateurController',
        ]);
    }

    #[Route('/freelancers', name: 'app_freelancers')]
    public function listFreelancers(UtilisateurRepository $utilisateurRepository): Response
    {
        // Fetch users where role = 'Freelancer'
        $freelancers = $utilisateurRepository->findBy(['role' => 'Freelancer']);
        
        return $this->render('accueil.html.twig', [
            'freelancers' => $freelancers,
        ]);
    }
}
