<?php

namespace App\Controller;
use App\Entity\Utilisateur;
use App\Entity\Matching;
use App\Form\Matching1Type;
use App\Repository\MatchingRepository;
use App\Repository\UtilisateurRepository;
use App\Entity\Planification;
use Doctrine\ORM\EntityManagerInterface;
use Symfony\Bundle\FrameworkBundle\Controller\AbstractController;
use Symfony\Component\HttpFoundation\Request;
use Symfony\Component\HttpFoundation\Response;
use Symfony\Component\HttpFoundation\JsonResponse;
use Psr\Log\LoggerInterface;
use Symfony\Component\HttpFoundation\File\Exception\FileException;
use Symfony\Component\HttpFoundation\File\UploadedFile;

use Symfony\Component\Routing\Annotation\Route;


#[Route('/matching')]
final class MatchingController extends AbstractController
{
    private $logger;

    public function __construct(LoggerInterface $logger)
    {
        $this->logger = $logger;
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

            return $this->redirectToRoute('doctor_matching_aff', [], Response::HTTP_SEE_OTHER);
        }

        return $this->render('matching/edit.html.twig', [
            'matching' => $matching,
            'form' => $form,
        ]);
    }

    #[Route('/{id}', name: 'app_matching_delete', methods: ['POST'])]
    public function delete(Request $request, Matching $matching, EntityManagerInterface $entityManager): Response
    {
        if ($this->isCsrfTokenValid('delete'.$matching->getId(), $request->getPayload()->getString('_token'))) {
            $entityManager->remove($matching);
            $entityManager->flush();
        }

        return $this->redirectToRoute('doctor_matching_aff', [], Response::HTTP_SEE_OTHER);
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
            'cv' => $matching->getCv() ?? 'No CV available'
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

}