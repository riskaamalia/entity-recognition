package front;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import syntatic.tagging;
import syntatic.words;

import java.util.Map;

/**
 * Created by 1224A on 9/20/2016.
 */
public class cobaSearch {

    private static final Logger logger = LoggerFactory.getLogger(cobaSearch.class);

    public static void main(String[] args){
        cobaSearch coba = new cobaSearch();
        coba.findResult("adnan buyung nasution");
    }

    public void findResult (String query) {
        words objword = new words();
        tagging objcari = new tagging();
        String [] kumpulan = objword.getToken(query);
        Map<String,String> result = objcari.fromKnowledge(kumpulan);
        for (Map.Entry<String, String> entry : result.entrySet())
        {
            logger.info(entry.getKey() + "/" + entry.getValue());
        }
    }


}
