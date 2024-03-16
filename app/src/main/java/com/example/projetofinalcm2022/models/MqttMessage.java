package com.example.projetofinalcm2022.models;

import java.io.Serializable;

public class MqttMessage implements Serializable {
    enum typeOfMessage {
        ACK, WAITING_FOR_PLAYERS,
    }
}
