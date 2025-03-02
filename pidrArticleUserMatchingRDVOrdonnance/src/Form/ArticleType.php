<?php

namespace App\Form;
use Symfony\Component\Form\Extension\Core\Type\TextType;
use Symfony\Component\Form\Extension\Core\Type\TextareaType;
use Symfony\Component\Form\Extension\Core\Type\DateType;
use Symfony\Component\Form\Extension\Core\Type\IntegerType;
use Symfony\Component\Form\Extension\Core\Type\SubmitType;
use Symfony\Component\Form\Extension\Core\Type\FileType;
use Symfony\Component\Validator\Constraints\File;
use Symfony\Component\Validator\Constraints as Assert;
use App\Entity\Article;
use App\Entity\Utilisateur;
use Symfony\Bridge\Doctrine\Form\Type\EntityType;
use Symfony\Component\Form\AbstractType;
use Symfony\Component\Form\FormBuilderInterface;
use Symfony\Component\OptionsResolver\OptionsResolver;

class ArticleType extends AbstractType
{
    public function buildForm(FormBuilderInterface $builder, array $options): void
    {
        $builder
        ->add('titre', TextType::class, [
            'label' => 'Titre de l\'article','attr' => ['class' => 'form-control', 'placeholder' => 'Entrez le titre ici'],
                'constraints' => [
                    new Assert\NotBlank(['message' => 'Le titre est obligatoire.']),
                    new Assert\Length([
                        'min' => 5,
                        'minMessage' => 'Le titre doit contenir au moins {{ limit }} caractères.',
                        'max' => 255,
                        'maxMessage' => 'Le titre ne peut pas dépasser {{ limit }} caractères.'
                    ])
                    ]
                    ,'attr' => ['class' => 'form-control'],
                    'empty_data' => ''
            ])


        ->add('contenue', TextareaType::class, [
            'label' => 'Contenue de l\'article',
            'attr' => ['class' => 'form-control', 'rows' => 4, 'placeholder' => 'Écrivez votre article ici...'],
                'constraints' => [
                    new Assert\NotBlank(['message' => 'Le contenu est obligatoire.']),
                    new Assert\Length([
                        'min' => 20,
                        'minMessage' => 'Le contenu doit contenir au moins {{ limit }} caractères.'
                    ])
                    ], 'attr' => ['class' => 'form-control']
        ])

       // ->add('date_article', DateType::class, [
         //   'label' => 'Date de l\'article',
           // 'widget' => 'single_text',
        //])

 


->add('urlimagearticle', FileType::class, [
    'label' => 'Télécharger une nouvelle image',
    'mapped' => false, // ⚠️ Empêche Symfony de lier ce champ directement à l'entité
    'required' => false, // Permet de garder l'ancienne image si aucune nouvelle n'est sélectionnée
    'data_class' => null, // Symfony ne s’attend pas à un objet `File`
    'constraints' => [
        new File([
            'maxSize' => '5M',
            'maxSizeMessage' => 'L\'image ne doit pas dépasser 5 Mo.',
            'mimeTypes' => ['image/jpeg', 'image/png', 'image/webp'],
            'mimeTypesMessage' => 'Veuillez télécharger une image valide (JPG, PNG, WEBP).'
        ])
    ]
])
       // ->add('nbreVue')
      ->add('utilisateur', TextType::class, [
            'disabled' => true, // Empêche la modification
            'mapped' => false, // Ne pas enregistrer dans l'entité
            'attr' => ['class' => 'form-control'],
            'data' => isset($options['user']) ? $options['user']->getNom() : 'Utilisateur inconnu', // Empêcher l'erreur
        ])
          
        ;
    }

    public function configureOptions(OptionsResolver $resolver): void
    {
        $resolver->setDefaults([
            'data_class' => Article::class,
            'user' => null, // Définir une valeur par défaut pour éviter l'erreur

        ]);
    }
}



//$builder
//            ->add('titre')
//            ->add('contenue')
//            ->add('date_article', null, [
//                'widget' => 'single_text',           ])
//          ->add('url_image_article')
//            ->add('nbreVue')          
//            ->add('utilisateur', EntityType::class, [
//                'class' => Utilisateur::class,
//                'choice_label' => 'id',           ])
