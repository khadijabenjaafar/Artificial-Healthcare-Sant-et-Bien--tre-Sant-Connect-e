<?php

namespace App\Controller;

use App\Service\MailerService;
use Symfony\Bundle\FrameworkBundle\Controller\AbstractController;
use Symfony\Component\Routing\Attribute\Route;
use Symfony\Component\HttpFoundation\Response;


class MailController extends AbstractController
{

    #[Route('/email-page', name: 'email_page')]
    public function emailPage(): Response
    {
        return $this->render('email/send_email.html.twig');
    }


    #[Route('/send-email', name: 'send_email')]
    public function sendEmail(MailerService $mailerService):void
    {
        $mailerService->sendEmail('khadija.benjaafar@esprit.tn', 'Sujet de test');

    
    }

}

