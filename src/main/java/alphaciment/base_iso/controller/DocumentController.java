package alphaciment.base_iso.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import alphaciment.base_iso.service.DocumentService;

@RestController
@RequestMapping("/document")
@CrossOrigin(origins="*", allowedHeaders="*",methods={RequestMethod.GET})

public class DocumentController {
    @Autowired
    DocumentService documentService;

    
    @PostMapping("/add")
    public ResponseEntity<?> add(
        @RequestParam(name = "titre") String titre,
        @RequestParam(name = "type") String type,
        @RequestParam(name = "confidentiel") String confidentiel )
    {
        if(titre.isEmpty() && type.isEmpty() && confidentiel.isEmpty()){
            return ResponseEntity.badRequest().body("Veuillez remplir tous les champs");
        }

        int idType = Integer.parseInt(type);
        int confid = Integer.parseInt(confidentiel);
        try{
            documentService.addDocument(titre, idType, confid);
            return ResponseEntity.ok("Document enregistré");
        }catch(Exception e){
            e.printStackTrace();
            System.out.println(e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }


}
