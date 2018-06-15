package test.com.netomation.core;

public class DummyClass {

    public synchronized void doYourThing() {
        System.out.println("Going to sleep...");
        try{wait();}catch (Exception exp){exp.printStackTrace();}
        System.out.println("Program Exiting...");
        System.exit(0);
    }

}
