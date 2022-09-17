package com.ServerBeatBox;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Iterator;

public class MusicServer {
    ArrayList<ObjectOutputStream> clientOutputStreams;

    public static void main(String[] args) {
        new MusicServer().go();
    }

    public class ClientHandler implements Runnable {
        ObjectInputStream in;
        Socket clientSocket;

        public ClientHandler(Socket socket) {
            try {
                clientSocket = socket;
                //ObjectInputStream используется для чтения источников данных,
                // созданных ObjectOutputStream
                in = new ObjectInputStream(clientSocket.getInputStream());
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

        public void run() {
            Object o2 = null;
            Object o1 = null;
            try {
                while ((o1 = in.readObject()) != null) {
                    o2 = in.readObject();
                    System.out.println("read two objects");
                    tellEveryone(o1, o2);
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    public void go() {
        clientOutputStreams = new ArrayList<ObjectOutputStream>();
        try {
            ServerSocket serverSock = new ServerSocket(4242);
            while (true) {
                Socket clientSocket = serverSock.accept(); // accept() будет ждать пока
                //кто-нибудь не захочет подключиться  и когда это происходит возвращает объект типа Socket, то есть воссозданный клиентский сокет
                // установив связь и воссоздав сокет для общения с клиентом можно перейти
                // к созданию потоков ввода/вывода.

                // нужно, чтобы при каждом новом подключении сервер не переходил
                // сразу к общению, а записывал это соединение в какой-то список
                // и переходил к ожиданию нового подключения, а общением с конкретным клиентом занимался бы какой-то вспомогательный сервис
                //записываем в список

                //InputStream – это интерфейс потока чтения (абстрактный класс), описывающий такую способность: «из меня можно читать байты».
               //OutputStream– это, соответственно, интерфейс потока записи, описывающий способность: «в меня можно записывать байты».
                //ObjectOutputStream - это подкласс класса OutputStream, который управляет объектом OutputStream и предоставляет методы для записи примитивных данных (primitive data) или объектов в OutputStream, которым он управляет.
                //Объекты должны быть сериализованы (serialized) перед записью в ObjectOutputStream. Эти объекты должны реализовывать (implement) интерфейс Serializable.
                //getOutputStream() - получаем поток исходящх данных, getInputStream() - получаем пото входящих данных
                // оба они возвращают поток байт. существуют, классы, которые помогают  представить байтовый поток в боле удобном формате
                // getInputSteam() - метод, который возвращает экземпляр класса InputStream , отвечает за входящй поток данных сокеты
                // getOutputSteam() - метод, который возвращает экземпляр класса OutputStream , отвечает за исходящй поток данных сокеты
                ObjectOutputStream out = new ObjectOutputStream(clientSocket.getOutputStream());
                clientOutputStreams.add(out);

                Thread t = new Thread(new ClientHandler(clientSocket));
                t.start();

                System.out.println("Server got a connection");
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void tellEveryone(Object one, Object two) {
        Iterator it = clientOutputStreams.iterator();
        while (it.hasNext()) {
            try {
                ObjectOutputStream out = (ObjectOutputStream) it.next();
                out.writeObject(one);
                out.writeObject(two);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }
}


