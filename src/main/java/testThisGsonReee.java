import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import it.polimi.ingsw.messages.MessageEnvelope;
import it.polimi.ingsw.messages.MessageID;
import it.polimi.ingsw.messages.concreteMessages.UpdateMessage;
import it.polimi.ingsw.misc.BiElement;
import it.polimi.ingsw.misc.Storage;
import it.polimi.ingsw.model.market.Market;
import it.polimi.ingsw.model.market.Resource;

import java.lang.reflect.Type;
import java.time.format.ResolverStyle;
import java.util.HashMap;
import java.util.Map;

public class testThisGsonReee {
    static BiElement<Resource, Storage> biel;
    static Gson gson = new Gson();
    static Map<BiElement<Resource, Storage>, Integer> actualTest = new HashMap<>();
    static Market board = new Market();
    static MessageEnvelope envelope;

    public static void main(String a[]){

        biel = new BiElement<>(Resource.STONE, Storage.WAREHOUSE_SMALL);
        actualTest.put(biel, 1);

        biel = new BiElement<>(Resource.COIN, Storage.WAREHOUSE_LARGE);
        actualTest.put(biel, 2);

        UpdateMessage ciccio = new UpdateMessage(1, 1,1, board.getMarketBoard(), board.getExtraMarble(), null, null, null, actualTest, actualTest);

        ciccio.setSerializedResources();

        envelope = new MessageEnvelope(MessageID.UPDATE, gson.toJson(ciccio, UpdateMessage.class));

        System.out.println(envelope.getPayload());

        UpdateMessage receivedMessage = envelope.deserializeUpdateMessage();

        System.out.println(receivedMessage.toString());

    }
}
