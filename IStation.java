import java.rmi.Remote;
import java.rmi.RemoteException;

// O Remote marca essa interface como remota, ou seja, seus métodos podem ser invocados de outro computador via rede usando Java RMI
public interface IStation extends Remote {
    void playBirdSong(int songId) throws RemoteException;
    void changeSoundPattern(String pattern) throws RemoteException;
    void pauseAudio() throws RemoteException;
    void resumeAudio() throws RemoteException;
    void skipForward5s() throws RemoteException;
    void skipBackward5s() throws RemoteException;
}