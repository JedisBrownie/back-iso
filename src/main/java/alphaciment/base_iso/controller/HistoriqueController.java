package alphaciment.base_iso.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import alphaciment.base_iso.service.HistoriqueService;

@RestController
@RequestMapping("/historique")
@CrossOrigin(origins="*", allowedHeaders="*",methods={RequestMethod.GET})

public class HistoriqueController {
    @Autowired
    HistoriqueService historiqueService;

    private final int redacteur = 80682;
    private final int sessionUtilisateur = 80246;
    private final int sessionApprobateur = 24566;


    // :::: ovaina session utilisateur JWT ::::: ///

    @PutMapping("/invalidation/{referenceDocument}/{idDocument}")
    public ResponseEntity<?> invalidationDocument(
        @RequestParam(name = "motif") String motif,
        @PathVariable(name = "referenceDocument") String referenceDocument,
        @PathVariable(name = "idDocument") String idDocument
    ){
        try {
            int id = Integer.parseInt(idDocument);
            historiqueService.saveEtatInvalidation(referenceDocument, id, sessionUtilisateur, motif);
            return ResponseEntity.ok("Document non validé"); 
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @PutMapping("/desapprobation/{referenceDocument}/{idDocument}")
    public ResponseEntity<?> desapprobationDocument(
        @RequestParam(name = "motif") String motif,
        @PathVariable(name = "referenceDocument") String referenceDocument,
        @PathVariable(name = "idDocument") String idDocument
    ){
        try {
            int id = Integer.parseInt(idDocument);
            historiqueService.saveEtatDesapprobation(referenceDocument, id, sessionApprobateur, motif);
            return ResponseEntity.ok("Document non approuvé"); 
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @PutMapping("/validation/{referenceDocument}/{idDocument}")
    public ResponseEntity<?> validationDocument(
        @PathVariable(name = "referenceDocument") String referenceDocument,
        @PathVariable(name = "idDocument") String idDocument
    ){
        try {
            int id = Integer.parseInt(idDocument);
            int etatValide = 4;
            historiqueService.saveEtatHistorique(referenceDocument, id, sessionUtilisateur, etatValide);
            String message = "Document validé et en attente d'approbation";
            return ResponseEntity.ok(message); 
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @PutMapping("/approbation/{referenceDocument}/{idDocument}")
    public ResponseEntity<?> approbationDocument(
        @PathVariable(name = "referenceDocument") String referenceDocument,
        @PathVariable(name = "idDocument") String idDocument
    ){
        try {
            int id = Integer.parseInt(idDocument);
            int etatApprouver = 6;
            historiqueService.saveEtatHistorique(referenceDocument, id, sessionApprobateur, etatApprouver);
            String message = "Document approuvé et applicable";
            return ResponseEntity.ok(message); 
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @PutMapping("/revision/{referenceDocument}/{idDocument}")
    public ResponseEntity<?> demandeRevision(
        @RequestParam(name = "motif") String motif, 
        @PathVariable(name = "referenceDocument") String referenceDocument,
        @PathVariable(name = "idDocument") String idDocument
    ){
        try {
            int id = Integer.parseInt(idDocument);
            historiqueService.demandeRevision(referenceDocument,id,sessionUtilisateur,motif);
            return ResponseEntity.ok("Votre demande a été envoyé");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    
    @PutMapping("/redaction/{referenceDocument}/{idDocument}")
    public ResponseEntity<?> confirmationRedaction(
        @PathVariable(name = "referenceDocument") String referenceDocument,
        @PathVariable(name = "idDocument") String idDocument
    ){
        try {
            int id = Integer.parseInt(idDocument);
            historiqueService.validerRedaction(referenceDocument, id, redacteur);
            return ResponseEntity.ok("Votre document a été envoyé en vérification");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    } 

    @PutMapping("/validerDemande/{referenceDocument}/{idDocument}")
    public ResponseEntity<?> confirmationDemande(
        @PathVariable(name = "referenceDocument") String referenceDocument,
        @PathVariable(name = "idDocument") String idDocument
    ){
        try {
            int id = Integer.parseInt(idDocument);
            historiqueService.validerDemandeRevision(referenceDocument, id, sessionApprobateur);
            return ResponseEntity.ok("Vous avez validé la demande de vérification");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

}
