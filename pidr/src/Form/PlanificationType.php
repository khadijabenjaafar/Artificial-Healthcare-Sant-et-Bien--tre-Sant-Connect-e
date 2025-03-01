<?php

namespace App\Form;

use App\Entity\Planification;
use Symfony\Component\Form\AbstractType;
use Symfony\Component\Form\FormBuilderInterface;
use Symfony\Component\Validator\Constraints\Date;
use VictorPrdh\RecaptchaBundle\Validator\Constraints\Recaptcha;
use VictorPrdh\RecaptchaBundle\Form\ReCaptchaType;
use Symfony\Component\Validator\Constraints\Length;
use Symfony\Component\Validator\Constraints\NotBlank;
use Symfony\Component\OptionsResolver\OptionsResolver;
use Symfony\Component\Form\Extension\Core\Type\DateType;
use Symfony\Component\Form\Extension\Core\Type\TextType;
use Symfony\Component\Validator\Constraints\GreaterThan;
use Symfony\Component\Form\Extension\Core\Type\ChoiceType;
use Symfony\Component\Form\Extension\Core\Type\TextareaType;
use VictorPrdh\RecaptchaBundle\Validator\Constraints\IsValidCaptcha;
class PlanificationType extends AbstractType
{
    public function buildForm(FormBuilderInterface $builder, array $options): void
    {
        $builder
            ->add('date', DateType::class, [
                'widget' => 'single_text',
                'required' => true,
               
            ])
            ->add('adresse', TextType::class, [
                'label' => 'Adresse',
                'required' => true,
                
            ])
            ->add('mode', ChoiceType::class, [
                'choices' => [
                    'Présentiel' => 'présentiel',
                    'En ligne' => 'en ligne',
                ],
                'label' => 'Mode',
                
            ])
            
           ;
            
    }

    public function configureOptions(OptionsResolver $resolver): void
    {
        $resolver->setDefaults([
            'data_class' => Planification::class,
        ]);
    }
}