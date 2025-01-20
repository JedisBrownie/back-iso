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




-- Views --
CREATE OR REPLACE VIEW v_doc_state AS
SELECT 
d.*, rd.matricule_utilisateur, he.id_etat, date_heure_etat
FROM document d
JOIN redacteur_document rd ON rd.ref_document = d.ref_document
JOIN historique_etat he ON he.ref_document = d.ref_document;

CREATE OR REPLACE VIEW v_document_state AS
SELECT DISTINCT ON (he.ref_document) he.ref_document, titre, d.id_type, td.nom, he.matricule_utilisateur, he.id_etat, ed.status, date_heure_etat::date
FROM historique_etat he
JOIN document d ON he.ref_document = d.ref_document
JOIN type_document td ON d.id_type = td.id_type
JOIN etat_document ed ON he.id_etat = ed.id_etat;