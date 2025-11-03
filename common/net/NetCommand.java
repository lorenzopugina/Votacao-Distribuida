package common.net;

public enum NetCommand {
    SEND_ELECTION,
    SEND_VOTE,
    END_CONNECTION;
}

// Será usado dentro de Message e por outras classes de rede