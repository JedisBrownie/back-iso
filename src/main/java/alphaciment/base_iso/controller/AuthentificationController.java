package alphaciment.base_iso.controller;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/test")
@CrossOrigin(origins="*", allowedHeaders="*",methods={RequestMethod.GET})

public class AuthentificationController 
{
    @GetMapping("/Hello")
    public String testHello(){
        return("Hello World");
    }

    


   
}
