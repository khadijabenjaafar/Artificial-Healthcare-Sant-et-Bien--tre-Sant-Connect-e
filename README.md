# HealthConnect - Plateforme Médicale Intelligente

**Année universitaire : 2024-2025**  
**Établissement : Esprit School of Engineering**  

## 📋 Table des Matières
- [Description](#-description)
- [Fonctionnalités](#-fonctionnalités)
- [Technologies](#-technologies)
- [Installation](#-installation)
- [Utilisation](#-utilisation)
- [Captures d'écran](#-captures-décran)
- [Contributions](#-contributions)
- [Licence](#-licence)
- [Remerciements](#-remerciements)

## 🌟 Description
Plateforme complète de gestion médicale développée dans le cadre du cours Projet Intégré : Développement Web Java à **l'Esprit School** of Engineering. Le système intègre 5 modules interconnectés pour digitaliser les processus de santé avec des fonctionnalités intelligentes.

## 🚀 Fonctionnalités

### 👥 Module Utilisateur
- **5 rôles** : Patient/Médecin/Pharmacien/Freelancer/Admin
- **Sécurité avancée** :
  - Authentification par Face ID
  - Hachage bcrypt des mots de passe
  - Blocage après 3 échecs + email automatique
  - Gestion des bannissements par l'admin

### 🗓 Module Rendez-vous
- **Double système** : Rendez-vous + Consultations
- **Fonctionnalités** :
  - Prise de RDV en ligne avec chatbot
  - Calendrier interactif pour médecins
  - Génération automatique de liens de visio
  - Dossiers médicaux complets (diagnostic, traitement, observations)

### 💊 Module Ordonnance
- **Workflow complet** :
  1. Médecin crée l'ordonnance 
  2. Pharmacien ajoute la facturation
  3. Patient peut :
     - Les consulter avec QR code
     - Exporter en PDF
     - Payer en ligne
     - Recevoir des notifications

### 📚 Module Article
- **Contenu enrichi** :
  - Articles médicaux par des professionnels
  - Description IA générée automatiquement
  - Synthèse vocale + traduction
- **Interaction** :
  - Système de commentaires hiérarchiques
  - Likes/Dislikes + signalements
  - Statistiques de vues

### 👨‍💻 Module Freelancer
- **Deux tables principales** :
  - Matching (CV, compétences)
  - Planification (disponibilités)
- **Communication** : Messagerie intégrée

### 📊 Dashboard Admin
- Supervision complète de toutes les tables
- Statistiques avancées et reporting
- Gestion des signalements et content moderation

## 💻 Technologies

### Backend
- **Framework** : Symfony 6.4
- **Base de données** : MySQL
- **Sécurité** : JWT, OAuth2

### Frontend
- **Framework** : symfony
- **Bibliothèques** :
  - FullCalendar pour les RDV
  - Googleapis pour les traductions
  - Chart.js pour les stats

### Services externes
- **Paiement** : Stripe API
- **Notifications** : Twilio (SMS/Email)
- **IA** : OpenAI (descriptions d'articles)
- **Authentification** : Face ID
- **ChatBot** : olama3.2

## 🛠 Installation

### Prérequis
- PHP 8.2+
- Composer
- MySQL 8.0+

### Étapes
```bash
# 1. Cloner le dépôt
git clone https://github.com/votre-user/HealthConnect.git
cd HealthConnect

# 2. Installer les dépendances
composer install
npm install

# 3. Configurer l'environnement
cp .env.example .env
# Modifier les variables dans .env

# 4. Lancer la base de données
mysql -u root -p < database.sql

# 5. Démarrer les serveurs
symfony server:start
npm run dev
````
## Captures d'écran
