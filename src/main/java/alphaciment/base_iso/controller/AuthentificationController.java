package alphaciment.base_iso.controller;

import java.util.ArrayList;
import java.util.List;

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

import alphaciment.base_iso.model.object.ProcessusGlobal;
import alphaciment.base_iso.service.EmailService;
import alphaciment.base_iso.service.ProcessusService;

@RestController
@RequestMapping("/test")
@CrossOrigin(origins="*", allowedHeaders="*",methods={RequestMethod.GET})

public class AuthentificationController 
{
    @Autowired
    ProcessusService processusService;

    @Autowired 
    EmailService emailService;

    @GetMapping("/Hello")
    public String testHello(){
        return("Hello World");
    }

    // @GetMapping("/processus")
    // public List<ProcessusGlobal> testProcess(){
    //     List<ProcessusGlobal> liste = new ArrayList<>();

    //     try{
    //         liste = processusService.getAllProcessusGlobal();
    //     }catch(Exception e){
    //         e.printStackTrace();
    //     }
    //     return liste;
    // }

    // @PostMapping("/email")
    // public ResponseEntity<?> email(
    //     @RequestParam(name = "destinataire") String destinataire,
    //     @RequestParam(name = "objet") String objet,
    //     @RequestParam(name = "sujet") String sujet
    // ){

    //     try{
    //         emailService.sendEmail(destinataire, objet, sujet);
    //         return ResponseEntity.ok("Email ajouté...");
    //     }catch(Exception e){
    //         return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
    //     }
    // }


    // @GetMapping("/find")
    // public List<ProcessusGlobal> find(
    //     @RequestParam(name = "reference") String reference,
    //     @RequestParam(name = "id") String id
    // ){
    //     List<ProcessusGlobal> liste = new ArrayList<>();
    //     try{
    //         int idDocument = Integer.parseInt(id);
    //         liste = processusService.findProcessusOfDocument(reference, idDocument);
    //     }catch(Exception e){
    //         e.printStackTrace();
    //     }
    //     return liste;
    // }


    


   
}
