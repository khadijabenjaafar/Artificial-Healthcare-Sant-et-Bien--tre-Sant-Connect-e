<?php

namespace App\Command;

use App\Repository\ConsultationRepository;
use Doctrine\ORM\EntityManagerInterface;
use Symfony\Component\Console\Attribute\AsCommand;
use Symfony\Component\Console\Command\Command;
use Symfony\Component\Console\Input\InputInterface;
use Symfony\Component\Console\Output\OutputInterface;

#[AsCommand(name: 'app:delete-expired-consultations', description: 'Supprime les consultations dont le prochain RDV est dépassé')]
class DeleteExpiredConsultationsCommand extends Command
{
    private ConsultationRepository $consultationRepository;
    private EntityManagerInterface $entityManager;

    public function __construct(ConsultationRepository $consultationRepository, EntityManagerInterface $entityManager)
    {
        parent::__construct();
        $this->consultationRepository = $consultationRepository;
        $this->entityManager = $entityManager;
    }

    protected function execute(InputInterface $input, OutputInterface $output): int
    {
        $expiredConsultations = $this->consultationRepository->findExpiredConsultations();

        foreach ($expiredConsultations as $consultation) {
            $this->entityManager->remove($consultation);
        }

        $this->entityManager->flush();

        $output->writeln(count($expiredConsultations) . ' consultations supprimées.');

        return Command::SUCCESS;
    }
}