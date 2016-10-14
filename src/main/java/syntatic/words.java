package syntatic;

/**
 * Created by 1224A on 9/20/2016.
 */
public class words {

    //words
    public String [] getToken (String query) {
//        yang belum itu punctuation,stopword kaya yg ga penting
        String [] result = query.split(" ");
        words token = new words();
        result = token.getTerm(result);
        return result;
    }

    //term buat cari yang unik
    public String [] getTerm (String [] query) {
        String [] unique = query;
        //find duplicate for get unique
        return unique;
    }

    public String stemming (String word) {
        String hasil = word;
        return hasil;
    }

    public String spellchecking (String query) {
        String result = "";

        return result;
    }
}
