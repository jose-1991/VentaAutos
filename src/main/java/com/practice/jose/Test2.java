package com.practice.jose;

public class Test2 {

}
class Recordable{
    private int recordingTime;

    public int getRecordingTime() {
        return recordingTime;
    }

    public void setRecordingTime(int recordingTime) {
        this.recordingTime = recordingTime;
    }
}
class Pet extends Recordable{

    public Pet() {
        super.setRecordingTime(30);
    }

class Person extends Recordable{

    public Person() {
        super.setRecordingTime(60);
    }

}
class Package extends Recordable{


    public Package() {
        super.setRecordingTime(120);
    }

}
class Car extends Recordable{

        boolean verdad;

    public Car() {
        super.setRecordingTime(0);
    }
}

class Camera{

   public int recognize(Recordable recordable){

       if (recordable instanceof Pet || recordable instanceof Person ||
               recordable instanceof Package || recordable instanceof Car ) {
          return recordable.getRecordingTime();
       }
       return 0;
   }

   public void record(Recordable recordable){
       int recordingTime = recognize(recordable);
       record(recordingTime);
   }
   public void record(int recordingTime){

   }

}


//    Una smart camara reconoze y graba dependiendo de lo que ve, si se trata de una mascota entonces graba automaticamente 30s,
//    si se trata de una persona graba 1.minutos, si se trata de una persona con un paquete en la mano graba 2 minutos y si es un auto no graba nada.
}