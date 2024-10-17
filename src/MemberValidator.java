import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class MemberValidator {

    Path membersPath = Paths.get("src/MemberList.txt");
    List<Person> members = readFileToList(membersPath);
    Path logbookPath = Paths.get("src/Logbook.txt");
    Scanner scanner = new Scanner(System.in);
    Person person;

    public void mainLoop(){
        while(true){
            System.out.print("Enter name or person number: ");
            String input = getUserInput();
            if (input == null || input.isEmpty()) break;
            input = input.trim();

            if (isPersonNumber(input)){
                input = personNumberNormaliser(input);
                person = checkForPersonNumberInMembers(input);
                if (person == null){
                    System.out.println("There is no such person number in our members list.");
                    continue;
                }
            } else {
                input = fullNameNormaliser(input);
                person = checkForNameInMembers(input);
                if (person == null){
                    System.out.println("The is no member with that name in our members list.");
                    continue;
                }
            }

            if (!checkIfLastPaymentWasWithinAYear(person.lastYearlyPayment(), LocalDate.now())){
                System.out.println("There was over a year since you payed the yearly fee.");
                continue;
            }

            System.out.println("Everything seems in order. Welcome " + person.fullName() + ".");
            updatePTLogbook(person);
        }
    }

    public String getUserInput(){
        return scanner.nextLine();
    }

    public String personNumberNormaliser(String personNumber){
        String normalisedPersonNumber = personNumber.length() % 2 == 1 ?
                personNumber.substring(0,personNumber.length() - 5) +
                        personNumber.substring(personNumber.length() -4) : personNumber;

        return normalisedPersonNumber.length() == 12 ?
                normalisedPersonNumber.substring(2) : normalisedPersonNumber;
    }

    public String fullNameNormaliser(String fullName){
        String[] names = fullName.trim().split("\\s+");
        StringBuilder normalisedName = new StringBuilder();
        for(String name: names){
            normalisedName.append(name.substring(0, 1).toUpperCase())
                    .append(name.substring(1).toLowerCase())
                    .append(" ");
        }
        normalisedName.setLength(normalisedName.length()-1);
        return normalisedName.toString();
    }

    public String dateNormaliser(LocalDate date){
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        return date.format(formatter);
    }

    public boolean isPersonNumber(String data){
        String regex = "^\\d{12}|^\\d{10}|^\\d{8}-\\d{4}|^\\d{6}-\\d{4}";
        return data.matches(regex);
    }

    public Person checkForNameInMembers(String data){
        for (Person person : members){
            if (data.equals(person.fullName())){
                return person;
            }
        }
        return null;
    }

    public Person checkForPersonNumberInMembers(String data){
        for (Person person : members){
            if (data.equals(person.personNumber())){
                return person;
            }
        }
        return null;
    }

    public boolean checkIfLastPaymentWasWithinAYear(String lastPayment, LocalDate today){
        return ChronoUnit.YEARS.between(LocalDate.parse(lastPayment), today) < 1;
    }

    public List<Person> readFileToList(Path path){
        List<Person> members = new ArrayList<>();
        String data;
        try (BufferedReader bufferedReader = new BufferedReader(new FileReader(path.toFile()))) {
            while ((data = bufferedReader.readLine()) != null) {
                data += ", " + bufferedReader.readLine();
                String[] dataParts = data.split(",");
                members.add(new Person(dataParts[0].trim(), dataParts[1].trim(), dataParts[2].trim()));
            }
        } catch (FileNotFoundException e){
            System.err.println("The file at path " + path + " was not found.");
        } catch (IOException e){
            System.err.println("IO exception occurred. " + e.getMessage());
        } catch (Exception e){
            e.printStackTrace();
        }
        return members;
    }

    public String getVisitorData(Person person, LocalDate today){
        return person.fullName() + ", " + person.personNumber() + ", " + dateNormaliser(today);
    }

    public void updatePTLogbook(Person person){
        try (BufferedWriter bufferedWriter = Files.newBufferedWriter(logbookPath, StandardOpenOption.CREATE, StandardOpenOption.APPEND)){
            if (Files.size(logbookPath) == 0){
                bufferedWriter.write("Namn, Person nummer, besökstillfälle (år, månad, dag)");
            }
            bufferedWriter.write("\n" + getVisitorData(person, LocalDate.now()));
        } catch (IOException e) {
            System.err.println("IO exception occurred. " + e.getMessage());
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    public static void main(String[] args){
        MemberValidator mv = new MemberValidator();
        mv.mainLoop();
    }
}
