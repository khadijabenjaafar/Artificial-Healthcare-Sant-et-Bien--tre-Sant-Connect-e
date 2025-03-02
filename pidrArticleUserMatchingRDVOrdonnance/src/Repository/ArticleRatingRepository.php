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
    public function findTopRatedArticles(int $limit = 5): array
    {
        return $this->createQueryBuilder('a')
            ->orderBy('a.avgRating', 'DESC') // 📌 Trier directement sur la colonne avgRating
            ->setMaxResults($limit)
            ->getQuery()
            ->getResult();
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

    









}
