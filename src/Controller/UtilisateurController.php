<?php

namespace App\Controller;
use Symfony\Component\PasswordHasher\Hasher\UserPasswordHasherInterface;
use App\Entity\Utilisateur;
use App\Entity\Article;
use Doctrine\Persistence\ManagerRegistry;
use App\Form\UserformType;
use App\Form\DoctorformType;
use App\Form\EdituserformType;
use App\Form\EditPatientformType;

use App\Form\loginformType;
use App\Repository\UtilisateurRepository;
use Symfony\Component\HttpFoundation\Request;
use Symfony\Bundle\FrameworkBundle\Controller\AbstractController;
use Symfony\Component\HttpFoundation\Response;
use Symfony\Component\Routing\Attribute\Route;
use Doctrine\ORM\EntityManagerInterface;
use Symfony\Component\String\Slugger\SluggerInterface;
use Symfony\Component\HttpFoundation\File\Exception\FileException;
use Symfony\Component\Security\Http\Authentication\AuthenticationUtils;
use App\Entity\RendezVous;
use App\Form\RendezVousType;
use App\Repository\MatchingRepository;
use Symfony\Bundle\SecurityBundle\Security;
use App\Entity\Planification;
use App\Form\PlanificationType;
use Symfony\Component\HttpFoundation\File\UploadedFile;

final class UtilisateurController extends AbstractController
{
    #[Route('/front', name: 'front_index')]
    public function index_front(EntityManagerInterface $entityManager,Request $request, EntityManagerInterface $entityManager1,UtilisateurRepository $userRepository): Response
    {   $users=$userRepository->findBy(['role' => 'ROLE_FREELANCER']);
        $Plannification = new Planification();
        $PlannificationForm = $this->createForm(PlanificationType::class, $Plannification);

        $PlannificationForm->handleRequest($request);
        if ($PlannificationForm->isSubmitted() && $PlannificationForm->isValid()) {
            $entityManager->persist($Plannification);
            $entityManager->flush();

            return $this->redirectToRoute('Plannification_success');
        }
        $rendezVou = new RendezVous();
       
        $form = $this->createForm(RendezVousType::class, $rendezVou);
        $form->handleRequest($request);

        if ($form->isSubmitted() && $form->isValid()) {
            $user = $this->getUser();

            // Associer l'utilisateur connecté comme patient
            if ($user) {
                $rendezVou->setIdPatient($user); // Assure-toi que la relation est bien définie dans ton entity
            }
        
            $rendezVou->setMotif($form->get('motif')->getData());
            $rendezVou->setStatut($form->get('statut')->getData());
            $rendezVou->setMode($form->get('mode')->getData());
            $entityManager1->persist($rendezVou);
            $entityManager1->flush();

            return $this->redirectToRoute('doctor_index', [], Response::HTTP_SEE_OTHER);
        }

        $articles = $entityManager->getRepository(Article::class)->findAll();
        return $this->render('front/index.html.twig', [
            'articles' => $articles,
            'freelancers' => $users,
            'rendez_vou' => $rendezVou,
            'form' => $form,
            'PlannificationForm' => $PlannificationForm->createView(),

        ]);
       
    }
    #[Route('/front1', name: 'front_index1')]
    public function index_front1()
    {
    
        return $this->render('back/index.html.twig');
       
    }

    #[Route('/utilisateur/login', name: 'app_login')]
    public function login(
        Request $request,
        UtilisateurRepository $utilisateurRepository,
        UserPasswordHasherInterface $passwordHasher,
        AuthenticationUtils $authenticationUtils
    ): Response {
         // Formulaire de connexion
         $userLogin = new Utilisateur();
         $loginForm = $this->createForm(LoginformType::class, $userLogin);
         $loginForm->handleRequest($request); 
        $error = $authenticationUtils->getLastAuthenticationError();
        $lastUsername = $authenticationUtils->getLastUsername();       
        if ($loginForm->isSubmitted() && $loginForm->isValid()) {
            $email = $userLogin->getEmail();
            $password = $userLogin->getPassword();

            $userFromDb = $utilisateurRepository->findOneBy(['email' => $email]);

            if ($userFromDb && $passwordHasher->isPasswordValid($userFromDb, $password)) {
                return $this->redirectToRoute('front_index');
            } else {
                $this->addFlash('error', 'Email ou mot de passe incorrect.');
            }
        }

    
        return $this->render('front/seconnect.html.twig', [
            'loginForm' => $loginForm->createView(),
            'signupForm' => null, // On ne passe pas le formulaire d'inscription ici
            'last_username' => $lastUsername,
            'error' => $error,
        ]);
    }
    #[Route('/utilisateur/signup', name: 'app_signup')]
    public function signup(
        Request $request,
        UtilisateurRepository $utilisateurRepository,
        UserPasswordHasherInterface $passwordHasher,
        SluggerInterface $slugger,
        ManagerRegistry $doctrine
    ): Response {
        $em = $doctrine->getManager();
        $userSignup = new Utilisateur();
        $signupForm = $this->createForm(UserformType::class, $userSignup);
        $signupForm->handleRequest($request);
    
        if ($request->isMethod('POST') && $signupForm->isSubmitted() && $signupForm->isValid()) {
            $hashedPassword = $passwordHasher->hashPassword($userSignup, $userSignup->getPassword());
            $userSignup->setPassword($hashedPassword);
            $userSignup->setRole($signupForm->get('role')->getData());
    
            // Gestion de l'image
            $image = $signupForm->get('image')->getData();
            if ($image) {
                $originalFilename = pathinfo($image->getClientOriginalName(), PATHINFO_FILENAME);
                $safeFilename = $slugger->slug($originalFilename);
                $newFilename = $safeFilename . '-' . uniqid() . '.' . $image->guessExtension();
                try {
                    $image->move($this->getParameter('User_directory'), $newFilename);
                } catch (FileException $e) {
                    $this->addFlash('error', 'Erreur lors du téléchargement de l’image.');
                }
                $userSignup->setImage($newFilename);
            }
    
            $em->persist($userSignup);
            $em->flush();
            $this->addFlash('success', 'Inscription réussie, vous pouvez vous connecter.');
    
            return $this->redirectToRoute('app_signup');
        }
    
        return $this->render('front/seconnect.html.twig', [
            'loginForm' => null, // On ne passe pas le formulaire de connexion ici
            'signupForm' => $signupForm->createView(),
            'last_username' => null,
            'error' => null,
        ]);
    }
        

    
    #[Route('/utilisateur/edit/{id}', name: 'edit_user')]
    public function edit(
        Request $request,
        EntityManagerInterface $entityManager,
        UserPasswordHasherInterface $passwordHasher,
        int $id
    ): Response {

        $user = $entityManager->getRepository(Utilisateur::class)->find($id);
        if (!$user) {
            throw $this->createNotFoundException('Utilisateur non trouvé');
        }

        $ancienMotDePasse = $user->getPassword();
        $form = $this->createForm(EdituserformType::class, $user);
        $form->handleRequest($request);

        if ($form->isSubmitted() && $form->isValid()) {
            // Vérifier si un nouveau mot de passe a été saisi
            if (!empty($user->getPassword())) {
                $hashedPassword = $passwordHasher->hashPassword($user, $user->getPassword());
                $user->setPassword($hashedPassword);
            } else {
                $user->setPassword($ancienMotDePasse); // Garde l'ancien mot de passe
            }

            // Gérer l'upload de l'image
            /** @var UploadedFile $imageFile */
            $imageFile = $form->get('image')->getData();
            if ($imageFile) {
                $destination = $this->getParameter('kernel.project_dir') . '/public/utilisateur/img';
                $newFilename = uniqid() . '.' . $imageFile->guessExtension();

                try {
                    $imageFile->move($destination, $newFilename);
                    $user->setImage($newFilename);  // Enregistre le chemin dans la base de données
                } catch (FileException $e) {
                    // Gère l'erreur si l'upload échoue
                }
            }

            // Sauvegarde les modifications en base de données
            $entityManager->flush();
            return $this->redirectToRoute('view_user');
        }

        return $this->render('utilisateur/edit.html.twig', [
            'form' => $form->createView(),
            'user' => $user,
        ]);
    }

    #[Route('/utilisateur/edit1/{id}', name: 'edit_user1')]
    public function editUser(
        Request $request,
        EntityManagerInterface $entityManager,
        UserPasswordHasherInterface $passwordHasher,
        int $id): Response 
    {
        $user= $entityManager->getRepository(Utilisateur::class)->find($id);
        
        if (!$user) {
            throw $this->createNotFoundException('Utilisateur non trouvé');
        }
    
        // Sauvegarde de l'ancien mot de passe
        $ancienMotDePasse = $user->getPassword();
        $form = $this->createForm(EditPatientformType::class, $user);
        $form->handleRequest($request);
    
        if ($form->isSubmitted() && $form->isValid()) {
            // Vérifie et gère le mot de passe
            $nouveauMotDePasse = $form->get('mot_de_passe')->getData();
    
            if ($nouveauMotDePasse !== null && $nouveauMotDePasse !== '') {
                // Hash le nouveau mot de passe
                $hashedPassword = $passwordHasher->hashPassword($user, $nouveauMotDePasse);
                $user->setPassword($hashedPassword);
            } else {
                // Conserve l'ancien mot de passe
                $user->setPassword($ancienMotDePasse);
            }
    
            // Gérer l'upload de l'image
            /** @var UploadedFile $imageFile */
            $imageFile = $form->get('image')->getData();
            if ($imageFile) {
                $destination = $this->getParameter('kernel.project_dir') . '/public/utilisateur/img';
                $newFilename = uniqid() . '.' . $imageFile->guessExtension();
    
                try {
                    $imageFile->move($destination, $newFilename);
                    $user->setImage($newFilename);  // Enregistre le chemin dans la base de données
                } catch (FileException $e) {
                    // Gère l'erreur si l'upload échoue
                }
            }
    
            // Sauvegarde les modifications en base de données
            $entityManager->flush();
            return $this->redirectToRoute('profile_user');
        }
    
        return $this->render('utilisateur/editPatient.html.twig', [
            'form' => $form->createView(),
            'user' => $user,
        ]);
    }

    

    #[Route('/utilisateur', name: 'app_utilisateur')]
    public function index(): Response
    {
        return $this->render('/front', [
            'controller_name' => 'UtilisateurController',
        ]);
    }
    
    #[Route('/utilisateur/table', name: 'view_user')]
    public function view(UtilisateurRepository $u): Response
    {
        $user=$u->findAll();
        return $this->render('utilisateur/list.html.twig', [
            'users' => $user,
        ]);
    }
    #[Route('/utilisateur/profile', name: 'profile_user')]
    public function profile(): Response
    {
        return $this->render('utilisateur/profile.html.twig');
    }

    

    #[Route('/utilisateur/delete/{id}', name: 'delete_user')]
    public function delete(EntityManagerInterface $entityManager, int $id): Response
    {
        $user = $entityManager->getRepository(Utilisateur::class)->find($id);
        
        if (!$user) {
            throw $this->createNotFoundException('Utilisateur non trouvé');
        }
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
            $data = $form->getData();
            $Image = $form->get('image')->getData();
            if (!$data->getDateNaissance()) {
                $this->addFlash('error', 'La date de naissance est obligatoire.');
                return $this->redirectToRoute('/utilisateur/add_doctor');
            }
            if ($Image) {
                $originalFilename = pathinfo($Image->getClientOriginalName(), PATHINFO_FILENAME);
                $safeFilename = $slugger->slug($originalFilename);
                $newFilename = $safeFilename.'-'.uniqid().'.'.$Image->guessExtension();
                try {
                    $Image->move(
                        $this->getParameter('User_directory'), $newFilename);
                } catch (FileException $e) {
                   
                }

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
    #[Route('/freelancers1', name: 'app_freelancers')]
    public function listFreelancers(UtilisateurRepository $utilisateurRepository): Response
    {
        // Fetch users where role = 'Freelancer'
        $freelancers = $utilisateurRepository->findBy(['role' => 'ROLE_FREELANCER']);

        return $this->render('front/index.html.twig', [
            'freelancers' => $freelancers,
        ]);
    }
    #[Route('/freelancers2', name: 'freelancer_list')]
    public function index12(EntityManagerInterface $entityManager): Response
    {
        // Fetch only users with role 'ROLE_FREELANCER'
        $freelancers = $entityManager->getRepository(Utilisateur::class)->findBy([
            'role' => 'ROLE_FREELANCER'
        ]);

        return $this->render('accueil.html.twig', [
            'freelancers' => $freelancers,
        ]);
    }

}
