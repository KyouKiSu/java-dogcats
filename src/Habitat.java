import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.*;
import java.util.Timer;

public class Habitat extends JPanel {
    final public JLabel indicator;
    final private JLabel results;
    final public JLabel time;
    public boolean timeAllowed;
    private long previousTime;
    final private long PERIOD = 33; // about 30 fps
    Vector<Animal> animals; // add destructor
    private TreeSet<Long> animalId;
    private HashMap<Animal, Long> animalBirthday;

    double pDog;
    long tDog;
    long dDog;
    double pCat;
    long tCat;
    long dCat;

    final int spriteSize = 75;
    private BufferedImage[] imageCollection;

    private java.util.Timer timer;
    private java.util.Timer tempTimer;
    private boolean status;
    private long firstTick;
    long previousTimePassed;

    private long amountCatTicks;
    private long amountDogTicks;

    private boolean wasEverPaused;

    CatAI myCatAI;
    boolean myCatAIallowed = true;
    DogAI myDogAI;
    boolean myDogAIallowed = true;

    boolean catSpawnAllowed = true;
    boolean dogSpawnAllowed = true;
    Application upper;

    Habitat(Application a,long _tCat, double _pCat, long _dCat, long _tDog, double _pDog, long _dDog) {

        status = false;
        // setLayout(new BorderLayout());
        setLayout(null);
        setBackground(new Color(152, 251, 152));
        initImages();
        upper=a;
        tCat = _tCat;
        pCat = _pCat;
        dCat = _dCat;

        tDog = _tDog;
        pDog = _pDog;
        dDog = _dDog;

        animals = new Vector<>();
        animalId = new TreeSet<>();
        animalBirthday = new HashMap<>();

        myDogAI = new DogAI(animals, PERIOD);
        myDogAI.start();
        myCatAI = new CatAI(animals, PERIOD);
        myCatAI.start();

        timeAllowed = true;

        indicator = new JLabel(" | |  ");
        indicator.setForeground(Color.RED);
        indicator.setFont(new Font("Corbel", Font.BOLD, 45));
        indicator.setVisible(false);

        results = new JLabel("");
        results.setFont(new Font("Corbel", Font.BOLD, 25));
        results.setVisible(false);

        time = new JLabel("");
        time.setFont(new Font("Arial", Font.BOLD, 25));
        time.setVisible(false);

        add(indicator);
        add(results);
        add(time);
    }

        private void initImages() {
            String catImagePath = "src/Pictures/cat.png";
            String dogImagePath = "src/Pictures/dog.png";
            imageCollection = new BufferedImage[2];
            try {
                imageCollection[0] = ImageIO.read(new File(catImagePath));
            } catch (IOException e) {
                System.out.println(catImagePath + " read error!");
            }
            try {
                imageCollection[1] = ImageIO.read(new File(dogImagePath));
            } catch (IOException e) {
                System.out.println(dogImagePath + " read error!");
            }
            return;
        }

    private void update(long timePassed) {
        boolean shouldRepaint = false;

        if (myCatAIallowed) {
            myCatAI.letgo();
            shouldRepaint = true;
        }
        if (myDogAIallowed) {
            myDogAI.letgo();
            shouldRepaint = true;
        }
        if(upper.serverHandler!=null){
            if(upper.serverHandler.onConnection){
                if(upper.myType==1){ // i send dogs
                    if (catSpawnAllowed) {
                        long missedCatTicks = timePassed / tCat - amountCatTicks;
                        for (int i = 0; i < missedCatTicks; i++) {
                            if (new Random().nextFloat() <= pCat) {
                                Cat newCat = new Cat(timePassed, dCat);

                                synchronized (animals) {
                                    animals.add(newCat);
                                }
                                synchronized (animalId) {
                                    animalId.add(newCat.id);
                                }
                                synchronized (animalBirthday) {
                                    animalBirthday.put(newCat, timePassed);
                                }
                                shouldRepaint = true;
                            }
                        }
                    }

                    if (dogSpawnAllowed) {
                        long missedDogTicks = timePassed / tDog - amountDogTicks;
                        for (int i = 0; i < missedDogTicks; i++) {
                            if (new Random().nextFloat() <= pDog) {
                                Dog newDog = new Dog(timePassed, dDog);
                                Thread t = new Thread(new Runnable() {
                                    @Override
                                    public void run() {
                                        upper.serverHandler.sendGameRequest(1,(int)newDog.x,(int)newDog.y);
                                    }
                                });
                                t.start();
                            }
                        }
                    }
                }
                if(upper.myType==2){
                    if (catSpawnAllowed) {
                        long missedCatTicks = timePassed / tCat - amountCatTicks;
                        for (int i = 0; i < missedCatTicks; i++) {
                            if (new Random().nextFloat() <= pCat) {
                                Cat newCat = new Cat(timePassed, dCat);
                                Thread t = new Thread(new Runnable() {
                                    @Override
                                    public void run() {
                                        upper.serverHandler.sendGameRequest(2,(int)newCat.x,(int)newCat.y);
                                    }
                                });
                                t.start();
                            }
                        }
                    }

                    if (dogSpawnAllowed) {
                        long missedDogTicks = timePassed / tDog - amountDogTicks;
                        for (int i = 0; i < missedDogTicks; i++) {
                            if (new Random().nextFloat() <= pDog) {
                                Dog newDog = new Dog(timePassed, dDog);

                                synchronized (animals) {
                                    animals.add(newDog);
                                }
                                synchronized (animalId) {
                                    animalId.add(newDog.id);
                                }
                                synchronized (animalBirthday) {
                                    animalBirthday.put(newDog, timePassed);
                                }
                                shouldRepaint = true;
                            }
                        }
                    }
                }
                if(upper.myType==-1){
                    if (catSpawnAllowed) {
                        long missedCatTicks = timePassed / tCat - amountCatTicks;
                        for (int i = 0; i < missedCatTicks; i++) {
                            if (new Random().nextFloat() <= pCat) {
                                Cat newCat = new Cat(timePassed, dCat);

                                synchronized (animals) {
                                    animals.add(newCat);
                                }
                                synchronized (animalId) {
                                    animalId.add(newCat.id);
                                }
                                synchronized (animalBirthday) {
                                    animalBirthday.put(newCat, timePassed);
                                }
                                shouldRepaint = true;
                            }
                        }
                    }

                    if (dogSpawnAllowed) {
                        long missedDogTicks = timePassed / tDog - amountDogTicks;
                        for (int i = 0; i < missedDogTicks; i++) {
                            if (new Random().nextFloat() <= pDog) {
                                Dog newDog = new Dog(timePassed, dDog);

                                synchronized (animals) {
                                    animals.add(newDog);
                                }
                                synchronized (animalId) {
                                    animalId.add(newDog.id);
                                }
                                synchronized (animalBirthday) {
                                    animalBirthday.put(newDog, timePassed);
                                }
                                shouldRepaint = true;
                            }
                        }
                    }
                }
            }
        }else{
            if (catSpawnAllowed) {
                long missedCatTicks = timePassed / tCat - amountCatTicks;
                for (int i = 0; i < missedCatTicks; i++) {
                    if (new Random().nextFloat() <= pCat) {
                        Cat newCat = new Cat(timePassed, dCat);

                        synchronized (animals) {
                            animals.add(newCat);
                        }
                        synchronized (animalId) {
                            animalId.add(newCat.id);
                        }
                        synchronized (animalBirthday) {
                            animalBirthday.put(newCat, timePassed);
                        }
                        shouldRepaint = true;
                    }
                }
            }

            if (dogSpawnAllowed) {
                long missedDogTicks = timePassed / tDog - amountDogTicks;
                for (int i = 0; i < missedDogTicks; i++) {
                    if (new Random().nextFloat() <= pDog) {
                        Dog newDog = new Dog(timePassed, dDog);

                        synchronized (animals) {
                            animals.add(newDog);
                        }
                        synchronized (animalId) {
                            animalId.add(newDog.id);
                        }
                        synchronized (animalBirthday) {
                            animalBirthday.put(newDog, timePassed);
                        }
                        shouldRepaint = true;
                    }
                }
            }
        }


        synchronized (animals) {
            for (int i = animals.size() - 1; i >= 0; i--) {
                if (timePassed > animals.get(i).birthday + animals.get(i).duration) {
                    synchronized (animalBirthday) {
                        animalBirthday.remove(animals.get(i));
                    }
                    synchronized (animalId) {
                        animalId.remove(animals.get(i).id);
                    }
                    animals.remove(animals.get(i));
                    shouldRepaint = true;
                }
            }
        }

        amountCatTicks = timePassed / tCat;
        amountDogTicks = timePassed / tDog;
        previousTimePassed = timePassed;
        if (shouldRepaint) {
            repaint();
        }
        return;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        animals.forEach((a) -> {
            showAnimal(g, a);
        });
    }

    private void showAnimal(Graphics g, Animal a) {
        int posX = (int) (((double) (a.x)) / 100 * (getWidth() - spriteSize));
        int posY = (int) (((double) (a.y)) / 100 * (getHeight() - spriteSize));
        if (a instanceof Cat) {
            g.drawImage(imageCollection[0], posX, posY, spriteSize, spriteSize, null);
        }
        if (a instanceof Dog) {
            g.drawImage(imageCollection[1], posX, posY, spriteSize, spriteSize, null);
        }
    }

    public void startSimulation() {
        if (status) {
            // System.out.println("Already working");
            return;
        }
        wasEverPaused = false;
        results.setVisible(false);
        status = true;
        animals.clear();
        Cat.resetAmount();
        Dog.resetAmount();
        amountCatTicks = 0;
        amountDogTicks = 0;

        firstTick = System.nanoTime();
        previousTimePassed = 0;
        previousTime = 0;
        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                update((System.nanoTime() - firstTick + previousTime) / 1000000);
                time.setText(String.format("Time passed: %.3f",
                        (float) (System.nanoTime() - firstTick + previousTime) / 1_000_000_000));
                time.setBounds(0, 0, time.getPreferredSize().width, time.getPreferredSize().height);
                return;
            }
        }, 0, PERIOD);
        time.setVisible(timeAllowed);
        updateIndicator(false);
    }

    private void updateIndicator(boolean state) {
        indicator.setBounds(getWidth() - indicator.getWidth(), 0, indicator.getPreferredSize().width,
                indicator.getPreferredSize().height);
        indicator.setVisible(state);
    }

    public void stopSimulationAndShowResults() {
        if (!status) {
            return;
        }
        timer.cancel();
        timer.purge();

        status = false;
        animals.clear();
        // if(!wasEverPaused){
        // previousTime=(System.nanoTime()-firstTick);
        // }
        generateResults();
        Cat.resetAmount();
        Dog.resetAmount();

        updateIndicator(false);
        time.setVisible(false);
        repaint();
    }

    public void pauseSimulation() {
        // System.out.println("BEFORE PAUSE "+previousTimePassed);

        status=false;
        timer.cancel();
        timer.purge();
        wasEverPaused = true;
        previousTime = (System.nanoTime() - firstTick + previousTime);
        // System.out.println("ON PAUSE "+previousTime+" "+previousTime/1000000);
        updateIndicator(true);
        return;
    }

    public void resumeSimulation() {
        // UPDATE STATUS || THIS THING CHANGE
        firstTick = System.nanoTime();
        previousTimePassed = previousTime;
        // System.out.println("ON RESUME "+firstTick/1000000+" "+previousTime/1000000+"
        // "+previousTimePassed/1000000);
        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                update((System.nanoTime() - firstTick + previousTime) / 1000000);
                time.setText(String.format("Time passed: %.3f",
                        (float) (System.nanoTime() - firstTick + previousTime) / 1_000_000_000));
                time.setBounds(0, 0, time.getPreferredSize().width, time.getPreferredSize().height);
                return;
            }
        }, 0, PERIOD);
        status=true;
        updateIndicator(false);
        return;
    }

    private void generateResults() {
        results.setText(createResultText());
        results.setSize(results.getPreferredSize());
        Point middle = new Point(getSize().width / 2, getSize().height / 2);
        Point resultsLocation = new Point(middle.x - results.getPreferredSize().width / 2,
                middle.y - results.getPreferredSize().height / 2);
        results.setLocation(resultsLocation);
        results.setVisible(true);
    }

    public long getDuration() {
        return previousTimePassed;
    }

    public String createResultTextNoHTML() {
        double timeUsed = (double) getDuration() / 1_000;
        long catsAmount = Cat.getAmount();
        long dogsAmount = Dog.getAmount();
        return String.format("Total cats: %d\nTotal dogs: %d\nTotal time: %.3f", catsAmount, dogsAmount, timeUsed);
    }

    public String createResultText() {
        double timeUsed = (double) getDuration() / 1_000;
        long catsAmount = Cat.getAmount();
        long dogsAmount = Dog.getAmount();
        return String.format("<html>Total cats: %d<br>Total dogs: %d<br>Total time: %.3f</html>", catsAmount,
                dogsAmount, timeUsed);
    }

    public void timeSwitch() {
        if (indicator.isVisible()) {
            return;
        }
        time.setBounds(0, 0, time.getPreferredSize().width, time.getPreferredSize().height);
        time.setVisible(timeAllowed);
        return;
    }

    public void changeTCat(long _tCat) {
        if (_tCat < 0) {
            return;
        }
        tCat = _tCat;
        amountCatTicks = previousTimePassed / tCat;
    }

    public void changeTDog(long _tDog) {
        if (_tDog < 0) {
            return;
        }
        tDog = _tDog;
        amountDogTicks = previousTimePassed / tDog;
    }

    public void changePCat(double _PCat) {
        if (_PCat < 0) {
            return;
        }
        pCat = _PCat;
    }

    public void changePDog(double _PDog) {
        if (_PDog < 0) {
            return;
        }
        pDog = _PDog;
    }

    public void changeDCat(long _dCat) {
        if (_dCat < 0) {
            return;
        }
        dCat = _dCat;
    }

    public void changeDDog(long _dDog) {
        if (_dDog < 0) {
            return;
        }
        dDog = _dDog;
    }

    public String existingAnimals() {
        String result = "Id\tType\tBirthday\tLife time\n";

        for (Animal a : animals) {
            result += "" + a.id + "\t";
            if (a instanceof Cat) {
                result += "Cat" + "\t";
            }
            if (a instanceof Dog) {
                result += "Dog" + "\t";
            }
            result += String.format("%.1f", ((float) a.birthday) / 1000) + "\t";
            result += a.duration + "\n";
        }
        return result;
    }

    public void allowCatAI() {
        myCatAIallowed = true;
    }

    public void allowDogAI() {
        myDogAIallowed = true;
    }

    public void restrictCatAI() {
        myCatAIallowed = false;
    }

    public void restrictDogAI() {
        myDogAIallowed = false;
    }

    public void setCatSpawn(boolean input) {
        catSpawnAllowed = input;
    }

    public void setDogSpawn(boolean input) {
        dogSpawnAllowed = input;
    }

    public void saveConfig(ObjectOutputStream configOutputStream) throws IOException, ClassNotFoundException {
        configOutputStream.writeObject(tCat);
        configOutputStream.writeObject(tDog);
        configOutputStream.writeObject(pCat);
        configOutputStream.writeObject(pDog);
        configOutputStream.writeObject(dCat);
        configOutputStream.writeObject(dDog);
        configOutputStream.writeObject(myCatAIallowed);
        configOutputStream.writeObject(myDogAIallowed);
        configOutputStream.writeObject(myCatAI.S);
        configOutputStream.writeObject(myCatAI.R);
        configOutputStream.writeObject(myDogAI.S);
        // objOut.close();
        return;
    }

    public void loadConfig(ObjectInputStream configInputStream) throws IOException, ClassNotFoundException {
        // same for loadAnimals
        Long _TCat = (Long) configInputStream.readObject();
        Long _TDog = (Long) configInputStream.readObject();
        Double _PCat = (Double) configInputStream.readObject();
        Double _PDog = (Double) configInputStream.readObject();
        Long _DCat = (Long) configInputStream.readObject();
        Long _DDog = (Long) configInputStream.readObject();
        Boolean _myCatAIallowed = (Boolean) configInputStream.readObject();
        Boolean _myDogAIallowed = (Boolean) configInputStream.readObject();
        Double cS = (Double) configInputStream.readObject();
        Integer cR = (Integer) configInputStream.readObject();
        Double dS = (Double) configInputStream.readObject();

        changeTCat(_TCat);
        changeTDog(_TDog);
        changePCat(_PCat);
        changePDog(_PDog);
        changeDCat(_DCat);
        changeDDog(_DDog);
        myCatAIallowed = _myCatAIallowed;
        myDogAIallowed = _myDogAIallowed;
        myCatAI.setS(cS);
        myCatAI.setR(cR);
        myDogAI.setS(dS);
        return;
    }

    public void saveAnimalState() throws IOException, ClassNotFoundException {
        pauseSimulation();
        FileOutputStream animalData = new FileOutputStream("animal.dat", false);
        ObjectOutputStream objOut = new ObjectOutputStream(animalData);
        objOut.writeObject(previousTime);
        objOut.writeObject(Cat.getAmount());
        objOut.writeObject(Dog.getAmount());
        objOut.writeObject(tCat);
        objOut.writeObject(tDog);
        objOut.writeObject(pCat);
        objOut.writeObject(pDog);
        objOut.writeObject(myCatAI.S);
        objOut.writeObject(myCatAI.R);
        objOut.writeObject(myDogAI.S);
        objOut.writeObject(animals);
        objOut.close();
        resumeSimulation();
    }

    public void loadAnimalState(String path) throws IOException, ClassNotFoundException {
        pauseSimulation();
        ObjectInputStream in = new ObjectInputStream(new BufferedInputStream(new FileInputStream(path)));

        Long _pt = (Long) in.readObject();
        Long _ca = (Long) in.readObject();
        Long _da = (Long) in.readObject();
        Long _tc = (Long) in.readObject();
        Long _td = (Long) in.readObject();
        Double _pc = (Double) in.readObject();
        Double _pd = (Double) in.readObject();
        Double cs = (Double) in.readObject();
        Integer cr = (Integer) in.readObject();
        Double ds = (Double) in.readObject();
        Vector<Animal> _a = (Vector<Animal>) in.readObject();

        previousTime = _pt;
        changeTCat(_tc);
        changeTDog(_td);
        changePCat(_pc);
        changePDog(_pd);
        myCatAI.setS(cs);
        myCatAI.setR(cr);
        myDogAI.setS(ds);
        in.close();

        synchronized (animals) {
            animals.clear();
            animals = _a;
            animalBirthday.clear();
            animalId.clear();
            Cat.setAmount(_ca);
            Dog.setAmount(_da);
            for (Animal a : animals) {
                animalBirthday.put(a, a.birthday + a.duration);
                animalId.add(a.id);
                if (a instanceof Cat) {
                    Cat.amount += 1;
                }
                if (a instanceof Dog) {
                    Dog.amount += 1;
                }
            }
        }
        myCatAI.reupdateLink(animals);
        myDogAI.reupdateLink(animals);

        amountCatTicks = previousTime / tCat;
        amountDogTicks = previousTime / tDog;

        resumeSimulation();
        repaint();
        return;
    }
    public void spawnAnimal(int type,int x,int y){
        if(status){
            if(type==2){
                if (catSpawnAllowed) {
                    Cat newCat = new Cat(x,y,previousTimePassed,dCat);
                    synchronized (animals) {
                        animals.add(newCat);
                    }
                    synchronized (animalId) {
                        animalId.add(newCat.id);
                    }
                    synchronized (animalBirthday) {
                        animalBirthday.put(newCat, previousTimePassed);
                    }
                    System.out.println("Spawned cat from friend <3");
                    repaint();
                }
            }
            if(type==1){
                if (dogSpawnAllowed) {
                    Dog newDog = new Dog(x,y,previousTimePassed,dDog);
                    synchronized (animals) {
                        animals.add(newDog);
                    }
                    synchronized (animalId) {
                        animalId.add(newDog.id);
                    }
                    synchronized (animalBirthday) {
                        animalBirthday.put(newDog, previousTimePassed);
                    }
                    System.out.println("Spawned dog from friend <3");
                    repaint();
                }
            }
        }
    }
    public void spawnAnimal(int type,int x,int y,long duration){
        System.out.println(status);
        if(status){
            if(type==2){
                if (catSpawnAllowed) {
                    Cat newCat = new Cat(x,y,previousTimePassed,duration);
                    synchronized (animals) {
                        animals.add(newCat);
                    }
                    synchronized (animalId) {
                        animalId.add(newCat.id);
                    }
                    synchronized (animalBirthday) {
                        animalBirthday.put(newCat, previousTimePassed);
                    }
                    repaint();
                }
            }
            if(type==1){
                if (dogSpawnAllowed) {
                    Dog newDog = new Dog(x,y,previousTimePassed,duration);
                    synchronized (animals) {
                        animals.add(newDog);
                    }
                    synchronized (animalId) {
                        animalId.add(newDog.id);
                    }
                    synchronized (animalBirthday) {
                        animalBirthday.put(newDog, previousTimePassed);
                    }
                    repaint();
                }
            }
        }
    }
    public void createDBCats(Vector<Animal> _a){
        long _ca=0;
        if(_a!=null){
            if(_a.size()>0){
                for (Animal c: _a) {
                    if(c instanceof Cat){
                        spawnAnimal(2,(int)c.x,(int)c.y,c.duration);
                        System.out.println("CAT SPAWWNED?");
                    }
                }
            }
        }
    }
    public void createDBDogs(Vector<Animal> _a){
        long _ca=0;
        if(_a!=null){
            if(_a.size()>0){
                for (Animal c: _a) {
                    if(c instanceof Dog){
                        spawnAnimal(1,(int)c.x,(int)c.y,c.duration);
                    }
                }
            }
        }
    }
}
