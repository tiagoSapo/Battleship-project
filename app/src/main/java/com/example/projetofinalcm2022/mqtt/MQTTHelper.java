package com.example.projetofinalcm2022.mqtt;

import android.content.Context;
import android.util.Log;

import org.eclipse.paho.client.mqttv3.DisconnectedBufferOptions;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttCallbackExtended;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;

import info.mqtt.android.service.Ack;
import info.mqtt.android.service.MqttAndroidClient;


public class MQTTHelper {

    public MqttAndroidClient mqttAndroidClient;

    final String server = "tcp://broker.hivemq.com";
    final String TAG = "abc";
    private String name;

    public static final String ARDUINO_ASK_TOPIC = "ARDUINO_ASK_TOPIC";
    public static final String ARDUINO_RECEIVE_TOPIC = "ARDUINO_RECEIVE_TOPIC";

    public MQTTHelper(Context context, String name) {
        this.name = name;
        mqttAndroidClient = new MqttAndroidClient(context, server, name, Ack.AUTO_ACK);
    }

    public void setCallback(MqttCallbackExtended callback) {
        mqttAndroidClient.setCallback(callback);
    }

    public void connect() {
        MqttConnectOptions mqttConnectOptions = new MqttConnectOptions();
        mqttConnectOptions.setAutomaticReconnect(true);
        mqttConnectOptions.setCleanSession(true);

        try {
            mqttAndroidClient.connect(mqttConnectOptions, null, new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {

                    //Adjusting the set of options that govern the behaviour of Offline (or Disconnected) buffering of messages
                    DisconnectedBufferOptions disconnectedBufferOptions = new DisconnectedBufferOptions();
                    disconnectedBufferOptions.setBufferEnabled(true);
                    disconnectedBufferOptions.setBufferSize(100);
                    disconnectedBufferOptions.setPersistBuffer(false);
                    disconnectedBufferOptions.setDeleteOldestMessages(false);
                    mqttAndroidClient.setBufferOpts(disconnectedBufferOptions);

                    Log.w("abc", "Connection with MQTT has been established with " + server);
                    MQTTHelper.this.subscribeToTopic(ARDUINO_RECEIVE_TOPIC);
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    Log.w(TAG, "Failed to connect to: " + server + exception.toString());
                }
            });


        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void stop() {
        try {
            if (mqttAndroidClient.isConnected())
                mqttAndroidClient.disconnect();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }


    public void subscribeToTopic(String topic) {
        try {
            mqttAndroidClient.subscribe(topic, 0, null, new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    Log.w(TAG, "Subscribed! " + topic);
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    Log.w(TAG, "Subscribed fail!");
                }
            });

        } catch (Exception ex) {
            Log.e("abc", "Exception subscribing: " + ex.getCause());
            ex.printStackTrace();
        }
    }

    public void unsubscribeFromTopic(String topic) {
        mqttAndroidClient.unsubscribe(topic);
    }

    public String getName() {
        return name;
    }
}
