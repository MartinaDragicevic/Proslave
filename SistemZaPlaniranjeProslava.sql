-- MySQL Workbench Forward Engineering

SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0;
SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0;
SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION';

-- -----------------------------------------------------
-- Schema SistemZaPlaniranjeProslava
-- -----------------------------------------------------

-- -----------------------------------------------------
-- Schema SistemZaPlaniranjeProslava
-- -----------------------------------------------------
CREATE SCHEMA IF NOT EXISTS `SistemZaPlaniranjeProslava` DEFAULT CHARACTER SET utf8 ;
USE `SistemZaPlaniranjeProslava` ;

-- -----------------------------------------------------
-- Table `SistemZaPlaniranjeProslava`.`Admin`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `SistemZaPlaniranjeProslava`.`Admin` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `ime` VARCHAR(45) NULL,
  `prezime` VARCHAR(45) NULL,
  `korisnicko_ime` VARCHAR(45) NULL,
  `lozinka` VARCHAR(45) NULL,
  PRIMARY KEY (`id`))
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `SistemZaPlaniranjeProslava`.`Bankovni racun`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `SistemZaPlaniranjeProslava`.`Bankovni racun` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `broj_racuna` VARCHAR(45) NULL,
  `jmbg` VARCHAR(13) NULL,
  `stanje` DECIMAL(8,2) NULL,
  PRIMARY KEY (`id`))
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `SistemZaPlaniranjeProslava`.`Klijent`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `SistemZaPlaniranjeProslava`.`Klijent` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `ime` VARCHAR(45) NULL,
  `prezime` VARCHAR(45) NULL,
  `jmbg` VARCHAR(13) NULL,
  `broj_racuna` VARCHAR(45) NULL,
  `korisnicko_ime` VARCHAR(45) NULL,
  `lozinka` VARCHAR(45) NULL,
  PRIMARY KEY (`id`))
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `SistemZaPlaniranjeProslava`.`Vlasnik`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `SistemZaPlaniranjeProslava`.`Vlasnik` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `ime` VARCHAR(45) NULL,
  `prezime` VARCHAR(45) NULL,
  `jmbg` VARCHAR(13) NULL,
  `broj_racuna` VARCHAR(45) NULL,
  `korisnicko_ime` VARCHAR(45) NULL,
  `lozinka` VARCHAR(45) NULL,
  PRIMARY KEY (`id`))
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `SistemZaPlaniranjeProslava`.`Objekat`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `SistemZaPlaniranjeProslava`.`Objekat` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `Vlasnik_id` INT NOT NULL,
  `naziv` VARCHAR(45) NULL,
  `cijena_rezervacije` DECIMAL(8,2) NULL,
  `grad` VARCHAR(45) NULL,
  `adresa` VARCHAR(45) NULL,
  `broj_mjesta` INT NULL,
  `broj_stolova` INT NULL,
  `datumi` VARCHAR(45) NULL,
  `zarada` DECIMAL(8,2) NULL,
  `status` ENUM('ODOBREN', 'ODBIJEN', 'NA CEKANJU') NULL DEFAULT 'NA CEKANJU',
  PRIMARY KEY (`id`),
  INDEX `fk_Objekat_Vlasnik_idx` (`Vlasnik_id` ASC) ,
  CONSTRAINT `fk_Objekat_Vlasnik`
    FOREIGN KEY (`Vlasnik_id`)
    REFERENCES `SistemZaPlaniranjeProslava`.`Vlasnik` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `SistemZaPlaniranjeProslava`.`Sto`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `SistemZaPlaniranjeProslava`.`Sto` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `Objekat_id` INT NOT NULL,
  `broj_mjesta` INT NULL,
  PRIMARY KEY (`id`),
  INDEX `fk_Sto_Objekat1_idx` (`Objekat_id` ASC) ,
  CONSTRAINT `fk_Sto_Objekat1`
    FOREIGN KEY (`Objekat_id`)
    REFERENCES `SistemZaPlaniranjeProslava`.`Objekat` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `SistemZaPlaniranjeProslava`.`Meni`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `SistemZaPlaniranjeProslava`.`Meni` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `Objekat_id` INT NOT NULL,
  `opis` VARCHAR(45) NULL,
  `cijena_po_osobi` DECIMAL(8,2) NULL,
  PRIMARY KEY (`id`),
  INDEX `fk_Meni_Objekat1_idx` (`Objekat_id` ASC) ,
  CONSTRAINT `fk_Meni_Objekat1`
    FOREIGN KEY (`Objekat_id`)
    REFERENCES `SistemZaPlaniranjeProslava`.`Objekat` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `SistemZaPlaniranjeProslava`.`Proslava`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `SistemZaPlaniranjeProslava`.`Proslava` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `Objekat_id` INT NOT NULL,
  `Klijent_id` INT NOT NULL,
  `Meni_id` INT NULL,
  `Proslavacol` VARCHAR(45) NULL,
  `datum` DATE NULL,
  `broj_gostiju` INT NULL,
  `ukupna_cijena` DECIMAL(8,2) NULL,
  `uplacen_iznos` DECIMAL(8,2) NULL,
  PRIMARY KEY (`id`),
  INDEX `fk_Proslava_Meni1_idx` (`Meni_id` ASC) ,
  INDEX `fk_Proslava_Objekat1_idx` (`Objekat_id` ASC) ,
  INDEX `fk_Proslava_Klijent1_idx` (`Klijent_id` ASC) ,
  CONSTRAINT `fk_Proslava_Meni1`
    FOREIGN KEY (`Meni_id`)
    REFERENCES `SistemZaPlaniranjeProslava`.`Meni` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_Proslava_Objekat1`
    FOREIGN KEY (`Objekat_id`)
    REFERENCES `SistemZaPlaniranjeProslava`.`Objekat` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_Proslava_Klijent1`
    FOREIGN KEY (`Klijent_id`)
    REFERENCES `SistemZaPlaniranjeProslava`.`Klijent` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `SistemZaPlaniranjeProslava`.`Obavjestenje`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `SistemZaPlaniranjeProslava`.`Obavjestenje` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `Objekat_id` INT NOT NULL,
  `tekst` LONGTEXT NULL,
  PRIMARY KEY (`id`),
  INDEX `fk_Obavjestenje_Objekat1_idx` (`Objekat_id` ASC) ,
  CONSTRAINT `fk_Obavjestenje_Objekat1`
    FOREIGN KEY (`Objekat_id`)
    REFERENCES `SistemZaPlaniranjeProslava`.`Objekat` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `SistemZaPlaniranjeProslava`.`Raspored`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `SistemZaPlaniranjeProslava`.`Raspored` (
  `idSto` INT NOT NULL,
  `idProslava` INT NOT NULL,
  `gosti` LONGTEXT NULL,
  PRIMARY KEY (`idSto`, `idProslava`),
  INDEX `fk_Sto_has_Proslava_Proslava1_idx` (`idProslava` ASC) ,
  INDEX `fk_Sto_has_Proslava_Sto1_idx` (`idSto` ASC) ,
  CONSTRAINT `fk_Sto_has_Proslava_Sto1`
    FOREIGN KEY (`idSto`)
    REFERENCES `SistemZaPlaniranjeProslava`.`Sto` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_Sto_has_Proslava_Proslava1`
    FOREIGN KEY (`idProslava`)
    REFERENCES `SistemZaPlaniranjeProslava`.`Proslava` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


SET SQL_MODE=@OLD_SQL_MODE;
SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS;
SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS;

-- -----------------------------------------------------
-- Data for table `SistemZaPlaniranjeProslava`.`Bankovni racun`
-- -----------------------------------------------------
START TRANSACTION;
USE `SistemZaPlaniranjeProslava`;
INSERT INTO `SistemZaPlaniranjeProslava`.`Bankovni racun` (`id`, `broj_racuna`, `jmbg`, `stanje`) VALUES (1, '1111222233334444', '0101994000001', 10543.00);
INSERT INTO `SistemZaPlaniranjeProslava`.`Bankovni racun` (`id`, `broj_racuna`, `jmbg`, `stanje`) VALUES (2, '1111222233335555', '0201994000011', 20022.00);
INSERT INTO `SistemZaPlaniranjeProslava`.`Bankovni racun` (`id`, `broj_racuna`, `jmbg`, `stanje`) VALUES (3, '1111222233336666', '1105994000201', 15320.00);
INSERT INTO `SistemZaPlaniranjeProslava`.`Bankovni racun` (`id`, `broj_racuna`, `jmbg`, `stanje`) VALUES (4, '1111222233337777', '2105998000001', 11252.71);
INSERT INTO `SistemZaPlaniranjeProslava`.`Bankovni racun` (`id`, `broj_racuna`, `jmbg`, `stanje`) VALUES (5, '7777222233334444', '1508997110001', 11230.00);
INSERT INTO `SistemZaPlaniranjeProslava`.`Bankovni racun` (`id`, `broj_racuna`, `jmbg`, `stanje`) VALUES (6, '7777222233331111', '2501999111000', 15420.00);
INSERT INTO `SistemZaPlaniranjeProslava`.`Bankovni racun` (`id`, `broj_racuna`, `jmbg`, `stanje`) VALUES (7, '1234000056742918', '1104001118093', 14251.00);
INSERT INTO `SistemZaPlaniranjeProslava`.`Bankovni racun` (`id`, `broj_racuna`, `jmbg`, `stanje`) VALUES (8, '7777222233339999', '0302999110201', 10002.00);
INSERT INTO `SistemZaPlaniranjeProslava`.`Bankovni racun` (`id`, `broj_racuna`, `jmbg`, `stanje`) VALUES (9, '1111543233331234', '1907000113051', 17232.00);
INSERT INTO `SistemZaPlaniranjeProslava`.`Bankovni racun` (`id`, `broj_racuna`, `jmbg`, `stanje`) VALUES (10, '1234567811112222', '2111993114021', 30151.00);

COMMIT;

