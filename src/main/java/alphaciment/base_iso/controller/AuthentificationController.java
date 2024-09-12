package alphaciment.base_iso.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import alphaciment.base_iso.model.object.ProcessusGlobal;
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
            e.printStackTrace();
        }
        return liste;
    }



    


   
}
