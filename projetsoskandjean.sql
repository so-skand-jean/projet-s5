-- phpMyAdmin SQL Dump
-- version 4.8.2
-- https://www.phpmyadmin.net/
--
-- Hôte : localhost
-- Généré le :  jeu. 10 jan. 2019 à 15:48
-- Version du serveur :  10.3.11-MariaDB
-- Version de PHP :  7.2.5

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
SET AUTOCOMMIT = 0;
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Base de données :  projetsoskandjean
--
CREATE DATABASE IF NOT EXISTS projetsoskandjean DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_bin;
USE projetsoskandjean;

-- --------------------------------------------------------

--
-- Structure de la table capteur
--

CREATE TABLE capteur (
  cpt_nom varchar(200) COLLATE utf8mb4_bin NOT NULL,
  cpt_type enum('AIRCOMPRIME','TEMPERATURE','EAU','ELECTRICITE') COLLATE utf8mb4_bin NOT NULL,
  cpt_batiment varchar(50) COLLATE utf8mb4_bin NOT NULL,
  cpt_etage int(11) NOT NULL,
  cpt_info_lieu varchar(200) COLLATE utf8mb4_bin NOT NULL,
  cpt_seuil_min int(11) NOT NULL,
  cpt_seuil_max int(11) NOT NULL,
  cpt_surnom varchar(200) NOT NULL,
  cpt_date_depassement timestamp NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin;

-- --------------------------------------------------------

--
-- Structure de la table `logs`
--

CREATE TABLE `logs` (
  log_cpt_nom varchar(50) COLLATE utf8mb4_bin NOT NULL,
  log_datetime timestamp NOT NULL DEFAULT current_timestamp() ON UPDATE current_timestamp(),
  log_valeur double NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin;

--
-- Index pour les tables déchargées
--

--
-- Index pour la table capteur
--
ALTER TABLE capteur
  ADD PRIMARY KEY (cpt_nom),
  ADD KEY cpt_nom (cpt_nom);

--
-- Index pour la table `logs`
--
ALTER TABLE `logs`
  ADD PRIMARY KEY (log_cpt_nom,log_datetime),
  ADD KEY log_cpt_nom (log_cpt_nom,log_datetime);

--
-- Contraintes pour les tables déchargées
--

--
-- Contraintes pour la table `logs`
--
ALTER TABLE `logs`
  ADD CONSTRAINT fk_capteur FOREIGN KEY (log_cpt_nom) REFERENCES capteur (cpt_nom) ON DELETE CASCADE ON UPDATE NO ACTION;
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
