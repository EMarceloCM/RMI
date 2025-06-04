import java.rmi.Naming;
import java.util.Scanner;

public class ControlClient {
    public static void main(String[] args) {
        if (args.length < 1 || args.length > 3) {
            System.out.println("Uso: java ControlClient <hostStation1> [<hostStation2>] [<hostStation3>]");
            System.exit(0);
        }

        try {
            int nStations = args.length;
            IStation[] stubs = new IStation[nStations];
            String[] hostnames = new String[nStations];

            for (int i = 0; i < nStations; i++) {
                hostnames[i] = args[i];
                String stationName = "Station" + (i + 1);
                stubs[i] = (IStation) Naming.lookup("rmi://" + hostnames[i] + "/" + stationName);
            }


            Scanner scanner = new Scanner(System.in);
            while (true) {
                System.out.println("\n---- Estações Disponíveis ----");
                for (int i=0; i < nStations; i++) {
                    System.out.printf("%d. %s (%s)%n", i + 1, "Station" + (i + 1), hostnames[i]);
                }
                System.out.println("0. Sair");

                System.out.print("\nEscolha uma estação: ");
                int escolhaEstacao = scanner.nextInt();
                scanner.nextLine();

                if (escolhaEstacao == 0) {
                    System.out.println("Encerrando estação de controle.");
                    break;
                }
                if (escolhaEstacao < 1 || escolhaEstacao > nStations) {
                    System.out.println("Estação inexistente, tente novamente.");
                    continue;
                }

                IStation estacaoSelecionada = stubs[escolhaEstacao-1];
                String nomeEstacao = "Station" + escolhaEstacao;

                while (true) {
                    System.out.println("\n---- Ações Disponíveis em " + nomeEstacao + " ----");
                    System.out.println("1. Tocar canto de pássaro");
                    System.out.println("2. Alterar padrão de som");
                    System.out.println("3. Pausar áudio");
                    System.out.println("4. Retomar áudio");
                    System.out.println("0. Trocar estação / Sair");
                    System.out.print("\nEscolha uma ação: ");

                    int escolhaAcao = scanner.nextInt();
                    scanner.nextLine();

                    if (escolhaAcao == 1) {
                        System.out.print("Informe o ID do canto (ex: 1, 2 ou 3): ");
                        int songId = scanner.nextInt();
                        scanner.nextLine();
                        estacaoSelecionada.playBirdSong(songId);
                    } else if (escolhaAcao == 2) {
                        System.out.print("Informe o novo padrão de som (string): ");
                        String pattern = scanner.nextLine();
                        estacaoSelecionada.changeSoundPattern(pattern);
                    } else if (escolhaAcao == 3) {
                        estacaoSelecionada.pauseAudio();
                    } else if (escolhaAcao == 4) {
                        estacaoSelecionada.resumeAudio();
                    } else if (escolhaAcao == 0) {
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