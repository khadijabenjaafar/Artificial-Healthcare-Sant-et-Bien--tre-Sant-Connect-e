<?php

namespace App\Controller;

use Symfony\Component\Mailer\MailerInterface;
use Symfony\Component\Mime\Email;
use Symfony\Component\HttpFoundation\Response;
use Symfony\Component\Routing\Attribute\Route;
use Symfony\Bundle\FrameworkBundle\Controller\AbstractController;
use App\Entity\Planification;
use Doctrine\ORM\EntityManagerInterface;


final class MailtestController extends AbstractController{
    #[Route('/planification/send-email/{id}', name: 'send_email')]
    public function sendEmail(int $id, MailerInterface $mailer, EntityManagerInterface $entityManager): Response
    {
        $planification = $entityManager->getRepository(Planification::class)->find($id);
    
        if (!$planification) {
            return $this->json(['success' => false, 'message' => 'Planification not found'], 404);
        }
    
        $patientEmail = $planification->getUtilisateur()->getEmail(); // Ensure `getUtilisateur()` returns the patient
        $status = $planification->getStatut();
        
        $subject = "Mise à jour de votre rendez-vous";
        $message = ($status === 'confirmé') ? "Votre rendez-vous a été confirmé." : "Votre rendez-vous a été annulé.";
    
        $email = (new Email())
            ->from('hanaharragi555@gmail.com')
            ->to($patientEmail)
            ->subject($subject)
            ->text($message);
    
        $mailer->send($email);
    
        return $this->json(['success' => true, 'message' => 'Email sent successfully']);
    }
    

}
