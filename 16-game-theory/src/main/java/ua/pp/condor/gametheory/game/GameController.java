package ua.pp.condor.gametheory.game;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Controller
@RequestMapping("/")
public class GameController {

    private static final Logger log = LoggerFactory.getLogger(GameController.class);

    @RequestMapping(method = RequestMethod.GET)
    public String index() {
        log.debug("Get index page");
        return "index";
    }

    @RequestMapping(value = "/play", method = RequestMethod.POST)
    @ResponseBody
    public List<Integer> play(GameRequest request) {
        //TODO add checks of input data
        return new Game(request).play();
    }
}
