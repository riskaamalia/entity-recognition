package syntatic;

import com.msk.graph.AccumuloLegacyGraph;
import com.msk.graph.Vertex;
import com.msk.graph.Vertices;
import com.msk.graph.indexer.*;
import com.msk.graph.storage.AccumuloLegacyStorage2;
import com.msk.graph.storage.MultiStorage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * Created by 1224A on 9/20/2016.
 */
public class tagging {
    private static final Logger logger = LoggerFactory.getLogger(tagging.class);
    //kemungkinan si kata itu apa aja apa verb noun atau apa
    public Map<String,String> fromKnowledge (String [] words) {
        Map <String,String> result = new HashMap<>() ;
        tagging objhasil = new tagging();

        //ini yang buat semua
        for (String ss:words) {
            result = objhasil.getKnowledge(words);
        }

        return result;
    }

    public Map <String,String> getKnowledge (String [] words) {
        tagging objtagging = new tagging();
        Map <String,String> result = new HashMap<>() ;
        logger.info("Running...");
        logger.debug("DEBUG");
        logger.trace("TRACE");
        int j=0;

        List<String> tabel = new ArrayList<String>(Arrays.asList
                ("data_company","kata_indonesia","dikbud_test","pajak_graph","geoword4","master_address","master_data_jalan","master_data_administrasi","master_data_dbpedia"));
        AccumuloLegacyStorage2[] storages = new AccumuloLegacyStorage2[tabel.size()];
        for (int i = 0; i < tabel.size(); i++) {
            AccumuloLegacyStorage2.Builder builder = AccumuloLegacyStorage2.Builder.RajaampatBuilder();
            builder.setTablename(tabel.get(i));
            builder.setUserAuth("riska");
            builder.setPassword("12345678");
            storages[i] = new AccumuloLegacyStorage2(builder);
        }
        MultiStorage multiStorage = new MultiStorage(storages);
        AccumuloLegacyGraph graph = new AccumuloLegacyGraph("validator", multiStorage);
        graph.addNewIndexer(new NeighborOutIndex(graph.indexes.storage,graph));
        graph.addNewIndexer(new NeighborInIndex(graph.indexes.storage, graph));
        graph.addNewIndexer(new VertexTypeIndex(graph.indexes.storage));
        graph.indexes.indexers.add(new DataTypeIndexer(graph.indexes.storage));
        graph.indexes.indexers.add(new EdgeTypeIndexer(graph.indexes.storage));
        graph.addNewIndexer(new WordIndexer(graph.indexes.storage, graph));

        //string wordnya sama vertices kemungkinan phrasenya
        for (String word:words){
            if (objtagging.next(word,graph) == true ) {
                result.put(word,objtagging.maybe(word,words,graph));
            } else {
                result.put(word,graph.getVertex("word",word).getId()) ;
                logger.info(" === {}",graph.getVertex("word",word).getId());
            }
        }

        return result;
    }

    public boolean next (String words,AccumuloLegacyGraph graph) {
        if (graph.getVertex("word",words).getNeighbors("phrase_builder").iterator().hasNext())
            return true;
        else
            return false;
    }

    public String maybe (String word,String [] tetangganya, AccumuloLegacyGraph graph) {
        Vertices kemungkinan = null,v1=null,v2=null;
        String hasil = "";

        for (String ww:tetangganya) {
            if (word.equals(ww) == false) {
                v1 = graph.getVertex("word",word).getNeighbors("phrase_builder");
                v2 = graph.getVertex("word",ww).getNeighbors("phrase_builder");
                kemungkinan = v1.intersect(v2);
                if (kemungkinan.iterator().hasNext()) {
                    for (Vertex vv:kemungkinan) {
                        hasil = hasil + "==" + vv.getId();
                    }
                }
            }
        }

        return hasil;
    }
}
