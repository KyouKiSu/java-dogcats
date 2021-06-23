import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;

class Client {
    public static void main(String[] args) throws Exception {
        System.out.println("STARTTTT CLINNT");
        String host = "localhost";
        int port = 3334;
        // Протокол передачи
        // Запрос (3 целых чила): [операция][аргумент 1][аргумент 2]
        // Ответ (1 целое число): [результат]
        // Операции: 0 - сложение, 1 - умножение
        try {
            System.out.println("Client is running");
            Socket sock = new Socket(host, port);
            DataOutputStream outStream = new DataOutputStream(sock.getOutputStream());
            DataInputStream inputStream = new DataInputStream(sock.getInputStream());
            outStream.writeInt(RequestType.TEXT_TYPE);
            outStream.writeUTF("RUNNNN1");
            outStream.flush();
            outStream.writeInt(RequestType.LIST_TYPE);
            int message = inputStream.readInt();
            System.out.println(message); //ping catch
            System.out.println(inputStream.readUTF()); //message catch
            Thread.currentThread().sleep(500);
            outStream.close();
            sock.close();
        } catch (Exception e) {
            System.out.println(e);
        }
    }
}
