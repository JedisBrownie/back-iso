CREATE DATABASE base_iso;
\c base_iso;

CREATE TABLE PROCESSUS_GLOBAL(
    id_processus_global INTEGER,
    nom VARCHAR(90),
    PRIMARY KEY(id_processus_global)
);

CREATE TABLE PROCESSUS_LIE(
    id_processus_lie INT,
    id_processus_global INTEGER NOT NULL,
    nom TEXT,
    PRIMARY KEY(id_processus_lie),
    FOREIGN KEY(id_processus_global) REFERENCES processus_global(id_processus_global) 
);

CREATE TABLE ETAT_DOCUMENT(
    id_etat SERIAL,
    nom VARCHAR(50),
    status VARCHAR(30),
    PRIMARY KEY(id_etat)
);

CREATE TABLE TYPE_DOCUMENT(
    id_type SERIAL,
    nom VARCHAR(90),
    PRIMARY KEY(id_type)
);

-- ### document ### --


CREATE TABLE DOCUMENT(
    ref_document VARCHAR(80),
    id_document INT,
    titre TEXT,
    id_type INT NOT NULL,
    id_entete INT NOT NULL,
    date_creation DATE,
    date_mise_application DATE,
    date_archive DATE,
    confidentiel BOOLEAN,
    id_approbateur BIGINT,
    id_validateur BIGINT,
    PRIMARY KEY(ref_document,id_document),
    FOREIGN KEY(id_type) REFERENCES type_document(id_type)
);

CREATE TABLE PROCESSUS_GLOBAL_DOCUMENT(
    ref_document VARCHAR(80),
    id_document INT NOT NULL,
    id_processus_global INT NOT NULL,
    FOREIGN KEY(ref_document) REFERENCES document(ref_document),
    FOREIGN KEY(id_document) REFERENCES document(id_document),
    FOREIGN KEY(id_processus_global) REFERENCES processus_global(id_processus_global)
);

CREATE TABLE PROCESSUS_LIE_DOCUMENT(
    ref_document VARCHAR(80),
    id_document INT NOT NULL,
    id_processus_lie INT NOT NULL,
    FOREIGN KEY(ref_document) REFERENCES document(ref_document),
    FOREIGN KEY(id_document) REFERENCES document(id_document),
    FOREIGN KEY(id_processus_lie) REFERENCES processus_lie(id_processus_lie)
);

CREATE TABLE HISTORIQUE_ETAT(
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

-- ### données test ### --

INSERT INTO processus_global(id_processus_global,nom) VALUES (1000,'Processus Management');
INSERT INTO processus_global(id_processus_global,nom) VALUES (2000,'Ressources');
INSERT INTO processus_global(id_processus_global,nom) VALUES (3000,'Processus Commercial');
INSERT INTO processus_global(id_processus_global,nom) VALUES (4000,'Processus Production');
INSERT INTO processus_global(id_processus_global,nom) VALUES (5000,'Activités Supports');
INSERT INTO processus_global(id_processus_global,nom) VALUES (5100,'Direction Administrative et financière');
INSERT INTO processus_global(id_processus_global,nom) VALUES (6000,'Gestion de crise');
INSERT INTO processus_global(id_processus_global,nom) VALUES (9000,'Surveillance et mesures');
INSERT INTO processus_global(id_processus_global,nom) VALUES (9200,'Non Conformités');

INSERT INTO type_document(nom) VALUES ('Processus');
INSERT INTO type_document(nom) VALUES ('Sous Processus');
INSERT INTO type_document(nom) VALUES ('Fiche d''instruction');
INSERT INTO type_document(nom) VALUES ('Enregistrement');
INSERT INTO type_document(nom) VALUES ('Navigateur');

INSERT INTO etat_document(nom,status) VALUES ('Brouillon','Brouillon');
INSERT INTO etat_document(nom,status) VALUES ('Redaction','En cours de vérification');
INSERT INTO etat_document(nom,status) VALUES ('Invalidation','Non validé');
INSERT INTO etat_document(nom,status) VALUES ('Validation','En cours d''approbation');
INSERT INTO etat_document(nom,status) VALUES ('Désapprobation','Non approuvé');
INSERT INTO etat_document(nom,status) VALUES ('Approbation','Applicable');
INSERT INTO etat_document(nom,status) VALUES ('Demande révision','Applicable');
INSERT INTO etat_document(nom,status) VALUES ('Modifiable','Applicable');
INSERT INTO etat_document(nom,status) VALUES ('Archives','Archive');

INSERT INTO processus_lie(id_processus_lie,id_processus_global,nom) VALUES (1100,1000,'Planification');
INSERT INTO processus_lie(id_processus_lie,id_processus_global,nom) VALUES (1200,1000,'Revue de direction');
INSERT INTO processus_lie(id_processus_lie,id_processus_global,nom) VALUES (1300,1000,'Communication');
INSERT INTO processus_lie(id_processus_lie,id_processus_global,nom) VALUES (2100,2000,'Ressources humaines');
INSERT INTO processus_lie(id_processus_lie,id_processus_global,nom) VALUES (2300,2000,'Travaux neufs');
INSERT INTO processus_lie(id_processus_lie,id_processus_global,nom) VALUES (2400,2000,'Système documentaire');
INSERT INTO processus_lie(id_processus_lie,id_processus_global,nom) VALUES (4111,4000,'Extraction matières premières');
INSERT INTO processus_lie(id_processus_lie,id_processus_global,nom) VALUES (4121,4000,'Cru blanc');
INSERT INTO processus_lie(id_processus_lie,id_processus_global,nom) VALUES (4122,4000,'Cru noir');
INSERT INTO processus_lie(id_processus_lie,id_processus_global,nom) VALUES (4130,4000,'Cuisson');
INSERT INTO processus_lie(id_processus_lie,id_processus_global,nom) VALUES (5110,5000,'Achats biens et services');


CREATE VIEW v_utilisateur AS(
    SELECT *
    FROM dblink('base_rh_connection','SELECT matricule,nom,prenom,fonction_poste,service,lieu_travail,email FROM UTILISATEUR')
    AS tb2(matricule int,nom varchar(50),prenom varchar(150),fonction_poste text,service text,lieu_travail varchar(50),email text)
);

-- ## trigger ## --
CREATE OR REPLACE FUNCTION check_approbateur_validateur()
RETURNS TRIGGER AS $$
BEGIN
  IF NOT EXISTS (SELECT 1 FROM v_utilisateur WHERE id = NEW.id_approbateur) THEN
    RAISE EXCEPTION 'L''approbateur spécifié n''existe pas dans la vue v_utilisateur';
  END IF;

  IF NOT EXISTS (SELECT 1 FROM v_utilisateur WHERE id = NEW.id_validateur) THEN
    RAISE EXCEPTION 'Le validateur spécifié n''existe pas dans la vue v_utilisateur';
  END IF;

  RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trg_check_approbateur_validateur_before_insert
BEFORE INSERT ON document
FOR EACH ROW
EXECUTE FUNCTION check_approbateur_validateur();


-- ## trigger before insert on document ##--
CREATE TRIGGER before_insert_document
BEFORE INSERT ON documents
FOR EACH ROW
BEGIN
    DECLARE last_id INT;
    DECLARE type_code TEXT;
    DECLARE process_id INT;
    DECLARE current_date TEXT;
    DECLARE document_rank INT;

    IF NEW.ref_document IS NULL THEN
        type_code := SUBSTRING(NEW.type_document, 1, 2);
        process_id := NEW.id_processus;
        current_date := TO_CHAR(NOW(), 'YYYYMMDD');
        document_rank := (SELECT COUNT(*) FROM documents WHERE DATE(created_at) = CURRENT_DATE);
        NEW.ref_document := type_code || process_id || '-' || current_date || '-' || LPAD(document_rank::text, 2, '0');
    END IF;

    SELECT MAX(id_document) INTO last_id
    FROM documents
    WHERE ref_document = NEW.ref_document;

    IF last_id IS NOT NULL THEN
        NEW.id_document := last_id + 1;
    ELSE
        NEW.id_document := 1;
    END IF;
END;