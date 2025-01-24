-- For Methods --

/**
* Check if the document has a valid redaction
*/
SELECT * FROM redacteur_document WHERE ref_document = ? AND id_document = ?;
-- If there is even one redaction_document_date that is null -> document is still draft
-- If all of the redaction_document_date aren't null -> document "En cours de vérification"


/**
* Check a document last state
*/
SELECT ref_document, id_etat FROM historique_etat WHERE ref_document = <ref_document> ORDER BY date_heure_etat DESC LIMIT 1;


/**
* Check all documents last state
*/
SELECT DISTINCT ON (he.ref_document) he.ref_document, titre, d.id_type, td.nom, he.matricule_utilisateur, he.id_etat, ed.status, date_heure_etat:date as date_etat
FROM historique_etat he
JOIN document d ON he.ref_document = d.ref_document
JOIN type_document td ON d.id_type = td.id_type
JOIN etat_document ed ON he.id_etat = ed.id_etat
ORDER BY ref_document, date_heure_etat DESC;


/**
* Group concerns the concerned users by documents
*/
SELECT
    v_ds.ref_document, 
    v_ds.titre, 
    v_ds.id_type, 
    v_ds.nom, 
    v_ds.id_etat, 
    v_ds.status, 
    v_ds.date_heure_etat,
    STRING_AGG(DISTINCT rd.matricule_utilisateur::TEXT, ', ') AS redacteurs,
    STRING_AGG(DISTINCT vd.matricule_utilisateur::TEXT, ', ') AS verificateurs,
    STRING_AGG(DISTINCT ad.matricule_utilisateur::TEXT, ', ') AS approbateurs
FROM
    v_document_state v_ds
JOIN
    redacteur_document rd ON rd.ref_document = v_ds.ref_document
JOIN
    verificateur_document vd ON vd.ref_document = v_ds.ref_document
JOIN
    approbateur_document ad ON ad.ref_document = v_ds.ref_document
GROUP BY
    v_ds.ref_document, v_ds.titre, v_ds.id_type, v_ds.nom, v_ds.id_etat, v_ds.status, v_ds.date_heure_etat;


/**
* Get documents where "verificateurs"
*/
SELECT * 
FROM v_document_concerned_users
WHERE '90511' = ANY(string_to_array(verificateurs, ', '));


/**
* Group documents by "redacteurs"
*/
SELECT 
    redacteur, 
    STRING_AGG(ref_document, ', ') AS documents
FROM (
    SELECT 
        ref_document, 
        UNNEST(STRING_TO_ARRAY(redacteurs, ', ')) AS redacteur
    FROM v_document_concerned_users
) AS expanded
GROUP BY redacteur
ORDER BY redacteur;




-- Views --
CREATE OR REPLACE VIEW v_document_state AS
SELECT DISTINCT ON (he.ref_document) he.ref_document, titre, d.id_type, td.nom, he.id_etat, ed.status, date_heure_etat::date
FROM historique_etat he
JOIN document d ON he.ref_document = d.ref_document
JOIN type_document td ON d.id_type = td.id_type
JOIN etat_document ed ON he.id_etat = ed.id_etat;


CREATE OR REPLACE VIEW v_document_concerned_users AS
SELECT
    v_ds.ref_document, 
    v_ds.titre, 
    v_ds.id_type, 
    v_ds.nom, 
    v_ds.id_etat, 
    v_ds.status, 
    v_ds.date_heure_etat,
    STRING_AGG(DISTINCT rd.matricule_utilisateur::TEXT, ', ') AS redacteurs,
    STRING_AGG(DISTINCT vd.matricule_utilisateur::TEXT, ', ') AS verificateurs,
    STRING_AGG(DISTINCT ad.matricule_utilisateur::TEXT, ', ') AS approbateurs
FROM
    v_document_state v_ds
JOIN
    redacteur_document rd ON rd.ref_document = v_ds.ref_document
JOIN
    verificateur_document vd ON vd.ref_document = v_ds.ref_document
JOIN
    approbateur_document ad ON ad.ref_document = v_ds.ref_document
GROUP BY
    v_ds.ref_document, v_ds.titre, v_ds.id_type, v_ds.nom, v_ds.id_etat, v_ds.status, v_ds.date_heure_etat;



CREATE OR REPLACE VIEW v_document_redactors as
SELECT 
    redacteur, 
    STRING_AGG(ref_document, ', ') AS documents
FROM (
    SELECT 
        ref_document, 
        UNNEST(STRING_TO_ARRAY(redacteurs, ', ')) AS redacteur
    FROM v_document_concerned_users
) AS expanded
GROUP BY redacteur
ORDER BY redacteur;