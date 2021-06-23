import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.Vector;

public class JServer {
    public static void main(String[] args) {
        Server myServer = new Server();
        myServer.startServer();
    }
}

class Server {
    private Vector<User> users;
    private int PORT;

    Server() {
        PORT = 3334;
    }

    Server(int _p) {
        PORT = _p;
    }

    public void initServer() {
        users = new Vector<User>();
        UserInputHandler.users = users;
    }

    public void startServer() {
        initServer();
        try {
            ServerSocket ss = new ServerSocket(PORT);
            System.out.println("Server is running");
            while (true) {
                Socket s = ss.accept();
                UserInputHandler p =
                        new UserInputHandler(s);
                UserHeartBeat h =
                        new UserHeartBeat(p.getUser());
                p.start();
                h.start();
            }

        } catch (Exception e) {
            System.out.println("Server down!");
            System.out.println(e);
        }
    }
}

class UserInputHandler extends Thread {
    private User user;
    static Vector<User> users;
    private final int TIMEOUT = 10000;
    private final long MAX_ATTEMPTS = 50;
    private final long TIME_PER_ATTEMPT = 10;
    // if no any responses for any
    private final long WAIT_DELAY = 1000;
    private final long MAX_TIME_WAIT = 10000;

    // outdated, useless
    public void updateUser() {
        user.lastTimeActive = System.nanoTime();
    }

    public void unpairUser(){
        synchronized (users){
            if(users.size()>0){
                for (User b:users){
                    if(b.partnerId==user.id){
                        synchronized (b){
                            b.partnerId=-1;
                            b.pairType=-1;
                            synchronized (b.outputUser){
                                try {
                                    b.outputUser.writeInt(RequestType.UNPAIR_TYPE);
                                    b.outputUser.flush();
                                } catch (Exception e) {
                                    System.out.println("removeUser error"+e);
                                    b.active = false;
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    public void removeUser() {
        user.close(); // close streams and socket
        unpairUser();
        synchronized (users) {
            users.remove(user);
        }
        System.out.println("Stopped " + user.getData());
        listUpdate();
    }

    public UserInputHandler(Socket _s) throws SocketException {
        _s.setSoTimeout(TIMEOUT);
        user = new User(_s, System.nanoTime());
        synchronized (users) {
            users.add(user);
        }
        listUpdate();
    }

    public static void listUpdate(){
        if(users.size()>0){
            String message = "";
            synchronized (users){

                for (User u:users) {
                    message+="#"+u.id;
                }
            }
            message=message.substring(1);
            synchronized (users){
                for(User u:users){
                    try{
                        synchronized (u.outputUser){
                            u.outputUser.writeInt(RequestType.LIST_TYPE);
                            u.outputUser.writeLong(u.id);
                            u.outputUser.writeUTF(message);
                            u.outputUser.flush();
                        }
                    }catch (Exception e){
                        // User died
                        System.out.println("Unknown error on listUpdate");
                        System.out.println(e);
                        u.active = false;
                    }
                }
            }
        }
    }

    @Override
    public void run() {
        System.out.println("Started new client " + user.getData());
        String message;
        String response;
        while (user.active) {
            try {
                synchronized (user.inputUser) {
                    int type = user.inputUser.readInt();
                    switch (type) {
                        case RequestType.TEXT_TYPE:
                            message=user.inputUser.readUTF();
                            System.out.println("Message from " + user.getData() + ":" + message);
                            break;
                        case RequestType.PING_TYPE:
                            System.out.println("Successful ping of user " + user.getData());
                            break;
                        case RequestType.PAIR_TYPE:
                            message=user.inputUser.readUTF();
                            if(user.partnerId==-1){
                                long p = Long.parseLong(message);
                                boolean f = false;
                                User pp=null;
                                synchronized (users){
                                    for (User u:users) {
                                        if(u.id==p && u.partnerId==-1){
                                            pp=u;
                                            f=true;
                                        }
                                    }
                                }
                                if(f && pp!=null){
                                    synchronized (user){
                                        user.partnerId=pp.id;
                                        user.pairType=1;
                                        user.outputUser.writeInt(RequestType.PAIR_TYPE);
                                        user.outputUser.writeInt((int)user.pairType);
                                    }
                                    synchronized (pp){
                                        pp.partnerId=user.id;
                                        pp.pairType=2; // 2 cats 1 dogs
                                        pp.outputUser.writeInt(RequestType.PAIR_TYPE);
                                        pp.outputUser.writeInt((int)pp.pairType);
                                    }
                                    System.out.println("User#"+user.id+" connected to User#"+pp.id);
                                }
                            }else{
                                synchronized (user.outputUser){
                                    System.out.println("User#"+user.id+" sent fail game request");
                                    user.outputUser.writeInt(RequestType.FAILPAIR_TYPE);
                                }
                            }
                            break;
                        case RequestType.UNPAIR_TYPE:
                            unpairUser();
                            user.partnerId=-1;
                            user.pairType=-1;
                            break;
                        case RequestType.GAME_TYPE:
                            int animalType = user.inputUser.readInt();
                            int x = user.inputUser.readInt();
                            int y = user.inputUser.readInt();
                            if(user.partnerId!=-1){
                                User pp=null;
                                synchronized (users){
                                    for (User u:users) {
                                        if(u.id==user.partnerId){
                                            pp=u;
                                        }
                                    }
                                }
                                synchronized (pp.outputUser){
                                    pp.outputUser.writeInt(RequestType.GAME_TYPE);
                                    pp.outputUser.writeInt(animalType);
                                    pp.outputUser.writeInt(x);
                                    pp.outputUser.writeInt(y);
                                    pp.outputUser.flush();
                                    System.out.println("I readdressed package from "+user.id+" to "+pp.id);
                                }
                            }else{
                                System.out.println("User#"+user.id+" tried to play with nobody...");
                                user.outputUser.writeInt(RequestType.UNPAIR_TYPE);
                                user.outputUser.flush();
                                System.out.println("Told him - his alone.");
                            }
                            break;
                        default:
                            System.out.println("Type is " + type + ". Bad request type. Kill connection.");
                            user.active = false;
                    }
                }
            } catch (java.net.SocketTimeoutException e) {
                System.out.println("Client " + user.getData() + " no response. Disconnected.");
                user.active = false;
            } catch (java.io.EOFException e) {
                System.out.println("Client " + user.getData() + " closed stream. Disconnected.");
                user.active = false;
            } catch (Exception e) {
                System.out.println("Unknown error occurred");
                System.out.println(e);
                user.active = false;
            }

        }
        removeUser();
    }

    public User getUser() {
        return user;
    }

    public boolean getPackage() {
        return false;
    }

    public void createEvent() {
        return;
    }

    public void checkEvent() {
        return;
    }
}

class UserHeartBeat extends Thread {
    // how often will server check user
    private static final long PERIOD = 5000;
    User user;

    UserHeartBeat(User _u) {
        user = _u;
    }

    public void ping() {
        boolean result = false;
        try {
            synchronized (user.outputUser) {
                user.outputUser.writeInt(RequestType.PING_TYPE); // type of request, 0 for ping
                user.outputUser.flush();
            }
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    @Override
    public void run() {
        while (user.active) {
            try {
                ping();
                sleep(PERIOD);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}

class User {
    private static long newId = 0;
    public long id;
    public long partnerId;
    public long pairType;
    public Socket socket;
    public long lastTimeActive;
    public DataInputStream inputUser;
    public DataOutputStream outputUser;
    public boolean active;

    User(Socket _s, long _lastTimeActive) {
        id = newId; partnerId=-1; pairType=-1;
        newId += 1;
        socket = _s;
        try {
            inputUser = new DataInputStream(socket.getInputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            outputUser = new DataOutputStream(socket.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
        lastTimeActive = _lastTimeActive;
        active = true;
    }

    public String getData() {
        return "" + socket.getInetAddress() + socket.getPort();
    }

    public void close() {
        try {
            inputUser.close();
        } catch (IOException e) {
            System.out.println("Failed to close inputUser");
        }
        try {
            outputUser.close();
        } catch (IOException e) {
            System.out.println("Failed to close outputUser");
        }
        try {
            socket.close();
        } catch (IOException e) {
            System.out.println("Failed to close socket");
        }
    }
}
