package org.example;

import com.ibm.mq.jms.*;

import javax.jms.*;

public class Main {
    public static void main(String[] args) {
        try {
            MQQueueConnection mqConn;
            MQQueueConnectionFactory mqCF;
            final MQQueueSession mqQSession;
            MQQueue mqIn;
            MQQueueReceiver mqReciever;

            MQQueue mqOut;
            MQQueueSender mqSender;

            mqCF = new MQQueueConnectionFactory();
            mqCF.setHostName("localhost");

            mqCF.setPort(1414);

            mqCF.setQueueManager("ADMIN");
            mqCF.setChannel("SYSTEM.DEF.SVRCONN");

            mqConn = (MQQueueConnection) mqCF.createQueueConnection();
            mqQSession = (MQQueueSession) mqConn.createQueueSession(true, Session.AUTO_ACKNOWLEDGE);

            mqIn = (com.ibm.mq.jms.MQQueue) mqQSession.createQueue("MQIN");
            mqReciever = (MQQueueReceiver) mqQSession.createReceiver((Queue) mqIn);

            mqOut = (com.ibm.mq.jms.MQQueue) mqQSession.createQueue("MQOUT");
            mqSender = (com.ibm.mq.jms.MQQueueSender) mqQSession.createSender((Queue) mqOut);
            TextMessage outMessage = mqQSession.createTextMessage("outMEsage");

            mqConn.start();

            MessageListener Listener = new MessageListener() {
                @Override
                public void onMessage(Message message) {
                    System.out.println("Got message from " + mqIn.getQueueName());
                    if (message instanceof TextMessage) {
                        try {
                            TextMessage tMessage = (TextMessage) message;
                            String msgText = tMessage.getText();
                            System.out.println(msgText);
                            outMessage.setText(msgText);
                            mqSender.send(outMessage);
                            mqQSession.commit();

                        } catch (JMSException e) {
                            e.printStackTrace();
                        }

                    }
                }
            };


            mqReciever.setMessageListener(Listener);

            System.out.println("Stub Started.");
        }
        catch (JMSException e) {
            e.printStackTrace();
        }
        try {
            Thread.sleep(600000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}