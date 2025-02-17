<?php
// src/Controller/FrontController.php
namespace App\Controller;

use Symfony\Bundle\FrameworkBundle\Controller\AbstractController;
use Symfony\Component\Routing\Annotation\Route;

class FrontController extends AbstractController
{
    #[Route('/front', name: 'front_index')]
    public function index_front()
    {
        return $this->render('front/index.html.twig');
    }
    #[Route('/doctor', name: 'front_index1')]
    public function index_front1()
    {
        return $this->render('front/doctor.html.twig');
    }
    #[Route('/back', name: 'back_index')]
    public function index_back()
    {
        return $this->render('back/index.html.twig');
    }
    #[Route('/login', name: 'app_register')]
    public function index_login()
    {
        return $this->render('front/seconnect.html.twig');
    }
    #[Route('/login', name: 'app_login')]
    public function index_login1()
    {
        return $this->render('front/seconnect.html.twig');
    }
    
}
