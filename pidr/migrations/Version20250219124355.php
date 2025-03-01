<?php

declare(strict_types=1);

namespace DoctrineMigrations;

use Doctrine\DBAL\Schema\Schema;
use Doctrine\Migrations\AbstractMigration;

/**
 * Auto-generated Migration: Please modify to your needs!
 */
final class Version20250219124355 extends AbstractMigration
{
    public function getDescription(): string
    {
        return '';
    }

    public function up(Schema $schema): void
    {
        // this up() migration is auto-generated, please modify it to your needs
        $this->addSql('CREATE TABLE article (id INT AUTO_INCREMENT NOT NULL, id_utilisateur INT DEFAULT NULL, titre VARCHAR(255) DEFAULT NULL, contenue LONGTEXT DEFAULT NULL, datearticle DATE DEFAULT NULL, urlimagearticle VARCHAR(255) DEFAULT NULL, nbre_vue INT NOT NULL, INDEX IDX_23A0E6650EAE44 (id_utilisateur), PRIMARY KEY(id)) DEFAULT CHARACTER SET utf8mb4 COLLATE `utf8mb4_unicode_ci` ENGINE = InnoDB');
        $this->addSql('CREATE TABLE commentaire (id_commentaire INT AUTO_INCREMENT NOT NULL, id_utilisateur INT DEFAULT NULL, article_id INT DEFAULT NULL, contenue VARCHAR(250) NOT NULL, date_commentaire DATE NOT NULL, status TINYINT(1) NOT NULL, heure TIME DEFAULT NULL, INDEX IDX_67F068BC50EAE44 (id_utilisateur), INDEX IDX_67F068BC7294869C (article_id), PRIMARY KEY(id_commentaire)) DEFAULT CHARACTER SET utf8mb4 COLLATE `utf8mb4_unicode_ci` ENGINE = InnoDB');
        $this->addSql('CREATE TABLE consultation (id INT AUTO_INCREMENT NOT NULL, id_rendez_vous_id INT DEFAULT NULL, id_rendezvous_id INT DEFAULT NULL, diagnostic VARCHAR(255) NOT NULL, traitement VARCHAR(255) NOT NULL, observation VARCHAR(255) NOT NULL, prix NUMERIC(10, 0) NOT NULL, prochain_rdv DATE NOT NULL, duree NUMERIC(10, 0) NOT NULL, UNIQUE INDEX UNIQ_964685A6AC89C3AC (id_rendez_vous_id), UNIQUE INDEX UNIQ_964685A68D6D31C6 (id_rendezvous_id), PRIMARY KEY(id)) DEFAULT CHARACTER SET utf8mb4 COLLATE `utf8mb4_unicode_ci` ENGINE = InnoDB');
        $this->addSql('CREATE TABLE facturation (id INT AUTO_INCREMENT NOT NULL, id_ordonnance_id_id INT DEFAULT NULL, date DATE NOT NULL, montant NUMERIC(10, 0) NOT NULL, methode_paiement VARCHAR(255) NOT NULL, statut VARCHAR(255) NOT NULL, UNIQUE INDEX UNIQ_17EB513AA3B07F (id_ordonnance_id_id), PRIMARY KEY(id)) DEFAULT CHARACTER SET utf8mb4 COLLATE `utf8mb4_unicode_ci` ENGINE = InnoDB');
        $this->addSql('CREATE TABLE matching (id INT AUTO_INCREMENT NOT NULL, id_freelancer_id INT DEFAULT NULL, utilisateur_id INT DEFAULT NULL, cin VARCHAR(255) NOT NULL, description VARCHAR(255) NOT NULL, date DATE NOT NULL, competences VARCHAR(255) NOT NULL, cv VARCHAR(255) DEFAULT NULL, UNIQUE INDEX UNIQ_DC10F2893B6D6C90 (id_freelancer_id), UNIQUE INDEX UNIQ_DC10F289FB88E14F (utilisateur_id), PRIMARY KEY(id)) DEFAULT CHARACTER SET utf8mb4 COLLATE `utf8mb4_unicode_ci` ENGINE = InnoDB');
        $this->addSql('CREATE TABLE ordonnance (id INT AUTO_INCREMENT NOT NULL, id_consultation_id INT DEFAULT NULL, date DATE NOT NULL, medicaments VARCHAR(255) NOT NULL, commantaire VARCHAR(255) NOT NULL, duree_utilisation VARCHAR(255) NOT NULL, quantite_utilisation VARCHAR(255) NOT NULL, UNIQUE INDEX UNIQ_924B326C8BA1AF57 (id_consultation_id), PRIMARY KEY(id)) DEFAULT CHARACTER SET utf8mb4 COLLATE `utf8mb4_unicode_ci` ENGINE = InnoDB');
        $this->addSql('CREATE TABLE planification (id INT AUTO_INCREMENT NOT NULL, freelancer_id INT NOT NULL, statut VARCHAR(255) DEFAULT \'en attente\' NOT NULL, date DATE NOT NULL, adresse VARCHAR(255) NOT NULL, reponse VARCHAR(255) DEFAULT NULL, mode VARCHAR(255) NOT NULL, INDEX IDX_FFC02E1B8545BDF5 (freelancer_id), PRIMARY KEY(id)) DEFAULT CHARACTER SET utf8mb4 COLLATE `utf8mb4_unicode_ci` ENGINE = InnoDB');
        $this->addSql('CREATE TABLE rendez_vous (id INT AUTO_INCREMENT NOT NULL, id_patient_id INT DEFAULT NULL, id_medecin_id INT DEFAULT NULL, date_heure DATETIME NOT NULL, motif VARCHAR(255) NOT NULL, statut VARCHAR(255) NOT NULL, mode VARCHAR(255) NOT NULL, commantaire VARCHAR(255) NOT NULL, INDEX IDX_65E8AA0ACE0312AE (id_patient_id), INDEX IDX_65E8AA0AA1799A53 (id_medecin_id), PRIMARY KEY(id)) DEFAULT CHARACTER SET utf8mb4 COLLATE `utf8mb4_unicode_ci` ENGINE = InnoDB');
        $this->addSql('CREATE TABLE utilisateur (id INT AUTO_INCREMENT NOT NULL, nom VARCHAR(255) NOT NULL, prenom VARCHAR(255) NOT NULL, email VARCHAR(255) NOT NULL, password VARCHAR(255) NOT NULL, date_naissance DATE NOT NULL, role VARCHAR(255) NOT NULL, adresse VARCHAR(255) NOT NULL, genre VARCHAR(255) NOT NULL, image VARCHAR(255) NOT NULL, PRIMARY KEY(id)) DEFAULT CHARACTER SET utf8mb4 COLLATE `utf8mb4_unicode_ci` ENGINE = InnoDB');
        $this->addSql('CREATE TABLE messenger_messages (id BIGINT AUTO_INCREMENT NOT NULL, body LONGTEXT NOT NULL, headers LONGTEXT NOT NULL, queue_name VARCHAR(190) NOT NULL, created_at DATETIME NOT NULL COMMENT \'(DC2Type:datetime_immutable)\', available_at DATETIME NOT NULL COMMENT \'(DC2Type:datetime_immutable)\', delivered_at DATETIME DEFAULT NULL COMMENT \'(DC2Type:datetime_immutable)\', INDEX IDX_75EA56E0FB7336F0 (queue_name), INDEX IDX_75EA56E0E3BD61CE (available_at), INDEX IDX_75EA56E016BA31DB (delivered_at), PRIMARY KEY(id)) DEFAULT CHARACTER SET utf8mb4 COLLATE `utf8mb4_unicode_ci` ENGINE = InnoDB');
        $this->addSql('ALTER TABLE article ADD CONSTRAINT FK_23A0E6650EAE44 FOREIGN KEY (id_utilisateur) REFERENCES utilisateur (id) ON DELETE CASCADE');
        $this->addSql('ALTER TABLE commentaire ADD CONSTRAINT FK_67F068BC50EAE44 FOREIGN KEY (id_utilisateur) REFERENCES utilisateur (id) ON DELETE CASCADE');
        $this->addSql('ALTER TABLE commentaire ADD CONSTRAINT FK_67F068BC7294869C FOREIGN KEY (article_id) REFERENCES article (id) ON DELETE CASCADE');
        $this->addSql('ALTER TABLE consultation ADD CONSTRAINT FK_964685A6AC89C3AC FOREIGN KEY (id_rendez_vous_id) REFERENCES rendez_vous (id)');
        $this->addSql('ALTER TABLE consultation ADD CONSTRAINT FK_964685A68D6D31C6 FOREIGN KEY (id_rendezvous_id) REFERENCES rendez_vous (id)');
        $this->addSql('ALTER TABLE facturation ADD CONSTRAINT FK_17EB513AA3B07F FOREIGN KEY (id_ordonnance_id_id) REFERENCES ordonnance (id)');
        $this->addSql('ALTER TABLE matching ADD CONSTRAINT FK_DC10F2893B6D6C90 FOREIGN KEY (id_freelancer_id) REFERENCES utilisateur (id)');
        $this->addSql('ALTER TABLE matching ADD CONSTRAINT FK_DC10F289FB88E14F FOREIGN KEY (utilisateur_id) REFERENCES utilisateur (id) ON DELETE CASCADE');
        $this->addSql('ALTER TABLE ordonnance ADD CONSTRAINT FK_924B326C8BA1AF57 FOREIGN KEY (id_consultation_id) REFERENCES consultation (id)');
        $this->addSql('ALTER TABLE planification ADD CONSTRAINT FK_FFC02E1B8545BDF5 FOREIGN KEY (freelancer_id) REFERENCES utilisateur (id)');
        $this->addSql('ALTER TABLE rendez_vous ADD CONSTRAINT FK_65E8AA0ACE0312AE FOREIGN KEY (id_patient_id) REFERENCES utilisateur (id)');
        $this->addSql('ALTER TABLE rendez_vous ADD CONSTRAINT FK_65E8AA0AA1799A53 FOREIGN KEY (id_medecin_id) REFERENCES utilisateur (id)');
    }

    public function down(Schema $schema): void
    {
        // this down() migration is auto-generated, please modify it to your needs
        $this->addSql('ALTER TABLE article DROP FOREIGN KEY FK_23A0E6650EAE44');
        $this->addSql('ALTER TABLE commentaire DROP FOREIGN KEY FK_67F068BC50EAE44');
        $this->addSql('ALTER TABLE commentaire DROP FOREIGN KEY FK_67F068BC7294869C');
        $this->addSql('ALTER TABLE consultation DROP FOREIGN KEY FK_964685A6AC89C3AC');
        $this->addSql('ALTER TABLE consultation DROP FOREIGN KEY FK_964685A68D6D31C6');
        $this->addSql('ALTER TABLE facturation DROP FOREIGN KEY FK_17EB513AA3B07F');
        $this->addSql('ALTER TABLE matching DROP FOREIGN KEY FK_DC10F2893B6D6C90');
        $this->addSql('ALTER TABLE matching DROP FOREIGN KEY FK_DC10F289FB88E14F');
        $this->addSql('ALTER TABLE ordonnance DROP FOREIGN KEY FK_924B326C8BA1AF57');
        $this->addSql('ALTER TABLE planification DROP FOREIGN KEY FK_FFC02E1B8545BDF5');
        $this->addSql('ALTER TABLE rendez_vous DROP FOREIGN KEY FK_65E8AA0ACE0312AE');
        $this->addSql('ALTER TABLE rendez_vous DROP FOREIGN KEY FK_65E8AA0AA1799A53');
        $this->addSql('DROP TABLE article');
        $this->addSql('DROP TABLE commentaire');
        $this->addSql('DROP TABLE consultation');
        $this->addSql('DROP TABLE facturation');
        $this->addSql('DROP TABLE matching');
        $this->addSql('DROP TABLE ordonnance');
        $this->addSql('DROP TABLE planification');
        $this->addSql('DROP TABLE rendez_vous');
        $this->addSql('DROP TABLE utilisateur');
        $this->addSql('DROP TABLE messenger_messages');
    }
}
