<?php

namespace App\Form;

use App\Entity\Matching;
use App\Entity\Utilisateur;
use Symfony\Bridge\Doctrine\Form\Type\EntityType;
use Symfony\Component\Form\AbstractType;
use Symfony\Component\Form\FormBuilderInterface;
use Symfony\Component\OptionsResolver\OptionsResolver;
use Symfony\Component\Form\Extension\Core\Type\DateType;
use Symfony\Component\Form\Extension\Core\Type\FileType;
use Symfony\Component\Form\Extension\Core\Type\TextType;
use Symfony\Component\Validator\Constraints\File;
use Symfony\Component\Validator\Constraints\NotBlank;
use Symfony\Component\Validator\Constraints\Length;
class MatchingType extends AbstractType
{
    public function buildForm(FormBuilderInterface $builder, array $options): void
    {
        $builder
            
        ->add('cin', TextType::class, [
            'constraints' => [
                new NotBlank(['message' => 'Le CIN ne peut pas être vide.']),
                new Length(['max' => 255, 'maxMessage' => 'Le CIN ne peut pas dépasser {{ limit }} caractères.']),
            ],
        ])
        ->add('description', TextType::class, [
            'constraints' => [
                new NotBlank(['message' => 'La description ne peut pas être vide.']),
                new Length(['max' => 255, 'maxMessage' => 'La description ne peut pas dépasser {{ limit }} caractères.']),
            ],
        ])
        ->add('date', DateType::class, [
            'widget' => 'single_text',
        ])
        ->add('competences', TextType::class, [
            'constraints' => [
                new NotBlank(['message' => 'Les compétences ne peuvent pas être vides.']),
                new Length(['max' => 255, 'maxMessage' => 'Les compétences ne peuvent pas dépasser {{ limit }} caractères.']),
            ],
        ])
        ->add('cv', FileType::class, [
            'label' => 'Upload CV (PDF only)',
            'mapped' => false, // Important since we're handling file uploads manually
            'required' => false, // Not mandatory
            'constraints' => [
                new File([
                    'maxSize' => '5M', // Limit file size
                    'mimeTypes' => ['application/pdf'],
                    'mimeTypesMessage' => 'Veuillez télécharger un fichier PDF valide.',
                ])
            ]
        ])
            ->add('id_freelancer', EntityType::class, [
                'class' => Utilisateur::class,
                'choice_label' => 'id',
            ])
            ->add('utilisateur', EntityType::class, [
                'class' => Utilisateur::class,
                'choice_label' => 'id',
            ])
        ;
    }

    public function configureOptions(OptionsResolver $resolver): void
    {
        $resolver->setDefaults([
            'data_class' => Matching::class,
        ]);
    }
}
