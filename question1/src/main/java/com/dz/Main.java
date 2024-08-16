package com.dz;

import java.util.ArrayList;
import java.util.Collections;


import ru.pflb.mq.dummy.exception.DummyException;
import ru.pflb.mq.dummy.implementation.*;
import ru.pflb.mq.dummy.interfaces.*;


public class Main {
    public static void main(String[] args) {

        Connection conn = new ConnectionImpl();

        conn.start();

        Session session = conn.createSession(true);

        try {
            Destination destionation = session.createDestination("Queue1");
            Producer producer = session.createProducer(destionation);

            ArrayList<String> list = new ArrayList<String>();
            String s[] = {"Четыре", "Пять", "Шесть"};

            Collections.addAll(list, s);


            // System.out.println(list);+ " " + str
            for (String str : list) {
                producer.send(str);
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }


        }catch (DummyException e) {
            throw new RuntimeException(e);
        }





        session.close();
        conn.close();



    }
}