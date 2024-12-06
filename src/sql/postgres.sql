CREATE DATABASE base_iso;
\c base_iso;

CREATE TABLE PROCESSUS_GLOBAL(
    id_processus_global INTEGER NOT NULL PRIMARY KEY,
    nom VARCHAR(90)
);

CREATE TABLE PROCESSUS_LIE(
    id_processus_lie INTEGER NOT NULL PRIMARY KEY,
    id_processus_global INTEGER NOT NULL,
    nom TEXT,
    FOREIGN KEY(id_processus_global) REFERENCES processus_global(id_processus_global) 
);

CREATE TABLE ETAT_DOCUMENT(
    id_etat SERIAL PRIMARY KEY,
    nom VARCHAR(50),
    status VARCHAR(30)
);

CREATE TABLE TYPE_DOCUMENT(
    id_type SERIAL PRIMARY KEY,
    nom VARCHAR(90)
);

-- ### connection base rh ### --

SELECT dblink_connect('base_rh_connection','dbname=base_rh port=5432 host=localhost user=postgres password=root');

-- SELECT dblink_disconnect('base_rh_connection');

-- ### document ### --
CREATE VIEW v_utilisateur AS(
    SELECT *
    FROM dblink('base_rh_connection','SELECT matricule,nom,prenom,fonction_poste,service,lieu_travail,email FROM UTILISATEUR')
    AS tb2(matricule int,nom varchar(50),prenom varchar(150),fonction_poste text,service text,lieu_travail varchar(50),email text)
);

CREATE TABLE DOCUMENT(
    ref_document VARCHAR(80),
    id_document INT,
    titre TEXT,
    id_type INT NOT NULL,
    id_entete INT,
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
    FOREIGN KEY(ref_document,id_document) REFERENCES document(ref_document,id_document),
    FOREIGN KEY(id_processus_global) REFERENCES processus_global(id_processus_global)
);

CREATE TABLE PROCESSUS_LIE_DOCUMENT(
    ref_document VARCHAR(80),
    id_document INT NOT NULL,
    id_processus_lie INT NOT NULL,
    FOREIGN KEY(ref_document,id_document) REFERENCES document(ref_document,id_document),
    FOREIGN KEY(id_processus_lie) REFERENCES processus_lie(id_processus_lie)
);

CREATE TABLE HISTORIQUE_ETAT(
    id_histo VARCHAR(80) PRIMARY KEY,
    ref_document VARCHAR(80),
    id_document INT NOT NULL,
    id_etat INT NOT NULL,
    id_utilisateur BIGINT NOT NULL,
    date_heure_etat TIMESTAMP,
    motif TEXT,
    FOREIGN KEY(ref_document,id_document) REFERENCES document(ref_document,id_document),
    FOREIGN KEY(id_etat) REFERENCES etat_document(id_etat)
);

-- ### document/personne ### --

-- CREATE TABLE PILOTE_DOCUMENT(
--     ref_document VARCHAR(80),
--     id_document INT NOT NULL,
--     id_utilisateur BIGINT NOT NULL,
--     FOREIGN KEY(ref_document,id_document) REFERENCES document(ref_document,id_document)
-- );

CREATE TABLE DIFFUSION_EMAIL(
    ref_document VARCHAR(80),
    id_document INT NOT NULL,
    id_utilisateur BIGINT NOT NULL,
    FOREIGN KEY(ref_document,id_document) REFERENCES document(ref_document,id_document)
);

CREATE TABLE LECTEUR_DOCUMENT(
    ref_document VARCHAR(80),
    id_document INT NOT NULL,
    id_utilisateur BIGINT NOT NULL,
    FOREIGN KEY(ref_document,id_document) REFERENCES document(ref_document,id_document)
);

CREATE TABLE REDACTEUR_DOCUMENT(
    ref_document VARCHAR(80),
    id_document INT NOT NULL,
    id_utilisateur BIGINT NOT NULL,
    FOREIGN KEY(ref_document,id_document) REFERENCES document(ref_document,id_document)
);

-- ### document/contenu ### --

CREATE TABLE CHAMP(
    ref_champ VARCHAR(90),
    nom VARCHAR(30),
    obligatoire BOOLEAN,
    PRIMARY KEY(ref_champ)
);

CREATE TABLE CHAMP_DOCUMENT(
    ref_document VARCHAR(90) NOT NULL,
    id_document INT NOT NULL,
    ref_champ VARCHAR(40), 
    valeur TEXT,
    FOREIGN KEY(ref_document,id_document) REFERENCES document(ref_document,id_document),
    FOREIGN KEY(ref_champ) REFERENCES champ(ref_champ)
);

CREATE TABLE FICHIER_DOCUMENT(
    ref_document VARCHAR(80) NOT NULL,
    id_document INT NOT NULL,
    chemin TEXT,
    nom TEXT,
    FOREIGN KEY(ref_document,id_document) REFERENCES document(ref_document,id_document)  
);

-- ## trigger ## --
    -- CREATE TRIGGER trg_check_approbateur_validateur
    -- BEFORE INSERT ON document
    -- FOR EACH ROW
    -- BEGIN
    -- IF NOT EXISTS (SELECT 1 FROM v_utilisateur WHERE matricule = NEW.id_approbateur) THEN
    --     RAISE EXCEPTION 'L''approbateur spécifié n''existe pas';
    -- END IF;

    -- IF NOT EXISTS (SELECT 1 FROM v_utilisateur WHERE matricule = NEW.id_validateur) THEN
    --     RAISE EXCEPTION 'Le validateur spécifié n''existe pas';
    -- END IF;
    -- RETURN NEW;
    -- END;


-- ## trigger generate reference on document ##--
    CREATE SEQUENCE seq_reference START WITH 1 INCREMENT BY 1;

    CREATE OR REPLACE FUNCTION generate_reference_document()
    RETURNS TRIGGER AS $$
    
    DECLARE last_id INT;
    DECLARE type_code TEXT;
    DECLARE process_id INT;
    DECLARE date_now TEXT;
    DECLARE document_rank INT;
    BEGIN

        IF NEW.ref_document IS NULL AND NEW.id_entete IS NOT NULL THEN
            type_code := SUBSTRING((SELECT UPPER(nom) FROM TYPE_DOCUMENT WHERE id_type = NEW.id_type), 1, 2);
            process_id := NEW.id_entete;
            date_now := TO_CHAR(NOW(), 'YYYYMMDD');
            document_rank := (SELECT COUNT(*)+1 FROM document WHERE DATE(date_creation) = CURRENT_DATE);
            NEW.ref_document := type_code || process_id || '-' || date_now || '-' || LPAD(document_rank::text, 2, '0');
        END IF;

        IF NEW.ref_document IS NULL AND NEW.id_entete IS NULL THEN
            type_code := SUBSTRING((SELECT UPPER(nom) FROM TYPE_DOCUMENT WHERE id_type = NEW.id_type), 1, 2);
            process_id := LPAD(nextval('seq_reference')::text, 4, '0');
            date_now := TO_CHAR(NOW(), 'YYYYMMDD');
            document_rank := (SELECT COUNT(*)+1 FROM document WHERE DATE(date_creation) = CURRENT_DATE);
            NEW.ref_document := type_code || process_id || '-' || date_now || '-' || LPAD(document_rank::text, 2, '0');
        END IF;

        SELECT MAX(id_document) INTO last_id
        FROM document
        WHERE ref_document = NEW.ref_document;

        IF last_id IS NOT NULL THEN
            NEW.id_document := last_id + 1;
        ELSE
            NEW.id_document := 1;
        END IF;

        RETURN NEW;
    END;
    $$ LANGUAGE plpgsql;

    CREATE TRIGGER trg_before_insert_document
    BEFORE INSERT ON document
    FOR EACH ROW
    EXECUTE FUNCTION generate_reference_document();

    SELECT LPAD(2::text, 4, '0')
    INSERT INTO DOCUMENT(titre,id_type,date_creation) VALUES ('AN-Traitement eau potable',2,CURRENT_DATE)
    
-- ## trigger generate reference on historique(prod)##--

    -- CREATE OR REPLACE FUNCTION generate_reference_historique()
    -- RETURNS TRIGGER AS $$
    -- DECLARE type_code TEXT;
    -- DECLARE date_now TEXT;
    -- DECLARE document_rank INT;
    -- BEGIN
    --     type_code := 'HE';
    --     date_now := TO_CHAR(NOW(), 'YYYYMMDD');
    --     document_rank := (SELECT COUNT(*) + 1 FROM historique_etat WHERE DATE(date_heure_etat) = CURRENT_DATE);
    --     NEW.id_histo := type_code || '-' || date_now || '-' || LPAD(document_rank::text, 3, '00');

    --     RETURN NEW;
    -- END;
    -- $$ LANGUAGE plpgsql;

    -- CREATE TRIGGER trg_before_insert_historique
    -- BEFORE INSERT ON historique_etat
    -- FOR EACH ROW
    -- EXECUTE FUNCTION generate_reference_historique();
    

-- ## trigger generate reference on historique(test)##--

    CREATE OR REPLACE FUNCTION generate_reference_historique()
    RETURNS TRIGGER AS $$
    DECLARE type_code TEXT;
    DECLARE date_now TEXT;
    DECLARE document_rank INT;
    BEGIN
        type_code := 'HE';
        date_now := TO_CHAR(NEW.date_heure_etat, 'YYYYMMDD');
        document_rank := (SELECT COUNT(*) + 1 FROM historique_etat WHERE DATE(date_heure_etat) = DATE(NEW.date_heure_etat));
        NEW.id_histo := type_code || '-' || date_now || '-' || LPAD(document_rank::text, 3, '00');

        RETURN NEW;
    END;
    $$ LANGUAGE plpgsql;

    CREATE TRIGGER trg_before_insert_historique
    BEFORE INSERT ON historique_etat
    FOR EACH ROW
    EXECUTE FUNCTION generate_reference_historique();
    



-- ## trigger verif utilisateur ##--
    -- CREATE OR REPLACE FUNCTION check_utilisateur(text_exception TEXT)
    -- RETURN TRIGGER AS $$
    -- BEGIN 
    --     IF NOT EXISTS(SELECT 1 FROM v_utilisateur WHERE matricule = NEW.id_utilisateur)THEN
    --         RAISE EXCEPTION text_exception;
    --     END IF;
    -- END;
    -- $$ LANGUAGE plpgsql;



    -- CREATE TRIGGER trg_check_pilote_document
    -- BEFORE INSERT ON pilote_document
    -- FOR EACH ROW
    -- EXECUTE FUNCTION check_utilisateur('Le pilote spécifié est introuvable.');

    -- CREATE TRIGGER trg_check_redacteur_document
    -- BEFORE INSERT ON redacteur_document
    -- FOR EACH ROW
    -- EXECUTE FUNCTION check_utilisateur('Le rédacteur spécifié est introuvable.');

    -- CREATE TRIGGER trg_check_lecteur_document
    -- BEFORE INSERT ON lecteur_document
    -- FOR EACH ROW
    -- EXECUTE FUNCTION check_utilisateur('Le lecteur spécifié est introuvable.');

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
INSERT INTO processus_lie(id_processus_lie,id_processus_global,nom) VALUES (2400,2000,'Systeme documentaire');
INSERT INTO processus_lie(id_processus_lie,id_processus_global,nom) VALUES (4111,4000,'Extraction matieres premieres');
INSERT INTO processus_lie(id_processus_lie,id_processus_global,nom) VALUES (4121,4000,'Cru blanc');
INSERT INTO processus_lie(id_processus_lie,id_processus_global,nom) VALUES (4122,4000,'Cru noir');
INSERT INTO processus_lie(id_processus_lie,id_processus_global,nom) VALUES (4130,4000,'Cuisson');

INSERT INTO processus_lie(id_processus_lie,id_processus_global,nom) VALUES (4140,4000,'Moulure Ciments');
INSERT INTO processus_lie(id_processus_lie,id_processus_global,nom) VALUES (4150,4000,'Chargement sacs');
INSERT INTO processus_lie(id_processus_lie,id_processus_global,nom) VALUES (4170,4000,'Exploitation silo');

INSERT INTO processus_lie(id_processus_lie,id_processus_global,nom) VALUES (5300,5000,'Maintenance');
INSERT INTO processus_lie(id_processus_lie,id_processus_global,nom) VALUES (5400,5000,'Gestion des dechets');
INSERT INTO processus_lie(id_processus_lie,id_processus_global,nom) VALUES (5500,5000,'Hygiene et securite');
INSERT INTO processus_lie(id_processus_lie,id_processus_global,nom) VALUES (5600,5000,'CSR');

INSERT INTO processus_lie(id_processus_lie,id_processus_global,nom) VALUES (5110,5100,'Achats biens et services');
INSERT INTO processus_lie(id_processus_lie,id_processus_global,nom) VALUES (5120,5100,'Comptabilite');
INSERT INTO processus_lie(id_processus_lie,id_processus_global,nom) VALUES (5130,5100,'Controle de gestion');
INSERT INTO processus_lie(id_processus_lie,id_processus_global,nom) VALUES (5140,5100,'Controle des stocks');
INSERT INTO processus_lie(id_processus_lie,id_processus_global,nom) VALUES (5150,5100,'Informatique');

INSERT INTO processus_lie(id_processus_lie,id_processus_global,nom) VALUES (3100,3000,'Demarche commerciale');
INSERT INTO processus_lie(id_processus_lie,id_processus_global,nom) VALUES (3200,3000,'Logistique');
INSERT INTO processus_lie(id_processus_lie,id_processus_global,nom) VALUES (3300,3000,'Suivi clientele');


INSERT INTO processus_lie(id_processus_lie,id_processus_global,nom) VALUES (6100,6000,'Identification des situations d''urgence et capacite a reagir');
INSERT INTO processus_lie(id_processus_lie,id_processus_global,nom) VALUES (6200,6000,'Revision des moyens de preventien et d''intervention');

INSERT INTO processus_lie(id_processus_lie,id_processus_global,nom) VALUES (9100,9000,'Reclamations');
INSERT INTO processus_lie(id_processus_lie,id_processus_global,nom) VALUES (9200,9000,'Actions Correctives/Actions Preventives');
INSERT INTO processus_lie(id_processus_lie,id_processus_global,nom) VALUES (9400,9000,'Mesures');
INSERT INTO processus_lie(id_processus_lie,id_processus_global,nom) VALUES (9410,9000,'Controle qualite');
INSERT INTO processus_lie(id_processus_lie,id_processus_global,nom) VALUES (9420,9000,'Metrologie');
INSERT INTO processus_lie(id_processus_lie,id_processus_global,nom) VALUES (9500,9000,'Audits');

INSERT INTO processus_lie(id_processus_lie,id_processus_global,nom) VALUES (9210,9200,'Maitrise du produit non-conforme');
INSERT INTO processus_lie(id_processus_lie,id_processus_global,nom) VALUES (9220,9200,'Maitrise des non-conformites environnementales');
INSERT INTO processus_lie(id_processus_lie,id_processus_global,nom) VALUES (9230,9200,'Maitrise des incidents / accidents');


INSERT INTO champ (ref_champ, nom) VALUES
('champMiseApplication', 'dateApplication'),
('champConfidentiel', 'confidentiel'),
('choixIso9001', 'iso9001'),
('choixIso14001', 'iso14001'),
('choixSecurite', 'securite'),
('choixSiteIso9001', 'siteIso9001'),
('choixSiteIso14001', 'siteIso14001'),
('choixSiteSecurite', 'siteSecurite'),
('choixProcessusGlobal', 'processusGlobal'),
('choixProcessusLie', 'processusLie'),
('champFinalite', 'finalite'),
('champDomaineApplication', 'domaineApplication'),
('choixPilote', 'piloteProcessus'),
('champConditionContrainte', 'conditionContrainte'),
('champDonneeEntre', 'donneeEntree'),
('champDonneeSortie', 'donneeSortie'),
('champProcessusAppelant', 'processusAppelant'),
('champProcessusAppele', 'procesussAppele'),
('choixDiffusionEmail', 'diffusionEmail'),
('choixDiffusionPapier', 'diffusionPapier'),
('choixRedacteur', 'redacteur'),
('choixVerificateur', 'verificateur'),
('choixApprobateur', 'approbateur'),
('champQuiRealise', 'quiRealise'),
('champQuiDecide', 'quiDecide'),
('champFaitQuoiDescription', 'quiFaitQuoi'),
('champLienMoyenDescription', 'lienMoyen'),
('champFaitQuoiCommentaire', 'quiFaitQuoiCommentaire'),
('champLienMoyenCommentaire', 'lienMoyenCommentaire'),
('champPerformanceAttendues', 'performanceAttendues'),
('champPropositionSurveillance', 'propositionSurveillance'),
('choixLecteur', 'lecteur'),
('champChampLibre', 'champLibre');


INSERT INTO document(ref_document,id_document,titre,id_type,confidentiel,date_creation,date_mise_application) VALUES ('NA1100-20240515-1',1,'Système de management environnemental',5,false,'2024-04-15','2024-05-15');
INSERT INTO document(ref_document,id_document,titre,id_type,confidentiel,date_creation,matricule_validateur,matricule_approbateur) VALUES ('PR1100-20240316-1',1,'Procédure de sécurisation des matières premières',1,false,'2024-02-16',80682,24566);
INSERT INTO document(ref_document,id_document,titre,id_type,confidentiel,date_creation,date_mise_application,matricule_validateur,matricule_approbateur) VALUES ('FI1100-20220905-1',1,'Gestion des changements',3,false,'2022-08-05','2022-09-05',80682,24566);
INSERT INTO document(ref_document,id_document,titre,id_type,confidentiel,date_creation,date_mise_application) VALUES ('EN1100-20220605-1',1,'Analyse des risques Ibity',4,false,'2022-05-05','2022-06-05');
INSERT INTO document(ref_document,id_document,titre,id_type,confidentiel,date_creation,date_mise_application) VALUES ('EN1100-20221208-1',1,'Analyse des risques dépôts',4,true,'2022-11-08','2022-12-08');
INSERT INTO document(ref_document,id_document,titre,id_type,confidentiel,date_creation,date_mise_application,matricule_validateur,matricule_approbateur) VALUES ('PR1300-20230922-1',1,'Communication',1,false,'2023-08-22','2023-09-22',78542,24566);
INSERT INTO document(ref_document,id_document,titre,id_type,confidentiel,date_creation,date_mise_application,matricule_validateur,matricule_approbateur) VALUES ('FI1300-20230211-1',1,'Demande de support en communication',3,false,'2023-01-11','2023-02-11',80246,24566);
INSERT INTO document(ref_document,id_document,titre,id_type,confidentiel,date_creation) VALUES ('EN1300-20220812-1',1,'Directive sur la communication',4,false,'2022-07-22');
INSERT INTO document(ref_document,id_document,titre,id_type,confidentiel,date_creation,date_mise_application,date_archive,matricule_validateur,matricule_approbateur) VALUES ('FI2100-20230908-1',1,'Déplacement par transport en commun de tout le personnel de Cementis(Madagascar) sur les axes Antsirabe - Tamatave - Majunga',3,false,'2023-08-08','2023-09-08','2023-12-15',78542,24566);
INSERT INTO document(ref_document,id_document,titre,id_type,confidentiel,date_creation,date_mise_application,matricule_validateur,matricule_approbateur) VALUES ('FI2100-20230908-1',2,'Déplacement par transport en commun de tout le personnel de Cementis(Madagascar) sur les axes Antsirabe - Tamatave - Majunga',3,true,'2023-12-15','2024-01-15',78542,24566);

INSERT INTO document(ref_document,id_document,titre,id_type,confidentiel,date_creation,date_mise_application,matricule_validateur,matricule_approbateur) VALUES ('FI2100-20230710-1',2,'Planification livraison logistique',2,false,'2023-07-05','2024-05-15',78542,24566);
INSERT INTO processus_global_document(ref_document,id_document,id_processus_global) VALUES ('FI2100-20230710-1',1,3000);
INSERT INTO processus_lie_document(ref_document,id_document,id_processus_lie) VALUES ('FI2100-20230710-1',1,3200);
INSERT INTO historique_etat(ref_document,id_document,id_etat,matricule_utilisateur,date_heure_etat)VALUES('FI2100-20230710-1',1,6,78542,'2024-05-15 10:20');
INSERT INTO historique_etat(ref_document,id_document,id_etat,matricule_utilisateur,date_heure_etat)VALUES('FI2100-20230710-1',1,8,78542,'2024-05-15 10:20');




INSERT INTO document(ref_document,id_document,titre,id_type,confidentiel,date_creation,date_mise_application,matricule_validateur,matricule_approbateur) VALUES ('FI4150-20241112-2',1,'Gestion des EPI',3,false,'2023-11-12','2024-11-18',78542,24566);
INSERT INTO processus_global_document(ref_document,id_document,id_processus_global) VALUES ('FI4150-20241112-2',1,4000);
INSERT INTO processus_lie_document(ref_document,id_document,id_processus_lie) VALUES ('FI4150-20241112-2',1,4150);
INSERT INTO processus_lie_document(ref_document,id_document,id_processus_lie) VALUES ('FI4150-20241112-2',1,4130);
INSERT INTO processus_lie_document(ref_document,id_document,id_processus_lie) VALUES ('FI4150-20241112-2',1,4111);
INSERT INTO historique_etat(ref_document,id_document,id_etat,matricule_utilisateur,date_heure_etat)VALUES('FI4150-20241112-2',1,6,78542,'2024-11-12 16:10');
INSERT INTO historique_etat(ref_document,id_document,id_etat,matricule_utilisateur,date_heure_etat)VALUES('FI4150-20241112-2',1,8,78542,'2024-11-12 16:10');




INSERT INTO processus_global_document(ref_document,id_document,id_processus_global) VALUES ('NA1100-20240515-1',1,1000);
INSERT INTO processus_global_document(ref_document,id_document,id_processus_global) VALUES ('PR1100-20240316-1',1,1000);
INSERT INTO processus_global_document(ref_document,id_document,id_processus_global) VALUES ('FI1100-20220905-1',1,1000);
INSERT INTO processus_global_document(ref_document,id_document,id_processus_global) VALUES ('EN1100-20220605-1',1,1000);
INSERT INTO processus_global_document(ref_document,id_document,id_processus_global) VALUES ('EN1100-20221208-1',1,1000);
INSERT INTO processus_global_document(ref_document,id_document,id_processus_global) VALUES ('PR1300-20230922-1',1,1000);
INSERT INTO processus_global_document(ref_document,id_document,id_processus_global) VALUES ('FI1300-20230211-1',1,1000);
INSERT INTO processus_global_document(ref_document,id_document,id_processus_global) VALUES ('EN1300-20220812-1',1,1000);
INSERT INTO processus_global_document(ref_document,id_document,id_processus_global) VALUES ('FI2100-20230908-1',1,2000);
INSERT INTO processus_global_document(ref_document,id_document,id_processus_global) VALUES ('FI2100-20230908-1',1,5000);
-- INSERT INTO processus_global_document(ref_document,id_document,id_processus_global) VALUES ('FI2100-20230908-1',2,5000);


INSERT INTO processus_lie_document(ref_document,id_document,id_processus_lie) VALUES ('NA1100-20240515-1',1,1100);
INSERT INTO processus_lie_document(ref_document,id_document,id_processus_lie) VALUES ('PR1100-20240316-1',1,1100);
INSERT INTO processus_lie_document(ref_document,id_document,id_processus_lie) VALUES ('FI1100-20220905-1',1,1100);
INSERT INTO processus_lie_document(ref_document,id_document,id_processus_lie) VALUES ('EN1100-20220605-1',1,1100);
INSERT INTO processus_lie_document(ref_document,id_document,id_processus_lie) VALUES ('EN1100-20221208-1',1,1100);
INSERT INTO processus_lie_document(ref_document,id_document,id_processus_lie) VALUES ('PR1300-20230922-1',1,1300);
INSERT INTO processus_lie_document(ref_document,id_document,id_processus_lie) VALUES ('FI1300-20230211-1',1,1300);
INSERT INTO processus_lie_document(ref_document,id_document,id_processus_lie) VALUES ('EN1300-20220812-1',1,1300);
INSERT INTO processus_lie_document(ref_document,id_document,id_processus_lie) VALUES ('FI2100-20230908-1',1,2100);
INSERT INTO processus_lie_document(ref_document,id_document,id_processus_lie) VALUES ('FI2100-20230908-1',1,5110);
INSERT INTO processus_lie_document(ref_document,id_document,id_processus_lie) VALUES ('FI2100-20230908-1',2,2100);
INSERT INTO processus_lie_document(ref_document,id_document,id_processus_lie) VALUES ('FI2100-20230908-1',2,5110);


INSERT INTO historique_etat(ref_document,id_document,id_etat,id_utilisateur,date_heure_etat,motif)VALUES('PR1100-20240316-1',1,1,80682,'2024-03-01 08:30','');
INSERT INTO historique_etat(ref_document,id_document,id_etat,id_utilisateur,date_heure_etat,motif)VALUES('PR1100-20240316-1',1,2,80682,'2024-03-03 08:20','');
INSERT INTO historique_etat(ref_document,id_document,id_etat,id_utilisateur,date_heure_etat,motif)VALUES('PR1100-20240316-1',1,4,80246,'2024-03-06 07:45','');
INSERT INTO historique_etat(ref_document,id_document,id_etat,id_utilisateur,date_heure_etat,motif)VALUES('PR1100-20240316-1',1,5,24566,'2024-03-06 12:30','Manques d''information');
INSERT INTO historique_etat(ref_document,id_document,id_etat,id_utilisateur,date_heure_etat,motif)VALUES('PR1100-20240316-1',1,1,80682,'2024-03-06 12:30','');
INSERT INTO historique_etat(ref_document,id_document,id_etat,id_utilisateur,date_heure_etat,motif)VALUES('PR1100-20240316-1',1,2,80682,'2024-03-10 10:20','');
-- INSERT INTO historique_etat(ref_document,id_document,id_etat,id_utilisateur,date_heure_etat,motif)VALUES('PR1100-20240316-1',1,4,80246,'2024-03-13 17:45','');
-- INSERT INTO historique_etat(ref_document,id_document,id_etat,id_utilisateur,date_heure_etat,motif)VALUES('PR1100-20240316-1',1,6,24566,'2024-03-16 08:00','');

INSERT INTO historique_etat(ref_document,id_document,id_etat,id_utilisateur,date_heure_etat,motif)VALUES('FI1100-20220905-1',1,2,78542,'2022-08-31 14:45','');
INSERT INTO historique_etat(ref_document,id_document,id_etat,id_utilisateur,date_heure_etat,motif)VALUES('FI1100-20220905-1',1,4,80682,'2022-09-02 08:20','');
INSERT INTO historique_etat(ref_document,id_document,id_etat,id_utilisateur,date_heure_etat,motif)VALUES('FI1100-20220905-1',1,6,24566,'2022-09-05 10:20','');
INSERT INTO historique_etat(ref_document,id_document,id_etat,id_utilisateur,date_heure_etat,motif)VALUES('FI1100-20220905-1',1,7,78542,'2022-10-17 10:20','Mise à jour');

INSERT INTO historique_etat(ref_document,id_document,id_etat,id_utilisateur,date_heure_etat,motif)VALUES('EN1100-20220605-1',1,2,78542,'2022-08-12 08:20','');
INSERT INTO historique_etat(ref_document,id_document,id_etat,id_utilisateur,date_heure_etat,motif)VALUES('EN1100-20220605-1',1,6,78542,'2022-08-12 08:20','');

-- INSERT INTO historique_etat(ref_document,id_document,id_etat,id_utilisateur,date_heure_etat,motif)VALUES('EN1100-20221208-1',1,6,78542,'2022-08-13 08:20','');

INSERT INTO historique_etat(ref_document,id_document,id_etat,id_utilisateur,date_heure_etat,motif)VALUES('NA1100-20240515-1',1,1,80682,'2024-05-06 08:20','');
INSERT INTO historique_etat(ref_document,id_document,id_etat,id_utilisateur,date_heure_etat,motif)VALUES('NA1100-20240515-1',1,2,80682,'2024-05-15 08:20','');
INSERT INTO historique_etat(ref_document,id_document,id_etat,id_utilisateur,date_heure_etat,motif)VALUES('NA1100-20240515-1',1,6,80682,'2024-05-15 08:20','');

INSERT INTO historique_etat(ref_document,id_document,id_etat,id_utilisateur,date_heure_etat,motif)VALUES('PR1300-20230922-1',1,2,78542,'2023-09-19 08:20','');
INSERT INTO historique_etat(ref_document,id_document,id_etat,id_utilisateur,date_heure_etat,motif)VALUES('PR1300-20230922-1',1,4,80246,'2023-09-20 08:20','');
INSERT INTO historique_etat(ref_document,id_document,id_etat,id_utilisateur,date_heure_etat,motif)VALUES('PR1300-20230922-1',1,6,24566,'2023-09-22 10:20','');

INSERT INTO historique_etat(ref_document,id_document,id_etat,id_utilisateur,date_heure_etat,motif)VALUES('FI1300-20230211-1',1,2,80682,'2023-02-03 08:20','');
INSERT INTO historique_etat(ref_document,id_document,id_etat,id_utilisateur,date_heure_etat,motif)VALUES('FI1300-20230211-1',1,4,80246,'2023-02-08 09:20','');
INSERT INTO historique_etat(ref_document,id_document,id_etat,id_utilisateur,date_heure_etat,motif)VALUES('FI1300-20230211-1',1,6,24566,'2023-02-11 10:20','');

INSERT INTO historique_etat(ref_document,id_document,id_etat,id_utilisateur,date_heure_etat,motif)VALUES('EN1300-20220812-1',1,1,80246,'2022-08-12 08:00','');

INSERT INTO historique_etat(ref_document,id_document,id_etat,id_utilisateur,date_heure_etat,motif)VALUES('FI2100-20230908-1',1,2,80682,'2023-09-01 08:00','');
INSERT INTO historique_etat(ref_document,id_document,id_etat,id_utilisateur,date_heure_etat,motif)VALUES('FI2100-20230908-1',1,4,80246,'2023-09-04 08:00','');
INSERT INTO historique_etat(ref_document,id_document,id_etat,id_utilisateur,date_heure_etat,motif)VALUES('FI2100-20230908-1',1,6,24566,'2023-09-08 08:00','');
INSERT INTO historique_etat(ref_document,id_document,id_etat,id_utilisateur,date_heure_etat,motif)VALUES('FI2100-20230908-1',1,7,80682,'2023-11-20 17:00','Mise à jour partenaires');
INSERT INTO historique_etat(ref_document,id_document,id_etat,id_utilisateur,date_heure_etat,motif)VALUES('FI2100-20230908-1',1,8,24566,'2023-11-21 17:00','');
INSERT INTO historique_etat(ref_document,id_document,id_etat,id_utilisateur,date_heure_etat,motif)VALUES('FI2100-20230908-1',1,9,24566,'2024-01-15 08:00','');

INSERT INTO historique_etat(ref_document,id_document,id_etat,id_utilisateur,date_heure_etat,motif)VALUES('FI2100-20230908-1',2,2,80682,'2024-01-01 08:00','');
INSERT INTO historique_etat(ref_document,id_document,id_etat,id_utilisateur,date_heure_etat,motif)VALUES('FI2100-20230908-1',2,4,80246,'2024-01-12 08:00','');
INSERT INTO historique_etat(ref_document,id_document,id_etat,id_utilisateur,date_heure_etat,motif)VALUES('FI2100-20230908-1',2,6,24566,'2024-01-15 08:00','');


INSERT INTO redacteur_document(ref_document,id_document,id_utilisateur) VALUES ('PR1100-20240306-1',1,80682);
INSERT INTO redacteur_document(ref_document,id_document,id_utilisateur) VALUES ('FI1100-20220905-1',1,78542);
INSERT INTO redacteur_document(ref_document,id_document,id_utilisateur) VALUES ('EN1100-20220605-1',1,80682);
INSERT INTO redacteur_document(ref_document,id_document,id_utilisateur) VALUES ('EN1100-20220605-1',1,78542);
INSERT INTO redacteur_document(ref_document,id_document,id_utilisateur) VALUES ('NA1100-20240515-1',1,80682);
INSERT INTO redacteur_document(ref_document,id_document,id_utilisateur) VALUES ('PR1300-20230922-1',1,78542);
INSERT INTO redacteur_document(ref_document,id_document,id_utilisateur) VALUES ('FI1300-20230211-1',1,80682);
INSERT INTO redacteur_document(ref_document,id_document,id_utilisateur) VALUES ('EN1300-20220812-1',1,80246);
INSERT INTO redacteur_document(ref_document,id_document,id_utilisateur) VALUES ('EN1300-20220812-1',1,78542);
INSERT INTO redacteur_document(ref_document,id_document,id_utilisateur) VALUES ('FI2100-20230908-1',1,80682);
INSERT INTO redacteur_document(ref_document,id_document,id_utilisateur) VALUES ('FI2100-20230908-1',2,80682);

-- ### vue ### --

SELECT he.id_histo,he.ref_document,he.id_document,dc.titre,et.nom,ut.prenom,he.date_heure_etat,he.motif
FROM historique_etat he 
JOIN etat_document et ON  he.id_etat = et.id_etat
JOIN v_utilisateur ut ON he.id_utilisateur = ut.matricule
JOIN document dc ON  he.ref_document = dc.ref_document AND he.id_document = dc.id_document
GROUP BY he.id_histo,he.ref_document,he.id_document,dc.titre,et.nom,ut.prenom,he.date_heure_etat,he.motif
ORDER BY date_heure_etat ASC

-- UPDATE historique_etat SET date_heure_etat = "2022-08-16 09:00" WHERE id_histo = "HE-20220831-001"


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
        WHEN h1.id_etat = 8 THEN true
        ELSE false
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
    GROUP BY vd.ref_document,vd.id_document,dc.id_type,vd.titre,vd.etat,dc.date_mise_application,vd.confidentiel,vd.nombre_revision,vd.modifiable,vd.status
    HAVING etat >= 6 AND etat <= 8
);

CREATE OR REPLACE VIEW v_document_en_cours AS (
    SELECT vd.ref_document,vd.id_document,dc.id_type,vd.titre,vd.etat,dc.date_creation,vd.confidentiel,vd.nombre_revision,vd.modifiable,vd.status
    FROM v_document vd 
    JOIN document dc
        ON dc.ref_document = vd.ref_document AND dc.id_document = vd.id_document
    GROUP BY vd.ref_document,vd.id_document,dc.id_type,vd.titre,vd.etat,dc.date_creation,vd.confidentiel,vd.nombre_revision,vd.modifiable,vd.status
    HAVING etat < 6
);

CREATE OR REPLACE VIEW v_document_en_cours_owner AS(
    SELECT vd.ref_document,vd.id_document,td.nom,vd.titre,vd.etat,vd.date_creation,vd.confidentiel,vd.nombre_revision,vd.modifiable,vd.status,
        CASE 
            WHEN vd.etat = 1 THEN rd.matricule_utilisateur
            WHEN vd.etat = 2 THEN dc.matricule_validateur
            WHEN vd.etat = 4 THEN dc.matricule_approbateur
        END AS owner
    FROM v_document_en_cours vd 
    JOIN document dc 
    ON dc.ref_document = vd.ref_document AND dc.id_document = vd.id_document
    LEFT JOIN redacteur_document rd 
    ON rd.ref_document = vd.ref_document AND rd.id_document =  vd.id_document
    JOIN type_document td ON td.id_type = dc.id_type
    GROUP BY vd.ref_document,vd.id_document,td.nom,vd.titre,vd.etat,vd.date_creation,vd.confidentiel,vd.nombre_revision,vd.modifiable,vd.status,owner
);


SELECT pld.ref_document,pld.id_document,pld.id_processus_lie
FROM v_document_en_cours_owner vde 
LEFT JOIN processus_lie_document pld ON pld.ref_document = vde.ref_document AND pld.id_document = vde.id_document 
WHERE owner = 80246


-- INSERT INTO processus_lie_document(ref_document,id_document,id_processus_lie) VALUES ('PR1100-20240316-1',1,2100);

-- INSERT INTO processus_lie_document(ref_document,id_document,id_processus_lie) VALUES ('FI2300-20240925-01',1,2300);


-- INSERT INTO historique_etat(ref_document,id_document,id_etat,id_utilisateur,date_heure_etat,motif)
-- VALUES ('PR1100-20240316-1',1,2,80246,CURRENT_TIMESTAMP,'')

-- SELECT 1 FROM redacteur_document WHERE ref_document = 'EN1300-20220812-1' AND id_document = 1 AND id_utilisateur = 80682


SELECT pld.ref_document,pld.id_document,pld.id_processus_lie
FROM v_document_en_cours_owner vde 
JOIN processus_lie_document pld ON pld.ref_document = vde.ref_document AND pld.id_document = vde.id_document
GROUP BY pld.ref_document,pld.id_document,pld.id_processus_lie




CREATE OR REPLACE VIEW v_document_archive AS(
    SELECT h1.id_histo,ver.ref_document,ver.id_document,dc.titre,h1.id_etat as etat,h1.date_heure_etat,dc.date_archive,COALESCE(vnb.nombre_revision,0) as nombre_revision,
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
    WHERE h1.id_etat = 9
);








drop view v_document,v_document_applicable,v_document_archive,v_document_en_cours,v_etat_recent,v_nombre_revision,v_processus;