import java.io.Serializable;
import java.util.Random;

public abstract class Animal implements IBehavior, Comparable<Animal>, Serializable {
    public double x;
    public double y;
    public long id;
    public long birthday;
    public long duration;
    Animal(){
        x=0;
        y=0;
        birthday=0;
        duration=0;
        id=(int) (new Random().nextFloat() * (1000000));
    }
    Animal(int _x, int _y, long _b, long _d){
        x=_x;
        y=_y;
        birthday=_b;
        duration=_d;
        id=(int) (new Random().nextFloat() * (1000000));
    }
    Animal(long _b,long _d){
        birthday=_b;
        duration=_d;
        id=(int) (new Random().nextFloat() * (1000000));
        x=(int) (new Random().nextFloat() * (100));
        y=(int) (new Random().nextFloat() * (100));

        //System.out.println("Object id:"+id+" at "+x+" "+y+" living for "+duration);
    }
    public int compareTo(Animal other){
        if(this.id==other.id)
            return 0;
        if(this.id>=other.id)
            return 1;
        return -1;
    }
    public abstract void Move();
    public void Move(double dx,double dy){
        x+=dx;
        y+=dy;
        if(x>100){
            x=100;
        }
        if(y>100){
            y=100;
        }
        if(x<0){
            x=0;
        }
        if(y<0){
            y=0;
        }
        return;
    }
};

class Dog extends Animal implements IBehavior{
    static long amount;
    Dog(){
        super();
        amount+=1;
    }
    Dog(int _x, int _y, long _b, long _d){
        super(_x,_y,_b,_d);
        amount+=1;
    }
    Dog(int _x, int _y, long _b, long _d, boolean _t){ //silent spawn
        super(_x,_y,_b,_d);
    }
    Dog(long _b,long _d){
        super(_b,_d);
        amount+=1;
    }
    public static void resetAmount(){
        amount=0;
    }
    public static void setAmount(long _a){
        amount=_a;
    }
    public static long getAmount(){
        return amount;
    }
    @Override
    public void Move(){
        return;
    }
}

class Cat extends Animal implements IBehavior{
    static long amount;
    Cat(){
        super();
        amount+=1;

    }
    Cat(int _x, int _y, long _b, long _d){
        super(_x,_y,_b,_d);
        amount+=1;
    }
    Cat(int _x, int _y, long _b, long _d, boolean _t){ //silent spawn
        super(_x,_y,_b,_d);
    }
    Cat(long _b,long _d){
        super(_b,_d);
        amount+=1;
    }
    public static void resetAmount(){
        amount=0;
    }
    public static void setAmount(long _a){
        amount=_a;
    }
    public static long getAmount(){
        return amount;
    }
    @Override
    public void Move(){
        return;
    }
}