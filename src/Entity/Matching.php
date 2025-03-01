<?php

namespace App\Entity;

use App\Repository\MatchingRepository;
use Doctrine\DBAL\Types\Types;
use Doctrine\ORM\Mapping as ORM;
use Symfony\Component\Validator\Constraints as Assert;

#[ORM\Entity(repositoryClass: MatchingRepository::class)]
class Matching
{
    #[ORM\Id]
    #[ORM\GeneratedValue]
    #[ORM\Column]
    private ?int $id = null;

    #[ORM\Column(length: 255)]
    #[Assert\NotBlank(message: "Le CIN ne peut pas être vide.")]
    private ?string $cin = null;

    #[ORM\Column(length: 255)]
    #[Assert\NotBlank(message: "La description ne peut pas être vide.")]
    private ?string $description = null;

    #[ORM\Column(type: Types::DATE_MUTABLE)]
    #[Assert\NotBlank(message: "La date ne peut pas être vide.")]
    private ?\DateTimeInterface $date = null;
    
    #[ORM\Column(length: 255)]
    #[Assert\NotBlank(message: "Les compétences ne peuvent pas être vides.")]
    private ?string $competences = null;

    #[ORM\Column(type: 'string', length: 255, nullable: true)]
    private ?string $cv = null;

    #[ORM\OneToOne(cascade: ['persist', 'remove'])]
    private ?Utilisateur $id_freelancer = null;

    #[ORM\OneToOne(targetEntity: Utilisateur::class, inversedBy: 'matching')]
    #[ORM\JoinColumn(name: 'utilisateur_id', referencedColumnName: 'id', onDelete: 'CASCADE')]
    #[Assert\NotNull(message: "L'utilisateur ne peut pas être nul.")]
    private ?Utilisateur $utilisateur = null;

    #[ORM\Column(type: 'decimal', precision: 10, scale: 2, nullable: true)]
    private ?float $price = null;

    #[ORM\Column(type: 'boolean', nullable: false)]
    private bool $availability = true;

    public function getUtilisateur(): ?Utilisateur
    {
        return $this->utilisateur;
    }

    public function setUtilisateur(?Utilisateur $utilisateur): self
    {
        $this->utilisateur = $utilisateur;
        return $this;
    }

    public function getId(): ?int
    {
        return $this->id;
    }

    public function getCin(): ?string
    {
        return $this->cin;
    }

    public function setCin(string $cin): static
    {
        $this->cin = $cin;
        return $this;
    }

    public function getDescription(): ?string
    {
        return $this->description;
    }

    public function setDescription(string $description): static
    {
        $this->description = $description;
        return $this;
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

    public function getCompetences(): ?string
    {
        return $this->competences;
    }

    public function setCompetences(string $competences): static
    {
        $this->competences = $competences;
        return $this;
    }

    public function getCv(): ?string
    {
        return $this->cv;
    }

    public function setCv(string $cv): static
    {
        $this->cv = $cv;
        return $this;
    }

    public function getIdFreelancer(): ?Utilisateur
    {
        return $this->id_freelancer;
    }

    public function setIdFreelancer(?Utilisateur $id_freelancer): static
    {
        $this->id_freelancer = $id_freelancer;
        return $this;
    }

    public function getPrice(): ?float
    {
        return $this->price;
    }

    public function setPrice(?float $price): self
    {
        $this->price = $price;
        return $this;
    }

    public function getAvailability(): bool
    {
        return $this->availability;
    }

    public function setAvailability(bool $availability): self
    {
        $this->availability = $availability;
        return $this;
    }
}
