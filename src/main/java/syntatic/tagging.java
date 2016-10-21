package syntatic;

import com.msk.graph.AccumuloLegacyGraph;
import com.msk.graph.CachedVertices;
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
    public Map<String,Vertices> fromKnowledge (String [] words) {
        Map <String,Vertices> result = new HashMap<>() ;
        tagging objhasil = new tagging();

        //ini yang buat semua
        for (String ss:words) {
            result = objhasil.getKnowledge(words);
        }

        return result;
    }

    public Map <String,Vertices> getKnowledge (String [] words) {
        tagging objtagging = new tagging();
        Map <String,Vertices> result = new HashMap<>() ;
        int j=0;

        List<String> tabel = new ArrayList<String>(Arrays.asList
                ("kata_indonesia","data_company","dikbud_test","pajak_graph","master_address","master_data_jalan","master_data_administrasi","master_data_dbpedia"));
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
                List<Vertex> hh = new ArrayList<>();
                Vertices aa = new CachedVertices(graph,hh);
                result.put(word,aa) ;
                try {
                    logger.info(" === {}",graph.getVertex("word",word).getId());
                } catch (NullPointerException op) {
                    logger.info("Tidak ada");
                }

            }
        }

        return result;
    }

    public boolean next (String words,AccumuloLegacyGraph graph) {
        try {
            if (graph.getVertex("word",words).getNeighbors("phrase_builder").iterator().hasNext())
                return true;
            else
                return false;
        } catch (NullPointerException op) {
            return false;
        }

    }

    public Vertices maybe (String word,String [] tetangganya, AccumuloLegacyGraph graph) {
        Vertices kemungkinan = null,v1=null,v2=null;

        try {
            for (String ww:tetangganya) {
                if (word.equals(ww) == false) {
                    if (kemungkinan == null || kemungkinan.iterator().hasNext() == false) {
                        v1 = graph.getVertex("word", word).getNeighbors("phrase_builder");
                        v2 = graph.getVertex("word", ww).getNeighbors("phrase_builder");
                        kemungkinan = v1.intersect(v2);
                    } else if (kemungkinan.iterator().hasNext()){
                        v1 = kemungkinan;
                        v2 = graph.getVertex("word", ww).getNeighbors("phrase_builder");
                        kemungkinan = v1.intersect(v2);
                    }
                }
            }
        } catch (NullPointerException op) {
            logger.info("Tidak ada");
        }


        return kemungkinan;
    }

    public void findResult (String query) {
        words objword = new words();
        tagging objcari = new tagging();
        String [] kumpulan = objword.getToken(query);
        Map<String,Vertices> result = objcari.fromKnowledge(kumpulan);
        try {
            for (Map.Entry<String, Vertices> entry : result.entrySet())
            {
                logger.info(entry.getKey() + "/");
                for (Vertex vv:entry.getValue()) {
                    System.out.print(vv.getId()+"==");
                }
                System.out.println();
            }
        } catch (NullPointerException op) {
            logger.info("Tidak Ada");
        }

        objcari.mungkinEntity(result);
        for (Map.Entry<String, Vertices> entry : objcari.mungkinEntity(result).entrySet())
        {
            logger.info(entry.getKey() + "/");
            for (Vertex vv:entry.getValue()) {
                System.out.print(vv.getId()+"==");
            }
            System.out.println();
        }
    }

    public Map<String,Vertices> mungkinEntity (Map<String,Vertices> kumpulan) {
        Map<String,Vertices> entitynya = new HashMap<>();
        Vertices hh = kumpulan.entrySet().iterator().next().getValue();
        tagging obj = new tagging();
        try {
            for (Map.Entry<String, Vertices> entry : kumpulan.entrySet()) {
                hh = hh.intersect(entry.getValue());
                if (hh != null) {
                    entitynya.put(entry.getKey(), obj.getType(hh.iterator().next()));
                }
            }
        } catch (NullPointerException op) {
            logger.info("Tidak Ada");
        }

        return entitynya;
    }

    public Vertices getType (Vertex phrase) {
        List<String> tabel = new ArrayList<String>(Arrays.asList
                ("kata_indonesia","data_company","dikbud_test","pajak_graph","geoword4","master_address","master_data_jalan","master_data_administrasi","master_data_dbpedia"));
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

        Vertices tetangga = graph.getVertex(phrase.getType(),phrase.getVertexId()).getNeighbors("wordtype");
        return tetangga;
    }
}
