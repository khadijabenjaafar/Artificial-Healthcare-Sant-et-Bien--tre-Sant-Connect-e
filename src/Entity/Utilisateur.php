<?php

namespace App\Entity;

use App\Repository\UtilisateurRepository;
use Doctrine\Common\Collections\ArrayCollection;
use Doctrine\Common\Collections\Collection;
use Doctrine\DBAL\Types\Types;
use Doctrine\ORM\Mapping as ORM;
use App\Entity\Planification;



#[ORM\Entity(repositoryClass: UtilisateurRepository::class)]
class Utilisateur
{
    #[ORM\Id]
    #[ORM\GeneratedValue]
    #[ORM\Column]
    private ?int $id = null;

    #[ORM\Column(length: 255)]
    private ?string $nom = null;

    #[ORM\Column(length: 255)]
    private ?string $prenom = null;

    #[ORM\Column(length: 255)]
    private ?string $email = null;

    #[ORM\Column(length: 255)]
    private ?string $mot_de_passe = null;

    #[ORM\Column(type: Types::DATE_MUTABLE)]
    private ?\DateTimeInterface $date_naissance = null;

    #[ORM\Column(length: 255)]
    private ?string $adresse = null;

    #[ORM\Column(length: 255)]
    private ?string $role = null;

    #[ORM\Column(length: 255)]
    private ?string $genre = null;

    #[ORM\OneToOne(targetEntity: Matching::class, mappedBy: 'utilisateur', cascade: ['persist', 'remove'])]
    #[ORM\OneToMany(mappedBy: 'utilisateur', targetEntity: Matching::class)]
    
    
private ?Matching $matching = null;

    /**
     * @var Collection<int, Planification>
     */
    #[ORM\OneToMany(targetEntity: Planification::class, mappedBy: 'id_freelancer')]
    private Collection $planifications;

    #[ORM\OneToOne(mappedBy: 'user', cascade: ['persist', 'remove'])]
    

    public function __construct()
    {
        $this->planifications = new ArrayCollection();
    }

    public function getId(): ?int
    {
        return $this->id;
    }

    public function getNom(): ?string
    {
        return $this->nom;
    }

    public function setNom(string $nom): static
    {
        $this->nom = $nom;

        return $this;
    }

    public function getPrenom(): ?string
    {
        return $this->prenom;
    }

    public function setPrenom(string $prenom): static
    {
        $this->prenom = $prenom;

        return $this;
    }

    public function getEmail(): ?string
    {
        return $this->email;
    }

    public function setEmail(string $email): static
    {
        $this->email = $email;

        return $this;
    }

    public function getMotDePasse(): ?string
    {
        return $this->mot_de_passe;
    }

    public function setMotDePasse(string $mot_de_passe): static
    {
        $this->mot_de_passe = $mot_de_passe;

        return $this;
    }

    public function getDateNaissance(): ?\DateTimeInterface
    {
        return $this->date_naissance;
    }

    public function setDateNaissance(\DateTimeInterface $date_naissance): static
    {
        $this->date_naissance = $date_naissance;

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

    public function getRole(): ?string
    {
        return $this->role;
    }

    public function setRole(string $role): static
    {
        $this->role = $role;

        return $this;
    }

    public function getGenre(): ?string
    {
        return $this->genre;
    }

    public function setGenre(string $genre): static
    {
        $this->genre = $genre;

        return $this;
    }

    


   


public function getMatching(): ?Matching
{
    return $this->matching;
}

public function setMatching(?Matching $matching): static
{
    // Ensure bidirectional relationship is set correctly
    if ($matching && $matching->getUtilisateur() !== $this) {
        $matching->setUtilisateur($this);
    }

    $this->matching = $matching;
    return $this;
}
/**
 * @return Collection<int, Planification>
 */
public function getPlanifications(): Collection
{
    return $this->planifications;
}

public function addPlanification(Planification $planification): static
{
    if (!$this->planifications->contains($planification)) {
        $this->planifications->add($planification);
        $planification->setFreelancer($this);
    }
    return $this;
}

public function removePlanification(Planification $planification): static
{
    if ($this->planifications->removeElement($planification)) {
        if ($planification->getFreelancer() === $this) {
            $planification->setFreelancer(null);
        }
    }
    return $this;
}




}
