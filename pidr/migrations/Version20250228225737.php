<?php

declare(strict_types=1);

namespace DoctrineMigrations;

use Doctrine\DBAL\Schema\Schema;
use Doctrine\Migrations\AbstractMigration;

/**
 * Auto-generated Migration: Please modify to your needs!
 */
final class Version20250228225737 extends AbstractMigration
{
    public function getDescription(): string
    {
        return '';
    }

    public function up(Schema $schema): void
    {
        // this up() migration is auto-generated, please modify it to your needs
        $this->addSql('CREATE TABLE message (id INT AUTO_INCREMENT NOT NULL, sender_id INT NOT NULL, receiver_id INT NOT NULL, content LONGTEXT NOT NULL, created_at DATETIME NOT NULL, INDEX IDX_B6BD307FF624B39D (sender_id), INDEX IDX_B6BD307FCD53EDB6 (receiver_id), PRIMARY KEY(id)) DEFAULT CHARACTER SET utf8mb4 COLLATE `utf8mb4_unicode_ci` ENGINE = InnoDB');
        $this->addSql('CREATE TABLE messenger_messages (id BIGINT AUTO_INCREMENT NOT NULL, body LONGTEXT NOT NULL, headers LONGTEXT NOT NULL, queue_name VARCHAR(190) NOT NULL, created_at DATETIME NOT NULL COMMENT \'(DC2Type:datetime_immutable)\', available_at DATETIME NOT NULL COMMENT \'(DC2Type:datetime_immutable)\', delivered_at DATETIME DEFAULT NULL COMMENT \'(DC2Type:datetime_immutable)\', INDEX IDX_75EA56E0FB7336F0 (queue_name), INDEX IDX_75EA56E0E3BD61CE (available_at), INDEX IDX_75EA56E016BA31DB (delivered_at), PRIMARY KEY(id)) DEFAULT CHARACTER SET utf8mb4 COLLATE `utf8mb4_unicode_ci` ENGINE = InnoDB');
        $this->addSql('ALTER TABLE message ADD CONSTRAINT FK_B6BD307FF624B39D FOREIGN KEY (sender_id) REFERENCES utilisateur (id)');
        $this->addSql('ALTER TABLE message ADD CONSTRAINT FK_B6BD307FCD53EDB6 FOREIGN KEY (receiver_id) REFERENCES utilisateur (id)');
        $this->addSql('ALTER TABLE article CHANGE titre titre VARCHAR(255) DEFAULT NULL, CHANGE datearticle datearticle DATE DEFAULT NULL, CHANGE urlimagearticle urlimagearticle VARCHAR(255) DEFAULT NULL');
        $this->addSql('ALTER TABLE commentaire CHANGE heure heure TIME DEFAULT NULL');
        $this->addSql('ALTER TABLE matching ADD price NUMERIC(10, 2) DEFAULT NULL, ADD availability TINYINT(1) NOT NULL, CHANGE cv cv VARCHAR(255) DEFAULT NULL');
        $this->addSql('ALTER TABLE planification ADD utilisateur_id INT DEFAULT NULL, CHANGE statut statut VARCHAR(255) DEFAULT \'en attente\' NOT NULL, CHANGE reponse reponse VARCHAR(255) DEFAULT NULL');
        $this->addSql('ALTER TABLE planification ADD CONSTRAINT FK_FFC02E1BFB88E14F FOREIGN KEY (utilisateur_id) REFERENCES utilisateur (id)');
        $this->addSql('CREATE INDEX IDX_FFC02E1BFB88E14F ON planification (utilisateur_id)');
        $this->addSql('ALTER TABLE utilisateur CHANGE reset_token reset_token VARCHAR(100) DEFAULT NULL');
    }

    public function down(Schema $schema): void
    {
        // this down() migration is auto-generated, please modify it to your needs
        $this->addSql('ALTER TABLE message DROP FOREIGN KEY FK_B6BD307FF624B39D');
        $this->addSql('ALTER TABLE message DROP FOREIGN KEY FK_B6BD307FCD53EDB6');
        $this->addSql('DROP TABLE message');
        $this->addSql('DROP TABLE messenger_messages');
        $this->addSql('ALTER TABLE article CHANGE titre titre VARCHAR(255) DEFAULT \'NULL\', CHANGE datearticle datearticle DATE DEFAULT \'NULL\', CHANGE urlimagearticle urlimagearticle VARCHAR(255) DEFAULT \'NULL\'');
        $this->addSql('ALTER TABLE commentaire CHANGE heure heure TIME DEFAULT \'NULL\'');
        $this->addSql('ALTER TABLE matching DROP price, DROP availability, CHANGE cv cv VARCHAR(255) DEFAULT \'NULL\'');
        $this->addSql('ALTER TABLE planification DROP FOREIGN KEY FK_FFC02E1BFB88E14F');
        $this->addSql('DROP INDEX IDX_FFC02E1BFB88E14F ON planification');
        $this->addSql('ALTER TABLE planification DROP utilisateur_id, CHANGE statut statut VARCHAR(255) DEFAULT \'\'\'en attente\'\'\' NOT NULL, CHANGE reponse reponse VARCHAR(255) DEFAULT \'NULL\'');
        $this->addSql('ALTER TABLE utilisateur CHANGE reset_token reset_token VARCHAR(100) DEFAULT \'NULL\'');
    }
}
