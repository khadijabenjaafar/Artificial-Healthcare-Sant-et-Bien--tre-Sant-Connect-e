<?php

namespace App\Controller;

use App\Entity\Message;
use App\Entity\Utilisateur;
use Doctrine\ORM\EntityManagerInterface;
use Symfony\Bundle\FrameworkBundle\Controller\AbstractController;
use Symfony\Component\HttpFoundation\JsonResponse;
use Symfony\Component\HttpFoundation\Request;
use Symfony\Component\Routing\Annotation\Route;
use Symfony\Component\HttpFoundation\Response;
use App\Repository\MessageRepository;

#[Route('/chat')]
final class ChatController extends AbstractController
{
    #[Route('/send', name: 'chat_send', methods: ['POST'])]
    public function sendMessage(Request $request, EntityManagerInterface $em): JsonResponse
    {
        try {
            $data = json_decode($request->getContent(), true);
    
            if (!isset($data['receiverId'], $data['content'])) {
                return new JsonResponse(['error' => 'Données manquantes'], 400);
            }
    
            $sender = $this->getUser();
            if (!$sender) {
                return new JsonResponse(['error' => 'Utilisateur non authentifié'], 401);
            }
    
            $receiver = $em->getRepository(Utilisateur::class)->find($data['receiverId']);
            if (!$receiver) {
                return new JsonResponse(['error' => 'Utilisateur non trouvé'], 404);
            }
    
            $message = new Message();
            $message->setSender($sender);
            $message->setReceiver($receiver);
            $message->setContent($data['content']);
            $message->setCreatedAt(new \DateTime()); // ✅ Ajoute la date de création
    
            $em->persist($message);
            $em->flush();
    
            return new JsonResponse(['success' => 'Message envoyé']);
        } catch (\Exception $e) {
            return new JsonResponse(['error' => 'Erreur serveur : ' . $e->getMessage()], 500);
        }
    }
    
 
 #[Route('/messages/{id}', name: 'chat_messages', methods: ['GET'])]
 
public function getMessages($id, MessageRepository $messageRepository): JsonResponse
{
    $messages = $messageRepository->findBy(['receiver' => $id], ['createdAt' => 'ASC']);

    if (!$messages) {
        return $this->json(['success' => false, 'message' => 'Aucun message trouvé.'], 404);
    }

    return $this->json(array_map(function ($message) {
        return [
            'senderName' => $message->getSender()->getNom(), // Remplace 'getUsername()' par la bonne méthode
            'content' => $message->getContent(),
            'time' => $message->getCreatedAt()->format('H:i'),
            'senderId' => $message->getSender()->getId(),
        ];
    }, $messages));
}


    #[Route('/latest-messages', name: 'latest_messages', methods: ['GET'])]
    public function latestMessages(EntityManagerInterface $em): JsonResponse
    {
        $user = $this->getUser();

        if (!$user) {
            return new JsonResponse(['error' => 'Utilisateur non authentifié'], 401);
        }

        // Récupérer les 5 derniers messages reçus
        $messages = $em->getRepository(Message::class)->findBy(
            ['receiver' => $user],
            ['createdAt' => 'DESC'],
            5
        );

        $data = array_map(function ($msg) {
            return [
                'senderId' => $msg->getSender()->getId(),
                'senderName' => $msg->getSender()->getPrenom() . " " . $msg->getSender()->getNom(),
                'senderImage' => '/uploads/profile/' . $msg->getSender()->getImage(),
                'content' => $msg->getContent(),
                'time' => $msg->getCreatedAt()->format('H:i'),
            ];
        }, $messages);

        return new JsonResponse($data);
    }

    #[Route('/', name: 'chat_page', methods: ['GET'])]
    public function chatPage(): Response
    {
        return $this->render('chat/index.html.twig');
    }

    #[Route('/users', name: 'chat_users', methods: ['GET'])]
    public function getUsers(EntityManagerInterface $em): JsonResponse
    {
        $currentUser = $this->getUser();

        $users = $em->getRepository(Utilisateur::class)->findAll();
        $usersList = array_map(fn($user) => [
            'id' => $user->getId(),
            'name' => $user->getPrenom() . " " . $user->getNom(),
            'avatar' => '/uploads/profile/' . $user->getImage(),
        ], $users);

        return new JsonResponse($usersList);
    }
}
