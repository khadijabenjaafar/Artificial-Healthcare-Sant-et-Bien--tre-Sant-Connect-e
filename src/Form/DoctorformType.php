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
use Symfony\Component\Form\Extension\Core\Type\DateType;
use Symfony\Component\Form\Extension\Core\Type\SubmitType;
use Symfony\Component\Form\Extension\Core\Type\FileType;
use Symfony\Component\Form\Extension\Core\Type\EnumType;
use Symfony\Component\Validator\Constraints as Assert;
use Symfony\Component\Validator\Constraints\File;

class DoctorformType extends AbstractType
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
            ->add('mot_de_passe',PasswordType::class,[
                'label' => false,
                'attr' => ['placeholder' => 'Mot de passe'],
            ])
            ->add('date_naissance', DateType::class, [
                'widget' => 'single_text',

            ])
            ->add('role', EnumType::class, [
                'class' => enumRole::class,
                'choices' => enumRole::cases(), 
                'choice_label' => fn($choice) => $choice->name, 
                'attr' => ['class' => 'form-select', 'id' => 'selectFloatingLabel'],
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
                'label' => 'Gender',
            ])
            ->add('image', FileType::class, [
                'label' => 'Telecharger votre image de profile (Des fichier image uniquement)',

                // unmapped means that this field is not associated to any entity property
                'mapped' => false,

                // make it optional so you don't have to re-upload the PDF file
                // every time you edit the Product details
                'required' => false,

                // unmapped fields can't define their validation using attributes
                // in the associated entity, so you can use the PHP constraint classes
                'constraints' => [
                    new File([
                        'maxSize' => '2048k',
                        'mimeTypes' => [
                            'image/jpg',
                            'image/png',
                            'image/jpeg',
                        ],
                        'mimeTypesMessage' => 'Please upload a valid Image document',
                    ])
                ],
            ])
            ->add('save', SubmitType::class, [
                'label' => 'Se connecter',
                'attr' => ['class' => 'btn btn-primary']
            ])
            ->add('cancel', SubmitType::class, [
                'label' => 'Annuler',
                'attr' => ['class' => 'btn btn-danger']
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
