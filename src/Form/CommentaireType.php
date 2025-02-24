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
