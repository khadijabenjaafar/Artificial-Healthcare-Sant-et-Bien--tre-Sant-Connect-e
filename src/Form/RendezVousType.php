<?php

namespace App\Form;

use App\Entity\RendezVous;
use App\Entity\Utilisateur;
use App\Enum\MotifType;
use App\Enum\StatutType;
use App\Enum\ModeType;
use Symfony\Bridge\Doctrine\Form\Type\EntityType;
use Symfony\Component\Form\AbstractType;
use Symfony\Component\Form\FormBuilderInterface;
use Symfony\Component\Form\Extension\Core\Type\ChoiceType;
use Symfony\Component\OptionsResolver\OptionsResolver;
use Symfony\Component\Form\Extension\Core\Type\EnumType;
use Symfony\Component\Form\Extension\Core\Type\DateTimeType;
use Symfony\Component\Validator\Constraints as Assert;
use Symfony\Component\Validator\Constraints\NotBlank;
use Symfony\Component\Form\Extension\Core\Type\TextareaType;




class RendezVousType extends AbstractType
{
    public function buildForm(FormBuilderInterface $builder, array $options): void
    {
        $builder
            ->add('date_heure', null, [
                'widget' => 'single_text',
                'html5' => true,
                'attr' => [
                    'min' => (new \DateTime())->format('Y-m-d\TH:i'), // Bloque les dates passées
                ],
                'constraints' => [
                    new Assert\NotNull(['message' => 'Veuillez sélectionner une date et une heure.']),
                    new Assert\GreaterThan([
                        'value' => 'now',
                        'message' => 'La date doit être dans le futur.'
                    ])
                ]
            ])
            ->add('motif', ChoiceType::class, [
                'choices' => array_combine(
                    array_map(fn ($case) => ucfirst($case->value), MotifType::cases()), // Labels
                    MotifType::cases() // Enum values
                ),
                'choice_label' => fn ($choice) => ucfirst($choice->value),
                
            ])
            
            ->add('statut', ChoiceType::class, [
                'choices' => array_combine(
                    array_map(fn ($case) => ucfirst($case->value), StatutType::cases()), // Labels
                    array_map(fn ($case) => $case, StatutType::cases()) // Enum values
                ),
                'choice_label' => fn ($choice) => ucfirst($choice->value),
                'data' => $options['data']->getStatut() ?? null,
            ])
            ->add('mode', ChoiceType::class, [
                'choices' => array_combine(
                    array_map(fn ($case) => ucfirst($case->value), ModeType::cases()), // Labels
                    array_map(fn ($case) => $case, ModeType::cases()) // Enum values
                ),
                'choice_label' => fn ($choice) => ucfirst($choice->value),
                'data' => $options['data']->getMode() ?? null,
            ])
            ->add('commantaire', TextareaType::class, [
                'required' => true,
                'constraints' => [
                    new NotBlank([
                        'message' => 'Le commentaire est obligatoire.',
                    ]),
                ],
                'required' => true, // Assure que le champ est requis
            ])
            ->add('id_patient', EntityType::class, [
                'class' => Utilisateur::class,
                'choice_label' => 'id',
            ])
            ->add('id_medecin', EntityType::class, [
                'class' => Utilisateur::class,
                'choice_label' => 'id',
            ])
        ;
    }

    public function configureOptions(OptionsResolver $resolver): void
    {
        $resolver->setDefaults([
            'data_class' => RendezVous::class,
        ]);
    }
}
