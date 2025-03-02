<?php

namespace App\Repository;

use App\Entity\RendezVous;
use Doctrine\Bundle\DoctrineBundle\Repository\ServiceEntityRepository;
use Doctrine\Persistence\ManagerRegistry;

/**
 * @extends ServiceEntityRepository<RendezVous>
 */
class RendezVousRepository extends ServiceEntityRepository
{
    public function __construct(ManagerRegistry $registry)
    {
        parent::__construct($registry, RendezVous::class);
    }
    
    public function searchRendezVous(string $searchTerm)
{
    return $this->createQueryBuilder('r')
        ->where('r.date_heure LIKE :search OR r.motif LIKE :search OR r.statut LIKE :search')
        ->setParameter('search', '%' . $searchTerm . '%')
        ->orderBy('r.date_heure', 'DESC')
        ->getQuery()
        ->getResult();
}

public function countByDay(): array
{
    return $this->createQueryBuilder('r')
        ->select("DATE(r.date_heure) as jour, COUNT(r.id) as total")
        ->groupBy('jour')
        ->orderBy('jour', 'ASC')
        ->getQuery()
        ->getResult();
}

//    /**
//     * @return RendezVous[] Returns an array of RendezVous objects
//     */
//    public function findByExampleField($value): array
//    {
//        return $this->createQueryBuilder('r')
//            ->andWhere('r.exampleField = :val')
//            ->setParameter('val', $value)
//            ->orderBy('r.id', 'ASC')
//            ->setMaxResults(10)
//            ->getQuery()
//            ->getResult()
//        ;
//    }

//    public function findOneBySomeField($value): ?RendezVous
//    {
//        return $this->createQueryBuilder('r')
//            ->andWhere('r.exampleField = :val')
//            ->setParameter('val', $value)
//            ->getQuery()
//            ->getOneOrNullResult()
//        ;
//    }
}
