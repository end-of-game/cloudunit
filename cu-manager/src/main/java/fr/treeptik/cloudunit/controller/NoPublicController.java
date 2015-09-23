package fr.treeptik.cloudunit.controller;

import fr.treeptik.cloudunit.exception.CheckException;
import fr.treeptik.cloudunit.exception.ServiceException;
import fr.treeptik.cloudunit.json.ui.HttpOk;
import fr.treeptik.cloudunit.json.ui.JsonResponse;
import fr.treeptik.cloudunit.service.ApplicationService;
import fr.treeptik.cloudunit.utils.AuthentificationUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.inject.Inject;
import java.io.Serializable;

@Controller
@RequestMapping("/nopublic")
public class NoPublicController implements Serializable {

    private static final long serialVersionUID = 1L;

    private Logger logger = LoggerFactory.getLogger(NoPublicController.class);

    @Inject
    private ApplicationService applicationService;

    @Inject
    private AuthentificationUtils authentificationUtils;


    @RequestMapping(value = "/git/push", method = RequestMethod.POST)
    public @ResponseBody JsonResponse saveGitPush(
            @RequestParam String applicationName, @RequestParam String userLogin)
            throws ServiceException, CheckException {
        logger.info("--CALL SAVE GIT PUSH");
        applicationService.saveGitPush(applicationName, userLogin);
        return new HttpOk();
    }

}