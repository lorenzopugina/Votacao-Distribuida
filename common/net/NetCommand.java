package common.net;

public enum NetCommand {
    REQUEST_ELECTION,   // client -> server
    SEND_ELECTION,      // server -> client

    SEND_VOTE,          // client -> server (e server -> client resposta)
    
    DISCONNECT,         // client -> server
    ERROR               // ambos
}

// Será usado dentro de Message e por outras classes de rede