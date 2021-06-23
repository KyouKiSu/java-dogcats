import java.util.Vector;


class CatAI extends Thread {
    boolean ongoing;
    Vector <Animal> objects;
    int R = 40;
    double S=1;
    long PERIOD;
    CatAI(Vector<Animal> _objects,long _PERIOD){
        objects=_objects;
        PERIOD=_PERIOD;
    }

    public synchronized void run() {
        ongoing=true;
        while(ongoing){
            synchronized (objects){
                for (Animal a:objects) {
                    if(a instanceof Cat){
                        double d = ((a.x-50)*(a.x-50)+(a.y-50)*(a.y-50));
                        double angle=Math.atan2((a.y-50),(a.x-50));
                        a.Move(S/2*Math.cos(angle-(3.14/2)*(d/R/R)),S/2*Math.sin(angle-(3.14/2)*(d/R/R)));
                    }
                }
            }
            try {
                //System.out.println("Cat thread paused");
                wait();
                //System.out.println("Cat thread resumed");
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
    public synchronized void letgo(){
        notify();
    }
    public void mystop(){
        ongoing=false;
    }
    public void setS(double _S){
        S=_S;
    }
    public void setR(int _R){
        R=_R;
    }
    public void reupdateLink(Vector<Animal> _a){
        objects=_a;
    }
}

class DogAI extends Thread {
    boolean ongoing;
    Vector <Animal> objects;
    double S=1;
    long PERIOD;
    DogAI(Vector<Animal> _objects,long _PERIOD){
        objects=_objects;
        PERIOD=_PERIOD;
    }

    public synchronized void run() {
        ongoing=true;
        while(ongoing){
            synchronized (objects){
                for (Animal a:objects) {
                    if(a instanceof Dog){
                        double distanceToClosestCat=101*101+101*101;
                        Cat closestCat=null;
                        for (Animal b:objects) {
                            if(b instanceof Cat){
                                if(distanceToClosestCat>(a.x-b.x)*(a.x-b.x)+(a.y-b.y)*(a.y-b.y)){
                                    closestCat=(Cat)b;
                                    distanceToClosestCat=(a.x-b.x)*(a.x-b.x)+(a.y-b.y)*(a.y-b.y);
                                }
                            }
                        }
                        if(closestCat!=null){
                            if(distanceToClosestCat>10){
                                double angle=Math.atan2(-(a.y-closestCat.y),-(a.x-closestCat.x));
                                a.Move(S/2*Math.cos(angle),S/2*Math.sin(angle));
                            }
                        }
                    }
                }
            }
            try {
                //System.out.println("Dog thread paused");
                wait();
                //System.out.println("Dog thread resumed");
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
    public synchronized void letgo(){
        notify();
    }
    public void mystop(){
        ongoing=false;
    }
    public void setS(double _S){
        S=_S;
    }
    public void reupdateLink(Vector<Animal> _a){
        objects=_a;
    }
}
