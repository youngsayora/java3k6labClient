package com.company;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.*;
import java.net.Socket;

class ClientActions extends JFrame {
    private Socket socket;
    private BufferedReader in; // поток чтения из сокета
    private BufferedWriter out; // поток чтения в сокет
    private BufferedReader reader; // поток чтения с консоли
    private String addr; // ip адрес клиента
    private int port; // порт соединения
    private String nickname; // имя клиента
    private String allText; // все сообщения
    private boolean isProgramClosing;

    private JButton sendMsgButton= new JButton("Отправить");
    private JButton closeButton= new JButton("Закрыть");
    private JTextField message=new JTextField("",40);
    JTextArea TextArea = new JTextArea(30,50);

    JFrame frame = new JFrame("Мессенджер");






    public ClientActions(String addr, int port) {
        this.addr = addr;
        this.port = port;
        try {
            this.socket = new Socket(addr, port);
        } catch (IOException e) {
            System.err.println("Socket failed");
        }
        try {
            // потоки чтения из сокета / записи в сокет, и чтения с консоли
            reader = new BufferedReader(new InputStreamReader(System.in));
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));

            new ReadMsg().start(); // нить читающая сообщения из сокета в бесконечном цикле
            //new WriteMsg().start(); // нить пишущая сообщения в сокет приходящие с консоли в бесконечном цикле

            frame.setLayout (new FlowLayout());
            frame.setSize(600,600);
            sendMsgButton.setBounds(480,520,100,35);
            ActionListener actionListener = new TestActionListener();
            CloseActionListener closeActionListener = new CloseActionListener();
            sendMsgButton.addActionListener(actionListener);
            closeButton.addActionListener(closeActionListener);

            message.setBounds(15,520,450,35);


            TextArea.setEditable(false);
            TextArea.setText("Укажите ваш ник: "+"\n");
            JScrollPane scroll = new JScrollPane(TextArea);
            scroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);

            frame.getContentPane().add(scroll);
            frame.add(message);
            frame.add(sendMsgButton);
            frame.add(closeButton);


            frame.setVisible(true);
            frame.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
            frame.addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosing(WindowEvent e) {
                    if(nickname != null){
                        try {
                            //TextArea.append(nickname + " вышел из чата ");
                            out.write(nickname + " вышел из чата "+"\n"); // отправляем на сервер

                            out.flush(); // чистим

                        } catch (IOException ignored) {

                        }







                    }
                    System.exit(1);
                    frame.dispose();


                }
            });



        } catch (IOException e) {
            // Сокет должен быть закрыт при любой
            // ошибке, кроме ошибки конструктора сокета:
            ClientActions.this.downService();
        }

    }
    boolean nicknameset = false;

    public class TestActionListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {


            if(!nicknameset) {
                pressNickname();
                nicknameset = true;
            }else{
                String userWord;
                try {
                    userWord = message.getText();// сообщения с консоли
                    if(!userWord.equals("")) {

                        out.write(nickname + ": " + userWord + "\n"); // отправляем на сервер
                    }

                    out.flush(); // чистим
                    message.setText("");
                } catch (IOException esdfg) {

                }
            }

        }
    }
    public class CloseActionListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            if(nickname != null) {
                try {
                    out.write(nickname + " вышел из чата " + "\n"); // отправляем на сервер
                    out.flush(); // чистим
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
            System.exit(1);
           frame.dispose();


        }
    }

    private void pressNickname() {


        try {
            nickname = message.getText();
            out.write(nickname+" присоединился к чату"+  "\n");
            out.flush();
            message.setText("");
        } catch (IOException ignored) {
        }

    }

    /**
     * закрытие сокета
     */
    private void downService() {
        try {
            if (!socket.isClosed()) {
                socket.close();
                in.close();
                out.close();
            }
        } catch (IOException ignored) {}
    }





    private class ReadMsg extends Thread {
    @Override
    public void run() {

        String str;
        try {
            while (true) {
                str = in.readLine() + "\n";// ждем сообщения с сервера
                TextArea.append(str);

            }
        } catch (IOException e) {

        }
    }
}
public class WriteMsg extends Thread {

    @Override
    public void run() {

            String userWord;
            try {
                userWord = message.getText();// сообщения с консоли
                if(!userWord.equals("")) {

                    out.write(nickname + ": " + userWord + "\n"); // отправляем на сервер
                }

                out.flush(); // чистим
                message.setText("");
            } catch (IOException e) {

            }


    }
}
}

