<?php

namespace App\Repository;

use App\Entity\Planification;
use Doctrine\Bundle\DoctrineBundle\Repository\ServiceEntityRepository;
use Doctrine\Persistence\ManagerRegistry;

/**
 * @extends ServiceEntityRepository<Planification>
 */
class PlanificationRepository extends ServiceEntityRepository
{
    public function __construct(ManagerRegistry $registry)
    {
        parent::__construct($registry, Planification::class);
    }

    public function countByStatus(): array
    {
        return $this->createQueryBuilder('p')
            ->select('p.statut AS statut, COUNT(p.id) AS count')
            ->groupBy('p.statut')
            ->orderBy('count', 'DESC') // Trier par ordre décroissant
            ->getQuery()
            ->getResult();
    }

    //    /**
    //     * @return Planification[] Returns an array of Planification objects
    //     */
    //    public function findByExampleField($value): array
    //    {
    //        return $this->createQueryBuilder('p')
    //            ->andWhere('p.exampleField = :val')
    //            ->setParameter('val', $value)
    //            ->orderBy('p.id', 'ASC')
    //            ->setMaxResults(10)
    //            ->getQuery()
    //            ->getResult()
    //        ;
    //    }

    //    public function findOneBySomeField($value): ?Planification
    //    {
    //        return $this->createQueryBuilder('p')
    //            ->andWhere('p.exampleField = :val')
    //            ->setParameter('val', $value)
    //            ->getQuery()
    //            ->getOneOrNullResult()
    //        ;
    //    }
}
