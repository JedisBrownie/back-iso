package alphaciment.base_iso.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import alphaciment.base_iso.model.object.ProcessusGlobal;
import alphaciment.base_iso.model.object.ProcessusLie;
import alphaciment.base_iso.model.viewmodel.ViewListFromType;
import alphaciment.base_iso.service.ProcessusService;
import alphaciment.base_iso.service.ViewModelService;

@RestController
@RequestMapping("/processus")
@CrossOrigin(origins="*", allowedHeaders="*",methods={RequestMethod.GET})

public class ProcessusController {

    @Autowired
    ProcessusService processusService;

    @Autowired
    ViewModelService viewModelService;

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

    @GetMapping("/valable/{processusLie}")
    public ViewListFromType getDocumentValable(
        @PathVariable(name = "processusLie") String processusLie
    ){
        
        ViewListFromType liste = new ViewListFromType();
        try {
            int idProcessusLie = Integer.parseInt(processusLie);
            liste = viewModelService.getAllDocumentApplicable(idProcessusLie);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return  liste;
    }

    @GetMapping("/liste/{processusGlobal}")
    public List<ProcessusLie> findProcessusOfPg(
        @PathVariable(name = "processusGlobal") String processusGlobal
    ){
        
        List<ProcessusLie> liste = new ArrayList<>();
        try {
            int idProcessusGlobal = Integer.parseInt(processusGlobal);
            liste = processusService.findProcessusLieOfPg(idProcessusGlobal);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return  liste;
    }

    
    
}
