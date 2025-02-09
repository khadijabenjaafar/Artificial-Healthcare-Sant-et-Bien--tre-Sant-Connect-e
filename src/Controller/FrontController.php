<?php
// src/Controller/FrontController.php
namespace App\Controller;
use App\Form\UserformType;
use Symfony\Bundle\FrameworkBundle\Controller\AbstractController;
use Symfony\Component\Routing\Annotation\Route;
use Symfony\Component\HttpFoundation\Response;
use Symfony\Component\HttpFoundation\Request;
class FrontController extends AbstractController
{
    #[Route('/front', name: 'front_index')]
    public function index_front()
    {
        return $this->render('front/index.html.twig');
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
    #[Route('/log2', name: 'app_login')]
    public function index_login2()
    {
        return $this->render('front/login.html.twig');
    }
    #[Route('/essai', name: 'app_essai')]
    public function index_essai()
    {
        return $this->render('back/essai1.html.twig');
    }
    #[Route('/form', name: 'app_essai')]
    public function index_form(Request $request): Response
    {
        $form = $this->createForm(UserformType::class);

        return $this->render('front/seconnect.html.twig', [
            'UserformType' => $form->createView(),
        ]);
    }
    
    
}
