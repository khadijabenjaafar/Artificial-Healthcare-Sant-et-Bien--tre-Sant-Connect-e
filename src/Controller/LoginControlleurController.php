<?php

namespace App\Controller;

use App\Form\ResetPasswordFormType;
use App\Form\ResetPasswordRequestFormType;
use App\Repository\UtilisateurRepository;
use App\Service\SendMailService;
use Doctrine\ORM\EntityManager;
use Doctrine\ORM\EntityManagerInterface;
use Doctrine\ORM\Tools\Console\EntityManagerProvider;
use Symfony\Bundle\FrameworkBundle\Controller\AbstractController;
use Symfony\Component\HttpFoundation\Request;
use Symfony\Component\HttpFoundation\Response;
use Symfony\Component\PasswordHasher\Hasher\UserPasswordHasherInterface;
use Symfony\Component\Routing\Attribute\Route;
use Symfony\Component\Routing\Generator\UrlGeneratorInterface;
use Symfony\Component\Security\Csrf\TokenGenerator\TokenGeneratorInterface;
use Symfony\Component\Security\Http\Authentication\AuthenticationUtils;

class LoginControlleurController extends AbstractController
{

    #[Route(path: '/login', name: 'app_login32')]
    public function login(AuthenticationUtils $authenticationUtils): Response
    {
        // if ($this->getUser()) {
        //     return $this->redirectToRoute('target_path');
        // }

        // get the login error if there is one
        $error = $authenticationUtils->getLastAuthenticationError();
        // last username entered by the user
        $lastUsername = $authenticationUtils->getLastUsername();

        return $this->render('front/seconnect.html.twig', ['last_username' => $lastUsername, 'error' => $error]);
    }

    #[Route(path: '/logout', name: 'app_logout')]
    public function logout(): void
    {
        throw new \LogicException('This method can be blank - it will be intercepted by the logout key on your firewall.');
    }

    #[Route(path: '/oubli-pass', name: 'forgotten_password')]
    public function forgottenPassword (Request $request,UtilisateurRepository $utilisateurRepository,
    TokenGeneratorInterface $tokenGeneratorInterface,
    EntityManagerInterface $entityManager,
    SendMailService $mail):Response
    {
        $form = $this->createForm(ResetPasswordRequestFormType::class);
        $form->handleRequest($request);

        if ($form->isSubmitted() && $form->isValid())
        {
            $user = $utilisateurRepository->findOneByEmail($form->get('email')->getData());
            if ($user)
            {
                $token=$tokenGeneratorInterface->generateToken();
                $user->setResetToken($token);
                $entityManager->persist($user);
                $entityManager->flush();


                $url =$this->generateUrl('reset_pass',['token'=>$token],
                UrlGeneratorInterface::ABSOLUTE_URL);
                $context = compact ('url', 'user');
                
                $mail->send(
                    'no-reply@clinicflow.com',
                    $user->getEmail(),
                    'Reinitialisation de mot de passe',
                    'password_reset',
                    $context
                );
                $this->addFlash('success','Un mail est envoye avec succes');
                return $this->redirectToRoute('app_login');

            }
            $this->addFlash('danger','Un probleme est survenue');
            return $this->redirectToRoute('app_login');
        }

        return $this->render('security/reset_password_request.html.twig' ,[
            'requestPassForm' =>$form->createView()
        ]);
    }

    #[Route('/oubli-pass/{token}', name:'reset_pass')]
    public function resetPass(String $token,Request $request,
    UtilisateurRepository $utilisateurRepository,
     EntityManagerInterface $entityManager, 
     UserPasswordHasherInterface $passwordHasher): Response
    {
        $user=$utilisateurRepository->findOneByResetToken($token);
        if ($user)
        {
            $form =$this->createForm(ResetPasswordFormType::class);
            $form->handleRequest($request);
            if ($form->isSubmitted() && $form->isValid())
            {
                $user->setResetToken('');
                $user->setPassword(
                    $passwordHasher->hashPassword(
                        $user,
                        $form->get('password')->getData()
                ));
                $entityManager->persist($user);
                $entityManager->flush();

                $this->addFlash('sucess','Mot de passe change avec success');
                return $this->redirectToRoute('app_login');
                
            }
            return $this->render('security/reset_password.html.twig',[
                'passForm' => $form->createView()
            ]);
        }
        $this->addFlash('danger','Jeton invalide');
        return $this->redirectToRoute('app_login');
    }
}
