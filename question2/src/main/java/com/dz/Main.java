package com.dz;

import java.io.IOException;
import java.io.BufferedReader;
import java.io.RandomAccessFile;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

import ru.pflb.mq.dummy.exception.DummyException;
import ru.pflb.mq.dummy.implementation.*;
import ru.pflb.mq.dummy.interfaces.*;

class RandomAccessFileInputStream extends java.io.InputStream {
    private final RandomAccessFile file;

    public RandomAccessFileInputStream(RandomAccessFile file) {
        this.file = file;
    }

    @Override
    public int read() throws IOException {
        return file.read();
    }

    @Override
    public int read(byte[] b, int off, int len) throws IOException {
        return file.read(b, off, len);
    }
}

public class Main {
    public static void main(String[] args) {
        if(args.length != 1){
            throw new RuntimeException();
        }
        Connection conn = new ConnectionImpl();

        conn.start();

        Session session = conn.createSession(true);

        String namefile;
        namefile = args[0];
        //String namefile = "./src/main/resources/test.txt";
            try {
                Destination destionation = session.createDestination("Queue1");
                Producer producer = session.createProducer(destionation);
                // Открываем файл в режиме чтения байтов
                RandomAccessFile file = new RandomAccessFile(namefile, "r");

                while (true) {
                    // Возвращаемся в начало файла
                    file.seek(0);

                    // Используем InputStreamReader с кодировкой UTF-8
                    BufferedReader reader = new BufferedReader(new InputStreamReader(
                            new RandomAccessFileInputStream(file), StandardCharsets.UTF_8));

                    String line;
                    while ((line = reader.readLine()) != null) {
                        producer.send(line);  // Отправляем строку (в данном случае выводим в консоль)
                        try {
                            Thread.sleep(2000);
                        } catch (InterruptedException e) {
                            throw new RuntimeException(e);
                        }
                    }

                    // Закрываем BufferedReader, чтобы корректно завершить чтение
                    reader.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            } catch (DummyException e) {
                throw new RuntimeException(e);
            }

        session.close();
        conn.close();

    }
}