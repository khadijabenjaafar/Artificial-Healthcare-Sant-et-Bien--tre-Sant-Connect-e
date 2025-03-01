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
use App\Service\JWTService;
use App\Service\SendMailService;
use App\Controller\PdfController;

class FrontController extends AbstractController
{
   

    #[Route('/back', name: 'back_index')]
    public function index_back()
    {
        return $this->render('back/index.html.twig');
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
    public function doctor(UtilisateurRepository $utilisateurRepository,
    PlanificationRepository $planificationRepository,
    MatchingRepository $matchingRepository,
     Security $security,RendezVousRepository $rendezVousRepository,
     OrdonnanceRepository $ordonnanceRepository,Request $request,Request $request3,
      EntityManagerInterface $entityManager,
      FacturationRepository $facturationRepository,Request $request1,
      Request $request2,SluggerInterface $slugger,ArticleRepository $articleRepository,
      CommentaireRepository $commentaireRepository,ConsultationRepository $consultationRepository,
      SendMailService $mail,
      JWTService $jwt, PdfController $pdf): Response
    {
        $article = new Article();
        $user = $security->getUser();
        $consultation = new Consultation();

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
    
            return $this->redirectToRoute('doctor_index', [], Response::HTTP_SEE_OTHER);
        }
    
        $form3 = $this->createForm(ConsultationType::class, $consultation);
        $form3->handleRequest($request);

        if ($form3->isSubmitted() && $form3->isValid()) {
            
            $entityManager->persist($consultation);
            $entityManager->flush();

            return $this->redirectToRoute('doctor_index', [], Response::HTTP_SEE_OTHER);
        }
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
        
        $facturation = new Facturation();
        $form1 = $this->createForm(FacturationType::class, $facturation);
        $form1->handleRequest($request1);

        $ordonnance = new Ordonnance();
        $form = $this->createForm(OrdonnanceType::class, $ordonnance);
        $form->handleRequest($request);

        
       
        $freelancers = $utilisateurRepository->findAll(); 
        $planifications = $planificationRepository->findBy(['freelancer' => $freelancers]); // Filtrer par utilisateur
        $matchings=$matchingRepository->findBy(['utilisateur'=>$freelancers]);
        
       
        $rendezVousList = $rendezVousRepository->findBy(['id_patient' => $user]);
        // Ordonnance
        if ($form->isSubmitted() && $form->isValid()) {
            $entityManager->persist($ordonnance);
            $entityManager->flush();
            $patient = $ordonnance->getIdConsultation()->getIdRendezVous()->getIdPatient()->getEmail();
            $response = $pdf->generatePdf();
            $responseData = json_decode($response->getContent(), true);
            if (isset($responseData['pdfUrl'])) {
                $pdfUrl = $responseData['pdfUrl'];
                
                // Envoyer l'email
                $mail->send($pdfUrl, $patient, 'Nouvelle ordonnance', 'generated');
            } else {
                // Gérer l'erreur si 'pdfUrl' n'est pas présent
                throw $this->createNotFoundException('Le lien de téléchargement du PDF est introuvable.');
            }
        
            return $this->redirectToRoute('doctor_index', [], Response::HTTP_SEE_OTHER);
        }
 // facturation
        if ($form1->isSubmitted() && $form1->isValid()) {

            $facturation->setMethodePaiement($form1->get('methode_paiement')->getData());
            
            $entityManager->persist($facturation);
            $entityManager->flush();

            return $this->redirectToRoute('doctor_index', [], Response::HTTP_SEE_OTHER);
        }
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
            return $this->redirectToRoute('doctor_index'); // Redirige après soumission
        }
        else {
            $this->addFlash('error', 'Veuillez remplir correctement le formulaire.');
        }
   
    // Récupérer l'ID du docteur sélectionné

    // Si un utilisateur est sélectionné, récupérer uniquement ses articles
    $articles = [];
    if ($user) {
        $articles = $articleRepository->findBy(['utilisateur' => $user]);
    }
        return $this->render('front/doctor.html.twig', [
            'freelancers' => $freelancers,
            'planifications' => $planifications,
            'matchings'=>$matchings,
            'rendez_vouses' => $rendezVousList,
            'ordonnances' => $ordonnanceRepository->findAll(),
            'ordonnance' => $ordonnance,
            'form' => $form,
            'facturations' => $facturationRepository->findAll(),
            'facturation' => $facturation,
            'form1' => $form1,
            'form2' => $form2->createView(),
            'articles' => $articles,
            'commentaireRepository' => $commentaireRepository,
            'consultations' => $consultationRepository->findAll(),
            'consultation' => $consultation,
            'form3' => $form3,
            'matching' => $matching,
            'form4' => $form4->createView(),
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
            return $this->redirectToRoute('doctor_index'); // Redirection si l'article n'existe pas
        }
    
        $entityManager->remove($article);
        $entityManager->flush();
    
        $this->addFlash('success', 'Article supprimé avec succès.');
        return $this->redirectToRoute('doctor_index'); // Redirection après suppression


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
        return $this->redirectToRoute('doctor_index');
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
        return $this->redirectToRoute('doctor_index');
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
            return $this->redirectToRoute('doctor_index');
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
            return $this->redirectToRoute('doctor_index');
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
