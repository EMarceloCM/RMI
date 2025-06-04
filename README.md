# ControlClient & StationServer RMI

Este projeto permite controlar remotamente até três estações de áudio via RMI. O operador pode executar ações como tocar canto de pássaro, alterar o padrão de som, pausar e retomar o áudio em cada estação registrada.

---

## 1. Pré-requisitos

- **Java JDK** instalado em todas as máquinas (estações e estação de controle).  
- **Porta 1099** liberada no firewall de cada máquina de estação (RMI Registry padrão).  
- Todas as máquinas devem estar na **mesma rede local** (sem NAT ou Wi-Fi isolado).

---

## 2. Estrutura dos Arquivos

```bash
├── IStation.java
├── Station.java
├── StationServer.java
└── ControlClient.java
```

---

## 3. Compilação

Em cada máquina (estações 1, 2 e 3, e estação de controle), navegue até a pasta que contém os quatro arquivos `.java` e execute:

```bash
javac IStation.java Station.java StationServer.java ControlClient.java
```

---

## 4. Execução das Máquinas de Estação

Cada instância de estação criará um registro RMI na porta 1099 e fará bind com o nome `Station<ID>`:

- Para a **Estação 1** (ex.: IP 192.168.0.101 e ID 1):
  ```bash
  java StationServer 192.168.0.101 1
  ```
- Para a **Estação 2** (ex.: IP 192.168.0.102 e ID 2):
  ```bash
  java StationServer 192.168.0.102 2
  ```
- Para a **Estação 3** (ex.: IP 192.168.0.103 e ID 3):
  ```bash
  java StationServer 192.168.0.103 3
  ```

Cada servidor ficará aguardando chamadas RMI no nome `Station1`, `Station2` ou `Station3`, conforme o ID informado.

---

## 5. Execução da Estação de Controle

No host da estação de controle, execute o cliente passando até três IPs das estações (mínimo 1, máximo 3):

```bash
java ControlClient <IP1> [<IP2>] [<IP3>]
```

Exemplo com três estações:
```bash
java ControlClient 192.168.0.101 192.168.0.102 192.168.0.103
```

O cliente exibirá um menu:
1. Escolher a estação (1, 2 ou 3).  
2. Selecionar a ação:
   - **Tocar canto de pássaro** (o cliente pedirá um ID inteiro).  
   - **Alterar padrão do gerador de sons** (o cliente pedirá uma string).  
   - **Pausar áudio**.  
   - **Retomar áudio**.  
3. Para trocar de estação ou sair, basta escolher 0 no menu principal.

---

## 6. Observações

- As máquinas de estação devem usar o mesmo **ID** no `StationServer` que corresponderá ao nome RMI (`Station1`, `Station2` ou `Station3`).  
- Verifique se o **firewall** de cada estação permite comunicações na porta 1099 (TCP).  
- Caso precise de apenas 1 ou 2 estações, basta omitir os parâmetros adicionais ao executar o cliente.  
- Se uma estação não estiver disponível, o cliente exibirá exceção ao tentar o `Naming.lookup`.