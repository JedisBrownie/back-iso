CREATE DATABASE base_iso;
USE base_iso;

CREATE TABLE processus_global(
    id_processus_global INTEGER,
    nom VARCHAR(90),
    PRIMARY KEY(id_processus_global)
);

CREATE TABLE processus_lie(
    id_processus_lie INT,
    id_processus_global INTEGER NOT NULL,
    nom TEXT,
    PRIMARY KEY(id_processus_lie),
    FOREIGN KEY(id_processus_global) REFERENCES processus_global(id_processus_global) 
);

CREATE TABLE etat_document(
    id_etat INT NOT NULL AUTO_INCREMENT,
    nom VARCHAR(50),
    PRIMARY KEY(id_etat)
);
ALTER TABLE etat_document ADD COLUMN status VARCHAR(30);


CREATE TABLE type_document(
    id_type INT NOT NULL AUTO_INCREMENT,
    nom VARCHAR(90),
    PRIMARY KEY(id_type)
);

-- ### document ### --

CREATE TABLE document(
    ref_document VARCHAR(80),
    id_document INT,
    titre TEXT,
    id_type INT NOT NULL,
    id_entete INT NOT NULL,
    date_creation DATE,
    date_mise_application DATE,
    date_archive DATE,
    confidentiel TINYINT(1),
    id_approbateur BIGINT,
    id_validateur BIGINT,
    PRIMARY KEY(ref_document,id_document),
    FOREIGN KEY(id_type) REFERENCES type_document(id_type),
    FOREIGN KEY(id_approbateur) REFERENCES base_rh.utilisateur(matricule),
    FOREIGN KEY(id_validateur) REFERENCES base_rh.utilisateur(matricule)
);

CREATE TABLE processus_global_document(
    ref_document VARCHAR(80),
    id_document INT NOT NULL,
    id_processus_global INT NOT NULL,
    FOREIGN KEY(ref_document) REFERENCES document(ref_document),
    FOREIGN KEY(id_document) REFERENCES document(id_document),
    FOREIGN KEY(id_processus_global) REFERENCES processus_global(id_processus_global)
);

CREATE TABLE processus_lie_document(
    ref_document VARCHAR(80),
    id_document INT NOT NULL,
    id_processus_lie INT NOT NULL,
    FOREIGN KEY(ref_document) REFERENCES document(ref_document),
    FOREIGN KEY(id_document) REFERENCES document(id_document),
    FOREIGN KEY(id_processus_lie) REFERENCES processus_lie(id_processus_lie)
);

CREATE TABLE historique_etat(
    id_histo VARCHAR(80),
    ref_document VARCHAR(80),
    id_document INT NOT NULL,
    id_etat INT NOT NULL,
    id_utilisateur BIGINT NOT NULL,
    date_heure_etat TIMESTAMP,
    motif TEXT,
    PRIMARY KEY (id_histo),
    FOREIGN KEY(ref_document) REFERENCES document(ref_document),
    FOREIGN KEY(id_document) REFERENCES document(id_document),
    FOREIGN KEY(id_utilisateur) REFERENCES base_rh.utilisateur(matricule),
    FOREIGN KEY(id_etat) REFERENCES etat(id_etat)
);

-- ### document/personne ### --

CREATE TABLE pilote_document(
    ref_document VARCHAR(80),
    id_document INT NOT NULL,
    id_utilisateur BIGINT NOT NULL,
    FOREIGN KEY(ref_document) REFERENCES document(ref_document),
    FOREIGN KEY(id_document) REFERENCES document(id_document),
    FOREIGN KEY(id_utilisateur) REFERENCES base_rh.utilisateur(matricule)
);

CREATE TABLE diffusion_email(
    ref_document VARCHAR(80)
    id_document INT NOT NULL,
    id_utilisateur BIGINT NOT NULL,
    FOREIGN KEY(ref_document) REFERENCES document(ref_document),
    FOREIGN KEY(id_document) REFERENCES document(id_document),
    FOREIGN KEY(id_utilisateur) REFERENCES base_rh.utilisateur(matricule)
);

CREATE TABLE lecteur_document(
    ref_document VARCHAR(80),
    id_document INT NOT NULL,
    id_utilisateur BIGINT NOT NULL,
    FOREIGN KEY(ref_document) REFERENCES document(ref_document),
    FOREIGN KEY(id_utilisateur) REFERENCES base_rh.utilisateur(matricule)
);

CREATE TABLE redacteur_document(
    ref_document VARCHAR(80),
    id_document INT NOT NULL,
    id_utilisateur BIGINT NOT NULL,
    FOREIGN KEY(ref_document) REFERENCES document(ref_document),
    FOREIGN KEY(id_utilisateur) REFERENCES base_rh.utilisateur(matricule)
);


-- ### document/contenu ### --

-- CREATE TABLE champ(
--     id_champ INT NOT NULL AUTO_INCREMENT,
--     nom VARCHAR(30),
--     obligatoire TINYINT(1)
--     PRIMARY KEY(id_champ)
-- );

-- CREATE TABLE champ_document(
--     ref_document VARCHAR(80) NOT NULL,
--     id_document INT NOT NULL,
--     id_champ INT NOT NULL,
--     affichable INT, 
--     valeur TEXT,
--     FOREIGN KEY(id_document) REFERENCES document(id_document),
--     FOREIGN KEY(id_champ) REFERENCES champ(id_champ)
-- );

-- CREATE TABLE fichier_document(
--     ref_document VARCHAR(80) NOT NULL,
--     id_document INT NOT NULL,
--     emplacement TEXT,
--     nom TEXT,
--     FOREIGN KEY(id_document) REFERENCES document(id_document),
--     FOREIGN KEY(ref_document) REFERENCES document(ref_document),     
-- );

-- ### trigger-reference-document ### --

CREATE TRIGGER before_insert_document
BEFORE INSERT ON documents
FOR EACH ROW
BEGIN
  DECLARE last_id INT;

  IF NEW.ref_document IS NULL THEN
    SET @type := SUBSTRING(NEW.type_document, 1, 2);
    SET @processus_id := NEW.id_processus;
    SET @date := DATE_FORMAT(NOW(), '%Y%m%d');
    SET @rang := (SELECT COUNT(*) FROM documents WHERE DATE(created_at) = CURDATE());
    SET NEW.ref_document := CONCAT(@type, @processus_id, '-', @date, '-', LPAD(@rang, 2, '0'));
  END IF;

  SELECT MAX(id_document) INTO last_id
  FROM documents
  WHERE ref_document = NEW.ref_document;
  IF last_id IS NOT NULL THEN
    SET NEW.id_document := last_id + 1;
  ELSE
    SET NEW.id_document := 1;
  END IF;
END;



-- ### trigger prod ### --

DELIMITER $$
CREATE TRIGGER before_insert_historique
BEFORE INSERT ON historique_etat
FOR EACH ROW
BEGIN
    SET @type := 'HE';
    SET @date := DATE_FORMAT(NOW(), '%Y%m%d');
    SET @rang := (SELECT COUNT(*)+1 FROM historique_etat WHERE DATE(date_heure_etat) = CURDATE());
    SET NEW.id_histo := CONCAT(CAST(@type AS CHAR CHARACTER SET utf8),'-',CAST(@date AS CHAR CHARACTER SET utf8),'-', CAST(LPAD(@rang, 3, '00') AS CHAR CHARACTER SET utf8));
END;
$$
DELIMITER


-- ### trigger données test ### --

-- DELIMITER $$
-- CREATE TRIGGER before_insert_historique
-- BEFORE INSERT ON historique_etat
-- FOR EACH ROW
-- BEGIN
--     SET @type := 'HE';
--     SET @date := DATE_FORMAT(NEW.date_heure_etat, '%Y%m%d');
--     SET @rang := (SELECT COUNT(*)+1 FROM historique_etat WHERE DATE(date_heure_etat) = DATE(NEW.date_heure_etat));
--     SET NEW.id_histo := CONCAT(CAST(@type AS CHAR CHARACTER SET utf8),'-',CAST(@date AS CHAR CHARACTER SET utf8),'-', CAST(LPAD(@rang, 3, '00') AS CHAR CHARACTER SET utf8));
-- END;
-- $$
-- DELIMITER

-- ### données test ### --

INSERT INTO processus_global(id_processus_global,nom) VALUES (1000,"Processus Management");
INSERT INTO processus_global(id_processus_global,nom) VALUES (2000,"Ressources");
INSERT INTO processus_global(id_processus_global,nom) VALUES (3000,"Processus Commercial");
INSERT INTO processus_global(id_processus_global,nom) VALUES (4000,"Processus Production");
INSERT INTO processus_global(id_processus_global,nom) VALUES (5000,"Activités Supports");
INSERT INTO processus_global(id_processus_global,nom) VALUES (5100,"Direction Administrative et financière");
INSERT INTO processus_global(id_processus_global,nom) VALUES (6000,"Gestion de crise");
INSERT INTO processus_global(id_processus_global,nom) VALUES (9000,"Surveillance et mesures");
INSERT INTO processus_global(id_processus_global,nom) VALUES (9200,"Non Conformités");

INSERT INTO type_document(nom) VALUES ("Processus");
INSERT INTO type_document(nom) VALUES ("Sous Processus");
INSERT INTO type_document(nom) VALUES ("Fiche d'instruction");
INSERT INTO type_document(nom) VALUES ("Enregistrement");
INSERT INTO type_document(nom) VALUES ("Navigateur");

INSERT INTO etat_document(nom) VALUES ("Brouillon");
INSERT INTO etat_document(nom) VALUES ("Redaction");
INSERT INTO etat_document(nom) VALUES ("Invalidation");
INSERT INTO etat_document(nom) VALUES ("Validation");
INSERT INTO etat_document(nom) VALUES ("Désapprobation");
INSERT INTO etat_document(nom) VALUES ("Approbation");
INSERT INTO etat_document(nom) VALUES ("Demande révision");
INSERT INTO etat_document(nom) VALUES ("Modifiable");
INSERT INTO etat_document(nom) VALUES ("Archives");

UPDATE etat_document SET status = "Brouillon" WHERE id_etat = 1;
UPDATE etat_document SET status = "En cours de vérification" WHERE id_etat = 2;
UPDATE etat_document SET status = "Non validé" WHERE id_etat = 3;
UPDATE etat_document SET status = "En cours d'approbation" WHERE id_etat = 4;
UPDATE etat_document SET status = "Non approuvé" WHERE id_etat = 5;
UPDATE etat_document SET status = "Applicable" WHERE id_etat = 6;
UPDATE etat_document SET status = "Applicable" WHERE id_etat = 7;
UPDATE etat_document SET status = "Applicable" WHERE id_etat = 8;
UPDATE etat_document SET status = "Archive" WHERE id_etat = 9;


INSERT INTO processus_lie(id_processus_lie,id_processus_global,nom) VALUES (1100,1000,"Planification");
INSERT INTO processus_lie(id_processus_lie,id_processus_global,nom) VALUES (1200,1000,"Revue de direction");
INSERT INTO processus_lie(id_processus_lie,id_processus_global,nom) VALUES (1300,1000,"Communication");
INSERT INTO processus_lie(id_processus_lie,id_processus_global,nom) VALUES (2100,2000,"Ressources humaines");

INSERT INTO processus_lie(id_processus_lie,id_processus_global,nom) VALUES (2300,2000,"Travaux neufs");
INSERT INTO processus_lie(id_processus_lie,id_processus_global,nom) VALUES (2400,2000,"Système documentaire");

INSERT INTO processus_lie(id_processus_lie,id_processus_global,nom) VALUES (4111,4000,"Extraction matières premières");
INSERT INTO processus_lie(id_processus_lie,id_processus_global,nom) VALUES (4121,4000,"Cru blanc");
INSERT INTO processus_lie(id_processus_lie,id_processus_global,nom) VALUES (4122,4000,"Cru noir");
INSERT INTO processus_lie(id_processus_lie,id_processus_global,nom) VALUES (4130,4000,"Cuisson");

INSERT INTO processus_lie(id_processus_lie,id_processus_global,nom) VALUES (5110,5000,"Achats biens et services");


INSERT INTO document(ref_document,id_document,titre,id_type,confidentiel,date_creation,date_mise_application) VALUES ("NA1100-20240515-1",1,"Système de management environnemental",5,0,"2024-04-15","2024-05-15");
INSERT INTO document(ref_document,id_document,titre,id_type,confidentiel,date_creation,id_validateur,id_approbateur) VALUES ("PR1100-20240316-1",1,"Procédure de sécurisation des matières premières",1,0,"2024-02-16",80682,24566);
INSERT INTO document(ref_document,id_document,titre,id_type,confidentiel,date_creation,date_mise_application,id_validateur,id_approbateur) VALUES ("FI1100-20220905-1",1,"Gestion des changements",3,0,"2022-08-05","2022-09-05",80682,24566);
INSERT INTO document(ref_document,id_document,titre,id_type,confidentiel,date_creation,date_mise_application) VALUES ("EN1100-20220605-1",1,"Analyse des risques Ibity",4,0,"2022-05-05","2022-06-05");

INSERT INTO document(ref_document,id_document,titre,id_type,confidentiel,date_creation,date_mise_application) VALUES ("EN1100-20221208-1",1,"Analyse des risques dépôts",4,1,"2022-11-08","2022-12-08");


INSERT INTO document(ref_document,id_document,titre,id_type,confidentiel,date_creation,date_mise_application,id_validateur,id_approbateur) VALUES ("PR1300-20230922-1",1,"Communication",1,0,"2023-08-22","2023-09-22",78542,24566);
INSERT INTO document(ref_document,id_document,titre,id_type,confidentiel,date_creation,date_mise_application,id_validateur,id_approbateur) VALUES ("FI1300-20230211-1",1,"Demande de support en communication",3,0,"2023-01-11","2023-02-11",80682,24566);
INSERT INTO document(ref_document,id_document,titre,id_type,confidentiel,date_creation) VALUES ("EN1300-20220812-1",1,"Directive sur la communication",4,0,"2022-07-22");

INSERT INTO document(ref_document,id_document,titre,id_type,confidentiel,date_creation,date_mise_application,id_validateur,id_approbateur) VALUES ("FI2100-20230908-1",1,"Déplacement par transport en commun de tout le personnel de Cementis(Madagascar) sur les axes Antsirabe - Tamatave - Majunga",3,0,"2023-08-08","2023-09-08",78542,24566);
INSERT INTO document(ref_document,id_document,titre,id_type,confidentiel,date_creation,date_mise_application,id_validateur,id_approbateur) VALUES ("FI2100-20230908-1",2,"Déplacement par transport en commun de tout le personnel de Cementis(Madagascar) sur les axes Antsirabe - Tamatave - Majunga",3,1,"2023-12-15","2024-01-15",78542,24566);

UPDATE document SET date_archive = "2024-01-15" WHERE ref_document = "FI2100-20230908-1" AND id_document = 1;

INSERT INTO processus_global_document(ref_document,id_document,id_processus_global) VALUES ("NA1100-20240515-1",1,1000);
INSERT INTO processus_global_document(ref_document,id_document,id_processus_global) VALUES ("PR1100-20240316-1",1,1000);
INSERT INTO processus_global_document(ref_document,id_document,id_processus_global) VALUES ("FI1100-20220905-1",1,1000);
INSERT INTO processus_global_document(ref_document,id_document,id_processus_global) VALUES ("EN1100-20220605-1",1,1000);
INSERT INTO processus_global_document(ref_document,id_document,id_processus_global) VALUES ("EN1100-20221208-1",1,1000);


INSERT INTO processus_global_document(ref_document,id_document,id_processus_global) VALUES ("PR1300-20230922-1",1,1000);
INSERT INTO processus_global_document(ref_document,id_document,id_processus_global) VALUES ("FI1300-20230211-1",1,1000);
INSERT INTO processus_global_document(ref_document,id_document,id_processus_global) VALUES ("EN1300-20220812-1",1,1000);

INSERT INTO processus_global_document(ref_document,id_document,id_processus_global) VALUES ("FI2100-20230908-1",1,2000);
INSERT INTO processus_global_document(ref_document,id_document,id_processus_global) VALUES ("FI2100-20230908-1",1,5000);

-- INSERT INTO processus_global_document(ref_document,id_document,id_processus_global) VALUES ("FI2100-20230908-1",2,5000);




INSERT INTO processus_lie_document(ref_document,id_document,id_processus_lie) VALUES ("NA1100-20240515-1",1,1100);
INSERT INTO processus_lie_document(ref_document,id_document,id_processus_lie) VALUES ("PR1100-20240316-1",1,1100);
INSERT INTO processus_lie_document(ref_document,id_document,id_processus_lie) VALUES ("FI1100-20220905-1",1,1100);
INSERT INTO processus_lie_document(ref_document,id_document,id_processus_lie) VALUES ("EN1100-20220605-1",1,1100);
INSERT INTO processus_lie_document(ref_document,id_document,id_processus_lie) VALUES ("EN1100-20221208-1",1,1100);


INSERT INTO processus_lie_document(ref_document,id_document,id_processus_lie) VALUES ("PR1300-20230922-1",1,1300);
INSERT INTO processus_lie_document(ref_document,id_document,id_processus_lie) VALUES ("FI1300-20230211-1",1,1300);
INSERT INTO processus_lie_document(ref_document,id_document,id_processus_lie) VALUES ("EN1300-20220812-1",1,1300);

INSERT INTO processus_lie_document(ref_document,id_document,id_processus_lie) VALUES ("FI2100-20230908-1",1,2100);
INSERT INTO processus_lie_document(ref_document,id_document,id_processus_lie) VALUES ("FI2100-20230908-1",1,5110);

INSERT INTO processus_lie_document(ref_document,id_document,id_processus_lie) VALUES ("FI2100-20230908-1",2,2110);
INSERT INTO processus_lie_document(ref_document,id_document,id_processus_lie) VALUES ("FI2100-20230908-1",2,5110);



INSERT INTO historique_etat(ref_document,id_document,id_etat,id_utilisateur,date_heure_etat,motif)VALUES("PR1100-20240316-1",1,1,80682,"2024-03-01 08:30","");
INSERT INTO historique_etat(ref_document,id_document,id_etat,id_utilisateur,date_heure_etat,motif)VALUES("PR1100-20240316-1",1,2,80682,"2024-03-03 08:20","");
INSERT INTO historique_etat(ref_document,id_document,id_etat,id_utilisateur,date_heure_etat,motif)VALUES("PR1100-20240316-1",1,4,80246,"2024-03-06 07:45","");
INSERT INTO historique_etat(ref_document,id_document,id_etat,id_utilisateur,date_heure_etat,motif)VALUES("PR1100-20240316-1",1,5,24566,"2024-03-06 12:30","Manques d'information");
INSERT INTO historique_etat(ref_document,id_document,id_etat,id_utilisateur,date_heure_etat,motif)VALUES("PR1100-20240316-1",1,1,80682,"2024-03-06 12:30","");
INSERT INTO historique_etat(ref_document,id_document,id_etat,id_utilisateur,date_heure_etat,motif)VALUES("PR1100-20240316-1",1,2,80682,"2024-03-10 10:20","");
INSERT INTO historique_etat(ref_document,id_document,id_etat,id_utilisateur,date_heure_etat,motif)VALUES("PR1100-20240316-1",1,4,80246,"2024-03-13 17:45","");
INSERT INTO historique_etat(ref_document,id_document,id_etat,id_utilisateur,date_heure_etat,motif)VALUES("PR1100-20240316-1",1,6,24566,"2024-03-16 08:00","");

INSERT INTO historique_etat(ref_document,id_document,id_etat,id_utilisateur,date_heure_etat,motif)VALUES("FI1100-20220905-1",1,2,78542,"2022-08-31 14:45","");
INSERT INTO historique_etat(ref_document,id_document,id_etat,id_utilisateur,date_heure_etat,motif)VALUES("FI1100-20220905-1",1,4,80682,"2022-09-02 08:20","");
INSERT INTO historique_etat(ref_document,id_document,id_etat,id_utilisateur,date_heure_etat,motif)VALUES("FI1100-20220905-1",1,6,24566,"2022-09-05 10:20","");
INSERT INTO historique_etat(ref_document,id_document,id_etat,id_utilisateur,date_heure_etat,motif)VALUES("FI1100-20220905-1",1,7,78542,"2022-10-17 10:20","Mise à jour");

INSERT INTO historique_etat(ref_document,id_document,id_etat,id_utilisateur,date_heure_etat,motif)VALUES("EN1100-20220605-1",1,2,78542,"2022-08-12 08:20","");
INSERT INTO historique_etat(ref_document,id_document,id_etat,id_utilisateur,date_heure_etat,motif)VALUES("EN1100-20220605-1",1,6,78542,"2022-08-12 08:20","");

-- INSERT INTO historique_etat(ref_document,id_document,id_etat,id_utilisateur,date_heure_etat,motif)VALUES("EN1100-20221208-1",1,6,78542,"2022-08-13 08:20","");


INSERT INTO historique_etat(ref_document,id_document,id_etat,id_utilisateur,date_heure_etat,motif)VALUES("NA1100-20240515-1",1,1,80682,"2024-05-06 08:20","");
INSERT INTO historique_etat(ref_document,id_document,id_etat,id_utilisateur,date_heure_etat,motif)VALUES("NA1100-20240515-1",1,2,80682,"2024-05-15 08:20","");
INSERT INTO historique_etat(ref_document,id_document,id_etat,id_utilisateur,date_heure_etat,motif)VALUES("NA1100-20240515-1",1,6,80682,"2024-05-15 08:20","");

INSERT INTO historique_etat(ref_document,id_document,id_etat,id_utilisateur,date_heure_etat,motif)VALUES("PR1300-20230922-1",1,2,78542,"2023-09-19 08:20","");
INSERT INTO historique_etat(ref_document,id_document,id_etat,id_utilisateur,date_heure_etat,motif)VALUES("PR1300-20230922-1",1,4,80246,"2023-09-20 08:20","");
INSERT INTO historique_etat(ref_document,id_document,id_etat,id_utilisateur,date_heure_etat,motif)VALUES("PR1300-20230922-1",1,6,24566,"2023-09-22 10:20","");

INSERT INTO historique_etat(ref_document,id_document,id_etat,id_utilisateur,date_heure_etat,motif)VALUES("FI1300-20230211-1",1,2,80682,"2023-02-03 08:20","");
INSERT INTO historique_etat(ref_document,id_document,id_etat,id_utilisateur,date_heure_etat,motif)VALUES("FI1300-20230211-1",1,4,80246,"2023-02-08 09:20","");
INSERT INTO historique_etat(ref_document,id_document,id_etat,id_utilisateur,date_heure_etat,motif)VALUES("FI1300-20230211-1",1,6,24566,"2023-02-11 10:20","");

INSERT INTO historique_etat(ref_document,id_document,id_etat,id_utilisateur,date_heure_etat,motif)VALUES("EN1300-20220812-1",1,1,80246,"2022-08-12 08:00","");

INSERT INTO historique_etat(ref_document,id_document,id_etat,id_utilisateur,date_heure_etat,motif)VALUES("FI2100-20230908-1",1,2,80682,"2023-09-01 08:00","");
INSERT INTO historique_etat(ref_document,id_document,id_etat,id_utilisateur,date_heure_etat,motif)VALUES("FI2100-20230908-1",1,4,80246,"2023-09-04 08:00","");
INSERT INTO historique_etat(ref_document,id_document,id_etat,id_utilisateur,date_heure_etat,motif)VALUES("FI2100-20230908-1",1,6,24566,"2023-09-08 08:00","");
INSERT INTO historique_etat(ref_document,id_document,id_etat,id_utilisateur,date_heure_etat,motif)VALUES("FI2100-20230908-1",1,7,80682,"2023-11-20 17:00","Mise à jour partenaires");
INSERT INTO historique_etat(ref_document,id_document,id_etat,id_utilisateur,date_heure_etat,motif)VALUES("FI2100-20230908-1",1,8,24566,"2023-11-21 17:00","");
INSERT INTO historique_etat(ref_document,id_document,id_etat,id_utilisateur,date_heure_etat,motif)VALUES("FI2100-20230908-1",1,9,24566,"2024-01-15 08:00","");


INSERT INTO historique_etat(ref_document,id_document,id_etat,id_utilisateur,date_heure_etat,motif)VALUES("FI2100-20230908-1",2,2,80682,"2024-01-01 08:00","");
INSERT INTO historique_etat(ref_document,id_document,id_etat,id_utilisateur,date_heure_etat,motif)VALUES("FI2100-20230908-1",2,4,80246,"2024-01-12 08:00","");
INSERT INTO historique_etat(ref_document,id_document,id_etat,id_utilisateur,date_heure_etat,motif)VALUES("FI2100-20230908-1",2,6,24566,"2024-01-15 08:00","");



INSERT INTO redacteur_document(ref_document,id_document,id_utilisateur) VALUES ("PR1100-20240306-1",1,80682);
INSERT INTO redacteur_document(ref_document,id_document,id_utilisateur) VALUES ("FI1100-20220905-1",1,78542);

INSERT INTO redacteur_document(ref_document,id_document,id_utilisateur) VALUES ("EN1100-20220605-1",1,80682);
INSERT INTO redacteur_document(ref_document,id_document,id_utilisateur) VALUES ("EN1100-20220605-1",1,78542);

INSERT INTO redacteur_document(ref_document,id_document,id_utilisateur) VALUES ("NA1100-20240515-1",1,80682);
INSERT INTO redacteur_document(ref_document,id_document,id_utilisateur) VALUES ("PR1300-20230922-1",1,78542);
INSERT INTO redacteur_document(ref_document,id_document,id_utilisateur) VALUES ("FI1300-20230211-1",1,80682);

INSERT INTO redacteur_document(ref_document,id_document,id_utilisateur) VALUES ("EN1300-20220812-1",1,80246);
INSERT INTO redacteur_document(ref_document,id_document,id_utilisateur) VALUES ("EN1300-20220812-1",1,78542);

INSERT INTO redacteur_document(ref_document,id_document,id_utilisateur) VALUES ("FI2100-20230908-1",1,80682);
INSERT INTO redacteur_document(ref_document,id_document,id_utilisateur) VALUES ("FI2100-20230908-1",2,80682);

-- base rh

CREATE TABLE utilisateur(
    matricule INT,
    nom VARCHAR(50),
    prenom VARCHAR(150),
    fonction_poste TINYTEXT,
    service TINYTEXT,
    lieu_travail VARCHAR(50),
    email TINYTEXT,
    PRIMARY KEY(matricule)
);

INSERT INTO utilisateur(matricule,prenom,fonction_poste,service,lieu_travail) VALUES (80682,"Mika","Planificateur Logistique","Logistique","SIEGE");
INSERT INTO utilisateur(matricule,prenom,fonction_poste,service,lieu_travail) VALUES (80246,"Michou","Chef de service dépot","Logistique","SIEGE");
INSERT INTO utilisateur(matricule,prenom,fonction_poste,service,lieu_travail) VALUES (78542,"Rindra","Chef de service planification","Logistique","SIEGE");
INSERT INTO utilisateur(matricule,prenom,fonction_poste,service,lieu_travail) VALUES (24566,"Dominique","Directeur General","Finance et Gestion","SIEGE");


SELECT pg.id_processus_global,pg.nom 
FROM processus_global_document pgd 
JOIN processus_global pg ON pg.id_processus_global = pgd.id_processus_global 
WHERE pgd.ref_document = ? AND pgd.id_document = ?


SELECT *
FROM processus_lie_document pld 
JOIN processus_lie pl ON pl.id_processus_lie = pld.id_processus_lie 
WHERE pld.ref_document = "FI4000-20240917-002" AND pld.id_document = 1; 


-- ### vue ### --
SELECT he.id_histo,he.ref_document,he.id_document,dc.titre,et.nom,ut.prenom,he.date_heure_etat,he.motif
FROM base_iso.historique_etat he 
JOIN base_iso.etat_document et ON  he.id_etat = et.id_etat
JOIN base_rh.utilisateur ut ON he.id_utilisateur = ut.matricule
JOIN base_iso.document dc ON  he.ref_document = dc.ref_document AND he.id_document = dc.id_document
GROUP BY he.id_histo,he.ref_document,he.id_document,dc.titre,et.nom
ORDER BY date_heure_etat ASC


-- SELECT he.id_histo,he.ref_document,he.id_document,dc.titre,et.nom,ut.prenom,he.date_heure_etat,he.motif
-- FROM base_iso.historique_etat he 
-- JOIN base_iso.etat_document et ON  he.id_etat = et.id_etat
-- JOIN base_rh.utilisateur ut ON he.id_utilisateur = ut.matricule
-- JOIN base_iso.document dc ON  he.ref_document = dc.ref_document AND he.id_document = dc.id_document
-- WHERE he.ref_document = "FI2100-20230908-1"
-- ORDER BY he.date_heure_etat ASC

UPDATE historique_etat SET date_heure_etat = "2022-08-16 09:00" WHERE id_histo = "HE-20220831-001"

    SELECT MAX(id_histo)as id_histo,ref_document,id_document,MAX(date_heure_etat) AS date_plus_récente
    FROM historique_etat
    GROUP BY ref_document,id_document
    ORDER BY id_histo DESC

---- ### applicable ### ----
    

CREATE OR REPLACE VIEW v_etat_recent AS(
    SELECT MAX(id_histo)as id_histo,ref_document,id_document,MAX(date_heure_etat) AS date_plus_récente
    FROM historique_etat
    GROUP BY ref_document,id_document
    ORDER BY id_histo DESC
);

CREATE OR REPLACE VIEW v_nombre_revision AS(
    SELECT ver.ref_document,COUNT(*) AS nombre_revision 
    FROM v_etat_recent ver 
    JOIN historique_etat h1 
        ON h1.id_histo = ver.id_histo
    WHERE h1.id_etat = 9
    GROUP by ver.ref_document
);

CREATE OR REPLACE VIEW v_processus AS(
    SELECT pg.id_processus_global,pg.nom as nom_processus_global,pl.id_processus_lie,pl.nom as nom_processus_lie
    FROM processus_global pg 
    JOIN processus_lie pl
    ON pg.id_processus_global = pl.id_processus_global
);

CREATE OR REPLACE VIEW v_document AS(
    SELECT h1.id_histo,ver.ref_document,ver.id_document,dc.titre,h1.id_etat as etat,h1.date_heure_etat,dc.confidentiel,COALESCE(vnb.nombre_revision,0) as nombre_revision,
    CASE
        WHEN h1.id_etat = 8 THEN 1
        ELSE 0
    END as modifiable,
    CASE
        WHEN h1.id_etat = ed.id_etat THEN ed.status
    END as status
    FROM historique_etat h1
    JOIN v_etat_recent ver 
        ON h1.id_histo = ver.id_histo
    JOIN etat_document ed 
        ON ed.id_etat = h1.id_etat
    JOIN document dc 
        ON dc.ref_document = ver.ref_document AND dc.id_document = ver.id_document
    LEFT JOIN v_nombre_revision vnb 
        ON vnb.ref_document = ver.ref_document
);

CREATE OR REPLACE VIEW v_document_applicable AS(
    SELECT vd.ref_document,vd.id_document,dc.id_type,vd.titre,vd.etat,dc.date_mise_application,vd.confidentiel,vd.nombre_revision,vd.modifiable,vd.status
    FROM v_document vd 
    JOIN document dc
        ON dc.ref_document = vd.ref_document AND dc.id_document = vd.id_document
    GROUP BY ref_document,id_document
    HAVING etat >= 6 AND etat <= 8
);

CREATE OR REPLACE VIEW v_document_en_cours AS (
    SELECT vd.ref_document,vd.id_document,dc.id_type,vd.titre,vd.etat,dc.date_creation,vd.confidentiel,vd.nombre_revision,vd.modifiable,vd.status
    FROM v_document vd 
    JOIN document dc
        ON dc.ref_document = vd.ref_document AND dc.id_document = vd.id_document
    GROUP BY ref_document,id_document
    HAVING etat < 6
);



CREATE OR REPLACE VIEW v_document_en_cours_owner AS(
    SELECT vd.ref_document,vd.id_document,vd.titre,vd.etat,vd.date_creation,vd.confidentiel,vd.nombre_revision,vd.modifiable,vd.status,
        CASE 
            WHEN vd.etat = 1 THEN rd.id_utilisateur
            WHEN vd.etat = 2 THEN dc.id_validateur
            WHEN vd.etat = 4 THEN dc.id_approbateur
        END AS owner
    FROM v_document_en_cours vd 
    JOIN document dc 
    ON dc.ref_document = vd.ref_document AND dc.id_document = vd.id_document
    LEFT JOIN redacteur_document rd 
    ON rd.ref_document = vd.ref_document AND rd.id_document =  vd.id_document
);






-- ### ||  Sandbox || ### --
SELECT vda.ref_document,vda.id_document,vda.titre,vda.etat,vda.date_mise_application,vda.confidentiel,vda.nombre_revision,vda.modifiable,vda.status,pld.id_processus_lie,vda.id_type
FROM v_document_applicable vda 
JOIN processus_lie_document pld ON pld.ref_document = vda.ref_document AND pld.id_document = vda.id_document 




SELECT pg.id_processus_global,pg.nom as nom_processus_global,pl.id_processus_lie,pl.nom as nom_processus_lie
FROM processus_global pg 
JOIN processus_lie pl
ON pg.id_processus_global = pl.id_processus_global



SELECT vda.id_type,td.nom,pld.id_processus_lie
FROM v_document_applicable vda  
LEFT JOIN processus_lie_document pld ON pld.ref_document = vda.ref_document AND pld.id_document = vda.id_document  
JOIN type_document td ON td.id_type = vda.id_type 
GROUP BY vda.id_type,td.nom






SELECT vda.ref_document,vda.id_document,vda.titre,vda.etat,vda.date_mise_application,vda.confidentiel,vda.nombre_revision,vda.modifiable,vda.status , pld.id_processus_lie , vda.id_type
FROM v_document_applicable vda  
JOIN processus_lie_document pld ON pld.ref_document = vda.ref_document AND pld.id_document = vda.id_document 
WHERE pld.id_processus_lie = 1100 AND vda.id_type = ?

















