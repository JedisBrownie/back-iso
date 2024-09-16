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
    confidentiel TINYINT(1),
    id_approbateur BIGINT,
    id_validateur BIGINT,
    PRIMARY KEY(ref_document,id_document),
    FOREIGN KEY(id_type) REFERENCES type_document(id_type)
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
    id_histo INT NOT NULL AUTO_INCREMENT,
    ref_document VARCHAR(80),
    id_document INT NOT NULL,
    id_etat INT NOT NULL,
    id_utilisateur BIGINT NOT NULL,
    date_heure_etat TIMESTAMP,
    motif TEXT,
    PRIMARY KEY (id_histo),
    FOREIGN KEY(ref_document) REFERENCES document(ref_document),
    FOREIGN KEY(id_document) REFERENCES document(id_document)
);

-- ### document/personne ### --

CREATE TABLE pilote_document(
    ref_document VARCHAR(80)
    id_document INT NOT NULL,
    id_utilisateur BIGINT NOT NULL,
    FOREIGN KEY(ref_document) REFERENCES document(ref_document),
    FOREIGN KEY(id_document) REFERENCES document(id_document)
);

CREATE TABLE diffusion_email(
    ref_document VARCHAR(80)
    id_document INT NOT NULL,
    id_utilisateur BIGINT NOT NULL,
    FOREIGN KEY(ref_document) REFERENCES document(ref_document),
    FOREIGN KEY(id_document) REFERENCES document(id_document)
);

CREATE TABLE lecteur_document(
    ref_document BIGINT NOT NULL,
    id_utilisateur BIGINT NOT NULL,
    FOREIGN KEY(ref_document) REFERENCES document(ref_document)
);

CREATE TABLE redacteur_document(
    ref_document BIGINT NOT NULL,
    id_utilisateur BIGINT NOT NULL,
    FOREIGN KEY(ref_document) REFERENCES document(ref_document)
);


-- ### document/contenu ### --

-- CREATE TABLE champ(
--     id_champ INT NOT NULL AUTO_INCREMENT,
--     nom VARCHAR(30),
--     obligatoire TINYINT(1)
--     PRIMARY KEY(id_champ)
-- );

-- CREATE TABLE champ_document(
--     id_document INT NOT NULL,
--     id_champ INT NOT NULL,
--     affichable INT, 
--     valeur TEXT,
--     FOREIGN KEY(id_document) REFERENCES document(id_document),
--     FOREIGN KEY(id_champ) REFERENCES champ(id_champ)
-- );

-- CREATE TABLE fichier_document(
--     ref_document BITINT NOT NULL,
--     id_document INT NOT NULL,
--     emplacement TEXT,
--     nom TEXT,
--     FOREIGN KEY(id_document) REFERENCES document(id_document),
--     FOREIGN KEY(ref_document) REFERENCES document(ref_document),     
-- );

-- ### trigger-reference-document ### --

DELIMITER $$
CREATE TRIGGER before_insert_document
BEFORE INSERT ON document
FOR EACH ROW
BEGIN
  DECLARE last_id INT;
  IF NEW.ref_document = '' THEN
    SET @type := SUBSTRING( (SELECT UPPER(nom) FROM type_document WHERE id_type = NEW.id_type), 1, 2);
    SET @processus_id := NEW.id_entete;
    SET @date := DATE_FORMAT(NOW(), '%Y%m%d');
    SET @rang := (SELECT COUNT(*)+1 FROM document WHERE DATE(date_creation) = CURDATE());
    SET NEW.ref_document := CONCAT(CAST(@type AS CHAR CHARACTER SET utf8), CAST(@processus_id AS CHAR CHARACTER SET utf8), '-',CAST(@date AS CHAR CHARACTER SET utf8), '-', CAST(LPAD(@rang, 3, '00') AS CHAR CHARACTER SET utf8));
  END IF;

  SELECT MAX(id_document) INTO last_id
  FROM document
  WHERE ref_document = NEW.ref_document;
  IF last_id IS NOT NULL THEN
    SET NEW.id_document := last_id + 1;
  ELSE
    SET NEW.id_document := 1;
  END IF;
END;
$$
DELIMITER;



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
INSERT INTO etat_document(nom) VALUES ("Rediger");
INSERT INTO etat_document(nom) VALUES ("Verifier");
INSERT INTO etat_document(nom) VALUES ("Approuver");
INSERT INTO etat_document(nom) VALUES ("Modifier");
INSERT INTO etat_document(nom) VALUES ("Archive");

INSERT INTO processus_lie(id_processus_lie,id_processus_global,nom) VALUES (1100,1000,"Planification");
INSERT INTO processus_lie(id_processus_lie,id_processus_global,nom) VALUES (1200,1000,"Revue de direction");
INSERT INTO processus_lie(id_processus_lie,id_processus_global,nom) VALUES (1300,1000,"Communication");


INSERT INTO document(titre,id_type,confidentiel,date_creation,id_entete) VALUES ("Inspection de dépôt",2,0,CURDATE(),2000);
INSERT INTO document(titre,id_type,confidentiel,date_creation,id_entete) VALUES ("Gestion matériel IT",3,0,CURDATE(),4000)


INSERT INTO document(ref_document,titre,id_type,confidentiel,date_creation,id_entete) VALUES ("SO2000-20240912-001","Inspection de dépôt",2,0,CURDATE(),2000)


SELECT CONCAT(SUBSTRING("SOUS PROCESSUS",1,2), 4600 , '-', DATE_FORMAT(NOW(), '%Y%m%d') , '-', LPAD(0, 3, '00')) AS REFERENCE;


INSERT INTO processus_global_document VALUES ("SO2000-20240912-001",1,2000);
INSERT INTO processus_global_document VALUES ("SO2000-20240912-001",1,1000);

INSERT INTO processus_global_document VALUES ("SO2000-20240912-001",2,5000);