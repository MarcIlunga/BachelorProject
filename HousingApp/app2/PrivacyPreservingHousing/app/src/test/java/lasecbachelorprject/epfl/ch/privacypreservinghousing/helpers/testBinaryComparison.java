package lasecbachelorprject.epfl.ch.privacypreservinghousing.helpers;

import org.junit.Test;

import java.util.Random;

import static org.junit.Assert.assertEquals;

/**
 * Created by Marc Ilunga on 19.05.2016.
 */
public class testBinaryComparison {

    @Test
    public void testComp(){
        int l = 6;
        Random r = new Random();
        int[] aB = new int[l];
        int[] bB = new int[l];

        int a  = r.nextInt(64);
        int b = r.nextInt(32);
        int tmpA = a;
        int tmpB = b;

        int[] gamas = new int[l];
        int[] omegas = new int[l];
        int[] taus = new int[l];

        for (int i = 0; i < l ; i++) {
            aB[i] = (tmpA % 2);
            bB[i] = (tmpB % 2);
            tmpA = tmpA >> 1;
            tmpB = tmpB >> 1;
        }

        for (int i = 0; i < l ; i++) {
            gamas[i] = (aB[i] + bB[i]) % 2;
        }

        for (int i = 0; i <l ; i++) {
           tmpA = (l - i);
            tmpB = 0;
            for (int j = i+1; j <l ; j++) {
                tmpB += gamas[j];
            }
            omegas[i] = tmpA +tmpB -(l-i)*gamas[i];
        }
        int count = 0;
        for (int i = 0; i <l ; i++) {
            taus[i] = omegas[i] + bB[i];
            if(taus[i] == 0){
                count++;
            }

        }
        assertEquals(1, count);
    }

}
