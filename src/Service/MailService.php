<?php
namespace App\Service;

use Symfony\Component\Mailer\MailerInterface;
use Symfony\Component\Mime\Email;

class MailerService
{
    private MailerInterface $mailer;

    public function __construct(MailerInterface $mailer)
    {
        $this->mailer = $mailer;
    }

    public function sendEmail(string $to,)
    {
        $email = (new Email())
            ->from('khadija.benjaafar@esprit.tn')
            ->to($to)
            ->subject('Rendez-Vous')
            ->text('Votre rendez-vous est confirmé.')
            ->html('<p> Votre rendez-vous est confirmé </p>
            <p> Rejoignez le rendez-vous en cliquant sur le lien ci-dessous : </p>
           <p> <a href="https://meet.jit.si/myRoom">Rejoindre la réunion</a> </p>'); 

        $this->mailer->send($email);
    }

    // Envoi d'un email d'annulation de rendez-vous
    public function sendCancellationEmail(string $to)
    {
        $email = (new Email())
            ->from('khadija.benjaafar@esprit.tn')
            ->to($to)
            ->subject('Rendez-Vous')
            ->text('Votre rendez-vous a été annulé.')
            ->html('<p>Votre rendez-vous est annulé.</p>
                    <p>Si vous souhaitez prendre un nouveau rendez-vous, veuillez nous contacter.</p>'); 

        $this->mailer->send($email);
    }
}
