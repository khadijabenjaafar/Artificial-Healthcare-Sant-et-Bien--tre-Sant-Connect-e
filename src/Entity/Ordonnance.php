<?php

namespace App\Entity;

use App\Repository\OrdonnanceRepository;
use Doctrine\DBAL\Types\Types;
use Doctrine\ORM\Mapping as ORM;
use Symfony\Component\Validator\Constraints as Assert;
#[ORM\Entity(repositoryClass: OrdonnanceRepository::class)]
class Ordonnance
{
    #[ORM\Id]
    #[ORM\GeneratedValue]
    #[ORM\Column]
    private ?int $id = null;

    #[ORM\Column(type: "date")]
    #[Assert\NotNull(message: "La date est obligatoire.")]
    #[Assert\Type("\DateTimeInterface")]
  
    private ?\DateTimeInterface $date = null;
    

    #[ORM\Column(length: 255)]
    #[Assert\Length(max: 255, maxMessage: "Les médicaments ne peuvent pas dépasser 255 caractères.")]
    #[Assert\NotBlank(message: "Le champ des médicaments ne peut pas être vide.")]
    private ?string $medicaments = null;

    #[ORM\Column(length: 255)]
    #[Assert\Length(max: 500, maxMessage: "Le commentaire ne peut pas dépasser 500 caractères.")]
    private ?string $commantaire = null;

    #[ORM\Column(length: 255)]
    #[Assert\Length(max: 255, maxMessage: "La durée d'utilisation ne peut pas dépasser 255 caractères.")]
    #[Assert\NotBlank(message: "La durée d'utilisation est obligatoire.")]
    private ?string $duree_utilisation = null;

    #[ORM\Column(length: 255)]
    #[Assert\Length(max: 255, maxMessage: "La quantité d'utilisation ne peut pas dépasser 255 caractères.")]
    #[Assert\NotBlank(message: "La quantité d'utilisation est obligatoire.")]
    private ?string $quantite_utilisation = null;

    #[ORM\OneToOne(cascade: ['persist', 'remove'],orphanRemoval: true)]
    #[ORM\JoinColumn(name: 'id_consultation_id', referencedColumnName: 'id', onDelete: 'CASCADE')]
    private ?Consultation $id_consultation = null;

    public function getId(): ?int
    {
        return $this->id;
    }
    public function __toString(): string
    {
        return (string) $this->id; // Ou toute autre propriété que vous souhaitez afficher
    }
    public function getDate(): ?\DateTimeInterface
    {
        return $this->date;
    }

    public function setDate(\DateTimeInterface $date): static
    {
        $this->date = $date;

        return $this;
    }

    public function getMedicaments(): ?string
    {
        return $this->medicaments;
    }

    public function setMedicaments(string $medicaments): static
    {
        $this->medicaments = $medicaments;

        return $this;
    }

    public function getCommantaire(): ?string
    {
        return $this->commantaire;
    }

    public function setCommantaire(string $commantaire): static
    {
        $this->commantaire = $commantaire;

        return $this;
    }

    public function getDureeUtilisation(): ?string
    {
        return $this->duree_utilisation;
    }

    public function setDureeUtilisation(string $duree_utilisation): static
    {
        $this->duree_utilisation = $duree_utilisation;

        return $this;
    }

    public function getQuantiteUtilisation(): ?string
    {
        return $this->quantite_utilisation;
    }

    public function setQuantiteUtilisation(string $quantite_utilisation): static
    {
        $this->quantite_utilisation = $quantite_utilisation;

        return $this;
    }

    public function getIdConsultation(): ?Consultation
    {
        return $this->id_consultation;
    }

    public function setIdConsultation(?Consultation $id_consultation): static
    {
        $this->id_consultation = $id_consultation;

        return $this;
    }
}
