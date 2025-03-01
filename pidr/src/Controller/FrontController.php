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
use App\Entity\ArticleRating; // ✅ Vérifie que cette ligne est bien présente !
use App\Entity\CommentaireSignalement; // ✅ Vérifie que cette ligne est bien présente !
use App\Entity\CommentaireVote; // ✅ Vérifie que cette ligne est bien présente !
use App\Form\MatchingType;
use Symfony\Component\HttpFoundation\File\Exception\FileException;
use Symfony\Component\HttpFoundation\JsonResponse ;
use Snipe\BanBuilder\CensorWords;
//use App\Service\TextToSpeechService;

use App\Service\SpeechifyService;
use App\Service\TranslationService;


class FrontController extends AbstractController
{
   

    #[Route('/back', name: 'back_index')]
    public function index_back()
    {
        return $this->render('back/index.html.twig');
    }




    #[Route('/back/a', name: 'back')]
    public function index_ba()
    {
        return $this->render('back/stat.html.twig');
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
      Request $request2,SluggerInterface $slugger,ArticleRepository $articleRepository,CommentaireRepository $commentaireRepository,ConsultationRepository $consultationRepository)
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
        CommentaireRepository $commentaireRepository,
        TranslationService $translationService,
        SpeechifyService $textToSpeechService,
   ): Response {
        $user = $security->getUser(); // Récupérer l'utilisateur connecté
        $article = $articleRepository->find($id);

        
    
        if (!$article) {
            throw $this->createNotFoundException('Article non trouvé');
        }

// Traduction de l'article api 
        $lang = $request->query->get('lang', 'en'); // Langue par défaut

        $originalText = $article->getContenue();

        $files = $textToSpeechService->generateSpeechVideo($article->getContenue()); // Générer un fichier audio

// Détection automatique de la langue du texte original
$detectedLang = (preg_match('/[éèàùçêîôû]/', $originalText)) ? 'fr' : 'en';

// Traduire uniquement si nécessaire
if ($lang !== $detectedLang) {
    $translatedText = $translationService->translate($originalText, $detectedLang, $lang);
} else {
    $translatedText = $originalText;
}


        //$translatedText = $translationService->translateText($article->getContenue(), $lang);      
          // naarech $article->setContent($translatedText);

        $article->incrementNbreVue() ;
        $entityManager->persist($article);
        $entityManager->flush();
    
        $commentaires = $commentaireRepository->findBy(
           ['article' => $article, 'parent' => null], // Récupérer seulement les commentaires parents
            ['date_commentaire' => 'DESC']
        );




        // Lire `reply_to` depuis les paramètres GET
        $reply_to = $request->query->getInt('reply_to', 0);


    
        $commentaire = new Commentaire();
        $form = $this->createForm(CommentaireType::class, $commentaire);
    
       $form->handleRequest($request);
        if ($form->isSubmitted() && $form->isValid()) {
            // Assigner automatiquement l'utilisateur connecté
            if (!$user) {
                throw $this->createAccessDeniedException("Vous devez être connecté pour commenter.");
            }


           // $lang = $request->query->get('lang', 'fr'); // Langue cible par défaut : français
        //if ($lang !== 'fr') {
        //$translatedText = $translationService->translate($article->getContenue(), 'fr', $lang);
        //} else {
        //$translatedText = $article->getContenue();}








            if ($reply_to > 0) {
                $parentCommentaire = $commentaireRepository->find($reply_to);
                if ($parentCommentaire) {
                $commentaire->setParent($parentCommentaire);
                }
            }



            // 🔹 Détecter les mots interdits avec BanBuilder
            $censor = new CensorWords();
            $contenu = $commentaire->getContenue();
            $result = $censor->censorString($contenu);

            if ($result['matched']) {
                $this->addFlash('danger', 'Votre commentaire contient des mots inappropriés.');
                return $this->redirectToRoute('article_detail', ['id' => $article->getId()]);
            }


             // 🔹 Vérifier les mots interdits
        //$contenu = $commentaire->getContenue();
        if ($this->filterBadWords($contenu)) {
            $this->addFlash('danger', 'Votre commentaire contient des mots inappropriés.');
            return $this->redirectToRoute('article_detail', ['id' => $article->getId()]);
        }










    



            $commentaire->setUtilisateur($user);
            $commentaire->setArticle($article);
            $commentaire->setDateCommentaire(new \DateTime());
           $commentaire->setHeure(new \DateTime());
          // $commentaire->setRating($form->get('rating')->getData() ?? null);


           
            
            $entityManager->persist($commentaire);
            $entityManager->flush();


            $this->addFlash('success', 'Votre commentaire a été ajouté avec succès !');
            return $this->redirectToRoute('article_detail',['id' => $article->getId()]);
        }
    
        return $this->render('front/article/article_details.html.twig', [
            'article' => $article,
           'commentaires' => $commentaires,
          'form' => $form->createView(), 
          'reply_to' => $reply_to > 0 ? $reply_to : null,
          'formReponse' => $this->createForm(CommentaireType::class)->createView(), // Formulaire de réponse
          'lang' => $lang, // Passer la langue pour la gestion du changement de langue
          'translatedText' => $translatedText,
          //'audioFile' => $audioFile,
          'audioFile' => $files['mp3'],
         // 'translatedText' => $translatedText, // Ajouter la traduction à la vue Twig
          
        
        ]); }


        private function filterBadWords(string $text): bool
{
    // Charger le fichier JSON avec les mots interdits
    $badWords = json_decode(file_get_contents(__DIR__ . '/../../config/badwords_fr.json'), true)['badwords'];

    // Vérifier si le texte contient un mot interdit
    foreach ($badWords as $badWord) {
        if (stripos($text, $badWord) !== false) {
            return true; // Contient un mot interdit
        }
    }
    return false; // Aucun mot interdit trouvé
}





#[Route('/admin/stats', name: 'admin_stats')]
public function stats(
    ArticleRepository $articleRepository,
    CommentaireRepository $commentaireRepository,
    EntityManagerInterface $entityManager
): Response {
    
    // 🟢 Nombre total d'articles
    $totalArticles = $articleRepository->count([]);

    // 🟢 Nombre total de commentaires
    $totalCommentaires = $commentaireRepository->count([]);

    // 🟢 Nombre total de signalements
    $totalSignalements = $entityManager->getRepository(CommentaireSignalement::class)->count([]);

    // 🟢 Top 5 articles les plus vus
    $articlesLesPlusVus = $articleRepository->findBy([], ['nbreVue' => 'DESC'], 5);

    // 🟢 Top 5 articles les mieux notés
    $articlesLesMieuxNotes = $articleRepository->findTopRatedArticles(5);

    // 🟢 Top 5 commentaires les plus aimés
    $commentairesLesPlusAimes = $commentaireRepository->findBy([], ['likes' => 'DESC'], 5);

    return $this->render('front/stat.html.twig', [
        'totalArticles' => $totalArticles,
        'totalCommentaires' => $totalCommentaires,
        'totalSignalements' => $totalSignalements,
        'articlesLesPlusVus' => $articlesLesPlusVus,
        'articlesLesMieuxNotes' => $articlesLesMieuxNotes,
        'commentairesLesPlusAimes' => $commentairesLesPlusAimes
    ]);
}
















    #[Route('/commentaire/repondre', name: 'commentaire_repondre', methods: ['POST'])]
    public function repondre(Request $request, EntityManagerInterface $entityManager, Security $security, CommentaireRepository $commentaireRepository, ArticleRepository $articleRepository): JsonResponse
    {
    $user = $security->getUser();
    
    if (!$user) {
        return new JsonResponse(['status' => 'error', 'message' => 'Vous devez être connecté pour répondre.'], 403);
    }

    $data = json_decode($request->getContent(), true);
    $articleId = $data['articleId'] ?? null;
    $parentId = $data['parentId'] ?? null;
    $contenue = $data['contenue'] ?? '';

    if (!$articleId || !$parentId || empty($contenue)) {
        return new JsonResponse(['status' => 'error', 'message' => 'Données invalides.'], 400);
    }

    $article = $articleRepository->find($articleId);
    $parentCommentaire = $commentaireRepository->find($parentId);

    if (!$article || !$parentCommentaire) {
        return new JsonResponse(['status' => 'error', 'message' => 'Article ou commentaire introuvable.'], 404);
    }

    // Création du nouveau commentaire
    $reponse = new Commentaire();
    $reponse->setUtilisateur($user);
    $reponse->setArticle($article);
    $reponse->setParent($parentCommentaire);
    $reponse->setDateCommentaire(new \DateTime());
    $reponse->setHeure(new \DateTime());
    $reponse->setContenue($contenue);

    $entityManager->persist($reponse);
    $entityManager->flush();

    return new JsonResponse([
        'status' => 'success',
        'message' => 'Réponse ajoutée.',
        'html' => $this->renderView('front/commentaire/commentaire_replies.html.twig', [
            'commentaires' => [$reponse]
        ])
    ]);
}



#[Route('/article/{id}/rate', name: 'article_rate', methods: ['POST'])]
public function rateArticle(
    Request $request,
    Article $article,
    Security $security,
    EntityManagerInterface $entityManager
): JsonResponse {
    $user = $security->getUser();
    if (!$user) {
        return new JsonResponse(['status' => 'error', 'message' => 'Vous devez être connecté pour voter.'], 403);
    }

    $data = json_decode($request->getContent(), true);
    $ratingValue = $data['rating'] ?? null;

    if (!$ratingValue || $ratingValue < 1 || $ratingValue > 10) {
        return new JsonResponse(['status' => 'error', 'message' => 'Note invalide.'], 400);
    }

    // Vérifier si l'utilisateur a déjà voté
    $existingRating = $entityManager->getRepository(ArticleRating::class)
        ->findOneBy(['article' => $article, 'user' => $user]);

    if ($existingRating) {
        $existingRating->setRating($ratingValue);
    } else {
        $rating = new ArticleRating();
        $rating->setArticle($article);
        $rating->setUser($user);
        $rating->setRating($ratingValue);
        $entityManager->persist($rating);
    }

    $entityManager->flush();

    return new JsonResponse([
        'status' => 'success',
        'average' => $article->getAverageRating(),
        'distribution' => $article->getRatingDistribution()
    ]);
}




#[Route('/commentaire/signaler', name: 'commentaire_signaler', methods: ['POST'])]
public function signaler(
    Request $request,
    EntityManagerInterface $entityManager,
    Security $security,
    CommentaireRepository $commentaireRepository
): JsonResponse {
    $user = $security->getUser();

    if (!$user) {
        return new JsonResponse(['status' => 'error', 'message' => 'Vous devez être connecté pour signaler un commentaire.'], 403);
    }

    $data = json_decode($request->getContent(), true);
    $commentId = $data['commentId'] ?? null;


    error_log("Signalement reçu pour le commentaire ID : " . $commentId);

    if (!$commentId) {
        return new JsonResponse(['status' => 'error', 'message' => 'Commentaire introuvable.'], 400);
    }

    $commentaire = $commentaireRepository->find($commentId);

    if (!$commentaire) {
        return new JsonResponse(['status' => 'error', 'message' => 'Commentaire introuvable.'], 404);
    }

    // Vérifier si l'utilisateur a déjà signalé ce commentaire
    $existingSignalement = $entityManager->getRepository(CommentaireSignalement::class)->findOneBy([
        'commentaire' => $commentaire,
        'utilisateur' => $user
    ]);

    if ($existingSignalement) {
        return new JsonResponse(['status' => 'error', 'message' => 'Vous avez déjà signalé ce commentaire.'], 400);
    }

    error_log("Ajout d'un signalement pour le commentaire ID : " . $commentId . " par l'utilisateur ID : " . $user->getId());


    // Ajouter un nouveau signalement
    $signalement = new CommentaireSignalement();
    $signalement->setCommentaire($commentaire);
    $signalement->setUtilisateur($user);

    $entityManager->persist($signalement);
    $entityManager->flush();

    error_log("Nombre total de signalements pour le commentaire ID : " . $commentId . " = " . $commentaire->getNombreSignalements());


    // Supprimer automatiquement le commentaire si 10 signalements
    if ($commentaire->getNombreSignalements() >= 1) {
        error_log("Suppression du commentaire ID : " . $commentId . " après 10 signalements.");
        $entityManager->remove($commentaire);
        $entityManager->flush();
        return new JsonResponse(['status' => 'deleted', 'message' => 'Ce commentaire a été supprimé après 10 signalements.']);
    }

    return new JsonResponse(['status' => 'success', 'message' => 'Commentaire signalé avec succès.']);
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
       // $user=$security->getUser();
        
        $commentaire = $commentaireRepository->find($id);


      

        if (!$commentaire) {
            $this->addFlash('error', 'Commentaire non trouvé.');
            return $this->redirectToRoute('doctor_index');
        }


        

        $entityManager->remove($commentaire);
        $entityManager->flush();

        $articleId = $commentaire->getArticle()->getId();

        $this->addFlash('success', 'Commentaire supprimé avec succès.');
        return $this->redirectToRoute('article_detail', ['id' => $articleId]);
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



    #[Route('/commentaire/vote', name: 'commentaire_vote', methods: ['POST'])]
public function voteCommentaire(
    Request $request,
    EntityManagerInterface $entityManager,
    Security $security,
    CommentaireRepository $commentaireRepository
): JsonResponse {
    $user = $security->getUser();

    if (!$user) {
        return new JsonResponse(['status' => 'error', 'message' => 'Vous devez être connecté pour voter.'], 403);
    }

    $data = json_decode($request->getContent(), true);
    $commentId = $data['commentId'] ?? null;
    $voteType = $data['voteType'] ?? null;

    if (!$commentId || !in_array($voteType, ['like', 'dislike'])) {
        return new JsonResponse(['status' => 'error', 'message' => 'Données invalides.'], 400);
    }

    $commentaire = $commentaireRepository->find($commentId);
    if (!$commentaire) {
        return new JsonResponse(['status' => 'error', 'message' => 'Commentaire introuvable.'], 404);
    }

    // Vérifier si l'utilisateur a déjà voté
    $existingVote = $entityManager->getRepository(CommentaireVote::class)->findOneBy([
        'commentaire' => $commentaire,
        'utilisateur' => $user
    ]);

    if ($existingVote) {
        return new JsonResponse(['status' => 'error', 'message' => 'Vous avez déjà voté sur ce commentaire.'], 400);
    }

    // Enregistrer le vote
    $vote = new CommentaireVote();
    $vote->setCommentaire($commentaire);
    $vote->setUtilisateur($user);
    $vote->setVoteType($voteType);

    $entityManager->persist($vote);
    $entityManager->flush();

    return new JsonResponse([
        'status' => 'success',
        'likes' => $commentaire->getLikes(),
        'dislikes' => $commentaire->getDislikes()
    ]);
}



    
}
