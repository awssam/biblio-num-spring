package uit.fs.bibliotheque.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController extends AbstractController {

    @GetMapping("/")
    public String accueil(Model model) {
        return renderView(model, "index", "Accueil - Bibliothèque Numérique");
    }

}