<?php

namespace App\Entity;

use App\Repository\PlanificationRepository;
use Doctrine\DBAL\Types\Types;
use Doctrine\ORM\Mapping as ORM;
use Symfony\Component\Validator\Constraints as Assert;

#[ORM\Entity(repositoryClass: PlanificationRepository::class)]
class Planification
{
    #[ORM\Id]
    #[ORM\GeneratedValue]
    #[ORM\Column]
    private ?int $id = null;

    #[ORM\Column(length: 255, options: ["default" => "en attente"])]
    #[Assert\NotBlank(message: "Le statut ne peut pas être vide.")]
    private ?string $statut = "en attente";

    #[ORM\Column(type: 'date')]
    #[Assert\NotBlank(message: "La date ne peut pas être vide.")]
    #[Assert\GreaterThan("today", message: "La date doit être dans le futur.")]
    private ?\DateTimeInterface $date = null;

    #[ORM\Column(length: 255)]
    #[Assert\NotBlank(message: "L'adresse ne peut pas être vide.")]
    #[Assert\Length(
        max: 255,
        maxMessage: "L'adresse ne peut pas dépasser {{ limit }} caractères."
    )]
    private ?string $adresse = null;

    #[ORM\Column(length: 255, nullable: true)]
    private ?string $reponse = null;

    #[ORM\Column(length: 255)]
    #[Assert\NotBlank(message: "Le mode ne peut pas être vide.")]
    #[Assert\Length(
        max: 255,
        maxMessage: "Le mode ne peut pas dépasser {{ limit }} caractères."
    )]
    private ?string $mode = null;

    #[ORM\ManyToOne(targetEntity: Utilisateur::class, inversedBy: 'planifications')]
    #[ORM\JoinColumn(nullable: false)]
    #[Assert\NotNull(message: "Le freelancer ne peut pas être nul.")]
    private ?Utilisateur $freelancer = null;

    #[ORM\ManyToOne(targetEntity: Utilisateur::class,inversedBy: 'planifications')]
    #[ORM\JoinColumn(nullable: true)]
    #[Assert\NotNull(message: "L'utilisateur ne peut pas être nul.")]
    private ?Utilisateur $utilisateur = null;
    
    public function getId(): ?int
    {
        return $this->id;
    }

    public function getStatut(): ?string
    {
        return $this->statut;
    }

    public function setStatut(string $statut): static
    {
        $this->statut = $statut;

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

    public function getAdresse(): ?string
    {
        return $this->adresse;
    }

    public function setAdresse(string $adresse): static
    {
        $this->adresse = $adresse;

        return $this;
    }

    public function getReponse(): ?string
    {
        return $this->reponse;
    }

    public function setReponse(string $reponse): static
    {
        $this->reponse = $reponse;

        return $this;
    }

    public function getMode(): ?string
    {
        return $this->mode;
    }

    public function setMode(string $mode): static
    {
        $this->mode = $mode;

        return $this;
    }

    public function getFreelancer(): ?Utilisateur
    {
        return $this->freelancer;
    }

    public function setFreelancer(?Utilisateur $freelancer): static
    {
        $this->freelancer = $freelancer;

        return $this;
    }

    public function getUtilisateur(): ?Utilisateur
    {
        return $this->utilisateur;
    }
    
    public function setUtilisateur(?Utilisateur $utilisateur): static
    {
        $this->utilisateur = $utilisateur;
        return $this;
    }
}
