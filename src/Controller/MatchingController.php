<?php

namespace App\Controller;
use App\Entity\Matching;
use App\Entity\Utilisateur;
use App\Form\Matching1Type;
use Psr\Log\LoggerInterface;
use App\Entity\Planification;
use App\Service\HistoryLogger;
use App\Repository\MatchingRepository;
use Doctrine\ORM\EntityManagerInterface;
use App\Repository\UtilisateurRepository;
use Symfony\Component\BrowserKit\History;
use App\Repository\PlanificationRepository;
use Symfony\Component\HttpFoundation\Request;
use Symfony\Component\HttpFoundation\Response;
use Symfony\Component\Routing\Annotation\Route;
use Symfony\Component\HttpFoundation\JsonResponse;
use Symfony\Component\HttpFoundation\RedirectResponse;

use Symfony\Component\HttpFoundation\File\UploadedFile;
use Symfony\Bundle\FrameworkBundle\Controller\AbstractController;
use Symfony\Component\HttpFoundation\File\Exception\FileException;


#[Route('/matching')]
final class MatchingController extends AbstractController
{
    private $logger;
    private HistoryLogger $historyLogger;
    public function __construct(LoggerInterface $logger, HistoryLogger $historyLogger)
    {
        $this->logger = $logger;
        $this->historyLogger = $historyLogger;
    }
    #[Route(name: 'app_matching_index', methods: ['GET'])]
    public function index(MatchingRepository $matchingRepository): Response
    {
        return $this->render('matching/accueil.html.twig', [
            'matchings' => $matchingRepository->findAll(),
        ]);
        
    }
    #[Route('/backMatching',name: 'app_matching_index2', methods: ['GET'])]
    public function index44(MatchingRepository $matchingRepository): Response
    {
        return $this->render('matching/BackMatching.html.twig', [
            'matchings' => $matchingRepository->findAll(),
        ]);
    }

    

    #[Route('/new', name: 'app_matching_new', methods: ['GET', 'POST'])]
    public function new(Request $request, EntityManagerInterface $entityManager): Response
    {
        $matching = new Matching();
        $form = $this->createForm(Matching1Type::class, $matching);
        $form->handleRequest($request);
    
        if ($form->isSubmitted() && $form->isValid()) {
            // Handle CV file upload
            $cvFile = $form->get('cv')->getData();
    
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
            $this->historyLogger->logAction('add', 'matching');
            return $this->redirectToRoute('app_matching_index', [], Response::HTTP_SEE_OTHER);
        }
    
        return $this->render('matching/new.html.twig', [
            'matching' => $matching,
            'form' => $form->createView(),
        ]);
    }
    

    


    #[Route('/{id}', name: 'app_matching_show', methods: ['GET'])]
    public function show(Matching $matching): Response
    {
        return $this->render('matching/show.html.twig', [
            'matching' => $matching,
        ]);
    }

    #[Route('/{id}/edit', name: 'app_matching_edit', )]
    public function edit(Request $request, Matching $matching, EntityManagerInterface $entityManager): Response
    {
        $form = $this->createForm(Matching1Type::class, $matching);
        $form->handleRequest($request);

        if ($form->isSubmitted() && $form->isValid()) {
            $entityManager->flush();
            $this->historyLogger->logAction('modify', 'matching');
            return $this->redirectToRoute('doctor_index', [], Response::HTTP_SEE_OTHER);
        }

        return $this->render('matching/edit.html.twig', [
            'matching' => $matching,
            'form' => $form,
        ]);
    }
    

    #[Route('/{id}', name: 'app_matching_delete', methods: ['POST'])]
    public function delete(Request $request, Matching $matching, EntityManagerInterface $entityManager): Response
    {
        if ($this->isCsrfTokenValid('delete'.$matching->getId(), $request->request->get('_token'))) {
            $entityManager->remove($matching);
            $entityManager->flush();
            $this->historyLogger->logAction('delete', 'matching');
        }

        return $this->redirectToRoute('doctor_index', [], Response::HTTP_SEE_OTHER);
    }

    
    #[Route('/freelancers', name: 'freelancer_list')]
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
    



    
    #[Route('/freelancer/details/{id}', name: 'get_freelancer_details', methods: ['GET'])]
public function getFreelancerDetails($id, EntityManagerInterface $entityManager, LoggerInterface $logger): JsonResponse
{
    try {
        $logger->info('Fetching freelancer details for ID: ' . $id);

        $userRepo = $entityManager->getRepository(Utilisateur::class);
        $matchingRepo = $entityManager->getRepository(Matching::class);

        $freelancer = $userRepo->find($id);
        if (!$freelancer) {
            throw new \Exception("Freelancer with ID $id not found.");
        }

        $matching = $matchingRepo->findOneBy(['utilisateur' => $freelancer]);
        if (!$matching) {
            $matching = new Matching(); // Prevent null reference errors
        }

        $logger->info("Freelancer details fetched successfully for ID: $id");

        return new JsonResponse([
            'name' => ($freelancer->getPrenom() ?? '') . ' ' . ($freelancer->getNom() ?? ''),
            'email' => $freelancer->getEmail() ?? 'No email available',
            'date_naissance' => $freelancer->getDateNaissance() ? $freelancer->getDateNaissance()->format('Y-m-d') : 'Not specified',
            'adresse' => $freelancer->getAdresse() ?? 'No address specified',
            'genre' => $freelancer->getGenre() ?? 'Not specified',
            'description' => $matching->getDescription() ?? 'No bio available',
            'competences' => $matching->getCompetences() ?? 'Not specified',
            'cv' => $matching->getCv() ?? 'No CV available',
            'price' => $matching->getPrice() ?? 'Not specified',
            'available' => $matching->getDate() ? $matching->getDate()->format('Y-m-d') : 'Not specified',

        ]);

    } catch (\Exception $e) {
        $logger->error('Error fetching freelancer details: ' . $e->getMessage());
        return new JsonResponse(['error' => $e->getMessage()], 500);
    }
}
#[Route('/consultation/request', name: 'consultation_request', methods: ['POST'])]
    public function requestConsultation(Request $request, EntityManagerInterface $entityManager, LoggerInterface $logger): JsonResponse
    {
        $data = json_decode($request->getContent(), true);

        // Debugging: Log received data
        $logger->info('Received consultation request data: ' . json_encode($data));

        // Validate data
        if (!$data || !isset($data['freelancerId'], $data['date'], $data['adresse'], $data['mode'])) {
            return new JsonResponse(['success' => false, 'message' => 'Invalid request data'], 400);
        }

        try {
            // Create a new Planification entity and set its properties
            $planification = new Planification();
            $planification->setFreelancer($entityManager->getRepository(Utilisateur::class)->find($data['freelancerId']));
            $planification->setDate(new \DateTime($data['date']));
            $planification->setAdresse($data['adresse']);
            $planification->setMode($data['mode']);

            // Persist the Planification entity to the database
            $entityManager->persist($planification);
            $entityManager->flush();

            $logger->info('Consultation request saved successfully.');

            return new JsonResponse(['success' => true, 'message' => 'Consultation request saved successfully.']);
        } catch (\Exception $e) {
            $logger->error('Error saving consultation request: ' . $e->getMessage());
            return new JsonResponse(['success' => false, 'message' => 'An error occurred while saving the consultation request.'], 500);
        }
    }

    
    
   /*
   #[Route('/statistics', name: 'app_statistics')]
   public function index_back(MatchingRepository $matchingRepo): Response
   {
       $totalMatchings = $matchingRepo->countMatchings(); 
   
       return $this->render('back/index.html.twig', [
           'totalMatchings' => $totalMatchings,
       ]);
   }
    public function index_back2(PlanificationRepository $planificationRepo): Response
    {
         $totalPlanifications = $planificationRepo->countPlanifications(); 
    
         return $this->render('back/index.html.twig', [
              'totalPlanifications' => $totalPlanifications,
         ]);
    } 
    
    
    #[Route('/matching/{id}/toggle-availability', name: 'app_matching_toggle_availability')]
    public function toggleAvailability(int $id, EntityManagerInterface $entityManager): RedirectResponse
    {
        $matching = $entityManager->getRepository(Matching::class)->find($id);

        if (!$matching) {
            $this->addFlash('error', 'Matching not found.');
            return $this->redirectToRoute('app_matching_list');
        }

        // Toggle availability
        $matching->setAvailability(!$matching->getAvailability());
        $entityManager->flush();

        $this->addFlash('success', 'Availability updated successfully.');
        return $this->redirectToRoute('app_matching_list');
    }
        */

        #[Route('/history', name: 'app_matching_history', methods: ['GET'])]
        public function history(EntityManagerInterface $entityManager): JsonResponse
        {
            $history = $entityManager->getRepository(History::class)->findBy([], ['timestamp' => 'DESC']);
        
            $historyData = array_map(function ($entry) {
                return [
                    'action' => $entry->getAction(),
                    'entity' => $entry->getEntity(),
                    'timestamp' => $entry->getTimestamp()->format('d/m/Y H:i'),
                ];
            }, $history);
        
            return new JsonResponse($historyData);
        }
        
        
}
