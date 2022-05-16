package tn.example.samplews.boundaries;


import jakarta.json.JsonObject;
import jakarta.websocket.EncodeException;
import jakarta.websocket.Encoder;

public class JSONTextEncoder implements Encoder.Text<JsonObject>{

    @Override
    public String encode(JsonObject jsonObject) throws EncodeException {
        return jsonObject.toString();
    }
}
