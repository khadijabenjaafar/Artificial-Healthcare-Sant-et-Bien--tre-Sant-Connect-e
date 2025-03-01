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
use Symfony\Component\Form\Extension\Core\Type\CheckboxType;

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
            'required' => false,
            'mapped'   => false, 
            'label'    => 'password',
        ])
       
        ->add('csrfTokenManager', HiddenType::class, [
            'mapped' => false,
            'required' => false, 
        ])
        ->add('use_face_id', CheckboxType::class, [
            'label'    => 'Utiliser la reconnaissance faciale',
            'required' => false,
            'mapped'   => false, // Important : ce champ n'est pas lié à l'entité
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
