<?php

namespace App\Entity;

use App\Repository\FacturationRepository;
use Doctrine\DBAL\Types\Types;
use Doctrine\ORM\Mapping as ORM;
use App\Enum\Methode_facturation;
use App\Enum\Statut_Facturation;

#[ORM\Entity(repositoryClass: FacturationRepository::class)]
class Facturation
{
    #[ORM\Id]
    #[ORM\GeneratedValue]
    #[ORM\Column]
    private ?int $id = null;

    #[ORM\Column(type: Types::DATE_MUTABLE)]
    private ?\DateTimeInterface $date = null;

    #[ORM\Column(type: Types::DECIMAL, precision: 10, scale: 0)]
    private ?string $montant = null;

    #[ORM\Column(type: "string", enumType: Methode_facturation::class)]
    private Methode_facturation $methodePaiement;

    #[ORM\Column(type: "string", enumType: Statut_Facturation::class)]
    private Statut_Facturation $statut;

    #[ORM\OneToOne(cascade: ['persist', 'remove'])]
    #[ORM\JoinColumn(nullable: false)]
    private ?ordonnance $id_ordonnance = null;

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

    public function getMontant(): ?string
    {
        return $this->montant;
    }

    public function setMontant(string $montant): static
    {
        $this->montant = $montant;

        return $this;
    }

    public function getMethodePaiement(): Methode_facturation
    {
        return $this->methodePaiement;
    }

    public function setMethodePaiement(Methode_facturation $methodePaiement): static
    {
        $this->methodePaiement = $methodePaiement;

        return $this;
    }

    public function getStatut(): Statut_Facturation
    {
        return $this->statut;
    }

    public function setStatut(Statut_Facturation $statut): static
    {
        $this->statut = $statut;

        return $this;
    }

    public function getIdOrdonnance(): ?ordonnance
    {
        return $this->id_ordonnance;
    }

    public function setIdOrdonnance(ordonnance $id_ordonnance): static
    {
        $this->id_ordonnance = $id_ordonnance;

        return $this;
    }
}
