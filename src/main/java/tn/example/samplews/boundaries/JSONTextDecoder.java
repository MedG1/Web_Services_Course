package tn.example.samplews.boundaries;

import jakarta.json.Json;
import jakarta.json.JsonException;
import jakarta.json.JsonObject;
import jakarta.json.JsonReader;
import jakarta.websocket.DecodeException;
import jakarta.websocket.Decoder;

import java.io.StringReader;

public class JSONTextDecoder implements Decoder.Text<JsonObject>{
    @Override
    public JsonObject decode(String s) throws DecodeException {
        try(JsonReader jr = Json.createReader(new StringReader(s))){
            return jr.readObject();
        }
    }

    @Override
    public boolean willDecode(String s) {
        try(JsonReader jr = Json.createReader(new StringReader(s))){
            jr.readObject();
            return true;
        } catch(JsonException e){
            return false;
        }
    }
}
