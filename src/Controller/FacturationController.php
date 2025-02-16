<?php

namespace App\Controller;
use App\Entity\Facturation;
use App\Form\FacturationType;
use App\Enum\MethodePaiement;
use App\Repository\FacturationRepository;
use Doctrine\ORM\EntityManagerInterface;
use Symfony\Bundle\FrameworkBundle\Controller\AbstractController;
use Symfony\Component\HttpFoundation\Request;
use Symfony\Component\HttpFoundation\Response;
use Symfony\Component\Routing\Attribute\Route;

#[Route('/facturation')]
final class FacturationController extends AbstractController
{
    #[Route(name: 'app_facturation_index', methods: ['GET'])]
    public function index(FacturationRepository $facturationRepository): Response
    {
        return $this->render('facturation/index.html.twig', [
            'facturations' => $facturationRepository->findAll(),
        ]);
    }

    #[Route('/new', name: 'app_facturation_new', methods: ['GET', 'POST'])]
    public function new(Request $request, EntityManagerInterface $entityManager): Response
    {
        $facturation = new Facturation();
        $form = $this->createForm(FacturationType::class, $facturation);
        $form->handleRequest($request);

        if ($form->isSubmitted() && $form->isValid()) {

            $facturation->setMethodePaiement($form->get('methode_paiement')->getData());
            
            $entityManager->persist($facturation);
            $entityManager->flush();

            return $this->redirectToRoute('app_facturation_index', [], Response::HTTP_SEE_OTHER);
        }

        return $this->render('facturation/new.html.twig', [
            'facturation' => $facturation,
            'form' => $form,
        ]);
    }

    #[Route('/{id}', name: 'app_facturation_show', methods: ['GET'])]
    public function show(Facturation $facturation): Response
    {
        return $this->render('facturation/show.html.twig', [
            'facturation' => $facturation,
        ]);
    }

    #[Route('/{id}/edit', name: 'app_facturation_edit', methods: ['GET', 'POST'])]
public function edit(Request $request, Facturation $facturation, EntityManagerInterface $entityManager): Response
{
    // Sauvegarder les anciennes données (avant modification)
    $originalData = clone $facturation;

    // Créer et traiter le formulaire
    $form = $this->createForm(FacturationType::class, $facturation);
    $form->handleRequest($request);

    if ($form->isSubmitted() && $form->isValid()) {
        // Vérifier si des modifications ont été faites en comparant les anciennes et nouvelles valeurs
        if (
            $originalData->getMethodePaiement() === $facturation->getMethodePaiement() &&
            $originalData->getStatut() === $facturation->getStatut()
        ) {
            // Si aucune modification n'a été détectée
            $this->addFlash('warning', 'Aucune modification détectée.');
            return $this->redirectToRoute('app_facturation_edit', ['id' => $facturation->getId()]);
        }

        // Si des modifications sont détectées, effectuer l'update dans la base de données
        $entityManager->flush();
        $this->addFlash('success', 'Facturation mise à jour avec succès.');

        // Redirection après mise à jour réussie
        return $this->redirectToRoute('app_facturation_index', [], Response::HTTP_SEE_OTHER);
    }

    // Rendu du formulaire de modification
    return $this->render('facturation/edit.html.twig', [
        'facturation' => $facturation,
        'form' => $form->createView(),
    ]);
}


    #[Route('/{id}', name: 'app_facturation_delete', methods: ['POST'])]
    public function delete(Request $request, Facturation $facturation, EntityManagerInterface $entityManager): Response
    {
        if ($this->isCsrfTokenValid('delete'.$facturation->getId(), $request->getPayload()->getString('_token'))) {
            $entityManager->remove($facturation);
            $entityManager->flush();
        }

        return $this->redirectToRoute('app_facturation_index', [], Response::HTTP_SEE_OTHER);
    }
}
