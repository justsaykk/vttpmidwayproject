package fullstack.vttpfullstackproj.controllers;

import java.util.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import fullstack.vttpfullstackproj.models.*;
import fullstack.vttpfullstackproj.services.*;

@Controller
@RequestMapping
public class FrontEndController {

    @Autowired
    private ApiService apiSvc;

    @Autowired
    private RESTService restSvc;

    @Autowired
    private UserService userSvc;

    private String toCaps(String str) {
        return str.substring(0, 1).toUpperCase() + str.substring(1);
    }

    @GetMapping(path = "/menu")
    public String menu(
            @RequestParam(defaultValue = "Scotch", name = "drinkFilter") String ingredient,
            Model model) {
        List<Cocktail> listOfCocktails = apiSvc.fetchDrinksByIngredients(ingredient.toLowerCase());
        model.addAttribute("ingredient", toCaps(ingredient));
        model.addAttribute("listOfCocktails", listOfCocktails);
        return "menu";
    }

    @GetMapping(path = "/drink")
    public String getDrinkById(
            @RequestParam(name = "idDrink") String idDrink,
            Model model) {
        Cocktail cocktail = apiSvc.fetchDrinkById(idDrink);
        model.addAttribute("cocktailDetails", cocktail);
        return "drinkdetails";
    }

    @GetMapping(path = "/profile")
    public String profilePage() {
        return "login";
    }

    @GetMapping(path = "/profile/{name}")
    public String getProfileDetails(
            @PathVariable(value = "name") String rawName,
            Model model) {
        String name = rawName.toLowerCase();

        // Get list of drinks from name
        List<String> listOfidDrink = restSvc.getProfile(name);
        List<Cocktail> listOfCocktails = new LinkedList<>();
        for (String id : listOfidDrink) {
            Cocktail cocktail = apiSvc.fetchDrinkById(id);
            listOfCocktails.add(cocktail);
        }

        // Get user details
        String email = userSvc.getEmailfromName(name);
        Map<String, String> profileDetails = userSvc.getUserDetails(email).toMap();

        // Add to model
        model.addAttribute("email", email);
        model.addAttribute("profileDetails", profileDetails);
        model.addAttribute("name", toCaps(name.toLowerCase()));
        model.addAttribute("listOfCocktails", listOfCocktails);
        return "profiledetails";
    }

    @GetMapping(path = "/createprofile")
    public String createProfile() {
        return "createprofile";
    }

    @GetMapping(path = "/createprofile2/{name}")
    public String createProfile2(
            @PathVariable(value = "name") String name,
            Model model) {
        model.addAttribute("name", name);
        return "createprofile2";
    }

    @PostMapping(path = "/editprofile/{email}")
    public String editProfile(
            @PathVariable(value = "email") String email,
            Model model) {

        Map<String, String> profileDetails = userSvc.getUserDetails(email).toMap();
        model.addAttribute("profileDetails", profileDetails);
        return "editprofile";
    }
}
