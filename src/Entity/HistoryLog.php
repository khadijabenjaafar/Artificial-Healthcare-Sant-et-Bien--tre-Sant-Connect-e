<?php
namespace App\Entity;

use Doctrine\ORM\Mapping as ORM;

#[ORM\Entity]
class HistoryLog
{
    #[ORM\Id, ORM\GeneratedValue, ORM\Column(type: 'integer')]
    private ?int $id = null;

    #[ORM\Column(type: 'string', length: 255)]
    private string $action;

    #[ORM\Column(type: 'string', length: 255)]
    private string $entityType;

    #[ORM\Column(type: 'datetime')]
    private \DateTime $timestamp;

    public function getId(): ?int { return $this->id; }

    public function getAction(): string { return $this->action; }
    public function setAction(string $action): self { $this->action = $action; return $this; }

    public function getEntityType(): string { return $this->entityType; }
    public function setEntityType(string $entityType): self { $this->entityType = $entityType; return $this; }

    public function getTimestamp(): \DateTime { return $this->timestamp; }
    public function setTimestamp(\DateTime $timestamp): self { $this->timestamp = $timestamp; return $this; }
}
