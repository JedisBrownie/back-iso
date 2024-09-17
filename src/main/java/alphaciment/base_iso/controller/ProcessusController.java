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
@RequestMapping("/processus")
@CrossOrigin(origins="*", allowedHeaders="*",methods={RequestMethod.GET})

public class ProcessusController {

    @Autowired
    ProcessusService processusService;

    @GetMapping("/global/all")
    public List<ProcessusGlobal> getAllProcessusGlobal(){
        List<ProcessusGlobal> liste = new ArrayList<>();
        try{
            liste = processusService.getAllProcessusGlobal();
        }catch(Exception e){
            e.printStackTrace();
        }
        return liste;
    }

    
    

    
    
}
