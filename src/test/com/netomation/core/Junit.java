package test.com.netomation.core;

import main.com.netomation.api.SocialNetwork;
import main.com.netomation.cache.MongoCache;
import main.com.netomation.data.Filter;
import main.com.netomation.data.Globals;
import main.com.netomation.data.Messages;
import main.com.netomation.data.Preferences;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.io.File;

import static org.junit.jupiter.api.Assertions.*;

public class Junit {

    @BeforeAll
    public static void initTesting() {
        File prefFileToDelete = new File(Globals.PREFERENCES_FILE_NAME);
        if(prefFileToDelete.exists() && !prefFileToDelete.delete()) {
            System.err.println("Note! the preferences file could not be deleted! please delete by hand.");
        }
        Preferences.initPreferences();
        Filter.initFilter();
        assertTrue(MongoCache.validConnection(), "MongoDb not working! please turn on mongoDB.");
    }

    @Nested
    public class CacheTesting {

        @Test
        void createUsersTest() {
            for(int i = 0 ; i < 100 ; i++) {
                SocialNetwork.SocialNetworkUser user = MongoTestingUtils.addRandomUserToDb();
                assertNotEquals(0, MongoCache.getInstance().getDbKeyByUserId(user.getId()).length());
            }
            assertEquals(0, MongoCache.getInstance().getDbKeyByUserId("should-fail-id1").length());
            assertEquals(0, MongoCache.getInstance().getDbKeyByUserId("should-fail-id2").length());
            assertEquals(0, MongoCache.getInstance().getDbKeyByUserId("should-fail-id3").length());
            assertEquals(0, MongoCache.getInstance().getDbKeyByUserId("should-fail-id4").length());
        }

        @Test
        void deleteWholeDbTest() {
            MongoCache.getInstance().deleteAllDataFromDatabase();
        }

    }

    @Nested
    public class FilterTesting {

        @Test
        void languagePassFilterTest() {
            assertFalse(Filter.languagePassFilter("Egyptian", false));
            assertFalse(Filter.languagePassFilter("avl", false));
            assertFalse(Filter.languagePassFilter("apc", false));
            assertFalse(Filter.languagePassFilter("acm", false));
            assertFalse(Filter.languagePassFilter("ajp", false));
            assertFalse(Filter.languagePassFilter("auz", false));
            assertTrue(Filter.languagePassFilter("Egyptian", true));
            assertTrue(Filter.languagePassFilter("avl", true));
            assertTrue(Filter.languagePassFilter("apc", true));
            assertTrue(Filter.languagePassFilter("acm", true));
            assertTrue(Filter.languagePassFilter("ajp", true));
            assertTrue(Filter.languagePassFilter("auz", true));
            assertTrue(Filter.languagePassFilter("israel", false));
            assertTrue(Filter.languagePassFilter("isr", false));
            assertTrue(Filter.languagePassFilter("usa", false));
            assertTrue(Filter.languagePassFilter("uk", false));
            assertTrue(Filter.languagePassFilter("Usa", false));
            assertTrue(Filter.languagePassFilter("ger", false));
            assertFalse(Filter.languagePassFilter("israel", true));
            assertFalse(Filter.languagePassFilter("isr", true));
            assertFalse(Filter.languagePassFilter("usa", true));
            assertFalse(Filter.languagePassFilter("uk", true));
            assertFalse(Filter.languagePassFilter("Usa", true));
            assertFalse(Filter.languagePassFilter("ger", true));
        }

        @Test
        void descriptionPassFilterTest() {
            String[] backup = Globals.FILTER_KEYWORDS;
            Globals.FILTER_KEYWORDS = new String[] {"Computer", "System", "PC", "Comp"};
            assertTrue(Filter.descriptionPassFilter("Computer User", true));
            assertTrue(Filter.descriptionPassFilter("Compu", true));
            assertTrue(Filter.descriptionPassFilter("PC user", true));
            assertTrue(Filter.descriptionPassFilter("system User", true));
            assertFalse(Filter.descriptionPassFilter("Computer User", false));
            assertFalse(Filter.descriptionPassFilter("Compu", false));
            assertFalse(Filter.descriptionPassFilter("PC user", false));
            assertFalse(Filter.descriptionPassFilter("system User", false));
            assertFalse(Filter.descriptionPassFilter("Camputer", true));
            assertFalse(Filter.descriptionPassFilter("Campo", true));
            assertFalse(Filter.descriptionPassFilter("Psc use", true));
            assertFalse(Filter.descriptionPassFilter("sysstem User", true));
            assertTrue(Filter.descriptionPassFilter("Camputer", false));
            assertTrue(Filter.descriptionPassFilter("Campo", false));
            assertTrue(Filter.descriptionPassFilter("Psc use", false));
            assertTrue(Filter.descriptionPassFilter("sysstem User", false));
            Globals.FILTER_KEYWORDS = backup;
        }

        @Test
        void agePassFilterTest() {
            int originalMin = Globals.MIN_AGE_FILTER;
            int originalMax = Globals.MAX_AGE_FILTER;
            assertTrue(Filter.agePassFilter(50));
            assertTrue(Filter.agePassFilter(-3));
            assertTrue(Filter.agePassFilter(13));
            assertTrue(Filter.agePassFilter(0));
            Globals.MIN_AGE_FILTER = 30;
            assertTrue(Filter.agePassFilter(50));
            assertTrue(Filter.agePassFilter(30));
            assertFalse(Filter.agePassFilter(-3));
            assertFalse(Filter.agePassFilter(13));
            assertFalse(Filter.agePassFilter(0));
            Globals.MAX_AGE_FILTER = 60;
            assertTrue(Filter.agePassFilter(50));
            assertFalse(Filter.agePassFilter(-3));
            assertFalse(Filter.agePassFilter(13));
            assertFalse(Filter.agePassFilter(0));
            Globals.MIN_AGE_FILTER = originalMin;
            assertTrue(Filter.agePassFilter(50));
            assertFalse(Filter.agePassFilter(70));
            assertFalse(Filter.agePassFilter(-3));
            assertTrue(Filter.agePassFilter(13));
            assertTrue(Filter.agePassFilter(0));
            Globals.MIN_AGE_FILTER = 50;
            Globals.MAX_AGE_FILTER = 20;
            assertFalse(Filter.agePassFilter(50));
            assertFalse(Filter.agePassFilter(20));
            assertFalse(Filter.agePassFilter(37));
            assertFalse(Filter.agePassFilter(-3));
            assertFalse(Filter.agePassFilter(13));
            assertFalse(Filter.agePassFilter(0));
            Globals.MIN_AGE_FILTER = originalMin;
            Globals.MAX_AGE_FILTER = originalMax;
        }

        @Test
        void countryPassFilterTest() {
            Globals.FILTER_COUNTRIES = new String[] {"Israel", "usa"};
            assertTrue(Filter.countryPassFilter("israel", true));
            assertTrue(Filter.countryPassFilter("usa", true));
            assertFalse(Filter.countryPassFilter("germany", true));
            assertFalse(Filter.countryPassFilter("italy", true));
            assertFalse(Filter.countryPassFilter("israel", false));
            assertFalse(Filter.countryPassFilter("usa", false));
            assertTrue(Filter.countryPassFilter("germany", false));
            assertTrue(Filter.countryPassFilter("italy", false));
        }
    }

    @Nested
    public class MessagingTesting {

        @Test
        void generateGeneralMessageTest() {
            Messages.generateMessage("testing-id-1");
            Messages.generateMessage("testing-id-2");
            Messages.generateMessage("testing-id-3");
        }

        @Test
        void generateSpecificMessageTest() {
            SocialNetwork.SocialNetworkUser user1 = MongoTestingUtils.addRandomUserToDb();
            SocialNetwork.SocialNetworkUser user2 = MongoTestingUtils.addRandomUserToDb();
            SocialNetwork.SocialNetworkUser user3 = MongoTestingUtils.addRandomUserToDb();
            Messages.generateMessage(user1.getId());
            Messages.generateMessage(user2.getId());
            Messages.generateMessage(user3.getId());
            MongoCache.getInstance().deleteAllDataFromDatabase();
        }

    }

}
