<?php

namespace App\Repository;

use App\Entity\ArticleRating;
use Doctrine\Bundle\DoctrineBundle\Repository\ServiceEntityRepository;
use Doctrine\Persistence\ManagerRegistry;

/**
 * @extends ServiceEntityRepository<ArticleRating>
 */
class ArticleRatingRepository extends ServiceEntityRepository
{
    public function __construct(ManagerRegistry $registry)
    {
        parent::__construct($registry, ArticleRating::class);
    }

    //    /**
    //     * @return ArticleRating[] Returns an array of ArticleRating objects
    //     */
    //    public function findByExampleField($value): array
    //    {
    //        return $this->createQueryBuilder('a')
    //            ->andWhere('a.exampleField = :val')
    //            ->setParameter('val', $value)
    //            ->orderBy('a.id', 'ASC')
    //            ->setMaxResults(10)
    //            ->getQuery()
    //            ->getResult()
    //        ;
    //    }

    //    public function findOneBySomeField($value): ?ArticleRating
    //    {
    //        return $this->createQueryBuilder('a')
    //            ->andWhere('a.exampleField = :val')
    //            ->setParameter('val', $value)
    //            ->getQuery()
    //            ->getOneOrNullResult()
    //        ;
    //    }

    public function findTopRatedArticles(int $limit = 5): array
    {
        return $this->createQueryBuilder('a')
            ->select('a, AVG(r.rating) as avgRating')  // 📌 Calculer la moyenne des notes
            ->leftJoin('a.ratings', 'r')              // 📌 Joindre la table de notation
            ->groupBy('a.id')
            ->orderBy('avgRating', 'DESC')            // 📌 Trier par moyenne des notes
            ->setMaxResults($limit)
            ->getQuery()
            ->getResult();
    }











}
