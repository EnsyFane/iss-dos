package repository;

import domain.models.Drug;
import domain.validation.ValidationException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import test.utils.TestConstants;

import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class TestDrugRepository {
    private static IDrugRepository _drugRepo;

    @BeforeAll
    public static void SetupDB() {
        var props = new Properties();
        try {
            props.load(new FileReader(TestConstants.TEST_JAVA_DIRECTORY + '/' + TestConstants.TEST_CONFIG_FILE));
        } catch (IOException e) {
            System.out.println("Cannot find " + TestConstants.TEST_CONFIG_FILE + ".\n");
            e.printStackTrace();
            return;
        }

        _drugRepo = new DrugRepository(props.getProperty("jdbc.url"));
        _drugRepo.clear();
    }

    @AfterEach
    public void ClearDB() {
        _drugRepo.clear();
    }

    @Test
    public void DrugRepo_GetByIdWithNotStoredId_ReturnsEmptyOptional() {
        var storedInRepo = addDrugToRepo();

        var response = _drugRepo.getById(storedInRepo.getId() + 1);

        assertTrue(response.isEmpty());
    }

    @Test
    public void DrugRepo_GetById_ReturnsCorrectDrug() {
        var storedInRepo = addDrugToRepo();

        var response = _drugRepo.getById(storedInRepo.getId());

        assertTrue(response.isPresent());
        assertEquals(storedInRepo, response.get());
    }

    @Test
    public void DrugRepoWithMultipleDrugs_GetById_ReturnsCorrectDrug() {
        var drugs = addDrugsToRepo(4);

        var actualDrug0 = _drugRepo.getById(drugs.get(0).getId());
        var actualDrug1 = _drugRepo.getById(drugs.get(1).getId());

        assertTrue(actualDrug0.isPresent());
        assertTrue(actualDrug1.isPresent());
        assertEquals(drugs.get(0), actualDrug0.get());
        assertEquals(drugs.get(1), actualDrug1.get());
    }

    @Test
    public void EmptyDrugRepo_AddDrug_DrugIsAdded() {
        var drug = new Drug.Builder().build();

        var response = _drugRepo.add(drug);
        var allDrugs = _drugRepo.getAll();

        assertTrue(response.isEmpty());
        assertEquals(1, allDrugs.size());
        assertEquals(drug, allDrugs.get(0));
    }

    @Test
    public void DrugRepo_AddInvalidDrug_ValidationExceptionIsThrown() {
        var drug = new Drug.Builder().withName(null).build();

        var exception = assertThrows(ValidationException.class, () -> _drugRepo.add(drug));
        assertEquals("Some or all of the properties of the entity were null.", exception.getMessage());
    }

    @Test
    public void DrugRepo_AddNullDrug_IllegalArgumentExceptionThrown() {
        var exception = assertThrows(IllegalArgumentException.class, () -> _drugRepo.add(null));
        assertEquals("Null Drug received.", exception.getMessage());
    }

    @Test
    public void DrugRepoWithMultipleDrugs_GetAll_ReturnsAllStoredDrugs() {
        var drugs = addDrugsToRepo(50);

        var actualDrugs = _drugRepo.getAll();

        assertEquals(50, actualDrugs.size());
        assertEquals(drugs, actualDrugs);
    }

    @Test
    public void DrugRepoWithMultipleDrugs_GetAvailableDrugs_ReturnsOnlyAvailableDrugs() {
        var availableDrugs = addDrugsToRepo(5);
        var unavailableDrug = new Drug.Builder().withInStock(0).build();
        _drugRepo.add(unavailableDrug);

        var actualDrugs = _drugRepo.getAvailableDrugs();

        assertEquals(5, actualDrugs.size());
    }

    @Test
    public void DrugRepoWithDrug_Remove_DrugIsDeleted() {
        var drug = addDrugToRepo();

        var deleted = _drugRepo.remove(drug.getId());
        var inDatabase = _drugRepo.getById(drug.getId());

        assertTrue(inDatabase.isEmpty());
        assertTrue(deleted.isPresent());
        assertEquals(drug, deleted.get());
    }

    @Test
    public void DrugRepo_UpdateDrug_DrugIsUpdated() {
        var drug = addDrugToRepo();
        var updatedDrug = new Drug.Builder().from(drug).withName("my-name").build();

        var oldDrug = _drugRepo.update(updatedDrug);
        var inDatabase = _drugRepo.getById(updatedDrug.getId());

        assertTrue(oldDrug.isPresent());
        assertTrue(inDatabase.isPresent());
        assertEquals(drug, oldDrug.get());
        assertEquals(updatedDrug, inDatabase.get());
    }

    @Test
    public void DrugRepo_UpdateWithInvalidDrug_ValidationExceptionIsThrown() {
        addDrugsToRepo(2);
        var drug = new Drug.Builder().withName(null).build();

        var exception = assertThrows(ValidationException.class, () -> _drugRepo.update(drug));
        assertEquals("Some or all of the properties of the entity were null.", exception.getMessage());
    }

    @Test
    public void DrugRepo_UpdateWithNullDrug_IllegalArgumentExceptionThrown() {
        addDrugsToRepo(2);

        var exception = assertThrows(IllegalArgumentException.class, () -> _drugRepo.update(null));
        assertEquals("Null Drug received.", exception.getMessage());
    }

    private Drug addDrugToRepo() {
        var drug = new Drug.Builder().build();
        _drugRepo.add(drug);
        return drug;
    }

    private List<Drug> addDrugsToRepo(int amount) {
        var drugs = new ArrayList<Drug>();
        for (var i = 0; i < amount; i++) {
            var drug = new Drug.Builder().withName("drug-" + i).build();
            _drugRepo.add(drug);
            drugs.add(drug);
        }

        return drugs;
    }
}
