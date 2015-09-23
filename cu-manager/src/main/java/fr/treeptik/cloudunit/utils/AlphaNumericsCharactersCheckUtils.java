package fr.treeptik.cloudunit.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.UnsupportedEncodingException;
import java.text.Normalizer;
import java.text.Normalizer.Form;

/**
 * TODO : Replace with Apache StringUTils
 */
public class AlphaNumericsCharactersCheckUtils {

	private static Logger logger = LoggerFactory
			.getLogger(AlphaNumericsCharactersCheckUtils.class);

	public static String convertToAlphaNumerics(String value)
			throws UnsupportedEncodingException {
		logger.debug("Before : " + value);

		value = new String(value.getBytes("ISO-8859-1"), "UTF-8");
		value = Normalizer.normalize(value, Form.NFD);
		value = value.replaceAll("[^\\p{ASCII}]", "")
				.replaceAll("[^a-zA-Z0-9\\s]", "").replace(" ", "");

		if (value.equalsIgnoreCase("")) {
			value = "default";
		}

		logger.debug("After : "	+ value);

		return value;

	}
}
