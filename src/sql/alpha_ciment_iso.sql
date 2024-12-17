CREATE SEQUENCE etat_document_id_etat_seq START WITH 1 INCREMENT BY 1;
CREATE SEQUENCE type_document_id_type_seq START WITH 1 INCREMENT BY 1;

CREATE SEQUENCE seq_reference START WITH 1 INCREMENT BY 1;


CREATE  TABLE champ ( 
	ref_champ            varchar(90)  NOT NULL  ,
	nom                  varchar(30)    ,
	obligatoire          boolean    ,
	CONSTRAINT champ_pkey PRIMARY KEY ( ref_champ )
 );

CREATE  TABLE etat_document ( 
	id_etat              integer DEFAULT nextval('etat_document_id_etat_seq'::regclass) NOT NULL  ,
	nom                  varchar(50)    ,
	status               varchar(30)    ,
	CONSTRAINT etat_document_pkey PRIMARY KEY ( id_etat )
 );

CREATE  TABLE processus_global ( 
	id_processus_global  integer  NOT NULL  ,
	nom                  varchar(90)    ,
	CONSTRAINT processus_global_pkey PRIMARY KEY ( id_processus_global )
 );

CREATE  TABLE processus_lie ( 
	id_processus_lie     integer  NOT NULL  ,
	id_processus_global  integer  NOT NULL  ,
	nom                  text    ,
	CONSTRAINT processus_lie_pkey PRIMARY KEY ( id_processus_lie ),
	CONSTRAINT processus_lie_id_processus_global_fkey FOREIGN KEY ( id_processus_global ) REFERENCES processus_global( id_processus_global )   
 );

CREATE  TABLE type_document ( 
	id_type              integer DEFAULT nextval('type_document_id_type_seq'::regclass) NOT NULL  ,
	nom                  varchar(90)    ,
	CONSTRAINT type_document_pkey PRIMARY KEY ( id_type )
 );

CREATE  TABLE document ( 
	ref_document         varchar(80)  NOT NULL  ,
	id_document          integer  NOT NULL  ,
	titre                text    ,
	id_type              integer  NOT NULL  ,
	id_entete            integer    ,
	date_creation        date    ,
	date_mise_application date    ,
	date_archive         date    ,
	confidentiel         boolean    ,
	date_modification    timestamp(0)    ,
	CONSTRAINT document_pkey PRIMARY KEY ( ref_document, id_document ),
	CONSTRAINT document_id_type_fkey FOREIGN KEY ( id_type ) REFERENCES type_document( id_type )   
 );

CREATE  TABLE fichier_document ( 
	ref_document         varchar(80)  NOT NULL  ,
	id_document          integer  NOT NULL  ,
	fichier              text    ,
	CONSTRAINT fichier_document_ref_document_fkey FOREIGN KEY ( ref_document, id_document ) REFERENCES document( ref_document, id_document )   
 );

CREATE  TABLE historique_etat ( 
	id_histo             varchar(80)  NOT NULL  ,
	ref_document         varchar(80)    ,
	id_document          integer  NOT NULL  ,
	id_etat              integer  NOT NULL  ,
	matricule_utilisateur varchar(50)  NOT NULL  ,
	date_heure_etat      timestamp    ,
	motif                text    ,
	CONSTRAINT historique_etat_pkey PRIMARY KEY ( id_histo ),
	CONSTRAINT historique_etat_ref_document_fkey FOREIGN KEY ( ref_document, id_document ) REFERENCES document( ref_document, id_document )   ,
	CONSTRAINT historique_etat_id_etat_fkey FOREIGN KEY ( id_etat ) REFERENCES etat_document( id_etat )   
 );

CREATE  TABLE lecteur_document ( 
	ref_document         varchar(80)    ,
	id_document          integer  NOT NULL  ,
	matricule_utilisateur varchar(50)  NOT NULL  ,
	CONSTRAINT lecteur_document_ref_document_fkey FOREIGN KEY ( ref_document, id_document ) REFERENCES document( ref_document, id_document )   
 );

CREATE  TABLE processus_global_document ( 
	ref_document         varchar(80)    ,
	id_document          integer  NOT NULL  ,
	id_processus_global  integer  NOT NULL  ,
	CONSTRAINT processus_global_document_ref_document_fkey FOREIGN KEY ( ref_document, id_document ) REFERENCES document( ref_document, id_document )   ,
	CONSTRAINT processus_global_document_id_processus_global_fkey FOREIGN KEY ( id_processus_global ) REFERENCES processus_global( id_processus_global )   
 );

CREATE  TABLE processus_lie_document ( 
	ref_document         varchar(80)    ,
	id_document          integer  NOT NULL  ,
	id_processus_lie     integer  NOT NULL  ,
	CONSTRAINT processus_lie_document_ref_document_fkey FOREIGN KEY ( ref_document, id_document ) REFERENCES document( ref_document, id_document )   ,
	CONSTRAINT processus_lie_document_id_processus_lie_fkey FOREIGN KEY ( id_processus_lie ) REFERENCES processus_lie( id_processus_lie )   
 );

CREATE  TABLE redacteur_document ( 
	ref_document         varchar(80)    ,
	id_document          integer  NOT NULL  ,
	matricule_utilisateur varchar(50)  NOT NULL  ,
	CONSTRAINT redacteur_document_ref_document_fkey FOREIGN KEY ( ref_document, id_document ) REFERENCES document( ref_document, id_document )   
 );

CREATE  TABLE redaction_document ( 
	ref_document         varchar(80)  NOT NULL  ,
	id_document          integer  NOT NULL  ,
	matricule_utilisateur varchar(50)  NOT NULL  ,
	redaction_document_date date    ,
	CONSTRAINT fk_redaction_document_document FOREIGN KEY ( ref_document, id_document ) REFERENCES document( ref_document, id_document )   
 );

CREATE  TABLE validateur_document ( 
	ref_document         varchar(80)  NOT NULL  ,
	id_document          integer  NOT NULL  ,
	matricule_utilisateur varchar(50)  NOT NULL  ,
	CONSTRAINT fk_validateur_document FOREIGN KEY ( ref_document, id_document ) REFERENCES document( ref_document, id_document )   
 );

CREATE  TABLE validation_document ( 
	ref_document         varchar(80)  NOT NULL  ,
	id_document          integer  NOT NULL  ,
	matricule_utilisateur varchar(50)  NOT NULL  ,
	validation_document_date date    ,
	CONSTRAINT fk_validation_document FOREIGN KEY ( ref_document, id_document ) REFERENCES document( ref_document, id_document )   
 );

CREATE  TABLE approbateur_document ( 
	ref_document         varchar(80)  NOT NULL  ,
	id_document          integer  NOT NULL  ,
	marticule_utilisateur varchar(50)  NOT NULL  ,
	CONSTRAINT fk_approbateur_document FOREIGN KEY ( ref_document, id_document ) REFERENCES document( ref_document, id_document )   
 );

CREATE  TABLE approbation_document ( 
	ref_document         varchar(80)  NOT NULL  ,
	id_document          integer  NOT NULL  ,
	matricule_utilisateur varchar(50)  NOT NULL  ,
	approbation_document_date date    ,
	CONSTRAINT fk_approbation_document FOREIGN KEY ( ref_document, id_document ) REFERENCES document( ref_document, id_document )   
 );

CREATE  TABLE champ_document ( 
	ref_document         varchar(90)  NOT NULL  ,
	id_document          integer  NOT NULL  ,
	ref_champ            varchar(40)    ,
	valeur               text    ,
	CONSTRAINT champ_document_ref_champ_fkey FOREIGN KEY ( ref_champ ) REFERENCES champ( ref_champ )   ,
	CONSTRAINT champ_document_ref_document_fkey FOREIGN KEY ( ref_document, id_document ) REFERENCES document( ref_document, id_document )   
 );

CREATE  TABLE diffusion_email ( 
	ref_document         varchar(80)    ,
	id_document          integer  NOT NULL  ,
	matricule_utilisateur varchar(50)  NOT NULL  ,
	CONSTRAINT diffusion_email_ref_document_fkey FOREIGN KEY ( ref_document, id_document ) REFERENCES document( ref_document, id_document )   
 );

CREATE OR REPLACE FUNCTION public.generate_reference_document()
 RETURNS trigger
 LANGUAGE plpgsql
AS $function$
    
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
    $function$
;

CREATE OR REPLACE FUNCTION public.generate_reference_historique()
 RETURNS trigger
 LANGUAGE plpgsql
AS $function$
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
    $function$
;

CREATE VIEW "public".v_document AS  SELECT h1.id_histo,
    ver.ref_document,
    ver.id_document,
    dc.titre,
    h1.id_etat AS etat,
    h1.date_heure_etat,
    dc.confidentiel,
    COALESCE(vnb.nombre_revision, (0)::bigint) AS nombre_revision,
        CASE
            WHEN (h1.id_etat = 8) THEN true
            ELSE false
        END AS modifiable,
        CASE
            WHEN (h1.id_etat = ed.id_etat) THEN ed.status
            ELSE NULL::character varying
        END AS status
   FROM ((((historique_etat h1
     JOIN v_etat_recent ver ON (((h1.id_histo)::text = ver.id_histo)))
     JOIN etat_document ed ON ((ed.id_etat = h1.id_etat)))
     JOIN document dc ON ((((dc.ref_document)::text = (ver.ref_document)::text) AND (dc.id_document = ver.id_document))))
     LEFT JOIN v_nombre_revision vnb ON (((vnb.ref_document)::text = (ver.ref_document)::text)));

CREATE VIEW "public".v_document_applicable AS  SELECT vd.ref_document,
    vd.id_document,
    dc.id_type,
    vd.titre,
    vd.etat,
    dc.date_mise_application,
    vd.confidentiel,
    vd.nombre_revision,
    vd.modifiable,
    vd.status
   FROM (v_document vd
     JOIN document dc ON ((((dc.ref_document)::text = (vd.ref_document)::text) AND (dc.id_document = vd.id_document))))
  GROUP BY vd.ref_document, vd.id_document, dc.id_type, vd.titre, vd.etat, dc.date_mise_application, vd.confidentiel, vd.nombre_revision, vd.modifiable, vd.status
 HAVING ((vd.etat >= 6) AND (vd.etat <= 8));

CREATE VIEW "public".v_document_archive AS  SELECT h1.id_histo,
    ver.ref_document,
    ver.id_document,
    dc.titre,
    h1.id_etat AS etat,
    h1.date_heure_etat,
    dc.date_archive,
    COALESCE(vnb.nombre_revision, (0)::bigint) AS nombre_revision,
        CASE
            WHEN (h1.id_etat = ed.id_etat) THEN ed.status
            ELSE NULL::character varying
        END AS status
   FROM ((((historique_etat h1
     JOIN v_etat_recent ver ON (((h1.id_histo)::text = ver.id_histo)))
     JOIN etat_document ed ON ((ed.id_etat = h1.id_etat)))
     JOIN document dc ON ((((dc.ref_document)::text = (ver.ref_document)::text) AND (dc.id_document = ver.id_document))))
     LEFT JOIN v_nombre_revision vnb ON (((vnb.ref_document)::text = (ver.ref_document)::text)))
  WHERE (h1.id_etat = 9);

CREATE VIEW "public".v_document_en_cours AS  SELECT vd.ref_document,
    vd.id_document,
    dc.id_type,
    vd.titre,
    vd.etat,
    dc.date_creation,
    vd.confidentiel,
    vd.nombre_revision,
    vd.modifiable,
    vd.status
   FROM (v_document vd
     JOIN document dc ON ((((dc.ref_document)::text = (vd.ref_document)::text) AND (dc.id_document = vd.id_document))))
  GROUP BY vd.ref_document, vd.id_document, dc.id_type, vd.titre, vd.etat, dc.date_creation, vd.confidentiel, vd.nombre_revision, vd.modifiable, vd.status
 HAVING (vd.etat < 6);

CREATE VIEW "public".v_etat_recent AS  SELECT max((historique_etat.id_histo)::text) AS id_histo,
    historique_etat.ref_document,
    historique_etat.id_document,
    max(historique_etat.date_heure_etat) AS "date_plus_r‚cente"
   FROM historique_etat
  GROUP BY historique_etat.ref_document, historique_etat.id_document
  ORDER BY (max((historique_etat.id_histo)::text)) DESC;

CREATE VIEW "public".v_nombre_revision AS  SELECT ver.ref_document,
    count(*) AS nombre_revision
   FROM (v_etat_recent ver
     JOIN historique_etat h1 ON (((h1.id_histo)::text = ver.id_histo)))
  WHERE (h1.id_etat = 9)
  GROUP BY ver.ref_document;

CREATE VIEW "public".v_processus AS  SELECT pg.id_processus_global,
    pg.nom AS nom_processus_global,
    pl.id_processus_lie,
    pl.nom AS nom_processus_lie
   FROM (processus_global pg
     JOIN processus_lie pl ON ((pg.id_processus_global = pl.id_processus_global)));

CREATE TRIGGER trg_before_insert_document BEFORE INSERT ON public.document FOR EACH ROW EXECUTE PROCEDURE generate_reference_document();

CREATE TRIGGER trg_before_insert_historique BEFORE INSERT ON public.historique_etat FOR EACH ROW EXECUTE PROCEDURE generate_reference_historique();

INSERT INTO champ( ref_champ, nom, obligatoire ) VALUES ( 'champMiseApplication', 'dateApplication', false);
INSERT INTO champ( ref_champ, nom, obligatoire ) VALUES ( 'champConfidentiel', 'confidentiel', false);
INSERT INTO champ( ref_champ, nom, obligatoire ) VALUES ( 'choixIso9001', 'iso9001', false);
INSERT INTO champ( ref_champ, nom, obligatoire ) VALUES ( 'choixIso14001', 'iso14001', false);
INSERT INTO champ( ref_champ, nom, obligatoire ) VALUES ( 'choixSecurite', 'securite', false);
INSERT INTO champ( ref_champ, nom, obligatoire ) VALUES ( 'choixSiteIso9001', 'siteIso9001', false);
INSERT INTO champ( ref_champ, nom, obligatoire ) VALUES ( 'choixSiteIso14001', 'siteIso14001', false);
INSERT INTO champ( ref_champ, nom, obligatoire ) VALUES ( 'choixSiteSecurite', 'siteSecurite', false);
INSERT INTO champ( ref_champ, nom, obligatoire ) VALUES ( 'choixProcessusGlobal', 'processusGlobal', false);
INSERT INTO champ( ref_champ, nom, obligatoire ) VALUES ( 'choixProcessusLie', 'processusLie', false);
INSERT INTO champ( ref_champ, nom, obligatoire ) VALUES ( 'champFinalite', 'finalite', false);
INSERT INTO champ( ref_champ, nom, obligatoire ) VALUES ( 'champDomaineApplication', 'domaineApplication', false);
INSERT INTO champ( ref_champ, nom, obligatoire ) VALUES ( 'choixPilote', 'piloteProcessus', false);
INSERT INTO champ( ref_champ, nom, obligatoire ) VALUES ( 'champConditionContrainte', 'conditionContrainte', false);
INSERT INTO champ( ref_champ, nom, obligatoire ) VALUES ( 'champDonneeEntre', 'donneeEntree', false);
INSERT INTO champ( ref_champ, nom, obligatoire ) VALUES ( 'champDonneeSortie', 'donneeSortie', false);
INSERT INTO champ( ref_champ, nom, obligatoire ) VALUES ( 'champProcessusAppelant', 'processusAppelant', false);
INSERT INTO champ( ref_champ, nom, obligatoire ) VALUES ( 'champProcessusAppele', 'procesussAppele', false);
INSERT INTO champ( ref_champ, nom, obligatoire ) VALUES ( 'choixDiffusionEmail', 'diffusionEmail', false);
INSERT INTO champ( ref_champ, nom, obligatoire ) VALUES ( 'choixDiffusionPapier', 'diffusionPapier', false);
INSERT INTO champ( ref_champ, nom, obligatoire ) VALUES ( 'choixRedacteur', 'redacteur', false);
INSERT INTO champ( ref_champ, nom, obligatoire ) VALUES ( 'choixVerificateur', 'verificateur', false);
INSERT INTO champ( ref_champ, nom, obligatoire ) VALUES ( 'choixApprobateur', 'approbateur', false);
INSERT INTO champ( ref_champ, nom, obligatoire ) VALUES ( 'champQuiRealise', 'quiRealise', false);
INSERT INTO champ( ref_champ, nom, obligatoire ) VALUES ( 'champQuiDecide', 'quiDecide', false);
INSERT INTO champ( ref_champ, nom, obligatoire ) VALUES ( 'champFaitQuoiDescription', 'quiFaitQuoi', false);
INSERT INTO champ( ref_champ, nom, obligatoire ) VALUES ( 'champLienMoyenDescription', 'lienMoyen', false);
INSERT INTO champ( ref_champ, nom, obligatoire ) VALUES ( 'champFaitQuoiCommentaire', 'quiFaitQuoiCommentaire', false);
INSERT INTO champ( ref_champ, nom, obligatoire ) VALUES ( 'champLienMoyenCommentaire', 'lienMoyenCommentaire', false);
INSERT INTO champ( ref_champ, nom, obligatoire ) VALUES ( 'champPerformanceAttendues', 'performanceAttendues', false);
INSERT INTO champ( ref_champ, nom, obligatoire ) VALUES ( 'champPropositionSurveillance', 'propositionSurveillance', false);
INSERT INTO champ( ref_champ, nom, obligatoire ) VALUES ( 'choixLecteur', 'lecteur', false);
INSERT INTO champ( ref_champ, nom, obligatoire ) VALUES ( 'champChampLibre', 'champLibre', false);
INSERT INTO champ( ref_champ, nom, obligatoire ) VALUES ( 'champTitre', 'titre', false);
INSERT INTO champ( ref_champ, nom, obligatoire ) VALUES ( 'champIndicateurEventuel', 'indicateurEventuel', false);
INSERT INTO champ( ref_champ, nom, obligatoire ) VALUES ( 'champEvenementFrequence', 'evenementFrequence', false);
INSERT INTO champ( ref_champ, nom, obligatoire ) VALUES ( 'champParticipant', 'participant', false);
INSERT INTO champ( ref_champ, nom, obligatoire ) VALUES ( 'champPointAbordes', 'pointAbordes', false);
INSERT INTO champ( ref_champ, nom, obligatoire ) VALUES ( 'champDocument', 'document', false);
INSERT INTO champ( ref_champ, nom, obligatoire ) VALUES ( 'champDocumentDeSupport', 'documentDeSupport', false);

INSERT INTO etat_document( id_etat, nom, status ) VALUES ( 1, 'Brouillon', 'Brouillon');
INSERT INTO etat_document( id_etat, nom, status ) VALUES ( 2, 'Redaction', 'En cours de verification');
INSERT INTO etat_document( id_etat, nom, status ) VALUES ( 3, 'Invalidation', 'Non valide');
INSERT INTO etat_document( id_etat, nom, status ) VALUES ( 4, 'Validation', 'En cours d''approbation');
INSERT INTO etat_document( id_etat, nom, status ) VALUES ( 5, 'D‚sapprobation', 'Non approuve');
INSERT INTO etat_document( id_etat, nom, status ) VALUES ( 6, 'Approbation', 'Applicable');
INSERT INTO etat_document( id_etat, nom, status ) VALUES ( 7, 'Demande r‚vision', 'Applicable');
INSERT INTO etat_document( id_etat, nom, status ) VALUES ( 8, 'Modifiable', 'Applicable');
INSERT INTO etat_document( id_etat, nom, status ) VALUES ( 9, 'Archives', 'Archive');

INSERT INTO processus_global( id_processus_global, nom ) VALUES ( 1000, 'Processus Management');
INSERT INTO processus_global( id_processus_global, nom ) VALUES ( 2000, 'Ressources');
INSERT INTO processus_global( id_processus_global, nom ) VALUES ( 3000, 'Processus Commercial');
INSERT INTO processus_global( id_processus_global, nom ) VALUES ( 4000, 'Processus Production');
INSERT INTO processus_global( id_processus_global, nom ) VALUES ( 5000, 'Activites Supports');
INSERT INTO processus_global( id_processus_global, nom ) VALUES ( 5100, 'Direction Administrative et financiere');
INSERT INTO processus_global( id_processus_global, nom ) VALUES ( 6000, 'Gestion de crise');
INSERT INTO processus_global( id_processus_global, nom ) VALUES ( 9000, 'Surveillance et mesures');
INSERT INTO processus_global( id_processus_global, nom ) VALUES ( 9200, 'Non Conformites');

INSERT INTO processus_lie( id_processus_lie, id_processus_global, nom ) VALUES ( 1100, 1000, 'Planification');
INSERT INTO processus_lie( id_processus_lie, id_processus_global, nom ) VALUES ( 1200, 1000, 'Revue de direction');
INSERT INTO processus_lie( id_processus_lie, id_processus_global, nom ) VALUES ( 1300, 1000, 'Communication');
INSERT INTO processus_lie( id_processus_lie, id_processus_global, nom ) VALUES ( 2100, 2000, 'Ressources humaines');
INSERT INTO processus_lie( id_processus_lie, id_processus_global, nom ) VALUES ( 2300, 2000, 'Travaux neufs');
INSERT INTO processus_lie( id_processus_lie, id_processus_global, nom ) VALUES ( 2400, 2000, 'Systeme documentaire');
INSERT INTO processus_lie( id_processus_lie, id_processus_global, nom ) VALUES ( 4111, 4000, 'Extraction matieres premieres');
INSERT INTO processus_lie( id_processus_lie, id_processus_global, nom ) VALUES ( 4121, 4000, 'Cru blanc');
INSERT INTO processus_lie( id_processus_lie, id_processus_global, nom ) VALUES ( 4122, 4000, 'Cru noir');
INSERT INTO processus_lie( id_processus_lie, id_processus_global, nom ) VALUES ( 4130, 4000, 'Cuisson');
INSERT INTO processus_lie( id_processus_lie, id_processus_global, nom ) VALUES ( 4140, 4000, 'Moulure Ciments');
INSERT INTO processus_lie( id_processus_lie, id_processus_global, nom ) VALUES ( 4150, 4000, 'Chargement sacs');
INSERT INTO processus_lie( id_processus_lie, id_processus_global, nom ) VALUES ( 4170, 4000, 'Exploitation silo');
INSERT INTO processus_lie( id_processus_lie, id_processus_global, nom ) VALUES ( 5300, 5000, 'Maintenance');
INSERT INTO processus_lie( id_processus_lie, id_processus_global, nom ) VALUES ( 5400, 5000, 'Gestion des dechets');
INSERT INTO processus_lie( id_processus_lie, id_processus_global, nom ) VALUES ( 5500, 5000, 'Hygiene et securite');
INSERT INTO processus_lie( id_processus_lie, id_processus_global, nom ) VALUES ( 5600, 5000, 'CSR');
INSERT INTO processus_lie( id_processus_lie, id_processus_global, nom ) VALUES ( 5110, 5100, 'Achats biens et services');
INSERT INTO processus_lie( id_processus_lie, id_processus_global, nom ) VALUES ( 5120, 5100, 'Comptabilite');
INSERT INTO processus_lie( id_processus_lie, id_processus_global, nom ) VALUES ( 5130, 5100, 'Controle de gestion');
INSERT INTO processus_lie( id_processus_lie, id_processus_global, nom ) VALUES ( 5140, 5100, 'Controle des stocks');
INSERT INTO processus_lie( id_processus_lie, id_processus_global, nom ) VALUES ( 5150, 5100, 'Informatique');
INSERT INTO processus_lie( id_processus_lie, id_processus_global, nom ) VALUES ( 3100, 3000, 'Demarche commerciale');
INSERT INTO processus_lie( id_processus_lie, id_processus_global, nom ) VALUES ( 3200, 3000, 'Logistique');
INSERT INTO processus_lie( id_processus_lie, id_processus_global, nom ) VALUES ( 3300, 3000, 'Suivi clientele');
INSERT INTO processus_lie( id_processus_lie, id_processus_global, nom ) VALUES ( 6100, 6000, 'Identification des situations d''urgence et capacite a reagir');
INSERT INTO processus_lie( id_processus_lie, id_processus_global, nom ) VALUES ( 6200, 6000, 'Revision des moyens de prevention et d''intervention');
INSERT INTO processus_lie( id_processus_lie, id_processus_global, nom ) VALUES ( 9100, 9000, 'Reclamations');
INSERT INTO processus_lie( id_processus_lie, id_processus_global, nom ) VALUES ( 9200, 9000, 'Actions Correctives/Actions Preventives');
INSERT INTO processus_lie( id_processus_lie, id_processus_global, nom ) VALUES ( 9400, 9000, 'Mesures');
INSERT INTO processus_lie( id_processus_lie, id_processus_global, nom ) VALUES ( 9410, 9000, 'Controle qualite');
INSERT INTO processus_lie( id_processus_lie, id_processus_global, nom ) VALUES ( 9420, 9000, 'Metrologie');
INSERT INTO processus_lie( id_processus_lie, id_processus_global, nom ) VALUES ( 9500, 9000, 'Audits');
INSERT INTO processus_lie( id_processus_lie, id_processus_global, nom ) VALUES ( 9210, 9200, 'Maitrise du produit non-conforme');
INSERT INTO processus_lie( id_processus_lie, id_processus_global, nom ) VALUES ( 9220, 9200, 'Maitrise des non-conformites environnementales');
INSERT INTO processus_lie( id_processus_lie, id_processus_global, nom ) VALUES ( 9230, 9200, 'Maitrise des incidents / accidents');

INSERT INTO type_document( id_type, nom ) VALUES ( 1, 'Processus');
INSERT INTO type_document( id_type, nom ) VALUES ( 2, 'Sous Processus');
INSERT INTO type_document( id_type, nom ) VALUES ( 3, 'Fiche d''instruction');
INSERT INTO type_document( id_type, nom ) VALUES ( 4, 'Enregistrement');
INSERT INTO type_document( id_type, nom ) VALUES ( 5, 'Navigateur');




ALTER SEQUENCE seq_reference RESTART WITH 1;
truncate table document restart identity cascade;
