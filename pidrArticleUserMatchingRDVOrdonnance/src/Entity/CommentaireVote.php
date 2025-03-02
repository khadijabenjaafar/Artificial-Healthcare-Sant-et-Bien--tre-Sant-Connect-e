<?php

namespace App\Entity;

use App\Repository\CommentaireVoteRepository;
use Doctrine\ORM\Mapping as ORM;

#[ORM\Entity(repositoryClass: CommentaireVoteRepository::class)]
class CommentaireVote
{
    #[ORM\Id]
    #[ORM\GeneratedValue]
    #[ORM\Column]
    private ?int $id = null;

    #[ORM\ManyToOne(targetEntity: Commentaire::class, inversedBy: 'signalements')]
    #[ORM\JoinColumn(name: "commentaire_id", referencedColumnName: "id_commentaire", nullable: false, onDelete: "CASCADE")] 
    private ?Commentaire $commentaire = null;

    #[ORM\ManyToOne(targetEntity: Utilisateur::class)]
    #[ORM\JoinColumn(name: "id_utilisateur", referencedColumnName: "id", onDelete: "CASCADE")]
    private ?Utilisateur $utilisateur = null;

    #[ORM\Column(length: 255)]
    private ?string $voteType = null;


    public function getId(): ?int
    {
        return $this->id;
    }

    public function getCommentaire(): ?Commentaire
    {
        return $this->commentaire;
    }

    public function setCommentaire(?Commentaire $commentaire): static
    {
        $this->commentaire = $commentaire;

        return $this;
    }

    public function getVoteType(): ?string
    {
        return $this->voteType;
    }

    public function setVoteType(string $voteType): static
    {
        $this->voteType = $voteType;

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
