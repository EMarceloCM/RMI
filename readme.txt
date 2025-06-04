1. Compilar os arquivos nas máquinas de estação: javac IStation.java Station.java StationServer.java
2. Como executar em cada máquina de estação:
    java StationServer <IP_LOCAL> <ID_DA_ESTACAO>
    java StationServer 192.168.0.101 1 -> máquina da estação 1
    java StationServer 192.168.0.102 2 -> máquina da estação 2
    java StationServer 192.168.0.103 3 -> máquina da estação 3 
    Cada instância criará um registro RMI na porta 1099 e fará bind com o nome Station1, Station2 ou Station3, respectivamente.
3. Como executar na estação de controle:
    java ControlClient <IP1> <IP2> <IP3>
    java ControlClient 192.168.0.101 192.168.0.102 192.168.0.103
    O cliente irá exibir um menu no console.
    O operador escolhe qual estação ativar (1, 2 ou 3), depois escolhe a ação (“tocar canto de pássaro” ou “alterar padrão do gerador de sons”).
    Ao selecionar “tocar canto de pássaro”, o cliente solicita o ID do canto (um inteiro), que é repassado para o servidor.
    Ao selecionar “alterar padrão do gerador de sons”, o cliente solicita uma String e envia ao método remoto.

Pré-requisitos:
- Java JDK
- Porta 1099 liberada no firewall da máquina das estações
- Executar os programas na mesma rede local (sem NAT ou Wi-Fi isolado)