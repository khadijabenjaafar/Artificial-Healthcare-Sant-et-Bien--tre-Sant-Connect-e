<?php

namespace App\Form;


use App\Entity\Utilisateur;
use Symfony\Component\Form\AbstractType;
use Symfony\Component\Form\FormBuilderInterface;
use Symfony\Component\OptionsResolver\OptionsResolver;
use Symfony\Component\Form\Extension\Core\Type\EmailType;
use Symfony\Component\Form\Extension\Core\Type\PasswordType;
use Symfony\Component\Form\Extension\Core\Type\SubmitType;
use Symfony\Component\Validator\Constraints as Assert;
use Symfony\Component\Validator\Constraints\NotBlank;
use Symfony\Component\Form\Extension\Core\Type\HiddenType;
use Symfony\Component\Security\Csrf\CsrfTokenManagerInterface;

class loginformType extends AbstractType
{
    private $csrfTokenManager;

    public function buildForm(FormBuilderInterface $builder, array $options): void
    {
        $builder

        ->add('email', EmailType::class, [
            'constraints' => [new NotBlank(['message' => 'Veuillez entrer un email'])],
            'attr' => ['class' => 'form-control', 'name' => "email"]
        ])
        ->add('password', PasswordType::class, [
            'constraints' => [new NotBlank(['message' => 'Veuillez entrer un mot de passe'])],
            'attr' => ['class' => 'form-control']
        ])
        ->add('submit', SubmitType::class, [
            'attr' => ['class' => 'btn btn-primary', 'name' => "login_submit"]
        ]);
    }

    public function configureOptions(OptionsResolver $resolver): void
    {
        $resolver->setDefaults([
            'data_class' => Utilisateur::class,
        ]);
    }
}
