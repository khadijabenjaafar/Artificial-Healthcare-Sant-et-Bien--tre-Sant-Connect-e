<?php
namespace App\Service;

use Doctrine\ORM\EntityManagerInterface;
use App\Entity\HistoryLog;

class HistoryLogger
{
    private EntityManagerInterface $em;

    public function __construct(EntityManagerInterface $em)
    {
        $this->em = $em;
    }

    public function logAction(string $action, string $entityType)
    {
        $log = new HistoryLog();
        $log->setAction($action);
        $log->setEntityType($entityType);
        $log->setTimestamp(new \DateTime());

        $this->em->persist($log);
        $this->em->flush();
    }
}
