<?php

declare(strict_types=1);

namespace DoctrineMigrations;

use Doctrine\DBAL\Schema\Schema;
use Doctrine\Migrations\AbstractMigration;

/**
 * Auto-generated Migration: Please modify to your needs!
 */
final class Version20250301131044 extends AbstractMigration
{
    public function getDescription(): string
    {
        return '';
    }

    public function up(Schema $schema): void
    {
        // this up() migration is auto-generated, please modify it to your needs
        $this->addSql('CREATE TABLE article_rating (id INT AUTO_INCREMENT NOT NULL, article_id INT NOT NULL, user_id INT DEFAULT NULL, rating INT NOT NULL, INDEX IDX_B24EB1A27294869C (article_id), INDEX IDX_B24EB1A2A76ED395 (user_id), PRIMARY KEY(id)) DEFAULT CHARACTER SET utf8mb4 COLLATE `utf8mb4_unicode_ci` ENGINE = InnoDB');
        $this->addSql('CREATE TABLE commentaire_signalement (id INT AUTO_INCREMENT NOT NULL, commentaire_id INT NOT NULL, id_utilisateur INT DEFAULT NULL, INDEX IDX_19B5E006BA9CD190 (commentaire_id), INDEX IDX_19B5E00650EAE44 (id_utilisateur), PRIMARY KEY(id)) DEFAULT CHARACTER SET utf8mb4 COLLATE `utf8mb4_unicode_ci` ENGINE = InnoDB');
        $this->addSql('CREATE TABLE commentaire_vote (id INT AUTO_INCREMENT NOT NULL, commentaire_id INT NOT NULL, id_utilisateur INT DEFAULT NULL, vote_type VARCHAR(255) NOT NULL, INDEX IDX_B3E84136BA9CD190 (commentaire_id), INDEX IDX_B3E8413650EAE44 (id_utilisateur), PRIMARY KEY(id)) DEFAULT CHARACTER SET utf8mb4 COLLATE `utf8mb4_unicode_ci` ENGINE = InnoDB');
        $this->addSql('CREATE TABLE messenger_messages (id BIGINT AUTO_INCREMENT NOT NULL, body LONGTEXT NOT NULL, headers LONGTEXT NOT NULL, queue_name VARCHAR(190) NOT NULL, created_at DATETIME NOT NULL COMMENT \'(DC2Type:datetime_immutable)\', available_at DATETIME NOT NULL COMMENT \'(DC2Type:datetime_immutable)\', delivered_at DATETIME DEFAULT NULL COMMENT \'(DC2Type:datetime_immutable)\', INDEX IDX_75EA56E0FB7336F0 (queue_name), INDEX IDX_75EA56E0E3BD61CE (available_at), INDEX IDX_75EA56E016BA31DB (delivered_at), PRIMARY KEY(id)) DEFAULT CHARACTER SET utf8mb4 COLLATE `utf8mb4_unicode_ci` ENGINE = InnoDB');
        $this->addSql('ALTER TABLE article_rating ADD CONSTRAINT FK_B24EB1A27294869C FOREIGN KEY (article_id) REFERENCES article (id)');
        $this->addSql('ALTER TABLE article_rating ADD CONSTRAINT FK_B24EB1A2A76ED395 FOREIGN KEY (user_id) REFERENCES utilisateur (id)');
        $this->addSql('ALTER TABLE commentaire_signalement ADD CONSTRAINT FK_19B5E006BA9CD190 FOREIGN KEY (commentaire_id) REFERENCES commentaire (id_commentaire) ON DELETE CASCADE');
        $this->addSql('ALTER TABLE commentaire_signalement ADD CONSTRAINT FK_19B5E00650EAE44 FOREIGN KEY (id_utilisateur) REFERENCES utilisateur (id) ON DELETE CASCADE');
        $this->addSql('ALTER TABLE commentaire_vote ADD CONSTRAINT FK_B3E84136BA9CD190 FOREIGN KEY (commentaire_id) REFERENCES commentaire (id_commentaire) ON DELETE CASCADE');
        $this->addSql('ALTER TABLE commentaire_vote ADD CONSTRAINT FK_B3E8413650EAE44 FOREIGN KEY (id_utilisateur) REFERENCES utilisateur (id) ON DELETE CASCADE');
        $this->addSql('ALTER TABLE article CHANGE titre titre VARCHAR(255) DEFAULT NULL, CHANGE datearticle datearticle DATE DEFAULT NULL, CHANGE urlimagearticle urlimagearticle VARCHAR(255) DEFAULT NULL');
        $this->addSql('ALTER TABLE commentaire ADD parent_id INT DEFAULT NULL, ADD rating INT DEFAULT NULL, ADD likes INT NOT NULL, ADD dislikes INT NOT NULL, CHANGE heure heure TIME DEFAULT NULL');
        $this->addSql('ALTER TABLE commentaire ADD CONSTRAINT FK_67F068BC727ACA70 FOREIGN KEY (parent_id) REFERENCES commentaire (id_commentaire) ON DELETE CASCADE');
        $this->addSql('CREATE INDEX IDX_67F068BC727ACA70 ON commentaire (parent_id)');
        $this->addSql('ALTER TABLE matching CHANGE cv cv VARCHAR(255) DEFAULT NULL, CHANGE price price NUMERIC(10, 2) DEFAULT NULL');
        $this->addSql('ALTER TABLE planification CHANGE statut statut VARCHAR(255) DEFAULT \'en attente\' NOT NULL, CHANGE reponse reponse VARCHAR(255) DEFAULT NULL');
        $this->addSql('ALTER TABLE utilisateur CHANGE reset_token reset_token VARCHAR(100) DEFAULT NULL');
    }

    public function down(Schema $schema): void
    {
        // this down() migration is auto-generated, please modify it to your needs
        $this->addSql('ALTER TABLE article_rating DROP FOREIGN KEY FK_B24EB1A27294869C');
        $this->addSql('ALTER TABLE article_rating DROP FOREIGN KEY FK_B24EB1A2A76ED395');
        $this->addSql('ALTER TABLE commentaire_signalement DROP FOREIGN KEY FK_19B5E006BA9CD190');
        $this->addSql('ALTER TABLE commentaire_signalement DROP FOREIGN KEY FK_19B5E00650EAE44');
        $this->addSql('ALTER TABLE commentaire_vote DROP FOREIGN KEY FK_B3E84136BA9CD190');
        $this->addSql('ALTER TABLE commentaire_vote DROP FOREIGN KEY FK_B3E8413650EAE44');
        $this->addSql('DROP TABLE article_rating');
        $this->addSql('DROP TABLE commentaire_signalement');
        $this->addSql('DROP TABLE commentaire_vote');
        $this->addSql('DROP TABLE messenger_messages');
        $this->addSql('ALTER TABLE article CHANGE titre titre VARCHAR(255) DEFAULT \'NULL\', CHANGE datearticle datearticle DATE DEFAULT \'NULL\', CHANGE urlimagearticle urlimagearticle VARCHAR(255) DEFAULT \'NULL\'');
        $this->addSql('ALTER TABLE commentaire DROP FOREIGN KEY FK_67F068BC727ACA70');
        $this->addSql('DROP INDEX IDX_67F068BC727ACA70 ON commentaire');
        $this->addSql('ALTER TABLE commentaire DROP parent_id, DROP rating, DROP likes, DROP dislikes, CHANGE heure heure TIME DEFAULT \'NULL\'');
        $this->addSql('ALTER TABLE matching CHANGE cv cv VARCHAR(255) DEFAULT \'NULL\', CHANGE price price NUMERIC(10, 2) DEFAULT \'NULL\'');
        $this->addSql('ALTER TABLE planification CHANGE statut statut VARCHAR(255) DEFAULT \'\'\'en attente\'\'\' NOT NULL, CHANGE reponse reponse VARCHAR(255) DEFAULT \'NULL\'');
        $this->addSql('ALTER TABLE utilisateur CHANGE reset_token reset_token VARCHAR(100) DEFAULT \'NULL\'');
    }
}
