package alphaciment.base_iso.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import alphaciment.base_iso.model.viewmodel.ViewMyDocument;
import alphaciment.base_iso.service.DocumentService;
import alphaciment.base_iso.service.EmailService;




@RestController
@RequestMapping("/document")
@CrossOrigin(origins = {"http://localhost:3000", "http://10.192.193.81:3000"}, allowedHeaders = "*", methods = {RequestMethod.GET, RequestMethod.POST}, allowCredentials = "true")
public class DocumentController {

    /**
     * Fileds
     */
    @Autowired
    DocumentService documentService;

    @Autowired
    EmailService emailService;
    // private final int sessionUtilisateur = 80682;



    /**
     * Methods
     */
    @PostMapping("/add-draft")
    public ResponseEntity<?> addDraft(
        @RequestParam String titre,
        @RequestParam String type,
        @RequestParam(name = "date_mise_application") String miseEnApplication,
        @RequestParam String confidentiel,
        @RequestParam(name = "user_matricule") String userMatricule,
        @RequestParam String data,
        @RequestParam List<MultipartFile> files)
    {
        System.out.println("Data received: " + data);

        if(titre.isEmpty() && type.isEmpty() && confidentiel.isEmpty()) {
            return ResponseEntity.badRequest().body("Veuillez remplir tous les champs");
        }

        int idType = Integer.parseInt(type);
        boolean confid = Boolean.parseBoolean(confidentiel);     

        try {
            documentService.addDocumentDraft(titre, idType, miseEnApplication, confid, userMatricule, data, files);
            return ResponseEntity.ok("Brouillon enregistré");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }


    @PostMapping("/add-redaction")
    public ResponseEntity<?> addRedaction(
        @RequestParam String titre,
        @RequestParam String type,
        @RequestParam(name = "date_mise_application") String miseEnApplication,
        @RequestParam String confidentiel,
        @RequestParam(name = "user_matricule") String userMatricule,
        @RequestParam String data,
        @RequestParam List<MultipartFile> files)
    {
        System.out.println("Data received: " + data);

        if(titre.isEmpty() && type.isEmpty() && confidentiel.isEmpty()) {
            return ResponseEntity.badRequest().body("Veuillez remplir tous les champs");
        }

        int idType = Integer.parseInt(type);
        boolean confid = Boolean.parseBoolean(confidentiel);     

        try {
            documentService.addDocumentValidation(titre, idType, miseEnApplication, confid, userMatricule, data, files);
            return ResponseEntity.ok("Document enregistré");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }


    @GetMapping("/get")
    public List<ViewMyDocument> getUserDocuments(
            @RequestParam String userMatricule,
            @RequestParam String documentState) {
        List<ViewMyDocument> userDocument = new ArrayList<>();

        try {
            userDocument = documentService.listUserDocuments(Integer.parseInt(documentState), userMatricule);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return userDocument;
    }


    @GetMapping("/get/by-ref")
    public ViewMyDocument getDocumentWhereRef(@RequestParam String refDocument) {
        ViewMyDocument document = null;

        try {
            document = documentService.getDocumentByRef(refDocument);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return document;
    }
    


    @GetMapping("/to-verify")
    public List<Map<String, Object>> getDocumensToBeVerified(
        @RequestParam String userMatricule) {
        List<Map<String, Object>> documentsToBeVerified = new ArrayList<>();

        try {
            documentsToBeVerified = documentService.listDocumentToCheck(userMatricule);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return documentsToBeVerified;
    }


    @GetMapping("/to-approve")
    public List<Map<String, Object>> getDocumensToBeApproved(
        @RequestParam String userMatricule) {
        List<Map<String, Object>> documentsToBeApproved = new ArrayList<>();

        try {
            documentsToBeApproved = documentService.listDocumentToApprove(userMatricule);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return documentsToBeApproved;
    }


    @GetMapping("/test-mail")
    public void testMail() {
        try {
            emailService.mailSendingTest();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    


    // @PostMapping("/add")
    // public ResponseEntity<?> add(
    //     @RequestParam(name = "titre") String titre,
    //     @RequestParam(name = "type") String type,
    //     @RequestParam(name = "processus") String processusLie,
    //     @RequestParam(name = "confidentiel") String confidentiel)  
    // {
    //     if(titre.isEmpty() && type.isEmpty() && confidentiel.isEmpty()) {
    //         return ResponseEntity.badRequest().body("Veuillez remplir tous les champs");
    //     }

    //     int idType = Integer.parseInt(type);
    //     boolean confid = Boolean.parseBoolean(confidentiel);        
    //     int idProcessusLie = Integer.parseInt(processusLie);
    //     try {
    //         documentService.addDocument(titre,idType,confid,idProcessusLie);
    //         return ResponseEntity.ok("Document enregistré");
    //     } catch(Exception e) {
    //         e.printStackTrace();
    //         return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
    //     }
    // }

    // @PostMapping("/{typeDocument}/redaction")
    // public ResponseEntity<?> addDocumentRedaction(
    //     @RequestParam(name = "titre") String titre,
    //     @RequestParam(name = "confidentiel") String confidentiel,
    //     @RequestParam(name = "validateur") String validateur,
    //     @RequestParam(name = "approbateur") String approbateur,
    //     @RequestParam(name = "processusGlobal") String[] processusGlobal,
    //     @RequestParam(name = "processusLie") String[] processusLie,
    //     @RequestParam(name = "lecteur",required = false) String[] lecteur,
    //     @RequestParam(name = "redacteur",required = false) String[] redacteur,
    //     @PathVariable String typeDocument)
    // {
    //     if(titre.isEmpty() && confidentiel.isEmpty() && validateur.isEmpty() && approbateur.isEmpty()) {
    //         return ResponseEntity.badRequest().body("Veuillez remplir tous les champs");
    //     }

    //     boolean confid = Boolean.parseBoolean(confidentiel);       
    //     int idValidateur = Integer.parseInt(validateur);
    //     int idApprobateur = Integer.parseInt(approbateur);  
    //     int idType = Integer.parseInt(typeDocument);

    //     try {
    //         documentService.addDocumentRedaction(titre, idType, confid, idApprobateur, idValidateur, processusGlobal, processusLie,lecteur,redacteur,sessionUtilisateur);
    //         return ResponseEntity.ok("Processus enregistré");
    //     } catch(Exception e) {
    //         e.printStackTrace();
    //         return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
    //     }
    // }  

    // @PostMapping("/enregistrement/{typeDocument}/redaction")
    // public ResponseEntity<?> addEnregistrementRedaction(
    //     @RequestParam(name = "titre") String titre,
    //     @RequestParam(name = "processusGlobal") String[] processusGlobal,
    //     @RequestParam(name = "processusLie") String[] processusLie,
    //     @RequestParam(name = "confidentiel") String confidentiel,
    //     @RequestParam(name = "lecteur",required = false) String[] lecteur,
    //     @RequestParam(name = "redacteur",required = false) String[] redacteur,
    //     @PathVariable(name = "typeDocument") String typeDocument)
    // {
    //     int idType = Integer.parseInt(typeDocument);
    //     boolean confid = Boolean.parseBoolean(confidentiel); 
    //     try {
    //         documentService.addEnregistrementRedaction(titre,idType,processusGlobal,processusLie,confid,lecteur,redacteur,sessionUtilisateur);
    //         return ResponseEntity.ok("Enregistrement enregistré");
    //     } catch (Exception e) {
    //         e.printStackTrace();
    //         return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
    //     }
    // }


    // @GetMapping("/verifRedacteur/{referenceDocument}/{idDocument}")
    // public Boolean verifRedacteur(
    //     @RequestParam(name = "utilisateur") String utilisateur,
    //     @PathVariable(name = "referenceDocument") String reference,
    //     @PathVariable(name = "idDocument") String idDocument)
    // {
    //     boolean val = false;
        
    //     try {
    //         int idUtilisateur = Integer.parseInt(utilisateur);
    //         int idDoc = Integer.parseInt(idDocument);
    //         val = documentService.verifRedacteurDocument(reference, idDoc, idUtilisateur);
    //     } catch (Exception e) {
    //         e.printStackTrace();
    //     }

    //     return val;
    // }


    // @PostMapping("/processus/brouillon")
    // public ResponseEntity<?> addProcessusBrouillon(
    //     @RequestParam(name = "titre",required=false) String titre,
    //     @RequestParam(name = "confidentiel",required=false) String confidentiel,
    //     @RequestParam(name = "validateur",required=false) String validateur,
    //     @RequestParam(name = "approbateur",required=false) String approbateur,
    //     @RequestParam(name = "processusGlobal",required=false) String[] processusGlobal,
    //     @RequestParam(name = "processusLie",required=false) String[] processusLie)
    // {
    //     boolean confid = false;
    //     int idValidateur = 0;
    //     int idApprobateur = 0;
        
    //     if(!confidentiel.isEmpty()){
    //         confid = Boolean.parseBoolean(confidentiel);       
    //     }
    //     if(!validateur.isEmpty()){
    //         idValidateur = Integer.parseInt(validateur);
    //     }
    //     if(!approbateur.isEmpty()){
    //         idApprobateur = Integer.parseInt(approbateur);
    //     }
        
    //     int idType = 1;

    //     try{
    //         documentService.addDocumentBrouillon(titre, idType, confid, idApprobateur, idValidateur, processusGlobal, processusLie, idUtilisateur);
    //         return ResponseEntity.ok("Processus enregistré");
    //     }catch(Exception e){
    //         e.printStackTrace();
    //     
    //         return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
    //     }
    // } 


}
