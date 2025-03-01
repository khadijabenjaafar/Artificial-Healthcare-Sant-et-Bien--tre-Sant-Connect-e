<?php

namespace App\Form;

use App\Entity\Facturation;
use App\Entity\Ordonnance;
use App\Enum\MethodePaiement;
use Symfony\Bridge\Doctrine\Form\Type\EntityType;
use Symfony\Component\Form\AbstractType;
use Symfony\Component\Form\FormBuilderInterface;
use Symfony\Component\Form\Extension\Core\Type\ChoiceType;
use Symfony\Component\OptionsResolver\OptionsResolver;

class FacturationType extends AbstractType
{
    public function buildForm(FormBuilderInterface $builder, array $options): void
    {
        $builder
            ->add('id_ordonnance_id', EntityType::class, [
                'class' => Ordonnance::class,
                'choice_label' => 'id',
            ])
            ->add('date', null, [
                'widget' => 'single_text',
            ])
            ->add('montant')
            ->add('methode_paiement', ChoiceType::class, [
                'choices' => array_combine(
                    array_map(fn ($case) => ucfirst($case->value), MethodePaiement::cases()), // Labels
                    MethodePaiement::cases() // Enum values
                ),
                'choice_label' => fn ($choice) => ucfirst($choice->value),
            ])
            ->add('statut')
        ;
    }

    public function configureOptions(OptionsResolver $resolver): void
    {
        $resolver->setDefaults([
            'data_class' => Facturation::class,
        ]);
    }
}
