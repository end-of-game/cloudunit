package fr.treeptik.cloudunitmonitor.service;

import java.util.List;

import javax.inject.Inject;

import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

import fr.treeptik.cloudunitmonitor.dao.ErrorMessageDAO;
import fr.treeptik.cloudunitmonitor.exception.ServiceException;
import fr.treeptik.cloudunit.model.ErrorMessage;

@Service
public class ErrorMessageService {

	@Inject
	private ErrorMessageDAO errorMessageDAO;

	public ErrorMessage create(ErrorMessage errorMessage)
			throws ServiceException {
		try {
			errorMessage.setStatus(ErrorMessage.UNCHECKED_MESSAGE);
			errorMessageDAO.save(errorMessage);
		} catch (DataAccessException e) {
			throw new ServiceException("Error findAllUnchecked", e);
		}
		return errorMessage;
	}

	public List<ErrorMessage> findAllUnchecked() throws ServiceException {
		try {
			return errorMessageDAO.findAllUnchecked();
		} catch (DataAccessException e) {
			throw new ServiceException("Error findAllUnchecked", e);
		}
	}

	public void setChecked(List<ErrorMessage> errorMessages)
			throws ServiceException {
		try {
			for (ErrorMessage errorMessage : errorMessages) {
				errorMessage.setStatus(ErrorMessage.CHECKED_MESSAGE);
				errorMessageDAO.save(errorMessage);
			}
		} catch (DataAccessException e) {
			throw new ServiceException("Error findAllUnchecked", e);
		}
	}
}
