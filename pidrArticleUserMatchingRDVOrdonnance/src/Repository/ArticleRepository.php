<?php

namespace App\Repository;

use App\Entity\Article;
use Doctrine\Bundle\DoctrineBundle\Repository\ServiceEntityRepository;
use Doctrine\Persistence\ManagerRegistry;

/**
 * @extends ServiceEntityRepository<Article>
 */
class ArticleRepository extends ServiceEntityRepository
{
    public function __construct(ManagerRegistry $registry)
    {
        parent::__construct($registry, Article::class);
    }
    public function findTopRatedArticles(int $limit = 5): array
    {
        return $this->createQueryBuilder('a')
            ->leftJoin('a.ratings', 'r') // Joindre la table Rating
            ->addSelect('AVG(r.rating) as avgRating') // Calculer la moyenne
            ->groupBy('a.id') // Grouper par article
            ->orderBy('avgRating', 'DESC') // Trier par la moyenne des notes
            ->setMaxResults($limit) // Limiter les résultats
            ->getQuery()
            ->getResult();
    }
    public function getArticleStatistics(): array
    {
        return $this->createQueryBuilder('a')
            ->select(
                'COUNT(a.id) as totalArticles', // Nombre total d'articles
                'SUM(a.nbreVue) as totalVues',  // Nombre total de vues
                'AVG(a.nbreVue) as moyenneVues' // Moyenne des vues par article
            )
            ->getQuery()
            ->getSingleResult();
    }
    

    public function getArticleViews(): array
    {
        return $this->createQueryBuilder('a')
            ->select('a.titre', 'a.nbreVue')
            ->orderBy('a.nbreVue', 'DESC') // Trier par nombre de vues (optionnel)
            ->getQuery()
            ->getResult();
    }
    
    //    /**
    //     * @return Article[] Returns an array of Article objects
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

    //    public function findOneBySomeField($value): ?Article
    //    {
    //        return $this->createQueryBuilder('a')
    //            ->andWhere('a.exampleField = :val')
    //            ->setParameter('val', $value)
    //            ->getQuery()
    //            ->getOneOrNullResult()
    //        ;
    //    }
}
