package alphaciment.base_iso.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.ArrayList;

import alphaciment.base_iso.model.object.*;

import alphaciment.base_iso.service.ProcessusService;

@RestController
@RequestMapping("/test")
@CrossOrigin(origins="*", allowedHeaders="*",methods={RequestMethod.GET})

public class AuthentificationController 
{
    @Autowired
    ProcessusService processusService;

    @GetMapping("/Hello")
    public String testHello(){
        return("Hello World");
    }

    @GetMapping("/processus")
    public List<ProcessusGlobal> testProcess(){
        List<ProcessusGlobal> liste = new ArrayList<>();

        try{
            liste = processusService.getAllProcessusGlobal();
        }catch(Exception e){
            System.out.println(e.getMessage());
        }

        return liste;
    }

    


   
}
