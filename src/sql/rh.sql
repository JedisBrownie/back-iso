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

CREATE EXTENSION postgres_fdw;
CREATE SERVER fdw_server FOREIGN DATA WRAPPER postgres_fdw OPTIONS(host'localhost',dbname'base_rh',port'5432');
CREATE USER MAPPING FOR postgres SERVER fdw_server OPTIONS (user'postgres',password'root');
IMPORT FOREIGN SCHEMA public FROM SERVER fdw_server INTO public;
