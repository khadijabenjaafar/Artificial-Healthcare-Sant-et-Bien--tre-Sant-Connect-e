<?php

namespace App\Repository;

use App\Entity\Consultation;
use Doctrine\Bundle\DoctrineBundle\Repository\ServiceEntityRepository;
use Doctrine\Persistence\ManagerRegistry;

/**
 * @extends ServiceEntityRepository<Consultation>
 */
class ConsultationRepository extends ServiceEntityRepository
{
    public function __construct(ManagerRegistry $registry)
    {
        parent::__construct($registry, Consultation::class);
    }

    public function findExpiredConsultations(\DateTime $now): array
    {
        return $this->createQueryBuilder('c')
            ->where('c.prochain_rdv < :now')
            ->setParameter('now', $now)
            ->getQuery()
            ->getResult();
    }
    

    public function searchConsultation(string $searchTerm)
{
    return $this->createQueryBuilder('c')
        ->where('c.diagnostic LIKE :search OR c.traitement LIKE :search OR c.observation LIKE :search')
        ->setParameter('search', '%' . $searchTerm . '%')
        ->orderBy('c.id', 'DESC')
        ->getQuery()
        ->getResult();
}

public function countByDay(): array
{
    $qb = $this->createQueryBuilder('c')
        ->select("DATE(c.prochain_rdv) as jour, COUNT(c.id) as total")
        ->groupBy('jour')
        ->orderBy('jour', 'ASC')
        ->getQuery();

    return array_column($qb->getResult(), 'total', 'jour');
}




    //    /**
    //     * @return Consultation[] Returns an array of Consultation objects
    //     */
    //    public function findByExampleField($value): array
    //    {
    //        return $this->createQueryBuilder('c')
    //            ->andWhere('c.exampleField = :val')
    //            ->setParameter('val', $value)
    //            ->orderBy('c.id', 'ASC')
    //            ->setMaxResults(10)
    //            ->getQuery()
    //            ->getResult()
    //        ;
    //    }

    //    public function findOneBySomeField($value): ?Consultation
    //    {
    //        return $this->createQueryBuilder('c')
    //            ->andWhere('c.exampleField = :val')
    //            ->setParameter('val', $value)
    //            ->getQuery()
    //            ->getOneOrNullResult()
    //        ;
    //    }
}
