package org.bt2.Boot2Demo.doc;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import springfox.documentation.annotations.ApiIgnore;

/**
 * @author ld
 * @name
 * @table
 * @remarks
 */
@Controller
//api忽略此
@ApiIgnore
public class DocController {

    @RequestMapping(value = "/doc", method = RequestMethod.GET)
    public String docs() {
        return "/doc/index";
    }

    @RequestMapping(value = "/doc/{name}",method = RequestMethod.GET)
    public String find(@PathVariable("name") String name, Model model){
        model.addAttribute("name",name);
        return "/doc/right";
    }
}
