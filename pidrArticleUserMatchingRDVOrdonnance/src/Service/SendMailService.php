<?php 
namespace App\Service;

use Symfony\Component\Mime\Email;
use Symfony\Bridge\Twig\Mime\TemplatedEmail;
use Symfony\Component\Mailer\MailerInterface;

    class SendMailService{
        private $mailer;
        
        public function __construct(MailerInterface $mailer)
        {
            $this->mailer = $mailer;
        }

        public function send (string $from, string $to,
         string $subject, string $template,
        array $context):void
        {
            $email = (new TemplatedEmail())
                ->from($from)
                ->to($to)
                ->subject($subject)
                ->htmlTemplate("utilisateur/$template.html.twig")
                ->context($context);
            $this->mailer->send($email);
        }
        public function sendOr (string $pdfUrl, string $to,string $subject, string $template):void
        {

            $email = (new TemplatedEmail())
            ->from('nourjbeli78@gmail.com')
            ->to('khadija.benjaafar@esprit.tn')
            ->subject($subject)
            ->htmlTemplate("pdf/$template.html.twig")  // Template de l'email
            ->context([
                'pdfUrl' => $pdfUrl,  // Passer le lien PDF au template
            ]);

        // Envoi de l'email
        $this->mailer->send($email);
        }
    }