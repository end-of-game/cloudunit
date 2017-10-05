package fr.treeptik.cloudunit.service;

import javax.mail.MessagingException;

import fr.treeptik.cloudunit.model.Statistique;

/**
 * Created by guillaume on 09/10/16.
 */
public interface StatistiqueService {
    Statistique last();
    void boot();
}
