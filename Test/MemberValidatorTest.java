import org.junit.jupiter.api.Test;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class MemberValidatorTest {

    MemberValidator mv =  new MemberValidator();

    @Test
    public void personNumberNormaliserTest(){
        assertEquals("9904011324", mv.personNumberNormaliser("199904011324"));
        assertEquals("0501014832", mv.personNumberNormaliser("20050101-4832"));
    }

    @Test
    public void fullNameNormaliserTest(){
        assertEquals("Bonnie Bäver", mv.fullNameNormaliser("bonnie bäver"));
        assertEquals("Chris Cross", mv.fullNameNormaliser(" CHRIS   CROSS   "));
    }

    @Test
    public void dateNormaliserTest(){
        LocalDate localDate = LocalDate.of(2024, 10, 16);
        assertEquals("2024-10-16", mv.dateNormaliser(localDate));
    }

    @Test
    public void isPersonNumberTest(){
        assertTrue(mv.isPersonNumber("19900109-1312"));
        assertTrue(mv.isPersonNumber("0202020202"));
        assertFalse(mv.isPersonNumber("1231231231231"));
        assertFalse(mv.isPersonNumber("John Doe"));
    }

    @Test
    public void checkForNameInMembersTest(){
        assertNotNull(mv.checkForNameInMembers("Bear Belle"));
        assertNull(mv.checkForNameInMembers("John Doe"));
        assertNotNull(mv.checkForNameInMembers("Ida Idylle"));
        assertNull(mv.checkForNameInMembers("7703021234"));
    }

    @Test
    public void checkForPersonNumberInMembersTest(){
        assertNotNull(mv.checkForPersonNumberInMembers("7703021234"));
        assertNull(mv.checkForPersonNumberInMembers("Bear Belle"));
        assertNotNull(mv.checkForPersonNumberInMembers("8204021234"));
        assertNull(mv.checkForPersonNumberInMembers("198204021234"));
    }

    @Test
    public void checkIfLastPaymentWasWithinAYearTest(){
        String lastPayment1 = mv.dateNormaliser(LocalDate.of(2023, 11, 16));
        String lastPayment2 = mv.dateNormaliser(LocalDate.of(2022, 11, 16));
        LocalDate today = LocalDate.of(2024, 10,16);
        assertTrue(mv.checkIfLastPaymentWasWithinAYear(lastPayment1, today));
        assertFalse(mv.checkIfLastPaymentWasWithinAYear(lastPayment2, today));
    }

    @Test
    public void readFileToListTest(){
        Path path = Paths.get("Test/MemberListShort");
        List<Person> expectedMembers = new ArrayList<>(List.of(
                new Person("7703021234", "Alhambra Aromes", "2024-07-01"),
                new Person("8204021234", "Bear Belle", "2019-12-02"),
                new Person("8512021234", "Chamade Coriola", "2018-03-12")));
        List<Person> members = mv.readFileToList(path);
        for (int i = 0; i < members.size(); i++){
            assertEquals(expectedMembers.get(i).fullName(), members.get(i).fullName());
            assertEquals(expectedMembers.get(i).personNumber(), members.get(i).personNumber());
            assertEquals(expectedMembers.get(i).lastYearlyPayment(), members.get(i).lastYearlyPayment());
        }
    }

    @Test
    public void getVisitorDataTest() {
        LocalDate localDate = LocalDate.of(2024, 10, 16);
        Person person = new Person("7703021234", "Alhambra Aromes", "2024-07-01");
        String output = mv.getVisitorData(person, localDate);
        assertEquals("Alhambra Aromes, 7703021234, 2024-10-16", output);
    }
}
