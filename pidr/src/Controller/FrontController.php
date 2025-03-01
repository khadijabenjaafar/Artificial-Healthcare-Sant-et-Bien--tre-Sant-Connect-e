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
use App\Entity\Planification;
use App\Entity\Ordonnance;  
use App\Form\OrdonnanceType;

use Doctrine\ORM\EntityManagerInterface;
use App\Repository\ArticleRepository;
use App\Repository\CommentaireRepository;

use App\Entity\Consultation;
use App\Form\ConsultationType;
use App\Repository\ConsultationRepository;
use Symfony\Component\String\Slugger\SluggerInterface;
use App\Entity\Matching;
use App\Form\MatchingType;
use Symfony\Component\HttpFoundation\File\Exception\FileException;


class FrontController extends AbstractController
{
   
    #[Route('/back', name: 'app_back')]
    public function index1()
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
    public function doctor(
        UtilisateurRepository $utilisateurRepository,
        PlanificationRepository $planificationRepository,
        MatchingRepository $matchingRepository,
        Security $security,
        RendezVousRepository $rendezVousRepository,
        OrdonnanceRepository $ordonnanceRepository,
        Request $request,
        EntityManagerInterface $entityManager,
        ArticleRepository $articleRepository,
        CommentaireRepository $commentaireRepository,
        ConsultationRepository $consultationRepository
    ) {
        // Fetch data and ensure all referenced entities exist
        $planifications = $planificationRepository->findAll();
        $validPlanifications = [];
        $invalidPlanifications = [];

        foreach ($planifications as $planification) {
            if ($planification->getFreelancer() && $planification->getUtilisateur()) {
                $validPlanifications[] = $planification;
            } else {
                $invalidPlanifications[] = $planification;
            }
        }

        if (!empty($invalidPlanifications)) {
            // Log the invalid planifications or handle them as needed
            $this->addFlash('error', 'Some planifications have invalid data and were excluded.');
        }

        return $this->render('front/doctor.html.twig', [
            'freelancers' => $utilisateurRepository->findAll(),
            'planifications' => $validPlanifications,
            'matchings' => $matchingRepository->findAll(),
            'rendez_vouses' => $rendezVousRepository->findAll(),
            'ordonnances' => $ordonnanceRepository->findAll(),
            'form' => $this->createForm(OrdonnanceType::class)->createView(),
            'articles' => $articleRepository->findAll(),
            'commentaireRepository' => $commentaireRepository,
            'consultations' => $consultationRepository->findAll(),
            'form3' => $this->createForm(ConsultationType::class)->createView(),
            'matching' => new Matching(),
            'form4' => $this->createForm(MatchingType::class)->createView(),
            'utilisateur' => $this->getUser(), // Assuming you want to use the currently logged-in user
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
