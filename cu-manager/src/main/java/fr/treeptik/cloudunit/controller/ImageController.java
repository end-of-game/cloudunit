package fr.treeptik.cloudunit.controller;

import fr.treeptik.cloudunit.exception.ServiceException;
import fr.treeptik.cloudunit.model.Image;
import fr.treeptik.cloudunit.service.ImageService;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.inject.Inject;
import java.util.List;

@Controller
@RequestMapping("/image")
public class ImageController {

	@Inject
	private ImageService imageService;

	@Inject
	private Environment env;

	@RequestMapping(value = "/all", method = RequestMethod.GET)
	public @ResponseBody
	List<Image> listAllImages() throws ServiceException {
		return imageService.findAll();
	}

	@RequestMapping(value = "/module/enabled", method = RequestMethod.GET)
	public @ResponseBody
	List<Image> listAllEnabledModuleImages() throws ServiceException {
		return imageService.findEnabledImagesByType("module");
	}

	@RequestMapping(value = "/server/enabled", method = RequestMethod.GET)
	public @ResponseBody
	List<Image> listAllEnabledServerImages() throws ServiceException {
		return imageService.findEnabledImagesByType("server");
	}

	@RequestMapping(value = "/version", method = RequestMethod.GET)
	public @ResponseBody
	String getVersion() {
		return env.getProperty("api.version");
	}

}
