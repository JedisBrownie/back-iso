-- For Methods --

/**
* Check if the document has a valid redaction
*/
SELECT * FROM redacteur_document WHERE ref_document = ? AND id_document = ?;
-- If there is even one redaction_document_date that is null -> document is still draft
-- If all of the redaction_document_date aren't null -> document "En cours de vérification"