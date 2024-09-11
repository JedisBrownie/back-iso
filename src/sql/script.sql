CREATE DATABASE base_iso;
USE base_iso;

CREATE TABLE processus_global(
    id_processus_global INTEGER,
    nom VARCHAR(90),
    PRIMARY KEY(id_processus_global)
);

CREATE TABLE processus_lie(
    id_processus_lie INTEGER,
    nom TEXT,
    PRIMARY KEY(id_processus_lie)
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
    id_document INT NOT NULL,
    id_processus_global INT NOT NULL,
    id_processus_lie INT NOT NULL,
    id_type INT NOT NULL,
    date_mise_application DATE,
    confidentiel TINYINT(1),
    id_approbateur BIGINT,
    id_validateur BIGINT,
    PRIMARY KEY(ref_document,id_document),
    FOREIGN KEY(id_processus_global) REFERENCES processus_global(id_processus_global),
    FOREIGN KEY(id_processus_lie)REFERENCES processus_lie(id_processus_lie),
    FOREIGN KEY(id_type) REFERENCES type_document(id_type)
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

CREATE TRIGGER before_insert_document
BEFORE INSERT ON documents
FOR EACH ROW
BEGIN
  DECLARE last_id INT;

  -- Générer la référence si elle n'existe pas
  IF NEW.ref_document IS NULL THEN
    SET @type := SUBSTRING(NEW.type_document, 1, 2);
    SET @processus_id := NEW.id_processus;
    SET @date := DATE_FORMAT(NOW(), '%Y%m%d');
    SET @rang := (SELECT COUNT(*) FROM documents WHERE DATE(created_at) = CURDATE());
    SET NEW.ref_document := CONCAT(@type, @processus_id, '-', @date, '-', LPAD(@rang, 2, '0'));
  END IF;

  -- Incrémenter l'ID en fonction de la référence
  SELECT MAX(id_document) INTO last_id
  FROM documents
  WHERE ref_document = NEW.ref_document;
  IF last_id IS NOT NULL THEN
    SET NEW.id_document := last_id + 1;
  ELSE
    SET NEW.id_document := 1;
  END IF;
END;




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
