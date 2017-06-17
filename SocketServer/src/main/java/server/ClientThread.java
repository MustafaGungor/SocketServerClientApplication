package server;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.net.Socket;

/**
 * Client Thread Yönetimini sağlayan class
 *
 * @author MustafaGungor
 * @since 18/06/2017.
 */
class ClientThread extends Thread {

    private DataInputStream dataInputStream = null;
    private PrintStream printStream = null;
    private Socket clientSocket = null;
    private final ClientThread[] threads;
    private int maxClientSayisi;

    public ClientThread(Socket clientSocket, ClientThread[] threads) {
        this.clientSocket = clientSocket;
        this.threads = threads;
        maxClientSayisi = threads.length;
    }

    @Override
    public void run() {
        int maxClientSayisi = this.maxClientSayisi;
        ClientThread[] threads = this.threads;

        try {
            dataInputStream = new DataInputStream(clientSocket.getInputStream());
            printStream = new PrintStream(clientSocket.getOutputStream());
            printStream.println("Nickname: ");
            String name = dataInputStream.readLine().trim();
            printStream.println("Merhaba " + name + "! Mesajlasma uygulamasina hosgeldiniz. Uygulamadan cikmak icin -quit- yazip enterlayin.");
            for (int i = 0; i < maxClientSayisi; i++) {
                if (threads[i] != null && threads[i] != this) {
                    threads[i].printStream.println(name + " adli kisi odaya baglandi.");
                }
            }
            while (true) {
                String satir = dataInputStream.readLine();
                if (satir.startsWith("/quit")) {
                    break;
                }
                for (int i = 0; i < maxClientSayisi; i++) {
                    if (threads[i] != null) {
                        threads[i].printStream.println("<" + name + ">: " + satir);
                    }
                }
            }
            for (int i = 0; i < maxClientSayisi; i++) {
                if (threads[i] != null && threads[i] != this) {
                    threads[i].printStream.println(name + " adlı kisi odadan ayrildi.");
                }
            }
            printStream.println(name + " Gule Gule!");

            /*
             * Yeni bir Clientın bağlanabilmesi için aktif olan Client null yapılır
             */
            for (int i = 0; i < maxClientSayisi; i++) {
                if (threads[i] == this) {
                    threads[i] = null;
                }
            }
            dataInputStream.close();
            printStream.close();
            clientSocket.close();
        } catch (IOException e) {
        }
    }
}