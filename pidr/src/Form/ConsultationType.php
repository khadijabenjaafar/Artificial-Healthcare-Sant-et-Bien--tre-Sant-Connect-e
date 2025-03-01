<?php

namespace App\Form;

use App\Entity\Consultation;
use App\Entity\RendezVous;
use Symfony\Bridge\Doctrine\Form\Type\EntityType;
use Symfony\Component\Form\AbstractType;
use Symfony\Component\Form\FormBuilderInterface;
use Symfony\Component\OptionsResolver\OptionsResolver;

class ConsultationType extends AbstractType
{
    public function buildForm(FormBuilderInterface $builder, array $options): void
    {
        $builder
            ->add('diagnostic')
            ->add('traitement')
            ->add('observation')
            ->add('prix')
            ->add('prochain_rdv', null, [
                'widget' => 'single_text',
            ])
            ->add('duree')
            ->add('id_rendez_vous', EntityType::class, [
                'class' => RendezVous::class,
                'choice_label' => 'id',
            ])
        ;
    }

    public function configureOptions(OptionsResolver $resolver): void
    {
        $resolver->setDefaults([
            'data_class' => Consultation::class,
        ]);
    }
}
