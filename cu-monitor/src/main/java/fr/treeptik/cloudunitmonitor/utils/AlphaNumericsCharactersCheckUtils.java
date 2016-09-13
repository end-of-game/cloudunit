package fr.treeptik.cloudunitmonitor.utils;

import java.io.UnsupportedEncodingException;
import java.text.Normalizer;
import java.text.Normalizer.Form;

import org.apache.log4j.Logger;

/**
 * 
 * @author guillaume check and replace non alpha numerics chars
 */
public class AlphaNumericsCharactersCheckUtils {

	private static Logger logger = Logger
			.getLogger(AlphaNumericsCharactersCheckUtils.class);

	public static String convertToAlphaNumerics(String value)
			throws UnsupportedEncodingException {
		logger.info("Nom avant remplacement des caractères spéciaux : " + value);

		value = new String(value.getBytes("ISO-8859-1"), "UTF-8");
		value = Normalizer.normalize(value, Form.NFD);
		value = value.replaceAll("[^\\p{ASCII}]", "")
				.replaceAll("[^a-zA-Z0-9\\s]", "").replace(" ", "");

		if (value.equalsIgnoreCase("")) {
			value = "default";
		}

		logger.info("Nom après remplacement des caractères spéciaux : " + value);

		return value;

	}
}
