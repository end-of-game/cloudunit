package fr.treeptik.cloudunit.service;

import fr.treeptik.cloudunit.exception.ServiceException;
import fr.treeptik.cloudunit.model.Message;
import fr.treeptik.cloudunit.model.User;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;

import java.util.List;

public interface MessageService {

	@Caching(evict = {
			@CacheEvict(value="messageFindCache", key="#message.author.login"),
			@CacheEvict(value="messageFindCache", key="{#message.author.login, #message.applicationName}")
	})
	Message create(Message message) throws ServiceException;

	@CacheEvict(value="messageFindCache", key="#message.author.login")
	void delete(Message message) throws ServiceException;

	@Cacheable(value="messageFindCache", key="#user.login")
	List<Message> listByUser(User user, int index) throws ServiceException;

	@Cacheable(value="messageFindCache", key="{#user.login, #applicationName}")
	List<Message> listByApp(User user, String applicationName, int index)
			throws ServiceException;

}
