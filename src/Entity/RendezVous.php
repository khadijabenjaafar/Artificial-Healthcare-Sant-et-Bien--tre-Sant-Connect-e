<?php

namespace App\Entity;

use App\Repository\RendezVousRepository;
use Doctrine\DBAL\Types\Types;
use Doctrine\ORM\Mapping as ORM;
use App\Enum\RDVmode;
use App\Enum\RDVmotif;
use App\Enum\RDVstatut;
use ContainerN84oRMd\getRendezVousRepositoryService;

#[ORM\Entity(repositoryClass: RendezVousRepository::class)]
class RendezVous
{
    #[ORM\Id]
    #[ORM\GeneratedValue]
    #[ORM\Column]
    private ?int $id = null;

    #[ORM\Column(type: Types::DATETIME_MUTABLE)]
    private ?\DateTimeInterface $dateHeure = null;

    #[ORM\Column(type: "string", enumType: RDVmotif::class)]
    private RDVmotif $motif;

    #[ORM\Column(type: "string", enumType: RDVstatut::class)]
    private RDVstatut $statut;

    #[ORM\Column(type: "string", enumType: RDVmode::class)]
    private RDVmode $mode;

    #[ORM\Column(length: 255)]
    private ?string $commantaire = null;

    #[ORM\ManyToOne(inversedBy: 'rendezVouses')]
    #[ORM\JoinColumn(nullable: false)]
    private ?Utilisateur $id_medecin = null;

    #[ORM\ManyToOne(inversedBy: 'rendez_vous')]
    #[ORM\JoinColumn(nullable: false)]
    private ?Utilisateur $id_patient = null;

    public function getId(): ?int
    {
        return $this->id;
    }

    public function getDateHeure(): ?\DateTimeInterface
    {
        return $this->dateHeure;
    }

    public function setDateHeure(\DateTimeInterface $dateHeure)
    {
        $this->dateHeure = $dateHeure;
        return $this;
    }

    public function getMotif(): RDVmotif
    {
        return $this->motif;
    }

    public function setMotif(RDVmotif $motif)
    {
        $this->motif = $motif;
        return $this;
    }

    public function getStatut(): RDVstatut
    {
        return $this->statut;
    }

    public function setStatut(RDVstatut $statut)
    {
        $this->statut = $statut;
        return $this;
    }

    public function getMode(): RDVmode
    {
        return $this->mode;
    }

    public function setMode(RDVmode $mode)
    {
        $this->mode = $mode;
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

    public function getIdMedecin(): ?Utilisateur
    {
        return $this->id_medecin;
    }

    public function setIdMedecin(?Utilisateur $id_medecin): static
    {
        $this->id_medecin = $id_medecin;
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
}
