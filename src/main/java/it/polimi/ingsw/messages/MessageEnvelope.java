package it.polimi.ingsw.messages;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import it.polimi.ingsw.messages.concreteMessages.UpdateMessage;
import it.polimi.ingsw.misc.BiElement;
import it.polimi.ingsw.misc.Storage;
import it.polimi.ingsw.model.market.Resource;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**A MessageEnvelope's object represents a message in transit in a client-server connection.
 * It's a wrapper of the Message, since it flattens all the additional information into a string
 * called {@code payload}*/
public class MessageEnvelope {
    private final MessageID type;
    private final String payload;

    public MessageEnvelope(MessageID type, String payload) {
        this.type = type;
        this.payload = payload;
    }

    public MessageID getMessageID() {
        return type;
    }

    public String getPayload() {
        return payload;
    }

    /**
     * @return the correct version of the update message serialized into tje {@link UpdateMessage}
     */
    public UpdateMessage deserializeUpdateMessage() {

        JsonObject jsonObject = new Gson().fromJson(payload, JsonObject.class);

        UpdateMessageDeserializer updateMessageDeserializer = new UpdateMessageDeserializer();

        return updateMessageDeserializer.deserialize(jsonObject, UpdateMessage.class, null);
    }

    /**
     * Class that deserialize in the correct way the map of Bielement used for the update message
     */
    class UpdateMessageDeserializer implements JsonDeserializer<UpdateMessage> {

        public UpdateMessage deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {

            Type type = new TypeToken<HashMap<BiElement<Resource, Storage>, Integer>>(){}.getType();
            Gson gson = new Gson();
            GsonBuilder builder = new GsonBuilder();
            Gson gsun = builder.enableComplexMapKeySerialization().create();
            JsonObject jsonObject = json.getAsJsonObject();

            Resource[][] marketBoard = gson.fromJson(jsonObject.get("marketBoard"), Resource[][].class);
            Resource extraMarble = gson.fromJson(jsonObject.get("extraMarble"), Resource.class);
            List<Integer> availableProductionCards = gson.fromJson(jsonObject.get("availableProductionCards"), new TypeToken<ArrayList<Integer>>(){}.getType());
            List<BiElement<Integer, Integer>> productionCardsId = gson.fromJson(jsonObject.get("productionCardsId"), new TypeToken<ArrayList<BiElement<Integer, Integer>>>(){}.getType());
            List<BiElement<Integer, Boolean>> leadersId = gson.fromJson(jsonObject.get("leadersId"), new TypeToken<ArrayList<BiElement<Integer, Boolean>>>(){}.getType());
            String serializedAddedResources = gson.fromJson(jsonObject.get("serializedAddedResources"), String.class);
            String serializedRemovedResources = gson.fromJson(jsonObject.get("serializedRemovedResources"), String.class);
            Map<BiElement<Resource, Storage>, Integer> addedResources = gsun.fromJson(serializedAddedResources, type);
            Map<BiElement<Resource, Storage>, Integer> removedResources = gsun.fromJson(serializedRemovedResources, type);

            return new UpdateMessage(
                    jsonObject.get("playerId").getAsInt(),
                    jsonObject.get("playerPos").getAsInt(),
                    jsonObject.get("nextPlayerId").getAsInt(),
                    marketBoard,
                    extraMarble,
                    availableProductionCards,
                    productionCardsId,
                    leadersId,
                    addedResources,
                    removedResources
                    );
        }
    }
}
