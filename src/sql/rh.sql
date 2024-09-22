CREATE DATABASE base_rh;
\c base_rh;

CREATE TABLE UTILISATEUR(
    matricule INT,
    nom VARCHAR(50),
    prenom VARCHAR(150),
    fonction_poste TEXT,
    service TEXT,
    lieu_travail VARCHAR(50),
    email TEXT,
    PRIMARY KEY(matricule)
);

INSERT INTO utilisateur(matricule,prenom,fonction_poste,service,lieu_travail) VALUES (80682,'Mika','Planificateur Logistique','Logistique','SIEGE');
INSERT INTO utilisateur(matricule,prenom,fonction_poste,service,lieu_travail) VALUES (80246,'Michou','Chef de service dépot','Logistique','SIEGE');
INSERT INTO utilisateur(matricule,prenom,fonction_poste,service,lieu_travail) VALUES (78542,'Rindra','Chef de service planification','Logistique','SIEGE');
INSERT INTO utilisateur(matricule,prenom,fonction_poste,service,lieu_travail) VALUES (24566,'Dominique','Directeur General','Finance et Gestion','SIEGE');
INSERT INTO utilisateur(matricule,prenom,fonction_poste,service,lieu_travail) VALUES (47895,'Aina','Ministre de l''humour','Finance et Gestion','SIEGE');


-- dblink atao ao am base iso
SELECT dblink_connect
('base_rh_connection','dbname=base_rh port=5432 host=localhost user=postgres password=root');

SELECT dblink_disconnect('base_rh_connection');

