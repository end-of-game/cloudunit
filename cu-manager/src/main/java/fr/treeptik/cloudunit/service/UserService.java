package fr.treeptik.cloudunit.service;

import fr.treeptik.cloudunit.exception.CheckException;
import fr.treeptik.cloudunit.exception.ServiceException;
import fr.treeptik.cloudunit.model.User;

import java.util.List;

public interface UserService {

	List<User> findByEmail(String email) throws ServiceException;

	List<User> findAll() throws ServiceException;

	User findById(Integer id) throws ServiceException;

	void remove(User user) throws ServiceException;

	User update(User user) throws ServiceException;

	User create(User user) throws ServiceException, CheckException;

	void activationAccount(User user) throws ServiceException;

	void changePassword(User user, String newPassword) throws ServiceException;

	User findByLogin(String login) throws ServiceException;

	void authentificationGit(User user, String rsa_pub_key)
			throws ServiceException;

	void changeEmail(User user, String newEmail) throws ServiceException;

	String sendPassword(User user) throws ServiceException;

	void deleteAllUsersMessages(User user) throws ServiceException;

	void changeUserRights(String login, String roleValue)
			throws ServiceException, CheckException;

}
