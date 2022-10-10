-- phpMyAdmin SQL Dump
-- version 4.8.5
-- https://www.phpmyadmin.net/
--
-- Hôte : 127.0.0.1:3306
-- Généré le :  mar. 06 avr. 2021 à 16:51
-- Version du serveur :  5.7.26
-- Version de PHP :  7.3.5

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
SET AUTOCOMMIT = 0;
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Base de données :  `delivery_app_project`
--

-- --------------------------------------------------------

--
-- Structure de la table `commandes`
--

DROP TABLE IF EXISTS `commandes`;
CREATE TABLE IF NOT EXISTS `commandes` (
  `NumCmd` int(11) NOT NULL AUTO_INCREMENT,
  `NomClient` varchar(30) NOT NULL,
  `AdresseClient` varchar(150) NOT NULL,
  `NumTelClient` varchar(15) NOT NULL,
  `EmailClient` varchar(30) NOT NULL,
  `PrixCmd` int(11) NOT NULL,
  PRIMARY KEY (`NumCmd`)
) ENGINE=MyISAM AUTO_INCREMENT=5 DEFAULT CHARSET=latin1;

--
-- Déchargement des données de la table `commandes`
--

INSERT INTO `commandes` (`NumCmd`, `NomClient`, `AdresseClient`, `NumTelClient`, `EmailClient`, `PrixCmd`) VALUES
(1, 'aziz', 'Centre Commercial Bab Ezzouar, Bab Ezzouar', '1234', 'azizcom', 4000),
(2, 'aziza', 'Centre Hospitalier Universitaire Mustapha, Place du 1er Mai, Sidi M\'Hamed, Alger', '5678', 'azizacoma', 8500),
(3, 'azizo', 'Hotel Aurassi, Avenue du Docteur Frantz Fanon, Alger Centre, Alger', '1234', 'azizocom', 4000),
(4, 'azizi', 'Chéraga DJH, Rue Djenane Achabou, Chéraga', '5678', 'azizicom', 8500);

-- --------------------------------------------------------

--
-- Structure de la table `products`
--

DROP TABLE IF EXISTS `products`;
CREATE TABLE IF NOT EXISTS `products` (
  `ID` int(11) NOT NULL AUTO_INCREMENT,
  `Nom` varchar(30) NOT NULL,
  `Description` varchar(100) NOT NULL,
  `StockQte` int(11) NOT NULL,
  `PrixProduct` int(11) NOT NULL,
  PRIMARY KEY (`ID`)
) ENGINE=MyISAM AUTO_INCREMENT=4 DEFAULT CHARSET=latin1;

--
-- Déchargement des données de la table `products`
--

INSERT INTO `products` (`ID`, `Nom`, `Description`, `StockQte`, `PrixProduct`) VALUES
(1, 'p1', 'p1', 50, 1000),
(2, 'p2', 'p2', 40, 2000),
(3, 'p3', 'p3', 30, 3500);

-- --------------------------------------------------------

--
-- Structure de la table `products_commands`
--

DROP TABLE IF EXISTS `products_commands`;
CREATE TABLE IF NOT EXISTS `products_commands` (
  `CommandID` int(11) NOT NULL,
  `ProductID` int(11) NOT NULL,
  `ProductQte` int(11) NOT NULL,
  PRIMARY KEY (`CommandID`,`ProductID`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

--
-- Déchargement des données de la table `products_commands`
--

INSERT INTO `products_commands` (`CommandID`, `ProductID`, `ProductQte`) VALUES
(1, 1, 2),
(1, 2, 1),
(2, 1, 3),
(2, 2, 1),
(2, 3, 1),
(3, 1, 2),
(3, 2, 1),
(4, 1, 3),
(4, 2, 1),
(4, 3, 1);

-- --------------------------------------------------------

--
-- Structure de la table `users`
--

DROP TABLE IF EXISTS `users`;
CREATE TABLE IF NOT EXISTS `users` (
  `ID` int(11) NOT NULL AUTO_INCREMENT,
  `Username` varchar(30) NOT NULL,
  `Password` varchar(30) NOT NULL,
  PRIMARY KEY (`ID`),
  UNIQUE KEY `Username` (`Username`)
) ENGINE=MyISAM AUTO_INCREMENT=4 DEFAULT CHARSET=latin1;

--
-- Déchargement des données de la table `users`
--

INSERT INTO `users` (`ID`, `Username`, `Password`) VALUES
(1, 'user1', 'user1'),
(2, 'user2', 'user2'),
(3, 'user3', 'user3');

-- --------------------------------------------------------

--
-- Structure de la table `users_commands`
--

DROP TABLE IF EXISTS `users_commands`;
CREATE TABLE IF NOT EXISTS `users_commands` (
  `CommandID` int(11) NOT NULL,
  `UserID` int(11) NOT NULL,
  `Delivered` tinyint(1) NOT NULL,
  `DateRecup` varchar(10) DEFAULT NULL,
  PRIMARY KEY (`CommandID`,`UserID`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

--
-- Déchargement des données de la table `users_commands`
--

INSERT INTO `users_commands` (`CommandID`, `UserID`, `Delivered`, `DateRecup`) VALUES
(1, 1, 0, '01-03-2021'),
(2, 1, 1, '01-04-2021'),
(3, 1, 0, '02-04-2021'),
(4, 0, 0, NULL);
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
