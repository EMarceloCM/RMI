import java.rmi.Naming;
import java.util.Scanner;

public class ControlClient {
    public static void main(String[] args) {
        if (args.length < 1 || args.length > 3) {
            System.out.println("Uso: java ControlClient <hostStation1> [<hostStation2>] [<hostStation3>]");
            System.exit(0);
        }

        try {
            int numStations = args.length;
            IStation[] stubs = new IStation[numStations];
            String[] hostnames = new String[numStations];

            for (int i = 0; i < numStations; i++) {
                hostnames[i] = args[i];
                String stationName = "Station" + (i + 1);
                stubs[i] = (IStation) Naming.lookup("rmi://" + hostnames[i] + "/" + stationName); // registra o objeto remoto station no registro RMI e retorna sua referência
            }

            Scanner scanner = new Scanner(System.in);
            while (true) {
                System.out.println("\n---- Estações Disponíveis ----");
                for (int i=0; i < numStations; i++) {
                    System.out.printf("%d. %s (%s)%n", i + 1, "Station " + (i + 1), hostnames[i]);
                }
                System.out.println("0. Sair");

                System.out.print("\nEscolha uma estação: ");
                int chosenStation = scanner.nextInt();
                scanner.nextLine();

                if (chosenStation == 0) {
                    System.out.println("Encerrando estação de controle.");
                    break;
                }
                if (chosenStation < 1 || chosenStation > numStations) {
                    System.out.println("Estação inexistente, tente novamente.");
                    continue;
                }

                IStation selectedStation = stubs[chosenStation-1];
                String stationName = "Station " + chosenStation;

                while (true) {
                    System.out.println("\n---- Ações Disponíveis em " + stationName + " ----");
                    System.out.println("1. Tocar canto de pássaro");
                    System.out.println("2. Alterar padrão de som");
                    System.out.println("3. Pausar áudio");
                    System.out.println("4. Retomar áudio");
                    System.out.println("5. Recuar -5s");
                    System.out.println("6. Avançar +5s");
                    System.out.println("0. Trocar estação / Sair");
                    System.out.print("\nEscolha uma ação: ");

                    int chosenAction = scanner.nextInt();
                    scanner.nextLine();

                    if (chosenAction == 1) {
                        System.out.print("Informe o ID do canto (ex: 1, 2 ou 3): ");
                        int songId = scanner.nextInt();
                        scanner.nextLine();
                        selectedStation.playBirdSong(songId);
                    } else if (chosenAction == 2) {
                        System.out.print("Informe o novo padrão de som (string): ");
                        String pattern = scanner.nextLine();
                        selectedStation.changeSoundPattern(pattern);
                    } else if (chosenAction == 3) {
                        selectedStation.pauseAudio();
                    } else if (chosenAction == 4) {
                        selectedStation.resumeAudio();
                    } else if (chosenAction == 5) {
                        selectedStation.skipBackward5s();
                    } else if (chosenAction == 6) {
                        selectedStation.skipForward5s();
                    } else if (chosenAction == 0) {
                        break;
                    } else {
                        System.out.println("Ação inválida. Retornando ao menu principal.");
                    }
                }
            }

            scanner.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}