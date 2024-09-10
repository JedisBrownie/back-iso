CREATE DATABASE base_iso;
USE base_iso;

CREATE TABLE processus_global(
    id_processus_global,
    nom VARCHAR(90),
    PRIMARY KEY(id_processus_global)
);

CREATE TABLE processus_lie(
    id_processus_lie INTEGER
    nom TEXT,
    PRIMARY KEY(id_processus_lie)
);

CREATE TABLE champ(
    id_champ INT NOT NULL AUTO_INCREMENT,
    nom VARCHAR(30),
    obligatoire TINYINT(1)
    PRIMARY KEY(id_champ)
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

CREATE TABLE historique_etat(
    id_histo INT NOT NULL AUTO_INCREMENT,
    id_document INT NOT NULL,
    id_etat INT NOT NULL,
    id_utilisateur BIGINT NOT NULL,
    date_heure_etat TIMESTAMP,
    motif TEXT,
);

-- -- document

CREATE TABLE document(
    ref_document BIGINT NOT NULL,
    id_document INT NOT NULL AUTO_INCREMENT,
    id_processus_global INT NOT NULL,
    id_processus_lie INT NOT NULL,
    id_type INT NOT NULL,
    date_mise_application DATE,
    confidentiel INT,
    id_approbateur BIGINT,
    id_validateur BIGINT,
    PRIMARY KEY(ref_document,id_document),
    FOREIGN KEY(id_processus_global) REFERENCES processus_global(id_processus_global),
    FOREIGN KEY(id_processus_lie)REFERENCES processus_lie(id_processus_lie),
    FOREIGN KEY(id_type) REFERENCES type_document(id_type)
);

CREATE TABLE archive_document(
    id_archive BIGINT NOT NULL,
    ref_document BIGINT NOT NULL,
    id_document INT NOT NULL,
    id_type INT,
    date_mise_application DATE,
    date_archivage DATE,
    motif_revision TEXT
);




-- CREATE TABLE fichier_document_archive(
--     id_archive BIGINT NOT NULL,
--     emplacement_archive TEXT,
--     nom TEXT
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
--     id_document INT NOT NULL,
--     emplacement TEXT,
--     nom TEXT,
--     FOREIGN KEY(id_document) REFERENCES document(id_document)
-- );
