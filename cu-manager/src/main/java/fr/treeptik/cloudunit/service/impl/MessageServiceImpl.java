package fr.treeptik.cloudunit.service.impl;

import fr.treeptik.cloudunit.dao.MessageDAO;
import fr.treeptik.cloudunit.exception.ServiceException;
import fr.treeptik.cloudunit.model.Message;
import fr.treeptik.cloudunit.model.User;
import fr.treeptik.cloudunit.service.MessageService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import javax.persistence.PersistenceException;
import java.util.List;

@Service
public class MessageServiceImpl implements MessageService {

	@Inject
	private MessageDAO messageDAO;

	@Override
	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public Message create(Message message) throws ServiceException {
		try {
			return messageDAO.saveAndFlush(message);
		} catch (PersistenceException e) {
			throw new ServiceException(e.getLocalizedMessage(), e);
		}
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public void delete(Message message) throws ServiceException {
		try {
			messageDAO.delete(message);
		} catch (PersistenceException e) {
			throw new ServiceException(e.getLocalizedMessage(), e);
		}
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public List<Message> listByUser(User user, int nbRows)
			throws ServiceException {
		try {
			Pageable pageable = new PageRequest(0, nbRows, sortByLastNameAsc());
			Page<Message> requestedPage = messageDAO.listByUser(user, pageable);
			return requestedPage.getContent();
		} catch (PersistenceException e) {
			throw new ServiceException(e.getLocalizedMessage(), e);
		}
	}
	
	@Override
	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public List<Message> listByApp(User user, String applicationName,
			int nbMessages) throws ServiceException {
		try {
			Pageable pageable = new PageRequest(0, nbMessages,
					sortByLastNameAsc());
			Page<Message> requestedPage = messageDAO.listByApp(user,
					applicationName, pageable);
			return requestedPage.getContent();
		} catch (PersistenceException e) {
			throw new ServiceException(e.getLocalizedMessage(), e);
		}
	}

	/**
	 * Returns a Sort object which sorts persons in ascending order by using the
	 * last name.
	 * 
	 * @return
	 */
	private Sort sortByLastNameAsc() {
		return new Sort(Sort.Direction.DESC, "date");
	}

}
