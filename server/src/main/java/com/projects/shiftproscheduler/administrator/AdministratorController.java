package com.projects.shiftproscheduler.administrator;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
class AdministratorController {

    private final AdministratorRepository administrators;

    public AdministratorController(AdministratorRepository administrators) {
        this.administrators = administrators;
    }

    @GetMapping("/administrators")
    public @ResponseBody
    Administrators getAdministrators() {
        Administrators administrators = new Administrators();
        administrators.getAdministratorList().addAll(this.administrators.findAll());
        return administrators;
    }

}
