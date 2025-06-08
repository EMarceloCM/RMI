import java.io.File;
import java.io.IOException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import javax.sound.sampled.*;

// A classe Station estende UnicastRemoteObject para que seus objetos possam ser acessados remotamente via RMI, expostos na rede como serviços remotos.
public class Station extends UnicastRemoteObject implements IStation {
    private final String stationName;
    private volatile boolean tonePlaying = false;
    private Clip currentClip = null;
    private int pauseFramePosition = 0;

    public Station(String stationName) throws RemoteException {
        super();
        this.stationName = stationName;
    }

    @Override
    public synchronized void playBirdSong(int songId) throws RemoteException {
        stopCurrentClipIfPlaying();

        System.out.println("[" + stationName + "] Tocando canto de pássaro ID: " + songId);
        String caminho = "audios" + File.separator + "bird" + songId + ".wav"; // formato de exemplo: audios/bird1.wav
        File arquivo = new File(caminho);

        if (!arquivo.exists()) {
            System.err.println("Arquivo não encontrado: " + caminho);
            return;
        }

        try (AudioInputStream audioIn = AudioSystem.getAudioInputStream(arquivo)) {
            AudioFormat format = audioIn.getFormat();
            DataLine.Info info = new DataLine.Info(Clip.class, format); // define o tipo de linha de áudio (clip) e o formato que será usado para tocar som

            Clip clip = (Clip) AudioSystem.getLine(info); // obtém a linha que casa com a descrição especificada em 'info'
            clip.open(audioIn);
            currentClip = clip;
            pauseFramePosition = 0;

            clip.start();

            clip.addLineListener(event -> {
                if (event.getType() == LineEvent.Type.STOP) {
                    if (clip.getFramePosition() >= clip.getFrameLength()) {
                        clip.close();
                        currentClip = null;
                        pauseFramePosition = 0;
                        System.out.println("[" + stationName + "] Reprodução encerrada.");
                    }
                }
            });

        } catch (UnsupportedAudioFileException | LineUnavailableException | IOException ex) {
            System.err.println("Erro ao tocar áudio: " + ex.getMessage());
        }
    }

    @Override
    public synchronized void pauseAudio() throws RemoteException {
        if (currentClip != null && currentClip.isRunning()) {
            pauseFramePosition = currentClip.getFramePosition();
            currentClip.stop();
            System.out.println("[" + stationName + "] Áudio pausado.");
        } else {
            System.out.println("[" + stationName + "] Nenhum áudio ativo para pausar.");
        }
    }

    @Override
    public synchronized void resumeAudio() throws RemoteException {
        if (currentClip != null && !currentClip.isRunning()) {
            currentClip.setFramePosition(pauseFramePosition);
            currentClip.start();
            System.out.println("[" + stationName + "] Áudio retomado.");
        } else {
            System.out.println("[" + stationName + "] Nenhum áudio pausado para retomar.");
        }
    }

    @Override
    public void changeSoundPattern(String pattern) throws RemoteException {
        tonePlaying = false;
        if (pattern == null || pattern.trim().isEmpty()) {
            System.out.println("[" + stationName + "] Padrão vazio recebido.");
            return;
        }

        Thread toneThread = new Thread(() -> {
            tonePlaying = true;
            try {
                int sum = 0;
                for (char c : pattern.toCharArray()) sum += c;
                // Configurações do tom de áudio com duração de 10 segundos
                float frequency = 200 + (sum % 801);        // a frequência começa em 200 Hz e vai até 1000 Hz (200 + 800)
                float sampleRate = 44100;                   // define a taxa de amostragem (sample rate) do áudio. 44.100 amostras por segundo é o padrão de qualidade de CD
                int numSamples = (int) (10 * sampleRate);   // número total de amostras para 10 segundos de som
                byte[] buffer = new byte[2 * numSamples];   // cria buffer para armazenar o som. Cada amostra de som ocupa 2 bytes

                // laço que gera uma onda senoidal (som puro) de determinada frequência. Converte cada valor da onda para 16 bits (2 bytes)
                for (int i = 0; i < numSamples; i++) {
                    double angle = 2.0 * Math.PI * i * frequency / sampleRate;  // calcula o ângulo da função seno para a amostra de áudio i. A fórmula de uma onda senoidal é sin(2πft)
                    short value = (short) (Math.sin(angle) * Short.MAX_VALUE);  // gera o valor da onda senoidal para esse ângulo. Essa multiplicação resulta em uma faixa de valores de um áudio 16 bits (de -32768 a +32767)
                    buffer[2 * i] = (byte) (value & 0xff);                      // armazena o byte menos significativo (parte baixa do valor de 16 bits). & 0xff garante que só os últimos 8 bits sejam usados
                    buffer[2 * i + 1] = (byte) ((value >> 8) & 0xff);           // armazena o byte mais significativo (parte alta dos 16 bits). value >> 8 move os 8 bits mais altos para a direita. Isso é necessário porque o áudio 16 bits é armazenado como dois bytes (formato little-endian: primeiro o byte baixo, depois o alto)
                }

                AudioFormat format = new AudioFormat(sampleRate, 16, 1, true, false);
                try (SourceDataLine line = AudioSystem.getSourceDataLine(format)) {
                    line.open(format);
                    line.start();
                    for (int i = 0; i < buffer.length && tonePlaying; i += 4096) { // percorre o buffer em blocos de 4096, para enviar o áudio armazenado aos poucos para o sistema de som
                        int len = Math.min(4096, buffer.length - i); // garante que o último bloco lido não ultrapasse o tamanho do buffer
                        line.write(buffer, i, len);                    // enviar para o sistema de áudio, fazendo o som ser reproduzido aos poucos
                    }
                    line.drain(); // aguarda o esvaziamento completo do buffer interno de reprodução da linha de áudio
                    line.stop();
                    line.close();
                }

            } catch (LineUnavailableException e) {
                e.printStackTrace();
            }
        });

        toneThread.start();
    }

    @Override
    public synchronized void skipForward5s() throws RemoteException {
        if (currentClip == null) return;
        long pos = currentClip.getMicrosecondPosition();
        long target = pos + 5_000_000L; // 5 s em microssegundos
        long max = currentClip.getMicrosecondLength();
        currentClip.setMicrosecondPosition(Math.min(target, max));
    }

    @Override
    public synchronized void skipBackward5s() throws RemoteException {
        if (currentClip == null) return;
        long pos = currentClip.getMicrosecondPosition();
        long target = pos - 5_000_000L;
        currentClip.setMicrosecondPosition(Math.max(target, 0L));
    }

    private void stopCurrentClipIfPlaying() {
        if (currentClip != null && currentClip.isOpen()) {
            currentClip.stop();
            currentClip.close();
            currentClip = null;
            pauseFramePosition = 0;
        }
    }
}