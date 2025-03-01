<?php

namespace App\Entity;

use App\Enum\MethodePaiement;

use App\Repository\FacturationRepository;
use Doctrine\DBAL\Types\Types;
use Doctrine\ORM\Mapping as ORM;
use Symfony\Component\Validator\Constraints as Assert;

#[ORM\Entity(repositoryClass: FacturationRepository::class)]
class Facturation
{
    #[ORM\Id]
    #[ORM\GeneratedValue]
    #[ORM\Column]
    private ?int $id = null;
    #[ORM\OneToOne(cascade: ['persist', 'remove'],orphanRemoval: true)]
    #[ORM\JoinColumn(name: 'id_ordonnance_id_id', referencedColumnName: 'id', onDelete: 'CASCADE')]
    private ?Ordonnance $id_ordonnance_id = null;

    #[ORM\Column(type: Types::DATE_MUTABLE)]
    #[Assert\NotBlank(message: "La date est obligatoire.")]
    #[Assert\Type("\DateTimeInterface", message: "Format de date invalide.")]
    private ?\DateTimeInterface $date = null;

    #[ORM\Column(type: Types::DECIMAL, precision: 10, scale: 0)]
    #[Assert\NotBlank(message: "Le montant est obligatoire.")]
    #[Assert\Positive(message: "Le montant doit être un nombre positif.")]
    private ?string $montant = null;

    #[ORM\Column(enumType: MethodePaiement::class)]
    #[Assert\NotBlank(message: "La méthode de paiement est obligatoire.")]
    private ?MethodePaiement $methode_paiement = null;

    #[ORM\Column(type: "string",length:255)]
    #[Assert\NotBlank(message: "Le statut est obligatoire.")]
    #[Assert\Choice(choices: ["Payée", "Non Payée"], message: "Le statut doit être 'Payée' ou 'Non Payée'.")]
    private ?string $statut=null;

    public function getId(): ?int
    {
        return $this->id;
    }

    public function getIdOrdonnanceId(): ?Ordonnance
    {
        return $this->id_ordonnance_id;
    }

    public function setIdOrdonnanceId(Ordonnance $id_ordonnance_id): static
    {
        $this->id_ordonnance_id = $id_ordonnance_id;

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

    public function getMontant(): ?string
    {
        return $this->montant;
    }

    public function setMontant(string $montant): static
    {
        $this->montant = $montant;

        return $this;
    }

    public function getMethodePaiement(): ?MethodePaiement
    {
        return $this->methode_paiement;
    }

    public function setMethodePaiement(MethodePaiement $methode_paiement): static
    {
        $this->methode_paiement = $methode_paiement;

        return $this;
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
}
