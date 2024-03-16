#include <DHTesp.h>
#include <PubSubClient.h>
#include <LiquidCrystal_I2C.h>
#include <Wire.h>
#include <WiFi.h>

// Define the LCD screen
LiquidCrystal_I2C LCD = LiquidCrystal_I2C(0x27, 16, 2);

// Define random ID
String ID_MQTT;
char *letters = "abcdefghijklmnopqrstuvwxyz0123456789";

// Define MQTT Topics
#define ARDUINO_ASK_TOPIC "ARDUINO_ASK_TOPIC"
#define ARDUINO_RECEIVE_TOPIC "ARDUINO_RECEIVE_TOPIC"

// Update every 1 seconds
#define PUBLISH_DELAY 500
unsigned long publishUpdate;

// Wi-Fi settinges
const char *SSID = "Wokwi-GUEST";
const char *PASSWORD = "";

// Define MQTT Broker and PORT
const char *BROKER_MQTT = "broker.hivemq.com";
int BROKER_PORT = 1883;

// Defined ports
const int DHT_PIN = 15;
const int LED_PIN = 12;

// Global variables
WiFiClient espClient;
PubSubClient MQTT(espClient); 
DHTesp dhtSensor;

// Declarations
void startWifi(void);
void initMQTT(void);
void callbackMQTT(char *topic, byte *payload, unsigned int length);
void reconnectMQTT(void);
void reconnectWiFi(void);
void publicarTemperatura(String temperatura);
void publicarHumidade(String humidade);


// Starts the Wi-Fi
void startWifi(void) {
  reconnectWiFi();
}

// Starts everything from MQTT
void initMQTT(void) {
  MQTT.setServer(BROKER_MQTT, BROKER_PORT);
  MQTT.setCallback(callbackMQTT);
}

// Callback from Android 
// --- Get the messages here
void callbackMQTT(char *topic, byte *payload, unsigned int length) {
  String msg;

  // Convert payload to string
  for (int i = 0; i < length; i++) {
    char c = (char)payload[i];
    msg += c;
  }

  Serial.printf("\nTopic: %s\n", topic);
  Serial.printf("Message: %s\n", msg, topic);

  
  if (msg.equals("askPositions")) {
    LCD.clear();
    LCD.setCursor(0, 0);
    LCD.print("ENVIAR posicoes...");
    digitalWrite(LED_PIN, HIGH);

    // publish
    escolhe100posicoes();
    delay(50);

    LCD.clear();
    LCD.setCursor(0, 0);
    LCD.print("ENVIADO para Android");
    digitalWrite(LED_PIN, LOW);

  }
}

// Connects to the Broker with a specific random ID
void reconnectMQTT(void) {
  while (!MQTT.connected()) {
    ID_MQTT = "";
    Serial.print("* Starting connection with broker: ");
    Serial.println(BROKER_MQTT);

    int i = 0;
    for (i = 0; i < 10; i++) {
      ID_MQTT = ID_MQTT + letters[random(0, 36)];
    }

    if (MQTT.connect(ID_MQTT.c_str())) {
      Serial.print("* Connected to broker successfully with ID: ");
      Serial.println(ID_MQTT);
      MQTT.subscribe(ARDUINO_ASK_TOPIC);
    } else {
      Serial.println("* Failed to connected to broker. Trying again in 2 
seconds.");
      delay(2000);
    }
  }
}

// Checks both Wi-Fi and MQTT state, and reconnects if something failed.
void checkWiFIAndMQTT(void) {
  if (!MQTT.connected())
    reconnectMQTT();
  reconnectWiFi();
}

void reconnectWiFi(void) {
  if (WiFi.status() == WL_CONNECTED)
    return;

  WiFi.begin(SSID, PASSWORD); // Conecta na rede WI-FI

  Serial.print("* Connecting to Wifi ");
  while (WiFi.status() != WL_CONNECTED) {
    delay(100);
    Serial.print(".");
  }

  Serial.println("");
  Serial.print("* Successfully connected to Wi-Fi, with local IP: ");
  Serial.println(WiFi.localIP());

  LCD.clear();
  LCD.setCursor(0, 0);
  LCD.print("Finished!");
  LCD.setCursor(0, 1);
  LCD.print("-- ");
  LCD.print(WiFi.localIP());
}

// ==========================================
void setup() {
  Serial.begin(115200);

  pinMode(LED_PIN, OUTPUT);

  LCD.init();
  LCD.backlight();
  LCD.setCursor(0, 0);
  LCD.print("Initializing...");
  LCD.setCursor(0, 1);
  LCD.print("Please wait...");

  dhtSensor.setup(DHT_PIN, DHTesp::DHT22);

  startWifi();
  initMQTT();

  // GERAR semente aleatoria
  randomSeed(analogRead(0));
}

void publicarPosicao() {
    int posicao = random(64);
    String character = String(posicao);
    const char* json = character.c_str();
    MQTT.publish(ARDUINO_RECEIVE_TOPIC, json);
}

void escolhe100posicoes() {
  publicarPosicao(); //1
    publicarPosicao(); //2
    publicarPosicao(); //3
    publicarPosicao(); //4
    publicarPosicao(); //5
    publicarPosicao(); //6
    publicarPosicao(); //7
    publicarPosicao(); //8
    publicarPosicao(); //9
    publicarPosicao(); //10
    publicarPosicao(); //1
    publicarPosicao(); //2
    publicarPosicao(); //3
    publicarPosicao(); //4
    publicarPosicao(); //5
    publicarPosicao(); //6
    publicarPosicao(); //7
    publicarPosicao(); //8
    publicarPosicao(); //9
    publicarPosicao(); //10
    publicarPosicao(); //1
    publicarPosicao(); //2
    publicarPosicao(); //3
    publicarPosicao(); //4
    publicarPosicao(); //5
    publicarPosicao(); //6
    publicarPosicao(); //7
    publicarPosicao(); //8
    publicarPosicao(); //9
    publicarPosicao(); //10
    publicarPosicao(); //1
    publicarPosicao(); //2
    publicarPosicao(); //3
    publicarPosicao(); //4
    publicarPosicao(); //5
    publicarPosicao(); //6
    publicarPosicao(); //7
    publicarPosicao(); //8
    publicarPosicao(); //9
    publicarPosicao(); //10
    publicarPosicao(); //1
    publicarPosicao(); //2
    publicarPosicao(); //3
    publicarPosicao(); //4
    publicarPosicao(); //5
    publicarPosicao(); //6
    publicarPosicao(); //7
    publicarPosicao(); //8
    publicarPosicao(); //9
    publicarPosicao(); //10
    publicarPosicao(); //1
    publicarPosicao(); //2
    publicarPosicao(); //3
    publicarPosicao(); //4
    publicarPosicao(); //5
    publicarPosicao(); //6
    publicarPosicao(); //7
    publicarPosicao(); //8
    publicarPosicao(); //9
    publicarPosicao(); //10
    publicarPosicao(); //1
    publicarPosicao(); //2
    publicarPosicao(); //3
    publicarPosicao(); //4
    publicarPosicao(); //5
    publicarPosicao(); //6
    publicarPosicao(); //7
    publicarPosicao(); //8
    publicarPosicao(); //9
    publicarPosicao(); //10
    publicarPosicao(); //1
    publicarPosicao(); //2
    publicarPosicao(); //3
    publicarPosicao(); //4
    publicarPosicao(); //5
    publicarPosicao(); //6
    publicarPosicao(); //7
    publicarPosicao(); //8
    publicarPosicao(); //9
    publicarPosicao(); //10
    publicarPosicao(); //1
    publicarPosicao(); //2
    publicarPosicao(); //3
    publicarPosicao(); //4
    publicarPosicao(); //5
    publicarPosicao(); //6
    publicarPosicao(); //7
    publicarPosicao(); //8
    publicarPosicao(); //9
    publicarPosicao(); //10
    publicarPosicao(); //1
    publicarPosicao(); //2
    publicarPosicao(); //3
    publicarPosicao(); //4
    publicarPosicao(); //5
    publicarPosicao(); //6
    publicarPosicao(); //7
    publicarPosicao(); //8
    publicarPosicao(); //9
    publicarPosicao(); //10
    publicarPosicao(); //1
    publicarPosicao(); //2
    publicarPosicao(); //3
    publicarPosicao(); //4
    publicarPosicao(); //5
    publicarPosicao(); //6
    publicarPosicao(); //7
    publicarPosicao(); //8
    publicarPosicao(); //9
    publicarPosicao(); //10
    publicarPosicao(); //1
    publicarPosicao(); //2
    publicarPosicao(); //3
    publicarPosicao(); //4
    publicarPosicao(); //5
    publicarPosicao(); //6
    publicarPosicao(); //7
    publicarPosicao(); //8
    publicarPosicao(); //9
    publicarPosicao(); //10
}


void loop() {
  // Loop every 1 second
  
  if ((millis() - publishUpdate) >= PUBLISH_DELAY) {
    publishUpdate = millis();
    // Check if Wi-Fi and MQTT are up.
    checkWiFIAndMQTT();

    // MQTT Keep-alive loop
    Serial.println("Passou 1 segundo");
    LCD.clear();
    LCD.setCursor(0, 0);
    LCD.print("Estou 'a espera do Android");

    //escolhe100posicoes();
    MQTT.loop();
  }
}


