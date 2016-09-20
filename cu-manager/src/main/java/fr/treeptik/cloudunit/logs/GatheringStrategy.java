package fr.treeptik.cloudunit.logs;

import fr.treeptik.cloudunit.dto.LogResource;
import fr.treeptik.cloudunit.exception.ServiceException;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Created by nicolas on 20/09/2016.
 */
public interface GatheringStrategy<A, B> {
    public A gather(A n, A m, B p) throws ServiceException;
}


