<?php

namespace App\Entity;

use App\Repository\ArticleRepository;
use Doctrine\DBAL\Types\Types;
use Doctrine\ORM\Mapping as ORM;
use Symfony\Component\Validator\Constraints as Assert;
use Doctrine\Common\Collections\ArrayCollection;
use Doctrine\Common\Collections\Collection;


#[ORM\Entity(repositoryClass: ArticleRepository::class)]
class Article
{
    #[ORM\Id]
    #[ORM\GeneratedValue]
    #[ORM\Column]
    private ?int $id= null;

    #[ORM\Column(length: 255, nullable: true)]
    private ?string $titre = "";

    #[ORM\Column(type: Types::TEXT,
    nullable:true)]
    #[Assert\NotBlank(message: "Le contenu de l'article est obligatoire.")]
    #[Assert\Length(
        min: 20,
        minMessage: "Le contenu doit contenir au moins {{ limit }} caractères."
    )]
    private ?string $contenue = null;

    
    //#[ORM\Column(type: Types::DATE_MUTABLE, options: ["default" => "CURRENT_TIMESTAMP"])]
    #[ORM\Column(type: Types::DATE_MUTABLE, nullable: true)]
    private ?\DateTimeInterface $datearticle = null;

    #[ORM\Column(length: 255, nullable: true)]
    private ?string $urlimagearticle = " ";

    #[ORM\ManyToOne(targetEntity: Utilisateur::class)]
    #[ORM\JoinColumn(name: "id_utilisateur", referencedColumnName: "id", onDelete: "CASCADE")]
    private ?Utilisateur $utilisateur = null;

    #[ORM\Column]
    private ?int $nbreVue = 0;


    #[ORM\OneToMany(mappedBy: 'article', targetEntity: ArticleRating::class)]
    private Collection $ratings;
    
    public function __construct()
    {
    $this->datearticle = new \DateTime(); // ✅ Définit la date actuelle lors de la création de l'entité
    }



    public function getId(): ?int
    {
        return $this->id;
    }
   

    public function getTitre(): ?string
    {
        return $this->titre;
    }

    public function setTitre(string $titre): static
    {
        $this->titre = $titre;

        return $this;
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

    

   

   

    
    public function getDateArticle(): ?\DateTimeInterface
    {
        return $this->datearticle;
    }

    public function setDateArticle(\DateTimeInterface $datearticle): static
    {
        if ($this->datearticle === null) { // ✅ Ne modifie la date que si elle est nulle
            $this->datearticle = $datearticle;
        }
        
        return $this;
    }

    public function getUrlImageArticle(): ?string
    {
        return $this->urlimagearticle ?? "";
    }

    public function setUrlImageArticle(string $urlimagearticle): static
    {
        $this->urlimagearticle = $urlimagearticle;

        return $this;
    }

    public function getUtilisateur(): ?Utilisateur
    {
        return $this->utilisateur;
    }

    public function setUtilisateur(Utilisateur $utilisateur): static
{
    $this->utilisateur = $utilisateur;

    return $this;
}

   
    public function getNbreVue(): ?int
    {
        return $this->nbreVue;
    }

    public function setNbreVue(int $nbreVue): static
    {
        $this->nbreVue = $nbreVue;

        return $this;
    }

    public function incrementNbreVue(): self
    {
        $this->nbreVue++;
        return $this;
    }



    public function getRatings(): Collection
    {
        return $this->ratings;
    }











    // Calculer la moyenne des votes
public function getAverageRating(): float
{
    $ratings = $this->ratings->map(fn(ArticleRating $rating) => $rating->getRating())->toArray();
    if (count($ratings) === 0) {
        return 0;
    }
    return round(array_sum($ratings) / count($ratings), 1);
}

// Obtenir la distribution des votes (1 à 10)
public function getRatingDistribution(): array
{
    $distribution = array_fill(1, 10, 0);
    foreach ($this->ratings as $rating) {
        $distribution[$rating->getRating()]++;
    }
    return $distribution;
}







}
