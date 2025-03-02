<?php

namespace App\Entity;
use App\Entity\Planification;
use Symfony\Component\HttpFoundation\File\File;
use App\Entity\RendezVous;
use App\Enum\enumRole;
use App\Repository\UtilisateurRepository;
use Doctrine\Common\Collections\ArrayCollection;
use Doctrine\Common\Collections\Collection;
use Doctrine\DBAL\Types\Types;
use Doctrine\ORM\Mapping as ORM;
use Symfony\Component\Security\Core\User\UserInterface;
use Symfony\Component\Security\Core\User\PasswordAuthenticatedUserInterface;
use Symfony\Component\Validator\Constraints as Assert; 

#[ORM\Entity(repositoryClass: UtilisateurRepository::class)]
class Utilisateur  implements UserInterface, PasswordAuthenticatedUserInterface
{
    #[ORM\Id]
    #[ORM\GeneratedValue]
    #[ORM\Column]
    private ?int $id = null;

    #[ORM\Column(length: 255)]
    #[Assert\NotBlank(message: "Le nom est nécessaire")] 
    #[Assert\Length(min:4,minMessage: "Veuillez avoir au moin 4 caractères")] 
    private ?string $nom = null;

    #[ORM\Column(length: 255)]
    #[Assert\NotBlank(message: "Le prenom est nécessaire")] 
    #[Assert\Length(min:4,minMessage: "Veuillez avoir au moin 4 caractères")] 
    private ?string $prenom = null;

    #[ORM\Column(length: 255)]
    #[Assert\NotBlank(message: "L'email est nécessaire")]
    #[Assert\Email(message: 'email {{value}} is not a valid email')] 
    private ?string $email = null;

    #[ORM\Column(length: 255)]
    #[Assert\NotBlank(message: "Le mot de passe est nécessaire")] 
    #[Assert\Length(min:8,minMessage:"Votre mot  de passe ne contient pas {{ limit }} caractères.")]
    private ?string $password = null;

    #[ORM\Column(type: Types::DATE_MUTABLE)]
    #[Assert\NotBlank(message: "La date de naissance est requise.")]
    #[Assert\Type(\DateTimeInterface::class, message: "Veuillez entrer une date valide.")]
    #[Assert\LessThan("today", message: "La date de naissance doit être dans le passé.")]
    private ?\DateTimeInterface $date_naissance;

    #[ORM\Column(type: "string", enumType: enumRole::class)]
    private enumRole $role;

    #[ORM\Column(length: 255)]
    #[Assert\NotBlank(message: "L'adresse est nécessaire")]
    #[Assert\NotNull(message: "L'adresse ne peut pas être nulle")]
    #[Assert\Length(min: 5, minMessage: "L'adresse doit contenir au moins {{ limit }} caractères")]
    private ?string $adresse = null;

    #[ORM\Column(length: 255)]
    #[Assert\NotBlank(message: "Le genre est nécessaire")]
    private ?string $genre = null;

    #[ORM\Column(length: 255)]
    private ?string $image = null;

    #[ORM\Column(length: 255)]
    #[Assert\NotBlank(message: "Le numero de telephone est nécessaire")]
    private ?string $numTel ;
    
    #[ORM\Column(type: 'boolean')]
    private $is_verified = false;


    #[ORM\Column(type: 'string',length:100,nullable: true)]
    private $resetToken;

    #[ORM\Column(type: 'boolean')]
    private $Tel_verified = false;

    #[ORM\Column(type: 'string',length:100,nullable: true)]
    private $codeVerif;

    #[ORM\Column(length: 255)]
    #[Assert\NotBlank(message: "L'image est nécessaire")]
    private ?string $image1 = null;
    /**
     * @var Collection<int, RendezVous>
     */
    #[ORM\OneToMany(mappedBy: 'id_medecin', targetEntity: RendezVous::class)]
    private Collection $rendezVouses;

    /**
     * @var Collection<int, RendezVous>
     */
    #[ORM\OneToMany(mappedBy: 'id_patient', targetEntity: RendezVous::class)]
    private Collection $rendez_vous;

    /**
     * @var Collection<int, Article>
     */
    #[ORM\OneToMany(mappedBy: 'id_medecin', targetEntity: Article::class)]
    private Collection $article;

    /**
     * @var Collection<int, Commentaire>
     */
    #[ORM\OneToMany(mappedBy: 'id_utilisateur', targetEntity: Commentaire::class)]
    private Collection $commentaire;


   

    #[ORM\OneToOne(targetEntity: Matching::class, mappedBy: 'utilisateur', cascade: ['persist', 'remove'])]
    private ?Matching $matching = null;

    private array $roles = [];

    /**
     * @var Collection<int, Planification>
     */
    #[ORM\OneToMany(targetEntity: Planification::class, mappedBy: 'id_freelancer')]
    private Collection $planifications;

    /**
     * @var Collection<int, ArticleRating>
     */
    #[ORM\OneToMany(mappedBy: 'user', targetEntity: ArticleRating::class)]
    private Collection $articleRatings;
    public function __construct()
    {
        $this->rendezVouses = new ArrayCollection();
        $this->rendez_vous = new ArrayCollection();
        $this->article = new ArrayCollection();
        $this->commentaire = new ArrayCollection();
        $this->articleRatings = new ArrayCollection();
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

    public function getPassword(): ?string
    {
        return $this->password;
    }

    public function setPassword(string $password): static
    {
        $this->password = $password;

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

    public function getRole(): ?enumRole
    {
        return $this->role;
    }

    public function setRole(enumRole $role): static
    {
        $this->role = $role;

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

    public function getGenre(): ?string
    {
        return $this->genre;
    }

    public function setGenre(string $genre): static
    {
        $this->genre = $genre;

        return $this;
    }
    public function getImage(): ?string
    {
        return $this->image;
    }

    public function setImage(string $image): static
    {
        $this->image = $image;

        return $this;
    }

    public function getNumTel(): ?string
    {
        return $this->numTel;
    }

    public function setNumTel(string $numTel): static
    {
        $this->numTel = $numTel;

        return $this;
    }

    public function getIsVerified(): ?bool
    {
        return $this->is_verified;
    }

    public function setIsVerified(bool $is_verified): self
    {
        $this->is_verified = $is_verified;

        return $this;
    }

    public function getTelVerified(): ?bool
    {
        return $this->Tel_verified;
    }

    public function setTelVerified(bool $Tel_verified): self
    {
        $this->Tel_verified = $Tel_verified;

        return $this;
    }

    public function getResetToken (): ?string
    {
        return $this->resetToken;
    }

    public function setResetToken (string $resetToken): self
    {
        $this->resetToken = $resetToken;

        return $this;
    }

    public function getCodeVerif (): ?string
    {
        return $this->codeVerif;
    }

    public function setCodeVerif (string $codeVerif): self
    {
        $this->codeVerif = $codeVerif;

        return $this;
    }

    public function getImage1(): ?string
    {
        return $this->image1;
    }

    public function setImage1(string $image1): static
    {
        $this->image1 = $image1;

        return $this;
    }

    /**
     * @return Collection<int, RendezVous>
     */
    public function getRendezVouses(): Collection
    {
        return $this->rendezVouses;
    }

    public function addRendezVouse(RendezVous $rendezVouse): static
    {
        if (!$this->rendezVouses->contains($rendezVouse)) {
            $this->rendezVouses->add($rendezVouse);
            $rendezVouse->setIdMedecin($this);
        }

        return $this;
    }

    public function removeRendezVouse(RendezVous $rendezVouse): static
    {
        if ($this->rendezVouses->removeElement($rendezVouse)) {
            // set the owning side to null (unless already changed)
            if ($rendezVouse->getIdMedecin() === $this) {
                $rendezVouse->setIdMedecin(null);
            }
        }

        return $this;
    }

    /**
     * @return Collection<int, RendezVous>
     */
    public function getRendezVous(): Collection
    {
        return $this->rendez_vous;
    }

    public function addRendezVou(RendezVous $rendezVou): static
    {
        if (!$this->rendez_vous->contains($rendezVou)) {
            $this->rendez_vous->add($rendezVou);
            $rendezVou->setIdPatient($this);
        }

        return $this;
    }

    public function removeRendezVou(RendezVous $rendezVou): static
    {
        if ($this->rendez_vous->removeElement($rendezVou)) {
            // set the owning side to null (unless already changed)
            if ($rendezVou->getIdPatient() === $this) {
                $rendezVou->setIdPatient(null);
            }
        }

        return $this;
    }

    /**
     * @return Collection<int, Article>
     */
    public function getArticle(): Collection
    {
        return $this->article;
    }



    /**
     * @return Collection<int, Commentaire>
     */
    public function getCommentaire(): Collection
    {
        return $this->commentaire;
    }

    public function addCommentaire(Commentaire $commentaire): static
    {
        if (!$this->commentaire->contains($commentaire)) {
            $this->commentaire->add($commentaire);
            $commentaire->setIdUtilisateur($this);
        }

        return $this;
    }

    public function removeCommentaire(Commentaire $commentaire): static
    {
        if ($this->commentaire->removeElement($commentaire)) {
            // set the owning side to null (unless already changed)
            if ($commentaire->getIdUtilisateur() === $this) {
                $commentaire->setIdUtilisateur(null);
            }
        }

        return $this;
    }

    
    public function eraseCredentials(): void
    {
        // Effacer les informations sensibles, si nécessaire
    }

    public function getUserIdentifier(): string
    {
        return $this->email;
    }

    public function getRoles(): array
    {
        return [strtoupper($this->role->value)];
    }
    
    public function getSalt(): ?string
    {
        return null; // Pas nécessaire si vous utilisez bcrypt ou sodium
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

    /**
     * @return Collection<int, ArticleRating>
     */
    public function getArticleRatings(): Collection
    {
        return $this->articleRatings;
    }

    public function addArticleRating(ArticleRating $articleRating): static
    {
        if (!$this->articleRatings->contains($articleRating)) {
            $this->articleRatings->add($articleRating);
            $articleRating->setUser($this);
        }

        return $this;
    }

    public function removeArticleRating(ArticleRating $articleRating): static
    {
        if ($this->articleRatings->removeElement($articleRating)) {
            // set the owning side to null (unless already changed)
            if ($articleRating->getUser() === $this) {
                $articleRating->setUser(null);
            }
        }

        return $this;
    }
    
    public function __toString(): string
    {
        return $this->nom . ' ' . $this->prenom; // Or any meaningful representation
    }
    

}
