CREATE SEQUENCE etat_document_id_etat_seq START WITH 1 INCREMENT BY 1;

CREATE SEQUENCE type_document_id_type_seq START WITH 1 INCREMENT BY 1;

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

CREATE  TABLE validateur_document ( 
	ref_document         varchar(80)  NOT NULL  ,
	id_document          integer  NOT NULL  ,
	matricule_utilisateur varchar(50)  NOT NULL  ,
	CONSTRAINT fk_validateur_document FOREIGN KEY ( ref_document, id_document ) REFERENCES document( ref_document, id_document )   
 );

CREATE  TABLE approbateur_document ( 
	ref_document         varchar(80)  NOT NULL  ,
	id_document          integer  NOT NULL  ,
	marticule_utilisateur varchar(50)  NOT NULL  ,
	CONSTRAINT fk_approbateur_document FOREIGN KEY ( ref_document, id_document ) REFERENCES document( ref_document, id_document )   
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

