<?php

namespace App\Entity;

use App\Repository\CommentaireRepository;
use Doctrine\DBAL\Types\Types;
use Doctrine\ORM\Mapping as ORM;
use App\Repository\ArticleRepository;
use DateTime;
use Doctrine\Common\Collections\ArrayCollection;
use Doctrine\Common\Collections\Collection;



#[ORM\Entity(repositoryClass: CommentaireRepository::class)]
class Commentaire
{
    #[ORM\Id]
    #[ORM\GeneratedValue]
    #[ORM\Column]
    private ?int $id_commentaire = null;

    #[ORM\Column(length: 250)]
    private ?string $contenue = null;


    #[ORM\Column(type: Types::DATE_MUTABLE)]
    private ?\DateTimeInterface $date_commentaire = null;




    #[ORM\ManyToOne(targetEntity: Utilisateur::class)]
    #[ORM\JoinColumn(name: "id_utilisateur", referencedColumnName: "id", onDelete: "CASCADE")]
    private ?Utilisateur $utilisateur = null;

    

    #[ORM\ManyToOne(targetEntity: Article::class)]
    #[ORM\JoinColumn(name: "article_id", referencedColumnName: "id", onDelete: "CASCADE")]
    private ?Article $article = null;

    #[ORM\Column]
    private ?bool $status = false;



    #[ORM\Column(type: "time", nullable: true)]
    private ?\DateTimeInterface $heure = null;

    #[ORM\ManyToOne(targetEntity: self::class, inversedBy:"responses")]
    #[ORM\JoinColumn(name:"parent_id", referencedColumnName:"id_commentaire", onDelete: "CASCADE", nullable: true)]
    private ?Commentaire $parent = null;



    #[ORM\Column(type: 'integer', nullable: true)]
    private ?int $rating = null;




    #[ORM\OneToMany(mappedBy:"parent",targetEntity: self::class,cascade:["remove"])]
    private Collection $reponses;


   // #[ORM\Column(type: 'integer', options: ['default' => 0])]
   // private int $signalements = 0;


    #[ORM\OneToMany(mappedBy: "commentaire", targetEntity: CommentaireSignalement::class, cascade: ["remove"])]
    private Collection $signalements;


    #[ORM\Column(type: "integer")]
    private int $likes = 0;

    #[ORM\Column(type: "integer")]
    private int $dislikes = 0;



     #[ORM\OneToMany(mappedBy: "commentaire", targetEntity: CommentaireVote::class, cascade: ["remove"])]
    private Collection $votes;


    
    public function __construct()
    {
        $this->reponses=new ArrayCollection();
        $this->signalements = new ArrayCollection();
        $this->votes = new ArrayCollection();
    }


    public function getSignalements(): Collection
    {
        return $this->signalements;
    }

    public function getNombreSignalements(): int
    {
        return $this->signalements->count();
    }
   

    public function getIdCommentaire(): ?int
    {
        return $this->id_commentaire;
    }

   
    public function getContenue(): ?string
    {
        return $this->contenue;
    }

    public function setContenue(string $contenue): static
    {
        $this->contenue = $contenue;

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

    public function getArticle(): ?Article
    {
        return $this->article;
    }

    public function setArticle(?Article $article): static
    {
        $this->article = $article;
        return $this;
    }
   

    public function getDateCommentaire(): ?\DateTimeInterface
    {
        return $this->date_commentaire;
    }

    public function setDateCommentaire(\DateTimeInterface $date_commentaire): static
    {
        $this->date_commentaire = $date_commentaire;

        return $this;
    }

    public function isStatus(): ?bool
    {
        return $this->status;
    }

    public function setStatus(bool $status): static
    {
        $this->status = $status;

        return $this;
    }

    public function getHeure(): ?\DateTimeInterface
    {
    return $this->heure;
    }

    public function setHeure(\DateTimeInterface $heure): static
    {
        $this->heure = $heure;
        return $this;
    }

    public function getParent(): ?self
    {
        return $this->parent;
    }


    public function setParent(?self $parent): self
    {
        $this->parent = $parent;
        return $this;
    }





    public function getRating(): ?int
    {
        return $this->rating;
    }

    public function setRating(?int $rating): self
    {
        $this->rating = $rating;
        return $this;
    }



    public function getReponses(): Collection
{
    return $this->reponses ?? new ArrayCollection(); // Ajout d'une sécurité pour éviter les erreurs
}






// Getter et Setter pour les likes
//public function getLikes(): int
//{
 //   return $this->likes;
//}

public function addLike(): self
{
    $this->likes++;
    return $this;
}

public function removeLike(): self
{
    if ($this->likes > 0) {
        $this->likes--;
    }
    return $this;
}

// Getter et Setter pour les dislikes
//public function getDislikes(): int
//{
//    return $this->dislikes;
//}

public function addDislike(): self
{
    $this->dislikes++;
    return $this;
}

public function removeDislike(): self
{
    if ($this->dislikes > 0) {
        $this->dislikes--;
    }
    return $this;
}



public function getLikes(): int
{
    return $this->votes->filter(fn(CommentaireVote $vote) => $vote->getVoteType() === "like")->count();
}

// Récupérer le nombre de dislikes
public function getDislikes(): int
{
    return $this->votes->filter(fn(CommentaireVote $vote) => $vote->getVoteType() === "dislike")->count();
}


public function isAnonyme(): bool
{
    return $this->status ?? false;
}

}
