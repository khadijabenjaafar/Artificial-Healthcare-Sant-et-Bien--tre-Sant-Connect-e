<?php

namespace App\Entity;
use Symfony\Component\HttpFoundation\File\File;
use App\Entity\RendezVous;
use App\Enum\enumRole;
use App\Repository\RendezVousRepository;
use App\Repository\UtilisateurRepository;
use Doctrine\Common\Collections\ArrayCollection;
use Doctrine\Common\Collections\Collection;
use Doctrine\DBAL\Types\Types;
use Doctrine\ORM\Mapping as ORM;
use Symfony\Component\Validator\Constraints as Assert; 

#[ORM\Entity(repositoryClass: UtilisateurRepository::class)]
class Utilisateur
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
    private ?string $prenom = null;

    #[ORM\Column(length: 255)]
    #[Assert\NotBlank(message: "L'email est nécessaire")]
    #[Assert\Email(message: 'email {{value}} is not a valid email')] 
    private ?string $email = null;

    #[ORM\Column(length: 255)]
    #[Assert\NotBlank(message: "Le mot de passe est nécessaire")] 
    #[Assert\Length(min:10,minMessage:"Votre mot  de passe ne contient pas {{ limit }} caractères.")]
    private ?string $mot_de_passe = null;

    #[ORM\Column(type: Types::DATE_MUTABLE)]
    #[Assert\NotBlank(message: "La date de naissance est requise.")]
    #[Assert\Type(\DateTimeInterface::class, message: "Veuillez entrer une date valide.")]
    private ?\DateTimeInterface $date_naissance;

    #[ORM\Column(type: "string", enumType: enumRole::class)]
    private enumRole $role;

    #[ORM\Column(length: 255)]
    #[Assert\NotBlank(message: "L'adresse est nécessaire")] 
    private ?string $adresse = null;

    #[ORM\Column(length: 255)]
    private ?string $genre = null;

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

    /**
     * @var Collection<int, Matching>
     */
    #[ORM\OneToMany(mappedBy: 'id_freelancer', targetEntity: Matching::class, orphanRemoval: true)]
    private Collection $matchings;

    #[ORM\Column(length: 255)]
    private ?string $image = null;

   

    public function __construct()
    {
        $this->rendezVouses = new ArrayCollection();
        $this->rendez_vous = new ArrayCollection();
        $this->article = new ArrayCollection();
        $this->commentaire = new ArrayCollection();
        $this->matchings = new ArrayCollection();
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

    public function addArticle(Article $article): static
    {
        if (!$this->article->contains($article)) {
            $this->article->add($article);
            $article->setIdMedecin($this);
        }

        return $this;
    }

    public function removeArticle(Article $article): static
    {
        if ($this->article->removeElement($article)) {
            // set the owning side to null (unless already changed)
            if ($article->getIdMedecin() === $this) {
                $article->setIdMedecin(null);
            }
        }

        return $this;
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

    /**
     * @return Collection<int, Matching>
     */
    public function getMatchings(): Collection
    {
        return $this->matchings;
    }

    public function addMatching(Matching $matching): static
    {
        if (!$this->matchings->contains($matching)) {
            $this->matchings->add($matching);
            $matching->setIdFreelancer($this);
        }

        return $this;
    }

    public function removeMatching(Matching $matching): static
    {
        if ($this->matchings->removeElement($matching)) {
            // set the owning side to null (unless already changed)
            if ($matching->getIdFreelancer() === $this) {
                $matching->setIdFreelancer(null);
            }
        }

        return $this;
    }


}
