<?php

namespace App\Controller;

use App\Entity\Article;
use App\Entity\Commentaire;
use App\Entity\Utilisateur;

use App\Form\ArticleType;
use App\Form\CommentaireType;
use Symfony\Component\String\Slugger\SluggerInterface;
use App\Form\TextType;
use Symfony\Bundle\FrameworkBundle\Controller\AbstractController;
use Symfony\Component\HttpFoundation\Response;
use Symfony\Component\Routing\Attribute\Route;
use Symfony\Component\HttpFoundation\File\UploadedFile;

use Doctrine\ORM\EntityManagerInterface;
use App\Repository\ArticleRepository;
use App\Repository\UtilisateurRepository;
use App\Repository\CommentaireRepository;


use Symfony\Component\HttpFoundation\Request;



final class FrontController extends AbstractController
{
    #[Route('/patient', name: 'app_homeP')]
    public function index(EntityManagerInterface $entityManager,CommentaireRepository $commentaireRepository): Response
    {
        $articles = $entityManager->getRepository(Article::class)->findAll();
        return $this->render('front/profil.html.twig', [
            'articles' => $articles,
            'commentaireRepository' => $commentaireRepository,
            ]
    
    );
    }


    
   


    #[Route('/login', name: 'app_homeL')]
    public function login(): Response
    {
        return $this->render('front/login.html.twig');
    }

    


   


    #[Route('/exemple', name: 'app_home')]
    public function indexDoc(EntityManagerInterface $entityManager,CommentaireRepository $commentaireRepository): Response
    {
        // Récupérer tous les articles depuis la base de données
        $articles = $entityManager->getRepository(Article::class)->findAll();
        //$nomUtilisateur = $article->getUtilisateur()->getNom(); 

        return $this->render('front/exemple.html.twig', [
            'articles' => $articles,
            'commentaireRepository' => $commentaireRepository,
            
          

        ]);


    }


    #[Route('/article{id}', name: 'article_detail')]
    public function show(int $id,ArticleRepository $articleRepository,Request $request,EntityManagerInterface $entityManager,CommentaireRepository $commentaireRepository, 
): Response
    {
        $article = $articleRepository->find($id);

        if (!$article) {
            throw $this->createNotFoundException('Article non trouvé');
        }
        
        // Récupérer les commentaires de l'article
        $commentaires = $commentaireRepository->findBy(
            ['article' => $article],  // Filtrer par article
            ['date_commentaire' => 'DESC'] // Trier par date (optionnel)
        );
    

        // Créer un nouveau commentaire
        $commentaire = new Commentaire();
        //$commentaire = new Commentaire();
        //$utilisateurs = $entityManager->getRepository(Utilisateur::class)->findAll();
        //$form = $this->createForm(CommentaireType::class, $commentaire, [
            //'utilisateurs' => $utilisateurs,]);




        // Récupérer tous les utilisateurs pour les afficher dans la liste
        $utilisateurs = $entityManager->getRepository(Utilisateur::class)->findAll();


        //creer le formulaire en passant l'utili
        $form = $this->createForm(CommentaireType::class, $commentaire, [
            'utilisateurs' => $utilisateurs,  // Passer la liste des utilisateurs au formulaire
        ]);


        $form->handleRequest($request);



        if ($form->isSubmitted() && $form->isValid()) {

            if ($commentaire->getUtilisateur() === null) {
                $commentaire->setUtilisateur(null);  // ou l'utilisateur "Anonyme"
            }

        $commentaire->setArticle($article);
        $commentaire->setDateCommentaire(new \DateTime());
        $commentaire->setHeure(new \DateTime());

        //$commentaire->setStatus($this->getUser() ? true : false);



            //$commentaires->setDateCommentaire(new \DateTime()); //  Date
            //$commentaires->setStatus($this->getUser() ? true : false); // Détermine si l'utilisateur est connecté
            //$commentaires->setArticle($article);  //Associe le commentaire à l'article



            // Sauvegarde en base de données
            $entityManager->persist($commentaire);
            $entityManager->flush();

            // Redirection pour éviter la soumission multiple du formulaire
            return $this->redirectToRoute('article_detail', ['id' => $article->getId()]);
        }


        return $this->render('front/article/article_detail.html.twig', [
            'article' => $article,
            'commentaires' => $commentaires,
            'form' => $form->createView(),
          

        ]);


    }


   
   
   
   // #[Route('/blog', name: 'app_blog')]
    //public function indexblog(): Response{
     //   return $this->render('front/features.html.twig');}

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





    ////////////////////// doctor affichage/////////////////////////////////
    #[Route('/doctor', name: 'blogs')]
    public function newProfile(Request $request, EntityManagerInterface $entityManager,SluggerInterface $slugger,UtilisateurRepository $utilisateurRepository,ArticleRepository $articleRepository,CommentaireRepository $commentaireRepository)
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

            //if (empty($article->getTitre())) {
              //  $this->addFlash('error', 'Le titre de l\'article ne peut pas être vide.');
                //return $this->redirectToRoute('blogs');}

            //if (empty($article->getContenue())) {
              //  $this->addFlash('error', 'Le contenue de l\'article ne peut pas être vide.');
                //return $this->redirectToRoute('blogs');}
        
        



            $entityManager->persist($article);
            $entityManager->flush();

            
            $this->addFlash('success', 'Article ajouté avec succès !');
            return $this->redirectToRoute('blogs'); // Redirige après soumission
        }
        else {
    $this->addFlash('error', 'Veuillez remplir correctement le formulaire.');
}


    // Récupérer tous les utilisateurs pour la liste déroulante
    $utilisateurs = $utilisateurRepository->findAll();

    // Récupérer l'ID du docteur sélectionné
    $selectedUserId = $request->query->get('user_id');

    // Si un utilisateur est sélectionné, récupérer uniquement ses articles
    $articles = [];
    if ($selectedUserId) {
        $articles = $articleRepository->findBy(['utilisateur' => $selectedUserId]);
    }





        return $this->render('front/article/doctor.html.twig', [
            'form' => $form->createView(),
            'utilisateurs' => $utilisateurs,
            'selectedUserId' => $selectedUserId,
            'articles' => $articles,
            'commentaireRepository' => $commentaireRepository,
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


    #[Route('article/delete{id}', name: 'article_delete')]
    public function index3(int $id,EntityManagerInterface $entityManager ,ArticleRepository $articleRepository): Response
    {
        $article = $articleRepository->find($id);

        if (!$article) {
            $this->addFlash('error', 'Article non trouvé.');
            return $this->redirectToRoute('blogs'); // Redirection si l'article n'existe pas
        }
    
        $entityManager->remove($article);
        $entityManager->flush();
    
        $this->addFlash('success', 'Article supprimé avec succès.');
        return $this->redirectToRoute('blogs'); // Redirection après suppression


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
        return $this->redirectToRoute('blogs');
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

       // if (empty($article->getTitre())) {
         //   $this->addFlash('error', 'Le titre de l\'article ne peut pas être vide.');
           // return $this->redirectToRoute('blogs');
        //}


        //if (empty($article->getContenue())) {
          //  $this->addFlash('error', 'Le contenue de l\'article ne peut pas être vide.');
            //return $this->redirectToRoute('blogs');
        //}
    




        // ⚠️ Assurer que l'entité est persistée et mise à jour
        $entityManager->persist($article);
        $entityManager->flush();

        $this->addFlash('success', 'Article mis à jour avec succès.');
        return $this->redirectToRoute('blogs');
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
        return $this->redirectToRoute('blogs');
    }

    $entityManager->remove($commentaire);
    $entityManager->flush();

    $this->addFlash('success', 'Commentaire supprimé avec succès.');
    return $this->redirectToRoute('article_detail');
}



#[Route('/commentaire/edit/{id}', name: 'commentaire_edit')]
public function editCommentaire(int $id, Request $request, EntityManagerInterface $entityManager, CommentaireRepository $commentaireRepository): Response
{
    $commentaire = $commentaireRepository->find($id);

    if (!$commentaire) {
        $this->addFlash('error', 'Commentaire non trouvé.');
        return $this->redirectToRoute('blogs');
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



















}





