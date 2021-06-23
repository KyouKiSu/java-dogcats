class Object{
    int a;
    int b;
}

public class Trash {

    public static void main(String args[]) {
        Object p1, p2;
        p1 = new Object();
        p2 = p1;
        p1 = null;
        System.out.println(p1);
        System.out.println(p2);
    }
}