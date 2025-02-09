<?php

namespace App\Controller;
use Symfony\Component\PasswordHasher\Hasher\UserPasswordHasherInterface;
use App\Entity\Utilisateur;
use Doctrine\Persistence\ManagerRegistry;
use App\Form\UserformType;
use App\Form\DoctorformType;
use App\Form\EdituserformType;
use App\Form\loginformType;
use App\Repository\UtilisateurRepository;
use Symfony\Component\HttpFoundation\Request;
use Symfony\Bundle\FrameworkBundle\Controller\AbstractController;
use Symfony\Component\HttpFoundation\Response;
use Symfony\Component\Routing\Attribute\Route;
use Doctrine\ORM\EntityManagerInterface;
use Symfony\Component\String\Slugger\SluggerInterface;
use Symfony\Component\HttpFoundation\File\Exception\FileException;
use Symfony\Component\HttpFoundation\File\UploadedFile;
final class UtilisateurController extends AbstractController
{

    private $passwordHasher;
    #[Route('/utilisateur', name: 'app_utilisateur')]
    public function index(): Response
    {
        return $this->render('/front', [
            'controller_name' => 'UtilisateurController',
        ]);
    }
    #[Route('/utilisateur/add', name: 'app_login')]
    public function add(Request $request, ManagerRegistry $doctrine,UtilisateurRepository $utilisateurRepository,Request $request1,SluggerInterface $slugger): Response
    {
        $em=$doctrine->getManager();
        $user = new Utilisateur();
        $form = $this->createForm(UserformType::class, $user);
        $form->handleRequest($request);
        $user1 = new Utilisateur();
        $form1 = $this->createForm(loginformType::class, $user1);
        $form1->handleRequest($request1);

        if ($form1->isSubmitted() && $form1->isValid()) {
            // Récupérer les données du formulaire
            $email = $user1->getEmail();
            $password = $user1->getMotDePasse();

            // Vérification des informations d'identification
            if ($email && $password) {
                // Utilisation de la méthode findByEmailAndPassword pour valider les identifiants
                $user1 = $utilisateurRepository->findByEmailAndPassword($email, $password);
                if ($user1) {
                    // Redirection en cas de succès
                    return $this->redirectToRoute('front_index');
                } else {
                    $this->addFlash('error', 'Email ou mot de passe incorrect.');
                    return $this->redirectToRoute('/utilisateur/add'); 
                }
            }
        }
        if ($form->isSubmitted() && $form->isValid()) {
            $Image = $form->get('image')->getData();

            // this condition is needed because the 'brochure' field is not required
            // so the PDF file must be processed only when a file is uploaded
            if ($Image) {
                $originalFilename = pathinfo($Image->getClientOriginalName(), PATHINFO_FILENAME);
                // this is needed to safely include the file name as part of the URL
                $safeFilename = $slugger->slug($originalFilename);
                $newFilename = $safeFilename.'-'.uniqid().'.'.$Image->guessExtension();

                // Move the file to the directory where brochures are stored
                try {
                    $Image->move(
                        $this->getParameter('User_directory'), $newFilename);
                } catch (FileException $e) {
                    // ... handle exception if something happens during file upload
                }

                // updates the 'brochureFilename' property to store the PDF file name
                // instead of its contents
                $user->setImage($newFilename);
            }
            $em->persist($user);
            $em->flush();
            return $this->redirectToRoute('front_index'); 
        }
        
        return $this->render('front/seconnect.html.twig', [
            'form1' => $form1->createView(),
            'form' => $form,

        ]);
    }
    #[Route('/logout', name: 'app_logout', methods: ['GET'])]
    public function logout(): void
    {
        throw new \LogicException('This method can be blank - it will be intercepted by the logout key on your firewall.');
    }
    #[Route('/utilisateur/table', name: 'view_user')]
    public function view(UtilisateurRepository $u): Response
    {
        $user=$u->findAll();
        return $this->render('utilisateur/list.html.twig', [
            'users' => $user,
        ]);
    }

    #[Route('/utilisateur/edit/{id}', name: 'edit_user')]
    public function edit(Request $request, EntityManagerInterface $entityManager, int $id): Response
    {
        $user = $entityManager->getRepository(Utilisateur::class)->find($id);
        if (!$user) {
            throw $this->createNotFoundException('Utilisateur non trouvé');
        }
        $ancienMotDePasse = $user->getMotDePasse();
        $form = $this->createForm(EdituserformType::class, $user);
        $form->handleRequest($request);
        
        if ($form->isSubmitted() && $form->isValid()) {
            if (empty($user->getMotDePasse())) {
                $user->setMotDePasse($ancienMotDePasse);
            }

            $entityManager->flush(); 
            return $this->redirectToRoute('view_user'); 
        }
        return $this->render('utilisateur/edit.html.twig', [
            'form' => $form->createView(),
            'user' => $user,
        ]); 
    }


    #[Route('/utilisateur/delete/{id}', name: 'delete_user')]
    public function delete(EntityManagerInterface $entityManager, int $id): Response
    {
        // Récupérer l'auteur à supprimer
        $user = $entityManager->getRepository(Utilisateur::class)->find($id);
        
        if (!$user) {
            throw $this->createNotFoundException('Auteur non trouvé');
        }

        // Supprimer l'auteur
        $entityManager->remove($user);
        $entityManager->flush(); 

        return $this->redirectToRoute('view_user'); 
    }
    #[Route('/utilisateur/add_doctor', name: 'add_doctor')]
    public function add_doctor(Request $request, ManagerRegistry $doctrine,SluggerInterface $slugger): Response
    {
        $em=$doctrine->getManager();
        $user = new Utilisateur();
        $form = $this->createForm(DoctorformType::class, $user);
        $form->handleRequest($request);
        if ($form->isSubmitted() && $form->isValid()) {
            $Image = $form->get('image')->getData();

            // this condition is needed because the 'brochure' field is not required
            // so the PDF file must be processed only when a file is uploaded
            if ($Image) {
                $originalFilename = pathinfo($Image->getClientOriginalName(), PATHINFO_FILENAME);
                // this is needed to safely include the file name as part of the URL
                $safeFilename = $slugger->slug($originalFilename);
                $newFilename = $safeFilename.'-'.uniqid().'.'.$Image->guessExtension();

                // Move the file to the directory where brochures are stored
                try {
                    $Image->move(
                        $this->getParameter('User_directory'), $newFilename);
                } catch (FileException $e) {
                    // ... handle exception if something happens during file upload
                }

                // updates the 'brochureFilename' property to store the PDF file name
                // instead of its contents
                $user->setImage($newFilename);
            }
            $em->persist($user);
            $em->flush();
            return $this->redirectToRoute('back_index'); 
        }
        return $this->render('utilisateur/add_doctor.html.twig', [
            'form' => $form,

        ]);
    }

}
