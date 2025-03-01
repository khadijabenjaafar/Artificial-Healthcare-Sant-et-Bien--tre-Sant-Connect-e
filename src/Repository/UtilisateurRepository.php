<?php

namespace App\Repository;

use App\Entity\Utilisateur;
use Doctrine\Bundle\DoctrineBundle\Repository\ServiceEntityRepository;
use Doctrine\Persistence\ManagerRegistry;

/**
 * @extends ServiceEntityRepository<Utilisateur>
 */
class UtilisateurRepository extends ServiceEntityRepository
{
    public function __construct(ManagerRegistry $registry)
    {
        parent::__construct($registry, Utilisateur::class);
    }

    public function countUsersByRole(string $role): int
    {
        return $this->createQueryBuilder('u')
            ->select('count(u.id)')
            ->where('u.role = :role')
            ->setParameter('role', $role)
            ->getQuery()
            ->getSingleScalarResult();
    }
    public function findUserByNom(string $nom): array
    {
        return $this->createQueryBuilder('u')
            ->where('u.nom LIKE :nom')
            ->setParameter('nom', '%' . $nom . '%')
            ->getQuery()
            ->getResult();
    }


    public function findByEmailAndPassword(string $email, string $Password): ?Utilisateur
    {
        $user = $this->findOneBy(['email' => $email]);
        $user1 = $this->findOneBy(['mot_de_passe' => $Password]);
        // Vérifier si l'utilisateur existe
        if ($user == $user1) {
            return $user;
        }

        return null; // Retourner null si l'utilisateur n'est pas trouvé ou si les mots de passe ne correspondent pas
    }

    //    /**
    //     * @return Utilisateur[] Returns an array of Utilisateur objects
    //     */
    //    public function findByExampleField($value): array
    //    {
    //        return $this->createQueryBuilder('u')
    //            ->andWhere('u.exampleField = :val')
    //            ->setParameter('val', $value)
    //            ->orderBy('u.id', 'ASC')
    //            ->setMaxResults(10)
    //            ->getQuery()
    //            ->getResult()
    //        ;
    //    }

    //    public function findOneBySomeField($value): ?Utilisateur
    //    {
    //        return $this->createQueryBuilder('u')
    //            ->andWhere('u.exampleField = :val')
    //            ->setParameter('val', $value)
    //            ->getQuery()
    //            ->getOneOrNullResult()
    //        ;
    //    }
}
