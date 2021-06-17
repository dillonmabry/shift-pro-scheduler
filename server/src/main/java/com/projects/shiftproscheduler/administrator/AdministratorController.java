package com.projects.shiftproscheduler.administrator;

import javax.persistence.EntityNotFoundException;
import javax.servlet.http.HttpServletRequest;
import com.projects.shiftproscheduler.security.ErrorInfo;
import com.projects.shiftproscheduler.security.JWTUtil;

import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Validated
@PreAuthorize("hasRole('ADMIN')")
class AdministratorController {

    private final AdministratorRepository administrators;

    public AdministratorController(AdministratorRepository administrators) {
        this.administrators = administrators;
    }

    @GetMapping("/administrators")
    public @ResponseBody Administrators getAdministrators() {
        Administrators administrators = new Administrators();
        administrators.getAdministratorList().addAll(this.administrators.findAll());
        return administrators;
    }

    @GetMapping("/administrator/{username}")
    public @ResponseBody Administrator getAdministrator(
            @PathVariable(value = "username", required = true) String username) {
        return administrators.findByUserName(username).orElseThrow(() -> new EntityNotFoundException());
    }

    @PostMapping("/administrators/{username}")
    Administrator saveAdministrator(@RequestBody Administrator newAdministrator,
            @RequestHeader("Authorization") String token) {
        administrators.findById(newAdministrator.getId()).orElseThrow();
        UsernamePasswordAuthenticationToken authToken = JWTUtil.parseToken(token.split(" ")[1]);
        if (authToken == null || !authToken.getName().equals(newAdministrator.getUserName()))
            throw new AccessDeniedException("Authenticated user does not have access to save employee details");
        return administrators.save(newAdministrator);
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(IllegalStateException.class)
    @ResponseBody
    ErrorInfo illegalScheduleStateException(HttpServletRequest req, IllegalStateException ex) {
        return new ErrorInfo(req.getRequestURL().toString(), ex,
                "Model cannot be generated with available employees and shifts with date range");
    }

}
