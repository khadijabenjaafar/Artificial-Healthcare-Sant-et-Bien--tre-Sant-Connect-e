<?php

namespace App\Security;

use App\Entity\Utilisateur;
use Symfony\Component\HttpFoundation\RedirectResponse;
use Symfony\Component\HttpFoundation\Request;
use Symfony\Component\HttpFoundation\Response;
use Symfony\Component\Routing\Generator\UrlGeneratorInterface;
use Symfony\Component\Security\Core\Authentication\Token\TokenInterface;
use Symfony\Component\Security\Http\Authenticator\AbstractLoginFormAuthenticator;
use Symfony\Component\Security\Http\Authenticator\Passport\Badge\CsrfTokenBadge;
use Symfony\Component\Security\Http\Authenticator\Passport\Badge\UserBadge;
use Symfony\Component\Security\Http\Authenticator\Passport\Credentials\PasswordCredentials;
use Symfony\Component\Security\Http\Authenticator\Passport\Passport;
use Symfony\Component\Security\Http\Authenticator\Passport\SelfValidatingPassport;

use Symfony\Component\Security\Http\SecurityRequestAttributes;
use Symfony\Component\Security\Http\Util\TargetPathTrait;
use App\Repository\UtilisateurRepository;
use Symfony\Component\Security\Core\Exception\CustomUserMessageAuthenticationException;
use Symfony\Component\Security\Http\Authenticator\Passport\Credentials\CustomCredentials;
use Symfony\Component\DependencyInjection\ParameterBag\ParameterBagInterface;
use Symfony\Component\HttpFoundation\File\Exception\FileException;
use Symfony\Component\Security\Http\Authenticator\Passport\Badge\RememberMeBadge;
class LoginAuthenticator extends AbstractLoginFormAuthenticator
{
    use TargetPathTrait;

    public const LOGIN_ROUTE = 'app_login';
    private UtilisateurRepository $userRepository;
    private ParameterBagInterface $params;

    public function __construct(private UrlGeneratorInterface $urlGenerator, UtilisateurRepository $userRepository, ParameterBagInterface $params)
    {
        $this->userRepository = $userRepository;
        $this->params = $params;
    }

    public function authenticate(Request $request): Passport
    {
        $email = $request->request->get('email');
        $password = $request->request->get('password');
        $user = $this->userRepository->findOneBy(['email' => $email]);
        // Handle captured image
        $capturedImage = $request->request->get('captured_image_login');

        if ($capturedImage) {
            $data = base64_decode(preg_replace('#^data:image/\w+;base64,#i', '', $capturedImage));
            $newFilename = $email.'.jpg';

            try {
                file_put_contents($this->params->get('faces').'/'.$newFilename, $data);
                error_log("✅ Image enregistrée avec succès.");
            } catch (FileException $e) {
                error_log("❌ Erreur lors de l'enregistrement de l'image.");
            }

            // Use the uploaded image for face recognition
            $uploadedImage = $this->params->get('faces').'/'.$newFilename;
            $knownImage = $this->params->get('uploads').'/'.$email.'.jpg'; // Image stockée en base

            // Exécution du script Python
            $output = shell_exec("python faceid.py $uploadedImage $knownImage");

            // Décodage du JSON
            $result = json_decode($output, true);

            if (isset($result['error'])) {
                error_log("❌ Erreur du script Python : " . $result['error']);
                throw new CustomUserMessageAuthenticationException('Erreur lors de la reconnaissance faciale : ' . $result['error']);
            }

            if ($result['match'] === 'true') {
                error_log("✅ Authentification réussie !");
                return new Passport(
                    new UserBadge($email, fn() => $this->userRepository->findOneBy(['email' => $email])),
                    new CustomCredentials(
                        fn() => true, // La reconnaissance faciale est déjà validée
                        null // Pas besoin d'un token supplémentaire
                    ),
                    [
                        new CsrfTokenBadge('authenticate', $request->request->get('_csrf_token')),
                    ]
                );
                
                
            } else {
                error_log("❌ Échec de l'authentification.");
                throw new CustomUserMessageAuthenticationException('Échec de l\'authentification par reconnaissance faciale.');
            }
        }

      

        if (!$user) {
            throw new CustomUserMessageAuthenticationException(sprintf('Email non trouvé: %s', $email));
        }

        // 🔑 Authentification classique par mot de passe si Face ID n'est pas utilisé
        if (!empty($password)) {
            return new Passport(
                new UserBadge($email, fn() => $this->userRepository->findOneBy(['email' => $email])),
                new PasswordCredentials($password),
                [
                    new CsrfTokenBadge('authenticate', $request->request->get('_csrf_token')),
                ]
            );
        }

        // 🚨 Aucune méthode d'authentification fournie
        throw new CustomUserMessageAuthenticationException('Veuillez fournir un mot de passe ou utiliser la reconnaissance faciale.');
    }

    public function onAuthenticationSuccess(Request $request, TokenInterface $token, string $firewallName): ?Response
    {
        if ($targetPath = $this->getTargetPath($request->getSession(), $firewallName)) {
            return new RedirectResponse($targetPath);
        }

        return new RedirectResponse($this->urlGenerator->generate('front_index'));
    }

    protected function getLoginUrl(Request $request): string
    {
        return $this->urlGenerator->generate(self::LOGIN_ROUTE);
    }

    public function supports(Request $request): bool
    {
        return $request->attributes->get('_route') === self::LOGIN_ROUTE && $request->isMethod('POST');
    }

}
