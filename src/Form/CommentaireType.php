<?php

namespace App\Form;

use App\Entity\Article;
use App\Entity\Commentaire;
use App\Entity\Utilisateur;
use Symfony\Bridge\Doctrine\Form\Type\EntityType;
use Symfony\Component\Form\AbstractType;
use Symfony\Component\Form\FormBuilderInterface;
use Symfony\Component\OptionsResolver\OptionsResolver;
use Symfony\Component\Form\Extension\Core\Type\SubmitType;
use Symfony\Component\Form\Extension\Core\Type\TextareaType;
use Symfony\Component\Form\Extension\Core\Type\CheckboxType;
use Symfony\Component\Form\Extension\Core\Type\ChoiceType;



class CommentaireType extends AbstractType
{
    public function buildForm(FormBuilderInterface $builder, array $options): void
    {
        $builder
        ->add('contenue', TextareaType::class, [
            'label' => 'Votre commentaire',
            'required' => true
        ])
        ->add('status', CheckboxType::class, [
            'label' => 'Commentaire anonyme ?',
            'required' => false
        ]);




        //->add('rating', ChoiceType::class, [
          //  'choices' => [
            //    '⭐' => 1,
              //  '⭐⭐' => 2,
                //'⭐⭐⭐' => 3,
                //'⭐⭐⭐⭐' => 4,
                //'⭐⭐⭐⭐⭐' => 5,
            //],
            //'expanded' => true, // Affichage sous forme de boutons
            //'multiple' => false,
            //'label' => 'Votre note',
            //'required' => false, // Permettre de ne pas donner de note
        //]);
        
        
        
        
        //->add('parent', EntityType::class, [
          //  'class' => Commentaire::class,
            //'choice_label' => 'contenue', // Affiche le contenu du commentaire parent
            //'placeholder' => 'Répondre à un commentaire', // Optionnel, pour laisser vide par défaut
            //'required' => false,
        //]);



       
    

        //->add('submit', SubmitType::class, [
           // 'label' => 'Envoyer',
        //])
        
    }

    public function configureOptions(OptionsResolver $resolver): void
    {
        $resolver->setDefaults([
            'data_class' => Commentaire::class,
            'utilisateurs' => [], // Option pour passer la liste des utilisateurs

        ]);
    }
}
