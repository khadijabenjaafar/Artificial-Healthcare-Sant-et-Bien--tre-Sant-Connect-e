<?php

namespace App\Form;

use App\Entity\Matching;
use App\Entity\Utilisateur;
use Symfony\Bridge\Doctrine\Form\Type\EntityType;
use Symfony\Component\Form\AbstractType;
use Symfony\Component\Form\FormBuilderInterface;
use Symfony\Component\Form\Extension\Core\Type\FileType;
use Symfony\Component\Validator\Constraints\File;
use Symfony\Component\OptionsResolver\OptionsResolver;

class Matching1Type extends AbstractType
{
    public function buildForm(FormBuilderInterface $builder, array $options): void
    {
        $builder
            ->add('urlimagearticle', FileType::class, [
                'label' => 'URL de l\'image de l\'article',
                'mapped' => false, // important car l'image n'est pas directement mappée à une propriété
                'required' => false, // Permet de ne pas exiger une nouvelle image
                'data_class' => null, // Empêche Symfony de s’attendre à une instance de File
                'constraints' => [
                    new File([
                        'maxSize' => '5M',
                        'mimeTypes' => ['image/jpeg', 'image/png', 'image/webp'],
                        'mimeTypesMessage' => 'Veuillez télécharger une image valide (JPG, PNG, WEBP)',
                    ])
                ]
            ])
            ->add('cin')
            ->add('description')
            ->add('date', null, [
                'widget' => 'single_text',
            ])
            ->add('competences')
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
            ]) // ✅ Added the missing closing parenthesis here
            ->add('id_freelancer', EntityType::class, [
                'class' => Utilisateur::class,
                'choice_label' => 'id',
            ]);
           
    }

    public function configureOptions(OptionsResolver $resolver): void
    {
        $resolver->setDefaults([
            'data_class' => Matching::class,
        ]);
    }
}
