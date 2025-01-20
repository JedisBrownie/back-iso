package alphaciment.base_iso.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import alphaciment.base_iso.model.viewmodel.ViewDocument;
import alphaciment.base_iso.model.viewmodel.ViewListFromType;
import alphaciment.base_iso.model.viewmodel.ViewListFromUser;
import alphaciment.base_iso.service.ViewModelService;

@RestController
@RequestMapping("/viewdocument")
@CrossOrigin(origins = "http://localhost:3000", allowedHeaders = "*", methods = {RequestMethod.GET, RequestMethod.POST}, allowCredentials = "true")
public class ViewModelController {
    @Autowired
    ViewModelService viewModelService;

    @GetMapping("/valable/{processusLie}")
    public ViewListFromType getDocumentValable(
        @PathVariable String processusLie
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

    @GetMapping("/encours")
    public List<ViewDocument> getDocumentOwner(
        @RequestParam(name="utilisateur") String utilisateur
    ){
        List<ViewDocument> liste = new ArrayList<>();
        try {
            int idUtil = Integer.parseInt(utilisateur);
            liste = viewModelService.getAllViewDocumentOfOwner(idUtil);
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        return liste;
    }

    @GetMapping("/admin/encours")
    public List<ViewListFromUser> getAllDocumentEnCours(){
        List<ViewListFromUser> liste = new ArrayList<>();
        try{
            liste = viewModelService.getAllDocumentEnCours();
        }catch(Exception e){
            e.printStackTrace();
        }

        return liste;
    }



}
