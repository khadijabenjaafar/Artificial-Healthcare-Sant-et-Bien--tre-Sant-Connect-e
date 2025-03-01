<?php

namespace App\Entity;

use App\Enum\ModeType;
use App\Enum\MotifType;
use App\Enum\StatutType;
use App\Repository\RendezVousRepository;
use Doctrine\DBAL\Types\Types;
use Doctrine\ORM\Mapping as ORM;
use Symfony\Component\Validator\Constraints as Assert;
use Symfony\Component\Validator\Context\ExecutionContextInterface;


#[ORM\Entity(repositoryClass: RendezVousRepository::class)]
class RendezVous
{
    #[ORM\Id]
    #[ORM\GeneratedValue]
    #[ORM\Column]
    private ?int $id = null;

    #[ORM\Column(type: Types::DATETIME_MUTABLE)]
    #[Assert\NotNull(message: "La date et l'heure du rendez-vous sont obligatoires.")]
    #[Assert\GreaterThan("today", message: "La date du rendez-vous doit être dans le futur.")]
    private ?\DateTimeInterface $date_heure = null;

    #[ORM\Column(length: 255)]
    private ?MotifType $motif = null;

    #[ORM\Column(length: 255)]
    private ?StatutType $statut = null;

    #[ORM\Column(length: 255)]
    private ?ModeType $mode = null;

    #[ORM\Column(type: "string", length: 255,nullable: false)]
    #[Assert\NotBlank(message: "Le commentaire ne peut pas être vide.")]
    #[Assert\Length(max: 255, maxMessage: "Le commentaire ne doit pas dépasser 255 caractères.")]
    private ?string $commantaire = null;

    #[ORM\ManyToOne(inversedBy: 'id_medecin')]
    private ?Utilisateur $id_patient = null;

    #[ORM\ManyToOne(inversedBy: 'rendezVouses')]
    private ?Utilisateur $id_medecin = null;

    public function getId(): ?int
    {
        return $this->id;
    }

    public function getDateHeure(): ?\DateTimeInterface
    {
        return $this->date_heure;
    }

    public function setDateHeure(\DateTimeInterface $date_heure): static
    {
        $this->date_heure = $date_heure;

        return $this;
    }

    public function getMotif(): ?MotifType
    {
        return $this->motif;
    }

    public function setMotif(MotifType $motif): static
    {
        $this->motif = $motif;

        return $this;
    }

    public function getStatut(): ?StatutType
    {
        return $this->statut;
    }

    public function setStatut(StatutType $statut): static
    {
        $this->statut =  $statut;

        return $this;
    }

    public function getMode(): ?ModeType
    {
        return $this->mode;
    }

    public function setMode(ModeType $mode): static
    {
        $this->mode = $mode;

        return $this;
    }

    public function getCommantaire(): ?string
    {
        return $this->commantaire;
    }

    public function setCommantaire(string $commantaire): self
    {
        $this->commantaire = $commantaire;

        return $this;
    }

    public function getIdPatient(): ?Utilisateur
    {
        return $this->id_patient;
    }

    public function setIdPatient(?Utilisateur $id_patient): static
    {
        $this->id_patient = $id_patient;

        return $this;
    }

    public function getIdMedecin(): ?Utilisateur
    {
        return $this->id_medecin;
    }

    public function setIdMedecin(?Utilisateur $id_medecin): static
    {
        $this->id_medecin = $id_medecin;

        return $this;
    }

    #[Assert\Callback('validateDateHeure')]
    public function validateDateHeure(ExecutionContextInterface $context ): void
{
    if ($this->date_heure === null) {
        return;
    }

    $now = new \DateTime();
    $hour = (int) $this->date_heure->format('H');

    if ($this->date_heure < $now) {
        $context->buildViolation("La date du rendez-vous doit être dans le futur.")
            ->atPath('date_heure')
            ->addViolation();
    }

    if ($hour < 8 || $hour > 16) {
        $context->buildViolation("L'heure du rendez-vous doit être entre 08:00 et 16:00.")
            ->atPath('date_heure')
            ->addViolation();
    }
}

public function __construct()
{
    $this->date_heure = new \DateTime(); // Définit la date actuelle par défaut
}



}
