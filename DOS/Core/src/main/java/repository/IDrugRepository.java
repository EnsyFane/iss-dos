package repository;

import domain.models.Drug;

import java.util.List;

public interface IDrugRepository extends IRepository<Integer, Drug> {
    List<Drug> getAvailableDrugs();
}
