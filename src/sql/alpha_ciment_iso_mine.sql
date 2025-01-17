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
SELECT DISTINCT ON (ref_document) ref_document, id_etat, date_heure_etat
FROM historique_etat
ORDER BY ref_document, date_heure_etat DESC;




-- Views --
CREATE OR REPLACE VIEW v_doc_state AS
SELECT 
d.*, rd.matricule_utilisateur, he.id_etat, date_heure_etat
FROM document d
JOIN redacteur_document rd ON rd.ref_document = d.ref_document
JOIN historique_etat he ON he.ref_document = d.ref_document;
