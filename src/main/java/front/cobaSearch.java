package front;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import syntatic.tagging;

import java.util.Scanner;

/**
 * Created by 1224A on 9/20/2016.
 */
public class cobaSearch {

    private static final Logger logger = LoggerFactory.getLogger(cobaSearch.class);

    public static void main(String[] args){
        String nama="";
        System.out.println("Coba Tebak Tebakan");
        do {
            System.out.print("Apa pertanyaanmu : ");
            Scanner sc = new Scanner (System.in);
            nama = sc.nextLine ();
            if (!nama.equals("exit")) {
                tagging coba = new tagging();
                logger.info(nama);
                coba.findResult(nama);
            }
        } while (!nama.equals("exit"));


    }

}
