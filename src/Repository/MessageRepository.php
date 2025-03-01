<?php

namespace App\Repository;

use App\Entity\Message;
use Doctrine\Bundle\DoctrineBundle\Repository\ServiceEntityRepository;
use Doctrine\Persistence\ManagerRegistry;

/**
 * @extends ServiceEntityRepository<Message>
 */
class MessageRepository extends ServiceEntityRepository
{
    public function __construct(ManagerRegistry $registry)
    {
        parent::__construct($registry, Message::class);
    }

    public function findMessagesBetweenUsers($loggedInUser, $selectedUser)
{
    return $this->createQueryBuilder('m')
        ->where('(m.sender = :loggedInUser AND m.receiver = :selectedUser)')
        ->orWhere('(m.sender = :selectedUser AND m.receiver = :loggedInUser)')
        ->setParameter('loggedInUser', $loggedInUser)
        ->setParameter('selectedUser', $selectedUser)
        ->orderBy('m.createdAt', 'ASC')
        ->getQuery()
        ->getResult();
}
public function findConversation($userId, $otherUserId)
{
    return $this->createQueryBuilder('m')
        ->where('(m.sender = :user AND m.receiver = :otherUser)')
        ->orWhere('(m.sender = :otherUser AND m.receiver = :user)')
        ->setParameter('user', $userId)
        ->setParameter('otherUser', $otherUserId)
        ->orderBy('m.createdAt', 'ASC')
        ->getQuery()
        ->getResult();
}



    //    /**
    //     * @return Message[] Returns an array of Message objects
    //     */
    //    public function findByExampleField($value): array
    //    {
    //        return $this->createQueryBuilder('m')
    //            ->andWhere('m.exampleField = :val')
    //            ->setParameter('val', $value)
    //            ->orderBy('m.id', 'ASC')
    //            ->setMaxResults(10)
    //            ->getQuery()
    //            ->getResult()
    //        ;
    //    }

    //    public function findOneBySomeField($value): ?Message
    //    {
    //        return $this->createQueryBuilder('m')
    //            ->andWhere('m.exampleField = :val')
    //            ->setParameter('val', $value)
    //            ->getQuery()
    //            ->getOneOrNullResult()
    //        ;
    //    }
}
