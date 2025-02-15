<?php

namespace App\Entity;

use App\Repository\OrdonnanceRepository;
use Doctrine\DBAL\Types\Types;
use Doctrine\ORM\Mapping as ORM;

#[ORM\Entity(repositoryClass: OrdonnanceRepository::class)]
class Ordonnance
{
    #[ORM\Id]
    #[ORM\GeneratedValue]
    #[ORM\Column]
    private ?int $id = null;

    #[ORM\Column(type: Types::DATE_MUTABLE)]
    private ?\DateTimeInterface $date = null;

    #[ORM\Column(length: 255)]
    private ?string $medicaments = null;

    #[ORM\Column(length: 255)]
    private ?string $commantaire = null;

    #[ORM\Column(length: 255)]
    private ?string $duree_utilisation = null;

    #[ORM\Column(length: 255)]
    private ?string $quantite_utilisation = null;

    #[ORM\OneToOne(cascade: ['persist', 'remove'])]
    private ?consultation $id_consultation = null;

    public function getId(): ?int
    {
        return $this->id;
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

    public function getIdConsultation(): ?consultation
    {
        return $this->id_consultation;
    }

    public function setIdConsultation(?consultation $id_consultation): static
    {
        $this->id_consultation = $id_consultation;

        return $this;
    }
}
