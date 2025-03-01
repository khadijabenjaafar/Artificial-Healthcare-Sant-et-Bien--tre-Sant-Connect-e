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
use Symfony\Component\Validator\Constraints\File;
use Symfony\Component\Validator\Constraints as Assert;
use Symfony\Component\Form\Extension\Core\Type\TextType;


class UserformType extends AbstractType
{
    public function buildForm(FormBuilderInterface $builder, array $options): void
    {
        $builder
            ->add('nom', TextType::class, [
                'label' => false,
                'attr' => [
                    'placeholder' => 'Nom',
                    'required' => 'true',
                ],
            ])
            ->add('prenom', TextType::class, [
                'label' => false,
                'attr' => [
                    'placeholder' => 'Prénom',
                    'required' => 'true',
                ],
            ])
            ->add('email', EmailType::class, [
                'label' => false,
                'attr' => ['placeholder' => 'Email'],
                'constraints' => [
                    new Assert\NotBlank(['message' => 'L\'email est obligatoire']),
                    new Assert\Email(['message' => 'L\'adresse email n\'est pas valide']),
                ],
            ])
            ->add('password', PasswordType::class, [
                'label' => false,
                'attr' => ['placeholder' => 'Mot de passe'],
            ])
            ->add('date_naissance', DateType::class, [
                'required' => false,  // Permet de ne pas remplir le champ
                'widget' => 'single_text', // Option pour avoir un champ de type date
                'empty_data' => null,  // Permet de définir 'null' comme valeur par défaut
                'attr' => ['class' => 'js-datepicker'],  // Exemple d'attribut si nécessaire
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
            ->add('adresse', TextType::class, [
                'label' => false,
                'attr' => [
                    'placeholder' => 'Adresse',
                    'required' => 'true',
                ],

            ])
            ->add('numTel', TextType::class, [
                'label' => false,
                'attr' => [
                    'placeholder' => 'numero de telephone',
                    'required' => 'true',
                ],

            ])
            ->add('genre', ChoiceType::class, [
                'choices' => [
                    'Homme' => 'Homme',
                    'Femme' => 'Femme',
                ],
                'expanded' => true,
                'multiple' => false,
        
            ])
            ->add('image', FileType::class, [
                'label' => 'Telecharger votre image de profile (Des fichier image uniquement)',

                // unmapped means that this field is not associated to any entity property

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
                'label' => 'Creer un compte',
                'attr' => ['class' => 'btn btn-primary']
            ]);

    }

    public function configureOptions(OptionsResolver $resolver): void
    {
        $resolver->setDefaults([
            'data_class' => Utilisateur::class,
        ]);
    }
}
