<?php

declare(strict_types=1);

namespace DoctrineMigrations;

use Doctrine\DBAL\Schema\Schema;
use Doctrine\Migrations\AbstractMigration;

/**
 * Auto-generated Migration: Please modify to your needs!
 */
final class Version20250215095644 extends AbstractMigration
{
    public function getDescription(): string
    {
        return '';
    }

    public function up(Schema $schema): void
    {
        // this up() migration is auto-generated, please modify it to your needs
        $this->addSql('CREATE TABLE messenger_messages (id BIGINT AUTO_INCREMENT NOT NULL, body LONGTEXT NOT NULL, headers LONGTEXT NOT NULL, queue_name VARCHAR(190) NOT NULL, created_at DATETIME NOT NULL COMMENT \'(DC2Type:datetime_immutable)\', available_at DATETIME NOT NULL COMMENT \'(DC2Type:datetime_immutable)\', delivered_at DATETIME DEFAULT NULL COMMENT \'(DC2Type:datetime_immutable)\', INDEX IDX_75EA56E0FB7336F0 (queue_name), INDEX IDX_75EA56E0E3BD61CE (available_at), INDEX IDX_75EA56E016BA31DB (delivered_at), PRIMARY KEY(id)) DEFAULT CHARACTER SET utf8mb4 COLLATE `utf8mb4_unicode_ci` ENGINE = InnoDB');
        $this->addSql('ALTER TABLE consultation ADD id_rendez_vous_id INT DEFAULT NULL, ADD id_rendezvous_id INT DEFAULT NULL');
        $this->addSql('ALTER TABLE consultation ADD CONSTRAINT FK_964685A6AC89C3AC FOREIGN KEY (id_rendez_vous_id) REFERENCES rendez_vous (id)');
        $this->addSql('ALTER TABLE consultation ADD CONSTRAINT FK_964685A68D6D31C6 FOREIGN KEY (id_rendezvous_id) REFERENCES rendez_vous (id)');
        $this->addSql('CREATE UNIQUE INDEX UNIQ_964685A6AC89C3AC ON consultation (id_rendez_vous_id)');
        $this->addSql('CREATE UNIQUE INDEX UNIQ_964685A68D6D31C6 ON consultation (id_rendezvous_id)');
    }

    public function down(Schema $schema): void
    {
        // this down() migration is auto-generated, please modify it to your needs
        $this->addSql('DROP TABLE messenger_messages');
        $this->addSql('ALTER TABLE consultation DROP FOREIGN KEY FK_964685A6AC89C3AC');
        $this->addSql('ALTER TABLE consultation DROP FOREIGN KEY FK_964685A68D6D31C6');
        $this->addSql('DROP INDEX UNIQ_964685A6AC89C3AC ON consultation');
        $this->addSql('DROP INDEX UNIQ_964685A68D6D31C6 ON consultation');
        $this->addSql('ALTER TABLE consultation DROP id_rendez_vous_id, DROP id_rendezvous_id');
    }
}
