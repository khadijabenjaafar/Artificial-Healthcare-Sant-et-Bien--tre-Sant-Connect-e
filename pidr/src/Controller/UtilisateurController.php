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
use App\Entity\RendezVous;
use App\Form\RendezVousType;
use App\Entity\Planification;
use App\Form\PlanificationType;
use App\Repository\CommentaireRepository;
use App\Service\JWTService;
use App\Service\SendMailService;
use Symfony\Component\HttpFoundation\File\UploadedFile;
use Symfony\Component\HttpFoundation\JsonResponse;
use Symfony\Component\Serializer\SerializerInterface;
use Psr\Log\LoggerInterface;
use Symfony\Component\Notifier\TexterInterface;
use Symfony\Component\Notifier\Message\SmsMessage;

final class UtilisateurController extends AbstractController
{

    public function __construct()
    {
    }
    #[Route('/front', name: 'front_index')]
    public function index_front(EntityManagerInterface $entityManager, Request $request, EntityManagerInterface $entityManager1, UtilisateurRepository $userRepository, CommentaireRepository $CommentaireRepository): Response
    {
        $users = $userRepository->findBy(['role' => 'ROLE_FREELANCER']);
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
            if ($user) {
                $rendezVou->setIdPatient($user); // Assure-toi que la relation est bien définie dans ton entity
            }

            $rendezVou->setMotif($form->get('motif')->getData());
            $rendezVou->setStatut($form->get('statut')->getData());
            $rendezVou->setMode($form->get('mode')->getData());
            $entityManager1->persist($rendezVou);
            $entityManager1->flush();

            return $this->redirectToRoute('doctor_rendezVous', [], Response::HTTP_SEE_OTHER);
        }

        $articles = $entityManager->getRepository(Article::class)->findAll();
        return $this->render('front/index.html.twig', [
            'articles' => $articles,
            'freelancers' => $users,
            'rendez_vou' => $rendezVou,
            'form' => $form,
            'PlannificationForm' => $PlannificationForm->createView(),
            'commentaireRepository' => $CommentaireRepository,

        ]);
    }

    #[Route('/utilisateur/login', name: 'app_login')]
    public function login(
        Request $request,
        UtilisateurRepository $utilisateurRepository,
        UserPasswordHasherInterface $passwordHasher
    ): Response {
        $userLogin = new Utilisateur();
        $loginForm = $this->createForm(LoginformType::class, $userLogin);
        $loginForm->handleRequest($request);
        // Affiche la méthode de la requête dans les logs
        error_log('Méthode de la requête : ' . $request->getMethod());  // Doit être "POST"

        if ($loginForm->isSubmitted()) {
            $email = $userLogin->getEmail();
            $password = $userLogin->getPassword();
            $userFromDb = $utilisateurRepository->findOneBy(['email' => $email]);

            if (!$userFromDb) {
                $this->addFlash('error', 'Utilisateur non trouvé.');
                return $this->redirectToRoute('app_login');
            }

            // Vérification de l'authentification par reconnaissance faciale
            if ($request->request->get('use_face_id') === 'yes') {
                $capturedImage = $request->get('captured_image_login');

                if ($capturedImage) {
                    $data = base64_decode(preg_replace('#^data:image/\w+;base64,#i', '', $capturedImage));
                    $newFilename = $email . '.jpg';

                    try {
                        file_put_contents($this->getParameter('faces') . '/' . $newFilename, $data);
                    } catch (FileException $e) {
                        error_log("❌ Erreur lors de l'enregistrement de l'image.");
                    }
                    $uploadedImage = $this->getParameter('faces') . '/' . $newFilename;

                    $knownImage = $this->getParameter('uploads') . '/' . $email . '.jpg'; // Image stockée en base

                    // Exécution du script Python
                    $output = shell_exec("python faceid.py $uploadedImage $knownImage");

                    // Décodage du JSON
                    $result = json_decode($output, true);

                    if ($result && isset($result['match']) && $result['match'] === 'true') {
                        return $this->redirectToRoute('front_index');
                    } else {
                        $this->addFlash('error', 'Échec de l\'authentification par reconnaissance faciale.');
                        return $this->redirectToRoute('app_login');
                    }
                }
            }
            else {
                if ($passwordHasher->isPasswordValid($userFromDb, $password)) {

                    return $this->redirectToRoute('front_index');
                } else {
                    $this->addFlash('error', 'Email ou mot de passe incorrect.');
                }
            }
        } else {
            error_log("📭 Formulaire non soumis ou invalide");
        }
        return $this->render('front/seconnect.html.twig', [
            'loginForm' => $loginForm->createView(),
            'signupForm' => null,

        ]);
    }

    #[Route('/utilisateur/signup', name: 'app_signup')]
    public function signup(Request $request,
     UtilisateurRepository $utilisateurRepository,
      UserPasswordHasherInterface $passwordHasher, 
      ManagerRegistry $doctrine, SendMailService $mail,
      JWTService $jwt,TexterInterface $texter): Response
    {
        $user = new Utilisateur();
        $signupForm = $this->createForm(UserFormType::class, $user);
        $signupForm->handleRequest($request);
        $em = $doctrine->getManager();
        if ($signupForm->isSubmitted() && $signupForm->isValid()) {
            // Handle captured image
            $capturedImage = $request->get('captured_image');

            if ($capturedImage) {
                $data = base64_decode(preg_replace('#^data:image/\w+;base64,#i', '', $capturedImage));
                $newFilename = $user->getEmail() . '.jpg';

                try {
                    file_put_contents($this->getParameter('uploads') . '/' . $newFilename, $data);
                } catch (FileException $e) {
                    // Handle exception if something happens during file upload
                }
                $user->setImage($newFilename);
            }

            // Save the user to the database
            $hashedPassword = $passwordHasher->hashPassword($user, $user->getPassword());
            $user->setPassword($hashedPassword);

          
            $header=['typ'=>'JWT','alg'=>'HS256'];
            $payload=['user_id'=>$user->getId()];
            $token=$jwt->generate($header,$payload,$this->getParameter('app.jwtsecret'));  
            
            $mail->send('noreplay@ClinicFlow.com',
                 $user->getEmail(),
                 'Activation de votre compte sur ClinicFlow',
                 'register',
                  compact('user','token')
                );
                $phoneNumber = $user->getNumTel();
                if ($phoneNumber) {
                    try {
                        $verificationCode = random_int(100000, 999999); // Code à 6 chiffres
        
                        // Envoi du SMS avec Symfony Notifier
                        $sms = new SmsMessage(
                            $phoneNumber, 
                            "Votre code de vérification est : $verificationCode"
                        );
                        //$user->setCodeVerif($verifica);
                        $texter->send($sms);
                        $user->setCodeVerif((string) $verificationCode);
                        // Stockage du code en session
                        $request->getSession()->set('phone_verification_code', $verificationCode);
                    } catch (\Exception $e) {
                        $this->addFlash('danger', 'Erreur lors de l\'envoi du SMS : ' . $e->getMessage());
                    }
                }
                $em->persist($user);
                $em->flush();
        
            return $this->redirectToRoute('app_login');
        }
        return $this->render('front/seconnect.html.twig', [
            'signupForm' => $signupForm->createView(),
            'loginForm' => null,
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
        int $id
    ): Response {
        $user = $entityManager->getRepository(Utilisateur::class)->find($id);

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
            return $this->redirectToRoute('app_login');
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
        return $this->render('utilisateur/list.html.twig', [
            'users' =>  $u->findAll(),
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
    public function add_doctor(Request $request, UserPasswordHasherInterface $passwordHasher,    ManagerRegistry $doctrine, SluggerInterface $slugger): Response
    {
        $em = $doctrine->getManager();
        $user = new Utilisateur();
        $form = $this->createForm(DoctorformType::class, $user);
        $form->handleRequest($request);
        if ($form->isSubmitted() && $form->isValid()) {
            $data = $form->getData();
            $hashedPassword = $passwordHasher->hashPassword($user, $user->getPassword());
            $user->setPassword($hashedPassword);
            $user->setRole($form->get('role')->getData());
            $Image = $form->get('image')->getData();
            if (!$data->getDateNaissance()) {
                $this->addFlash('error', 'La date de naissance est obligatoire.');
                return $this->redirectToRoute('/utilisateur/add_doctor');
            }
            if ($Image) {
                $originalFilename = pathinfo($Image->getClientOriginalName(), PATHINFO_FILENAME);
                $safeFilename = $slugger->slug($originalFilename);
                $newFilename = $safeFilename . '-' . uniqid() . '.' . $Image->guessExtension();
                try {
                    $Image->move(
                        $this->getParameter('User_directory'),
                        $newFilename
                    );
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

    #[Route('/utilisateur/verif/{token}', name: 'verif_email')]
    public function verifyUser(string $token, JWTService $jwt,
    UtilisateurRepository $userRepository, EntityManagerInterface $em): Response
    {
        if ($jwt->isValid($token) && !$jwt->isExpired($token) && 
        $jwt->check($token, $this->getParameter('app.jwtsecret')))
        {
            $payload = $jwt->getPayload($token);
            $user = $userRepository->find($payload['user_id']);
            if ($user && !$user->getIsVerified()) {
                $user->setIsVerified(true);
                $em->flush();
                $this->addFlash('success', 'Votre compte est maintenant activé');
                return $this->redirectToRoute('front_index');
            }
        }
        $this->addFlash('danger', 'le token nest pas valide');
        return $this->redirectToRoute('app_login');
    }

    #[Route('/utilisateur/revoiverif', name: 'revoi_verif')]
    public function resendVerificationEmail(UtilisateurRepository $userRepository, SendMailService $mail, JWTService $jwt): Response
    {
        $user = $this->getUser();
        if (!$user) {
            $this->addFlash('danger', 'Vous devez être connecté pour effectuer cette action');
            return $this->redirectToRoute('app_login');
        }

        if ($user->getIsVerified()) {
            $this->addFlash('warning', 'Votre compte est déjà activé');
            return $this->redirectToRoute('front_index');
        }
        $header=['typ'=>'JWT','alg'=>'HS256'];
        $payload=['user_id'=>$user->getId()];
        $token=$jwt->generate($header,$payload,$this->getParameter('app.jwtsecret'));  
        
        $mail->send('noreplay@ClinicFlow.com',
             $user->getEmail(),
             'Activation de votre compte sur ClinicFlow',
             'register',
              compact('user','token')
            );
        
            $this->addFlash('success', 'Email de verification envoier');
            return $this->redirectToRoute('front_index');
    }
    #[Route('utilisateur/stat', name: 'user_stat')]
    public function UserStat(UtilisateurRepository $userRepository): Response
    {
        // Récupérer les statistiques des rendez-vous par jour
        $roles = ['ROLE_ADMIN', 'ROLE_FREELANCER', 'ROLE_MEDECIN','ROLE_PHARMACIEN','ROLE_PATIENT']; // Ajoute les rôles nécessaires
        $stats = [];

        foreach ($roles as $role) {
            $stats[$role] = $userRepository->countUsersByRole($role);
        }
        return $this->render('utilisateur/stat.html.twig', [
            'stats' => $stats,
        ]);
    }
    #[Route('/utilisateur/searchUser', name: 'SearchUser')]
    public function searchUser(Request $request, SerializerInterface $serializer, UtilisateurRepository $u ,LoggerInterface $logger): JsonResponse
    {
        try {
            $searchValue = $request->get('searchValue');
    
            if (!$searchValue) {
                return new JsonResponse(['error' => 'Aucune valeur de recherche fournie.'], 400);
            }
    
            $users = $u->findUserByNom($searchValue);
    
            if (!$users) {
                return new JsonResponse([], 200);
            }
    
            // Utiliser SerializerInterface au lieu de NormalizerInterface
            $jsonData = $serializer->serialize($users, 'json');
    
            return new JsonResponse($jsonData, 200, [], true);
        } catch (\Exception $e) {
            $logger->error('Erreur dans searchUser: ' . $e->getMessage());
            return new JsonResponse(['error' => 'Erreur interne du serveur.'], 500);
        }
    }
    #[Route('/verify-code', name: 'verify_code')]
    public function verifyCode(Request $request, EntityManagerInterface $em): Response
    {
        // Récupérer le code entré par l'utilisateur
        $enteredCode = $request->request->get('code');
        $enteredCode1=((string) $enteredCode);

        $user = $this->getUser();
        if (!$user) {
            $this->addFlash('danger', 'Vous devez être connecté pour effectuer cette action');
            return $this->redirectToRoute('app_login');
        }

        if ($user->getTelVerified()) {
            $this->addFlash('warning', 'Votre compte est déjà activé');
            return $this->redirectToRoute('front_index');
        }
        
        $sentCode = $user->getCodeVerif();
        if ($enteredCode1 === $sentCode) {
            $user->setTelVerified(true);
            $em->flush();

            $this->addFlash('success', 'Code vérifié avec succès !');
            return $this->redirectToRoute('front_index');
        } else {
            $this->addFlash('danger', 'Code incorrecte !');
            return $this->render('security/TelVerified.html.twig', [
                'error' => 'Le code est incorrect, veuillez réessayer.'
            ]);
        }
    }
}
