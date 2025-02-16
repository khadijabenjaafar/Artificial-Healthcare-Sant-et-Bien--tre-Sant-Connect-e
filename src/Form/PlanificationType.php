<?php

namespace App\Form;

use App\Entity\Planification;
use Symfony\Component\Form\AbstractType;
use Symfony\Component\Form\Extension\Core\Type\DateType;
use Symfony\Component\Form\Extension\Core\Type\TextType;
use Symfony\Component\Form\Extension\Core\Type\ChoiceType;
use Symfony\Component\Form\FormBuilderInterface;
use Symfony\Component\OptionsResolver\OptionsResolver;
use Symfony\Component\Validator\Constraints\NotBlank;
use Symfony\Component\Validator\Constraints\Length;
use Symfony\Component\Validator\Constraints\GreaterThan;
use Symfony\Component\Validator\Constraints\Date;

class PlanificationType extends AbstractType
{
    public function buildForm(FormBuilderInterface $builder, array $options): void
    {
        $builder
            ->add('date', DateType::class, [
                'widget' => 'single_text',
                'required' => true,
                'constraints' => [
                    new NotBlank(['message' => 'La date ne peut pas être vide.']),
                    new Date(['message' => 'La date doit être valide.']),
                    new GreaterThan(['value' => 'today', 'message' => 'La date doit être dans le futur.']),
                ],
                'attr' => [
                    'min' => (new \DateTime())->format('Y-m-d'), // HTML5 validation attribute to ensure future date
                ],
            ])
            ->add('adresse', TextType::class, [
                'label' => 'Adresse',
                'required' => true,
                'constraints' => [
                    new NotBlank(['message' => "L'adresse ne peut pas être vide."]),
                    new Length(['max' => 255, 'maxMessage' => "L'adresse ne peut pas dépasser {{ limit }} caractères."]),
                ],
                'attr' => [
                    'maxlength' => 255,
                ],
            ])
            ->add('mode', ChoiceType::class, [
                'choices' => [
                    'Présentiel' => 'présentiel',
                    'En ligne' => 'en ligne',
                ],
                'label' => 'Mode',
                'required' => true,
                'constraints' => [
                    new NotBlank(['message' => 'Le mode ne peut pas être vide.']),
                ],
            ]);
    }

    public function configureOptions(OptionsResolver $resolver): void
    {
        $resolver->setDefaults([
            'data_class' => Planification::class,
        ]);
    }
}