package fr.treeptik.cloudunit.service.impl;

import javax.inject.Inject;

import fr.treeptik.cloudunit.dao.StatistiqueDAO;
import fr.treeptik.cloudunit.model.Statistique;
import fr.treeptik.cloudunit.service.StatistiqueService;
import org.springframework.stereotype.Service;

@Service
public class StatistiqueServiceImpl implements StatistiqueService {

    @Inject
    private StatistiqueDAO statistiqueDAO;

    @Override
    public void boot() {
        Statistique statistique = new Statistique();
        statistiqueDAO.save(statistique);
    }

    public Statistique last() {
        return statistiqueDAO.findAll().get(0);
    }
}
