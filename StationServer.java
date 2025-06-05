import java.rmi.Naming;
import java.rmi.registry.LocateRegistry;

public class StationServer {
    public static void main(String[] args) {
        if (args.length < 2) {
            System.out.println("Uso: java StationServer <hostnameOuIP> <stationId>");
            System.exit(0);
        }

        try {
            String hostname = args[0];  // ex: "192.168.0.101"
            String stationId = args[1]; // ex: "1", "2" ou "3"
            System.setProperty("java.rmi.server.hostname", hostname);

            LocateRegistry.createRegistry(1099); // cria uma instância de um registro que aceita requisições na porta 1099
            Station station = new Station("Station" + stationId);
            Naming.rebind("rmi://" + hostname + "/Station" + stationId, station); // registra o objeto remoto station no registro RMI, associando-o a um nome acessível remotamente

            System.out.println("Station" + stationId + " pronta em rmi://" + hostname + "/Station" + stationId);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}