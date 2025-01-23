import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Scanner;

public class BestGymEver {

    // Filvägarna till in- och ut-filerna
    Path inFilePath = Paths.get("src/AllCustomers.txt");
    Path outFilePath = Paths.get("src/TrainingLog.txt");

    // Konstruktor som tar in en printstream
    public BestGymEver(PrintStream writer) {
        // Funktion för att fråga om en medlem
        searchForMember(writer);
    }

    // Metod för att fråga om en medlem
    private void searchForMember(PrintStream writer) {
        try (
                // PrintWriter för att skriva till träningslogsfilen
                // och Append för att för att nya medlemmar läggs till utan att skriva över gamla
                PrintWriter w = new PrintWriter(Files.newBufferedWriter(outFilePath, java.nio.file.StandardOpenOption.APPEND));
                // Scanner för att läsa från kundfilen
                Scanner sc = new Scanner(inFilePath);
                // Scanner för att ta input från användaren
                Scanner inputScanner = new Scanner(System.in);
        ) {
            // Fråga användaren om personens namn eller personnummer
            System.out.println("Ange namn eller personnummer på medlemmen du söker:");
            String searchACustomer = inputScanner.nextLine().trim().toLowerCase();

            boolean found = false; // boolean om medlemmen hittas

            // Går igenom in filen 2 rader åt gången
            while (sc.hasNextLine()) {
                String firstLine = sc.nextLine(); // Första raden där personnummer och namn står

                // Kontrollerar att det finns en andra rad för att läsa
                if (sc.hasNextLine()) {
                    String secondLine = sc.nextLine(); // Andra raden som innehåller datum

                    // Delar upp första raden för att hämta namn och personnummer
                    String[] parts = firstLine.split(",");
                    if (parts.length == 2) {
                        String personalNumber = parts[0].trim(); // Personnummer
                        String name = parts[1].trim().toLowerCase(); // Namn

                        // Kollar om det matchar antingen med personnummer eller namn
                        if (personalNumber.equals(searchACustomer) || name.equals(searchACustomer)) {
                            // Hittar personen och hanterar träningsloggning
                            manageCustomers(firstLine, secondLine, w, writer);
                            found = true;
                            break; // Avslutar loopen om vi hittar personen
                        }
                    }
                }
            }

            // Om ingen medlem hittas
            if (!found) {
                System.out.println("Obehörig person, ingen medlem eller fd. medlem hittades med det namnet eller personnummer.");
            }
        //Try catch resorces
        } catch (FileNotFoundException e) {
            System.out.println("Filen kunde inte hittas");
            e.printStackTrace();
            System.exit(0);
        } catch (IOException e) {
            System.out.println("Det gick inte att skriva till filen");
            e.printStackTrace();
            System.exit(0);
        } catch (Exception e) {
            System.out.println("Nu har det gått riktigt fel");
            e.printStackTrace();
            System.exit(0);
        }
    }

    // Metod för att hantera kundernas info och kolla medlemsskapet
    private void manageCustomers(String firstLine, String secondLine, PrintWriter w, PrintStream writer) {
        // Delar upp första raden för att hämta namn och personnummer
        String[] parts = firstLine.split(",");

        if (parts.length == 2) {
            String personalNumber = parts[0].trim(); // Personnummer
            String name = parts[1].trim(); // Namn

            // Försöker läsa in datum från andra raden
            try {
                LocalDate lastPaymentDate = LocalDate.parse(secondLine.trim(), DateTimeFormatter.ofPattern("yyyy-MM-dd"));

                // Kontrollerar om den senaste betalningen var inom ett år
                if (lastPaymentDate.isAfter(LocalDate.now().minusYears(1))) {
                    // Om kunden är en nuvarande medlem, loggas det i träningsloggen och skrivs ut i konsolen
                    writer.println(name + ", " + personalNumber + ", " + LocalDate.now());
                    System.out.println("Träning loggad för: " + name + " som är en nuvarande medlem");
                    w.println(name + ", " + personalNumber + ", " + LocalDate.now());
                    w.flush(); // Flushar skrivaren
                } else {
                    // Om kunden är en gammal medlem, loggas inget
                    System.out.println(name + " är en gammal medlem och måste köpa nytt årskort.");
                }
                // Fångar error
            } catch (Exception e) {
                System.out.println("Ogiltigt datumformat på raden: " + secondLine);
            }
        } else {
            // Felmeddelande om första raden inte har rätt format
            System.out.println("Ogiltig rad i kundfilen: " + firstLine);
        }
    }

    // Main för att köra programmet
    public static void main(String[] args) {
        BestGymEver b = new BestGymEver(System.out);
    }
}
