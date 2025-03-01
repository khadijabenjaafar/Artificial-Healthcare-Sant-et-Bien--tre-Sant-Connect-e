<?php
// src/Controller/FrontController.php
namespace App\Controller;
use App\Form\UserformType;
use App\Entity\Article;
use App\Entity\Commentaire;
use App\Form\ArticleType;
use App\Form\CommentaireType;
use Symfony\Bundle\FrameworkBundle\Controller\AbstractController;
use Symfony\Component\Routing\Annotation\Route;
use Symfony\Component\HttpFoundation\Response;
use Symfony\Component\HttpFoundation\Request;
use App\Repository\UtilisateurRepository;
use App\Repository\PlanificationRepository;
use App\Repository\MatchingRepository;
use App\Repository\RendezVousRepository;
use Symfony\Bundle\SecurityBundle\Security;
use App\Repository\OrdonnanceRepository;
use App\Repository\FacturationRepository;
use App\Entity\Ordonnance;  
use App\Form\OrdonnanceType;
use App\Form\FacturationType;
use App\Entity\Facturation;
use Doctrine\ORM\EntityManagerInterface;
use App\Repository\ArticleRepository;
use App\Repository\CommentaireRepository;
use App\Entity\Utilisateur;
use App\Entity\Consultation;
use App\Form\ConsultationType;
use App\Repository\ConsultationRepository;
use Symfony\Component\String\Slugger\SluggerInterface;
use App\Entity\Matching;
use App\Form\MatchingType;
use Symfony\Component\HttpFoundation\File\Exception\FileException;


class FrontController extends AbstractController
{
   

    #[Route('/back', name: 'back_index')]
    public function index_back(UtilisateurRepository $userRepository): Response
    {
         // Récupérer les statistiques des rendez-vous par jour
         $roles = ['ROLE_ADMIN', 'ROLE_FREELANCER', 'ROLE_MEDECIN','ROLE_PHARMACIEN','ROLE_PATIENT']; // Ajoute les rôles nécessaires
         $stats = [];
 
         foreach ($roles as $role) {
             $stats[$role] = $userRepository->countUsersByRole($role);
         }
         return $this->render('back/index.html.twig', [
             'stats' => $stats,
         ]);
    }
    #[Route('/doctor/ordonnance/Ajout', name: 'doctor_ordonnance_Ajout')]
    public function doctorOrdonnanceAjout(UtilisateurRepository $utilisateurRepository,Security $security,Request $request,
      EntityManagerInterface $entityManager) {
          $user = $security->getUser();
        if (!$user) {
            throw $this->createAccessDeniedException("Vous devez être connecté pour voir vos rendez-vous.");
        }
        $ordonnance = new Ordonnance();
        $form = $this->createForm(OrdonnanceType::class, $ordonnance);
        $form->handleRequest($request);
        if ($form->isSubmitted() && $form->isValid()) {
            $entityManager->persist($ordonnance);
            $entityManager->flush();

            return $this->redirectToRoute('doctor_ordonnance_Aff', [], Response::HTTP_SEE_OTHER);
        }
         return $this->render('ordonnance/docOrdonnance.html.twig', [
            'form' => $form
        ]);
    }
    #[Route('/doctor/ordonnance/Aff', name: 'doctor_ordonnance_Aff')]
    public function doctorOrdonnanceAff(Security $security,OrdonnanceRepository $ordonnanceRepository) {
        $user = $security->getUser();
        if (!$user) {
            throw $this->createAccessDeniedException("Vous devez être connecté pour voir vos ordonnances.");
        }
        // Requête pour récupérer les ordonnances liées au patient connecté
        $ordonnances = $ordonnanceRepository->createQueryBuilder('o')
            ->join('o.id_consultation', 'c')
            ->join('c.id_rendez_vous', 'r')
            ->where('r.id_patient = :user OR r.id_medecin = :user') 
            ->setParameter('user', $user)
            ->getQuery()
            ->getResult();

        return $this->render('ordonnance/AFFdoc.html.twig', [
            'ordonnances' => $ordonnances,
        ]);
    }

    #[Route('/doctor/matching/Ajout', name: 'doctor_matching_Ajout')]
    public function doctorMatchingAjout(UtilisateurRepository $utilisateurRepository,
    Security $security,Request $request3, EntityManagerInterface $entityManager)
    {
        $user = $security->getUser();
        $matching = new Matching();
        $form4 = $this->createForm(MatchingType::class, $matching);
        $form4->handleRequest($request3);
        if ($form4->isSubmitted() && $form4->isValid()) {
            // Handle CV file upload
            $cvFile = $form4->get('cv')->getData();
    
            if ($cvFile) {
                $newFilename = uniqid() . '.' . $cvFile->guessExtension();
                try {
                    $cvFile->move(
                        $this->getParameter('cv_directory'), // Directory must be configured
                        $newFilename
                    );
                    $matching->setCv($newFilename); // Save filename in database
                } catch (FileException $e) {
                    $this->addFlash('error', 'Erreur lors du téléchargement du fichier.');
                }
            } else {
                $matching->setCv(''); // or set null if your DB allows it
            }
    
            $entityManager->persist($matching);
            $entityManager->flush();
    
            return $this->redirectToRoute('doctor_matching_aff', [], Response::HTTP_SEE_OTHER);
        }
        if (!$user) {
            throw $this->createAccessDeniedException("Vous devez être connecté pour voir vos rendez-vous.");
        }
      
   
        return $this->render('matching/AjoutDoc.html.twig', [
           'matching' => $matching,
            'form4' => $form4->createView()
        ]);
    }
    
    #[Route('/doctor/matching/Aff', name: 'doctor_matching_aff')]
    public function doctorMatchingAff(UtilisateurRepository $utilisateurRepository,MatchingRepository $matchingRepository,Security $security,Request $request3, EntityManagerInterface $entityManager)
    {    
        $user = $security->getUser();
        if (!$user) {
            throw $this->createAccessDeniedException("Vous devez être connecté pour voir vos matchings.");
        }

        $matchings = $matchingRepository->createQueryBuilder('m')
            ->where('m.id_freelancer = :user OR m.utilisateur = :user')
            ->setParameter('user', $user)
            ->getQuery()
            ->getResult();

        return $this->render('matching/AffDoc.html.twig', [
            'matchings' => $matchings,
        ]);
    }

    #[Route('/doctor/plan/Aff', name: 'doctor_plan_Aff')]
    public function doctorPlan(UtilisateurRepository $utilisateurRepository,PlanificationRepository $planificationRepository,
     Security $security)
    {
       
        $user = $security->getUser();
        if (!$user) {
            throw $this->createAccessDeniedException("Vous devez être connecté pour voir vos rendez-vous.");
        }
    
        $freelancers = $utilisateurRepository->findAll(); 
        $planifications = $planificationRepository->findBy(['freelancer' => $freelancers]); // Filtrer par utilisateur
        return $this->render('planification/AffDoc.html.twig', [
            'freelancers' => $freelancers,
            'planifications' => $planifications
        ]);
    }

    #[Route('/doctor/rendezVous', name: 'doctor_rendezVous')]
    public function doctorRDV(UtilisateurRepository $utilisateurRepository,Security $security,RendezVousRepository $rendezVousRepository)
    {
        $user = $security->getUser();

        if (!$user) {
            throw $this->createAccessDeniedException("Vous devez être connecté pour voir vos rendez-vous.");
        }
        $rendezVousList = $rendezVousRepository->findBy(['id_patient' => $user]);
        return $this->render('rendez_vous/AffDoc.html.twig', [
            'rendez_vouses' => $rendezVousList
        ]);
    }
    #[Route('/doctor/facturation/Aff', name: 'doctor_facturation_Aff')]
    public function doctorFacturationAff(UtilisateurRepository $utilisateurRepository,
     Security $security,FacturationRepository $facturationRepository)
    {
        $user = $security->getUser();
        if (!$user) {
            throw $this->createAccessDeniedException("Vous devez être connecté pour voir vos rendez-vous.");
        }
        return $this->render('facturation/AffDoc.html.twig', [
            'facturations' => $facturationRepository->findAll()
        ]);
    }
    #[Route('/doctor/facturation/Ajout', name: 'doctor_facturation_Ajout')]
    public function doctorFacturationAjout(UtilisateurRepository $utilisateurRepository,
     Security $security,EntityManagerInterface $entityManager,Request $request1)
    {
        $user = $security->getUser();
        if (!$user) {
            throw $this->createAccessDeniedException("Vous devez être connecté pour voir vos rendez-vous.");
        }
        
        $facturation = new Facturation();
        $form1 = $this->createForm(FacturationType::class, $facturation);
        $form1->handleRequest($request1);
        if ($form1->isSubmitted() && $form1->isValid()) {

            $facturation->setMethodePaiement($form1->get('methode_paiement')->getData());
            
            $entityManager->persist($facturation);
            $entityManager->flush();

            return $this->redirectToRoute('doctor_facturation_Aff', [], Response::HTTP_SEE_OTHER);
        }
        return $this->render('facturation/AjoutDoc.html.twig', [
            'facturation' => $facturation,
            'form1' => $form1
        ]);
    }

    #[Route('/doctor/consultation/Aff', name: 'doctor_consul_Aff')]
    public function doctorConsulAff(UtilisateurRepository $utilisateurRepository,Security $security,ConsultationRepository $consultationRepository)
    {
        $user = $security->getUser();   
        if (!$user) {
            throw $this->createAccessDeniedException("Vous devez être connecté pour voir vos rendez-vous.");
        }
        return $this->render('consultation/AffDoc.html.twig', [
            'consultations' => $consultationRepository->findAll()
        ]);
    }
    #[Route('/doctor/consultation/Ajout', name: 'doctor_consul_Ajout')]
    public function doctorConsulAjout(UtilisateurRepository $utilisateurRepository,Security $security,Request $request,EntityManagerInterface $entityManager)
    {
        $user = $security->getUser();
        $consultation = new Consultation();
        $form3 = $this->createForm(ConsultationType::class, $consultation);
        $form3->handleRequest($request);

        if ($form3->isSubmitted() && $form3->isValid()) {
            
            $entityManager->persist($consultation);
            $entityManager->flush();

            return $this->redirectToRoute('doctor_consul_Aff', [], Response::HTTP_SEE_OTHER);
        }
        if (!$user) {
            throw $this->createAccessDeniedException("Vous devez être connecté pour voir vos rendez-vous.");
        }
        return $this->render('consultation/AjoutDoc.html.twig', [
            'consultation' => $consultation,
            'form3' => $form3
        ]);
    }

    #[Route('/doctor/article/aff', name: 'doctor_article_Aff')]
    public function doctorArcticleAff(UtilisateurRepository $utilisateurRepository,Security $security,ArticleRepository $articleRepository,CommentaireRepository $commentaireRepository)
    {
        $user = $security->getUser();   
        if (!$user) {
            throw $this->createAccessDeniedException("Vous devez être connecté pour voir vos rendez-vous.");
        }  
        $articles = [];
        if ($user) {
            $articles = $articleRepository->findBy(['utilisateur' => $user]);
        }
        return $this->render('front/article/affDoc.html.twig', [
            'articles' => $articles,
            'commentaireRepository' => $commentaireRepository]);
    }
    #[Route('/essai', name: 'app_essai')]
    public function index_essai()
    {
        return $this->render('back/essai1.html.twig');
    }
    #[Route('/form', name: 'app_essai')]
    public function index_form(Request $request): Response
    {
        $form = $this->createForm(UserformType::class);
        return $this->render('front/seconnect.html.twig', [
            'UserformType' => $form->createView(),
        ]);
    }
    #[Route('/patient', name: 'app_homeP')]
    public function index(EntityManagerInterface $entityManager): Response
    {
        $articles = $entityManager->getRepository(Article::class)->findAll();
        return $this->render('front/profil.html.twig', [
            'articles' => $articles]);
    }






    #[Route('/doctor', name: 'doctor_index')]
    public function doctor(UtilisateurRepository $utilisateurRepository,Security $security,EntityManagerInterface $entityManager,Request $request2,SluggerInterface $slugger): Response
    {
        $article = new Article();
        $user = $security->getUser();   
        if (!$user) {
            throw $this->createAccessDeniedException("Vous devez être connecté pour voir vos rendez-vous.");
        }
        else{
            $article->setUtilisateur($user);
        }
        $form2 = $this->createForm(ArticleType::class, $article, [
            'user' => $this->getUser(), // Passe l'utilisateur connecté
        ]);
        $form2->handleRequest($request2);
            // Article
        if ($form2->isSubmitted() && $form2->isValid()) {

            $image = $form2->get('urlimagearticle')->getData();
            if ($image) {
                // Générer un nom unique pour l'image
                $originalFilename = pathinfo($image->getClientOriginalName(), PATHINFO_FILENAME);
                $safeFilename = $slugger->slug($originalFilename);
                $newFilename = $safeFilename.'-'.uniqid().'.'.$image->guessExtension();

                // Déplacer le fichier dans le répertoire public/uploads
                $image->move(
                    $this->getParameter('kernel.project_dir').'/public/uploads',
                    $newFilename
                );

                // Mettre à jour l'URL de l'image dans l'entité
                $article->setUrlImageArticle('uploads/'.$newFilename);
            }
            $entityManager->persist($article);
            $entityManager->flush();
            $this->addFlash('success', 'Article ajouté avec succès !');
            return $this->redirectToRoute('doctor_article_Aff'); // Redirige après soumission
        }
        else {
            $this->addFlash('error', 'Veuillez remplir correctement le formulaire.');
        }
        return $this->render('front/doctor.html.twig', [
            'form2' => $form2->createView(),
        ]);
    }


    #[Route('/exemple', name: 'app_exemple')]
    public function indexDoc(EntityManagerInterface $entityManager): Response
    {
        // Récupérer tous les articles depuis la base de données
        $articles = $entityManager->getRepository(Article::class)->findAll();
        //$nomUtilisateur = $article->getUtilisateur()->getNom(); 

        return $this->render('front/exemple.html.twig', [
            'articles' => $articles
          

        ]);


    }
    #[Route('/article/{id}', name: 'article_detail')]
    public function show(
        int $id,
        ArticleRepository $articleRepository,
        Security $security,
        Request $request,
        EntityManagerInterface $entityManager,
        CommentaireRepository $commentaireRepository
    ): Response {
        $user = $security->getUser(); // Récupérer l'utilisateur connecté
        $article = $articleRepository->find($id);
    
        if (!$article) {
            throw $this->createNotFoundException('Article non trouvé');
        }
    
        $commentaires = $commentaireRepository->findBy(
            ['article' => $article],
            ['date_commentaire' => 'DESC']
        );
    
        $commentaire = new Commentaire();
        $form = $this->createForm(CommentaireType::class, $commentaire);
    
        $form->handleRequest($request);
        if ($form->isSubmitted() && $form->isValid()) {
            // Assigner automatiquement l'utilisateur connecté
            if (!$user) {
                throw $this->createAccessDeniedException("Vous devez être connecté pour commenter.");
            }
            $commentaire->setUtilisateur($user);
    
            $commentaire->setArticle($article);
            $commentaire->setDateCommentaire(new \DateTime());
            $commentaire->setHeure(new \DateTime());
    
            $entityManager->persist($commentaire);
            $entityManager->flush();
    
            return $this->redirectToRoute('article_detail', ['id' => $article->getId()]);
        }
    
        return $this->render('front/article/article_detail.html.twig', [
            'article' => $article,
            'commentaires' => $commentaires,
            'form' => $form->createView(),
        ]);
    }
    
    #[Route('/blog', name: 'app')]
    public function indexb(): Response
    {
        return $this->render('a.html.twig');
    }
///////////////////////////// formulaire article ///////////////////////////
    #[Route('/articleform', name: 'docteur_article')]
    public function newArticle(Request $request, EntityManagerInterface $entityManager,SluggerInterface $slugger)
    {
        $article = new Article();
        $form = $this->createForm(ArticleType::class, $article);

        // Traitement du formulaire
        $form->handleRequest($request);
        if ($form->isSubmitted() && $form->isValid()) {

            $image = $form->get('urlimagearticle')->getData();
            if ($image) {
                // Générer un nom unique pour l'image
                $originalFilename = pathinfo($image->getClientOriginalName(), PATHINFO_FILENAME);
                $safeFilename = $slugger->slug($originalFilename);
                $newFilename = $safeFilename.'-'.uniqid().'.'.$image->guessExtension();

                // Déplacer le fichier dans le répertoire public/uploads
                $image->move(
                    $this->getParameter('kernel.project_dir').'/public/uploads',
                    $newFilename
                );

                // Mettre à jour l'URL de l'image dans l'entité
                $article->setUrlImageArticle('uploads/'.$newFilename);
            }

            $entityManager->persist($article);
            $entityManager->flush();

            return $this->redirectToRoute('docteur_article'); // Redirige après soumission
        }

        return $this->render('front/article/article.html.twig', [
            'form' => $form->createView(),
        ]);
    }



///////////////////////// affichage des articles /////////////////////////////
    #[Route('/article', name: 'blog_index')]
    public function index2(EntityManagerInterface $entityManager): Response
    {
        // Récupérer tous les articles depuis la base de données
        $articles = $entityManager->getRepository(Article::class)->findAll();

        return $this->render('front/article/articleaff.html.twig', [
            'articles' => $articles,
            
        ]);


    }


    #[Route('article/delete/{id}', name: 'article_delete')]
    public function index3(int $id,EntityManagerInterface $entityManager ,ArticleRepository $articleRepository): Response
    {
        $article = $articleRepository->find($id);

        if (!$article) {
            $this->addFlash('error', 'Article non trouvé.');
            return $this->redirectToRoute('doctor_article_Aff'); // Redirection si l'article n'existe pas
        }
    
        $entityManager->remove($article);
        $entityManager->flush();
    
        $this->addFlash('success', 'Article supprimé avec succès.');
        return $this->redirectToRoute('doctor_article_Aff'); // Redirection après suppression


    }

    #[Route('/doctor/edit/{id}', name: 'article_edit')]
public function editArticle(
    int $id,
    Request $request,
    EntityManagerInterface $entityManager,
    SluggerInterface $slugger,
    ArticleRepository $articleRepository
) {
    // Récupérer l'article existant
    $article = $articleRepository->find($id);
    if (!$article) {
        $this->addFlash('error', 'Article non trouvé.');
        return $this->redirectToRoute('doctor_article_Aff');
    }

    // Conserver l'ancienne image
    $ancienneImage = $article->getUrlImageArticle();

    // Créer le formulaire
    $form = $this->createForm(ArticleType::class, $article);
    $form->handleRequest($request);

    if ($form->isSubmitted() && $form->isValid()) {

      //  dump('Formulaire soumis');
        //dump($form->isValid()); // Vérifie si la validation fonctionne
        //dump($form->getErrors(true, false)); // Affiche les erreurs
        //die(); // Arrête 

        if ($article->getTitre() === null) {
            $article->setTitre(""); // 👈 Forcer une chaîne vide
        }

        
        $image = $form->get('urlimagearticle')->getData();
        dump($image);


        if ($image) {
            // Supprimer l'ancienne image si une nouvelle est téléchargée
            if ($ancienneImage) {
                $oldFilePath = $this->getParameter('kernel.project_dir') . 'public/' . $ancienneImage;
                if (file_exists($oldFilePath)) {
                    unlink($oldFilePath);
                }
            }

            // Générer un nom unique pour la nouvelle image
            $originalFilename = pathinfo($image->getClientOriginalName(), PATHINFO_FILENAME);
            $safeFilename = $slugger->slug($originalFilename);
            $newFilename = $safeFilename . '-' . uniqid() . '.' . $image->guessExtension();

            // Déplacer la nouvelle image
            $image->move(
                $this->getParameter('kernel.project_dir') . '/public/uploads',
                $newFilename
            );

            // Mettre à jour l'URL de l'image dans l'entité
            $article->setUrlImageArticle('uploads/' . $newFilename);
        }
        $entityManager->persist($article);
        $entityManager->flush();

        $this->addFlash('success', 'Article mis à jour avec succès.');
        return $this->redirectToRoute('doctor_article_Aff');
    }

    else {
        $this->addFlash('error', 'Veuillez remplir correctement le formulaire.');
    }

    return $this->render('front/article/edit.html.twig', [
        'form' => $form->createView(),
        'article' => $article
    ]);
}



    #[Route('/commentaire/delete/{id}', name: 'commentaire_delete')]
    public function deleteCommentaire(int $id, EntityManagerInterface $entityManager, CommentaireRepository $commentaireRepository): Response
    {
        $commentaire = $commentaireRepository->find($id);

        if (!$commentaire) {
            $this->addFlash('error', 'Commentaire non trouvé.');
            return $this->redirectToRoute('doctor_article_Aff');
        }

        $entityManager->remove($commentaire);
        $entityManager->flush();

        $this->addFlash('success', 'Commentaire supprimé avec succès.');
        return $this->redirectToRoute('article_detail', ['id' => $commentaire->getArticle()]);
    }



    #[Route('/commentaire/edit/{id}', name: 'commentaire_edit')]
    public function editCommentaire(int $id, Request $request, EntityManagerInterface $entityManager, CommentaireRepository $commentaireRepository): Response
    {
        $commentaire = $commentaireRepository->find($id);

        if (!$commentaire) {
            $this->addFlash('error', 'Commentaire non trouvé.');
            return $this->redirectToRoute('doctor_article_Aff');
        }

        $form = $this->createForm(CommentaireType::class, $commentaire);
        $form->handleRequest($request);

        if ($form->isSubmitted() && $form->isValid()) {
            $entityManager->flush();
            $this->addFlash('success', 'Commentaire modifié avec succès.');
            return $this->redirectToRoute('article_detail',[
                'id' => $commentaire->getArticle()->getId()])
                
                ;}

        return $this->render('front/commentaire/edit.html.twig', [
            'form' => $form->createView(),
            'commentaire' => $commentaire
        ]);
    }
    #[Route('/backArticle', name: 'backArticle')]
    public function backArticle(EntityManagerInterface $entityManager): Response
    {
        // Récupérer tous les articles depuis la base de données
        $articles = $entityManager->getRepository(Article::class)->findAll();

        return $this->render('front/article/backAFF.html.twig', [
            'articles' => $articles,
            
        ]);


    }
}