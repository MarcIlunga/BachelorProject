package lasecbachelorprject.epfl.ch.privacypreservinghousing.helpers;

import org.junit.Test;

import java.util.List;

import lasecbachelorprject.epfl.ch.privacypreservinghousing.crypto.InitializeDatabase;
import lasecbachelorprject.epfl.ch.privacypreservinghousing.user.Participant;

import static lasecbachelorprject.epfl.ch.privacypreservinghousing.crypto.InitializeDatabase.mockDataBase;
import static org.junit.Assert.assertTrue;

public class DataBaseTest {

    @Test
    public void testKeyGenerationAndProof(){
        mockDataBase();
        DataBase dataBase = InitializeDatabase.database;
        List<Participant> participants = InitializeDatabase.participants;

        for (Participant p: participants) {
            p.generatePrivateKey();
            try {
                assertTrue(dataBase.proveKeyToOthers(p));
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }

        }
    }

}