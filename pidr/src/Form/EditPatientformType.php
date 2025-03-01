<?php

namespace App\Form;

use App\Enum\enumRole;
use App\Entity\Utilisateur;
use Symfony\Component\Form\AbstractType;
use Symfony\Component\Form\FormBuilderInterface;
use Symfony\Component\OptionsResolver\OptionsResolver;
use Symfony\Component\Form\Extension\Core\Type\ChoiceType;
use Symfony\Component\Form\Extension\Core\Type\EmailType;
use Symfony\Component\Form\Extension\Core\Type\PasswordType;
use Symfony\Component\Form\Extension\Core\Type\SubmitType;
use Symfony\Component\Form\Extension\Core\Type\DateType;
use Symfony\Component\Form\Extension\Core\Type\FileType;
use Symfony\Component\Form\Extension\Core\Type\TextType;


class EditPatientformType extends AbstractType
{
    public function buildForm(FormBuilderInterface $builder, array $options): void
    {
        $builder
            ->add('nom',null,[
                'label' => false,
                'attr' => [
                    'placeholder' => 'Nom',
                    'required' => 'true'
            ],
            ])
            ->add('prenom',null,[
                'label' => false,
                'attr' => [
                    'placeholder' => 'Prenom',
                    'required' => 'true'
                ],
            ])
            ->add('email', EmailType::class,[
                'label' => false,
                'attr' => ['placeholder' => 'Email'],
            ])
            ->add('ancien_mot_de_passe', TextType::class, [
                'label' => false,
                'mapped' => false, // Ce champ ne sera pas sauvegardé dans l'entité
                'attr' => [
                    'readonly' => true, // Empêche la modification
                    'class' => 'form-control',
                    'value' => 'Mot de passe déjà défini', // Message indicatif
                ],
            ])
            ->add('mot_de_passe', PasswordType::class, [
                'label' => false,
                'mapped' => false, 
                'required' => false, // Permet de ne pas le remplir
                'attr' => [
                    'placeholder' => 'Nouveau mot de passe (laisser vide si inchangé)',
                    'class' => 'form-control',
                ],
            ])
            ->add('date_naissance', DateType::class, [
                'widget' => 'single_text',
            ])
            ->add('role', ChoiceType::class, [
                'choices' => [
                    'Patient' => enumRole::PATIENT,
                    'Freelancer' => enumRole::FREELANCER,
                ],
                'choice_label' => fn($choice) => $choice->value, 
                'expanded' => false, 
                'multiple' => false,
            ])
            ->add('adresse',null,[
                'label' => false,
                'attr' => [
                    'placeholder' => 'Adresse',
                    'required' => 'true'
                ],
            ])
            ->add('genre', ChoiceType::class, [
                'choices' => [
                    'Homme' => 'Homme',
                    'Femme' => 'Femme',
                ],
                'expanded' => true, 
                'multiple' => false,
                'attr' => [
                   'required' => 'true'
                ],
            ])
            ->add('image', FileType::class, [
                'label' => 'Upload Image',
                'mapped' => false,  // Cela signifie que ce champ n'est pas mappé à une propriété d'entité
                'required' => false,  // Si le téléchargement d'une image est optionnel
            ])
            
            ->add('save', SubmitType::class, [
                'label' => 'Modifier',
                'attr' => ['class' => 'btn btn-primary']
            ]);

        ;

    }

    public function configureOptions(OptionsResolver $resolver): void
    {
        $resolver->setDefaults([
            'data_class' => Utilisateur::class,
        ]);
    }
}
