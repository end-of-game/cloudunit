/*
 * LICENCE : CloudUnit is available under the GNU Affero General Public License : https://gnu.org/licenses/agpl.html
 * but CloudUnit is licensed too under a standard commercial license.
 * Please contact our sales team if you would like to discuss the specifics of our Enterprise license.
 * If you are not sure whether the AGPL is right for you,
 * you can always test our software under the AGPL and inspect the source code before you contact us
 * about purchasing a commercial license.
 *
 * LEGAL TERMS : "CloudUnit" is a registered trademark of Treeptik and can't be used to endorse
 * or promote products derived from this project without prior written permission from Treeptik.
 * Products or services derived from this software may not be called "CloudUnit"
 * nor may "Treeptik" or similar confusing terms appear in their names without prior written permission.
 * For any questions, contact us : contact@treeptik.fr
 */

/*
 * LICENCE : CloudUnit is available under the Gnu Public License GPL V3 : https://www.gnu.org/licenses/gpl.txt
 *     but CloudUnit is licensed too under a standard commercial license.
 *     Please contact our sales team if you would like to discuss the specifics of our Enterprise license.
 *     If you are not sure whether the GPL is right for you,
 *     you can always test our software under the GPL and inspect the source code before you contact us
 *     about purchasing a commercial license.
 *
 *     LEGAL TERMS : "CloudUnit" is a registered trademark of Treeptik and can't be used to endorse
 *     or promote products derived from this project without prior written permission from Treeptik.
 *     Products or services derived from this software may not be called "CloudUnit"
 *     nor may "Treeptik" or similar confusing terms appear in their names without prior written permission.
 *     For any questions, contact us : contact@treeptik.fr
 */

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
public class MessageServiceImpl
    implements MessageService {

    @Inject
    private MessageDAO messageDAO;

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public Message create(Message message)
        throws ServiceException {
        try {
            return messageDAO.save(message);
        } catch (PersistenceException e) {
            throw new ServiceException(e.getLocalizedMessage(), e);
        }
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void delete(Message message)
        throws ServiceException {
        try {
            messageDAO.delete(message);
        } catch (PersistenceException e) {
            throw new ServiceException(e.getLocalizedMessage(), e);
        }
    }

    @Override
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
    public List<Message> listByApp(User user, String applicationName,
                                   int nbMessages)
        throws ServiceException {
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
