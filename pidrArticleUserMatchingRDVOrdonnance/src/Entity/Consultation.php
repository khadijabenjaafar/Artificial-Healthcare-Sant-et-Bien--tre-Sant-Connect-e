<?php

namespace App\Entity;

use App\Repository\ConsultationRepository;
use Doctrine\DBAL\Types\Types;
use Doctrine\ORM\Mapping as ORM;
use Symfony\Component\Validator\Constraints as Assert;

#[ORM\Entity(repositoryClass: ConsultationRepository::class)]
class Consultation
{
    #[ORM\Id]
    #[ORM\GeneratedValue]
    #[ORM\Column]
    private ?int $id = null;

    #[ORM\Column(length: 255)]
    #[Assert\NotBlank(message: "Le diagnostic est obligatoire.")]
    #[Assert\Length(min: 10, max: 255, minMessage: "Le diagnostic doit contenir au moins 10 caractères.")]
    private ?string $diagnostic = null;

    #[ORM\Column(length: 255)]
    #[Assert\NotBlank(message: "Le traitement est obligatoire.")]
    #[Assert\Length(min: 5, max: 255, minMessage: "Le traitement doit contenir au moins 5 caractères.")]
    private ?string $traitement = null;

    #[ORM\Column(length: 255)]
    #[Assert\NotBlank(message: "L'observation est obligatoire.")]
    #[Assert\Length(max: 500, maxMessage: "L'observation ne doit pas dépasser 500 caractères.")]
    private ?string $observation = null;

    #[ORM\Column(type: Types::DECIMAL, precision: 10, scale: 0)]
    #[Assert\NotBlank(message: "Le prix est obligatoire.")]
    #[Assert\Positive(message: "Le prix doit être un nombre positif.")]
    #[Assert\Type(type: 'numeric', message: "Le prix doit être un nombre.")]
    private ?string $prix = null;

    #[ORM\Column(type: Types::DATE_MUTABLE)]
    #[Assert\NotNull(message: "La date du prochain rendez-vous est obligatoire.")]
    #[Assert\Type(type: \DateTimeInterface::class, message: "La valeur doit être une date valide.")]
    #[Assert\GreaterThan("today", message: "La date du prochain rendez-vous doit être dans le futur.")]
    private ?\DateTimeInterface $prochain_rdv = null;

    #[ORM\Column(type: Types::DECIMAL, precision: 10, scale: 0)]
    #[Assert\NotBlank(message: "La durée est obligatoire.")]
    #[Assert\Positive(message: "La durée doit être un nombre positif.")]
    #[Assert\Type(type: 'numeric', message: "La durée doit être un nombre.")]
    private ?string $duree = null;

    #[ORM\OneToOne(cascade: ['persist', 'remove'],orphanRemoval: true)]
    #[ORM\JoinColumn(name: 'id_rendez_vous', referencedColumnName: 'id', onDelete: 'CASCADE')]
    private ?RendezVous $id_rendez_vous = null;

    #[ORM\OneToOne(cascade: ['persist', 'remove'])]
    private ?RendezVous $id_rendezvous = null;

    public function getId(): ?int
    {
        return $this->id;
    }

    public function getDiagnostic(): ?string
    {
        return $this->diagnostic;
    }

    public function setDiagnostic(string $diagnostic): static
    {
        $this->diagnostic = $diagnostic;

        return $this;
    }

    public function getTraitement(): ?string
    {
        return $this->traitement;
    }

    public function setTraitement(string $traitement): static
    {
        $this->traitement = $traitement;

        return $this;
    }

    public function getObservation(): ?string
    {
        return $this->observation;
    }

    public function setObservation(string $observation): static
    {
        $this->observation = $observation;

        return $this;
    }

    public function getPrix(): ?string
    {
        return $this->prix;
    }

    public function setPrix(string $prix): static
    {
        $this->prix = $prix;

        return $this;
    }

    public function getProchainRdv(): ?\DateTimeInterface
    {
        return $this->prochain_rdv;
    }

    public function setProchainRdv(\DateTimeInterface $prochain_rdv): static
    {
        $this->prochain_rdv = $prochain_rdv;

        return $this;
    }

    public function getDuree(): ?string
    {
        return $this->duree;
    }

    public function setDuree(string $duree): static
    {
        $this->duree = $duree;

        return $this;
    }

    public function getIdRendezVous(): ?RendezVous
    {
        return $this->id_rendez_vous;
    }

    public function setIdRendezVous(?RendezVous $id_rendez_vous): static
    {
        $this->id_rendez_vous = $id_rendez_vous;

        return $this;
    }
    public function __construct()
{
    // Assure-toi que la date est initialisée à une valeur valide par défaut.
    $this->prochain_rdv = new \DateTime('tomorrow');  // Valeur par défaut
}
}
