package lasecbachelorprject.epfl.ch.privacypreservinghousing.crypto;


import java.math.BigInteger;
import java.security.SecureRandom;

/*
 * Class that a represent parties in secure dot product protocol
 */

public class SecureDotProductParty  {

    private BigInteger [][] Q;
    private BigInteger  [][] X;
    private BigInteger  factors[];
    private BigInteger  qTimesX[][];
    private BigInteger [] cPrime;
    private BigInteger [] g;

    private int  dDimension;
    private SecureRandom secureRandom;
    private int  sDimension;

    private BigInteger  b;
    private BigInteger [] c;
    private int  rThRow;
    private BigInteger  R1;
    private BigInteger  R2;
    private BigInteger  R3;

    private BigInteger [] y;
    private BigInteger  z;
    private BigInteger  a;
    private BigInteger  h;
    private BigInteger  rho;
    private BigInteger  rhoMax;
    private BigInteger  beta;

    public BigInteger [] getPrimeVector() {
        return primeVector.clone();
    }

    private BigInteger [] primeVector;

    public BigInteger  dotProduct;
    public BigInteger gain;

    //TODO: Copy the values so that the vector can't be modified in the outside
    public SecureDotProductParty(BigInteger rho){

        secureRandom = new SecureRandom();
        this.rhoMax = new BigInteger(String.valueOf(rho));

    }

    public SecureDotProductParty(){
        secureRandom = new SecureRandom();
    }


    //initiate The dot product for myVector
    public void  initiateDotProduct(){



        //TODO : Constant matrix dimension
        sDimension = 10; //1 + secureRandom.nextInt(10);
        rThRow = secureRandom.nextInt(sDimension);

        //Genrate Q  and compute b. TODO: Skip rth row in the for an assigne later
        Q = new BigInteger [sDimension][sDimension];
        for (int i = 0; i < sDimension ; i++) {
            for (int j = 0; j <sDimension ; j++) {
                //TODO: Correct BOUnd
                    Q[i][j] = BigInteger.ONE;//new BigInteger(String.valueOf(secureRandom.nextInt(100)));

            }
        }


        //TODO: b!!!
        b = BigInteger.ZERO;
        for (int i = 0; i < sDimension; i++) {
            b = b.add(Q[i][rThRow]);// safeAdd(b,Q[i][rThRow]);
        }
        //Generate X
        X = new BigInteger [sDimension][dDimension];

        for (int i = 0; i <sDimension ; i++) {
            for (int j = 0; j <dDimension ; j++) {
                if(i != rThRow){
                    //TODO: Ccrrect next INT LIMIT
                    X[i][j] = BigInteger.valueOf(secureRandom.nextInt(100)+1);
                }
                else{
                    X[i][j] = primeVector[j];
                    }
                }

            }




        //scalar factor to compute the "c" vector.
        //factors[i] = sum(Qji).
        factors = new BigInteger [sDimension];

        factors[rThRow] = BigInteger.ZERO;
        for (int i = 0; i <sDimension ; i++) {
            factors[i] = BigInteger.ZERO;
            if(i != rThRow) {
                for (int j = 0; j < sDimension; j++) {

                    factors[i] =  factors[i].add(Q[j][i]);
                }
            }
        }

        //Generate c
        c = new BigInteger [dDimension];

        for (int i = 0; i < dDimension ; i++) {
            c[i] = BigInteger.ZERO;
            for (int j = 0; j < sDimension ; j++) {
                c[i] = c[i].add(X[j][i].multiply(factors[j]));
            }
        }

        //Generate f
        BigInteger [] f = new BigInteger [dDimension];
        for (int i = 0; i <dDimension ; i++) {
            f[i] = BigInteger.valueOf(secureRandom.nextInt(100) + 1);
        }

        //TODO: Change the 1
        R1 = BigInteger.valueOf(secureRandom.nextInt(100) +1);
        R2 = BigInteger.valueOf(secureRandom.nextInt(100) +1);
        R3 = BigInteger.valueOf(secureRandom.nextInt(100) +1);

        //Compute Q*X
         qTimesX = new BigInteger [sDimension][dDimension];
        for (int i = 0; i <sDimension ; i++) {
            for (int j = 0; j <dDimension ; j++) {
                qTimesX[i][j] = BigInteger.ZERO;
                for (int k = 0; k <sDimension ; k++) {
                    qTimesX[i][j] = qTimesX[i][j].add(Q[i][k].multiply(X[k][j]));
                }
            }
        }

        //c'
        cPrime = new BigInteger [dDimension];
        BigInteger  R1TimesR2 = R1.multiply(R2);
        for (int i = 0; i <dDimension ; i++) {
            cPrime[i] =  c[i].add(R1TimesR2.multiply(f[i]));
        }

        BigInteger  R1TimesR3 = R1.multiply(R3);

        //Generate g
        g = new BigInteger [dDimension];
        for (int i = 0; i <dDimension ; i++) {
            g[i] = R1TimesR3.multiply(f[i]);
        }

    }



    public void sendInitialDataToOtherParty(SecureDotProductParty party){
        party.receiveQTimesX(qTimesX,cPrime,g);
    }

    public void sendAH(SecureDotProductParty party){
        party.receiveAH(a, h);
    }

    private void receiveAH(BigInteger  a, BigInteger  h) {
        this.a = a;
        this.h = h;
        beta = (a.add((h.multiply(R2)).divide(R3))).divide(b);
    }

    public void sendBeta(SecureDotProductParty party){
        party.receiveBeta(beta);
    }

    public void sendAlpha(SecureDotProductParty party){
        party.receiveAlpha(rho);
    }
    private void receiveBeta(BigInteger  beta) {
        this.beta = beta;
    }

    public BigInteger  getBeta(){
            return beta;
    }
    public BigInteger  getAlpha(){return rho;}



    private void receiveQTimesX(BigInteger [][] qTimesX, BigInteger [] cPrime, BigInteger [] g) {
       /* if(this.cPrime.length != primeVector.length || this.g.length != primeVector.length){
            throw new IllegalArgumentException("The dot product can't be computed because of dimensions mismatch. Expected vector size: "+
                                                myvector.length + " received cPrime Size: " + (this.cPrime.length - 1) +" received g size: "+ (this.g.length -1));
        }*/
        this.qTimesX = qTimesX;
        this.cPrime = cPrime;
        this.g = g;
        BigInteger [] rhoVector;


        rho = new BigInteger(rhoMax.bitLength() -1, secureRandom);
        rhoVector = primeVector.clone();
        rhoVector [rhoVector.length - 1 ] = rho;

        /*double[][] qTimesX = (double[][]) get("QtimesX");
        double [] cPrime = (double[]) get("cPrime");
        double [] g = (double[]) get("G");*/
        y = computeY(qTimesX, rhoVector);
        z = vectorElementsSum(y);
        a = z.subtract(normalDotProduct(cPrime,rhoVector));
        h = normalDotProduct(g,rhoVector);

        /*this.put("Y", y);
        this.put("Z", z);
        this.put("A", a);
        this.put("H",h);*/

    }




    private BigInteger [] computeY(BigInteger [][] qTimesX, BigInteger [] myVectorPrime) {
        int dim = qTimesX.length;
        BigInteger  y[] = new BigInteger [dim];
        for (int i = 0; i <dim ; i++) {
            y[i] = normalDotProduct(qTimesX[i], myVectorPrime);
        }
        return y;
    }

    public static BigInteger  normalDotProduct(BigInteger [] v1, BigInteger [] v2 ){
        BigInteger  res = BigInteger.ZERO;
        for (int i = 0; i < v1.length ; i++) {
            res = res.add(v1[i].multiply(v2[i]));
        }
        return res;
    }

    private static BigInteger [] vectorAddition(BigInteger [] v1, BigInteger  v2[]){
        int dim = v1.length;
        BigInteger  [] v = new BigInteger [dim];
        for (int i = 0; i < dim; i++) {
            v[i] = v1[i].add(v2[i]);
        }
        return v;
    }

    public static BigInteger[] vectorsElemMult(BigInteger[] v1, BigInteger[] v2){
        BigInteger[] res = new BigInteger[v1.length];
        for (int i = 0; i <v1.length ; i++) {
            res[i] = v1[i].multiply(v2[i]);
        }
        return res;
    }

    public static BigInteger  vectorElementsSum(BigInteger [] v1){
        int dim = v1.length;
        BigInteger  res = BigInteger.ZERO;
        for (int i = 0; i <dim; i++) {
            res = res.add(v1[i]);
        }
        return res;
    }

    public static BigInteger [] vectorScalarMult(BigInteger []vector, BigInteger  scalar){
        for (int i = 0; i < vector.length ; i++) {
            vector[i] = vector[i].multiply(scalar);
        }
        return vector;
    }

    public static BigInteger[] vectorsElemExpo(BigInteger[] v, int expo){
        for (int i = 0; i < v.length; i++) {
            v[i] = v[i].pow(expo);
        }
        return v;
    }

    public static BigInteger[] vectorsElemModExpo(BigInteger[] v, BigInteger expo, BigInteger group){
        for (int i = 0; i < v.length; i++) {
            v[i] = v[i].modPow(expo,group);
        }
        return v;
    }




    public void setAlphaMax(BigInteger  alphaMax) {
        this.rhoMax = alphaMax;
    }


    private void receiveAlpha(BigInteger  alpha) {
        this.rho = alpha;
    }


    public void getPartialGain(int  l) {
        BigInteger two = BigInteger.ONE.add(BigInteger.ONE);
        gain = (new BigInteger(String.valueOf(dotProduct))).add(two.pow(l-1));
    }

    public void setMyvector(BigInteger [] vector) {
        dDimension = vector.length + 1;
        primeVector = new BigInteger [dDimension];
        System.arraycopy(vector,0,primeVector,0,vector.length);
        primeVector[vector.length] = BigInteger.ONE;

    }

    public static void copyBigIntArrayToIntArray(BigInteger[] src, int  srcPos,BigInteger [] des, int  destPos, int  number ){
        for (int i = srcPos,j= destPos; i <srcPos+number ; i++, j++) {
            des[j] = src[i];
        }
    }

    public BigInteger  getRhoForParticipant() {
        return rho;
    }


    static final long  safeAdd(long  left, long  right) {
        if (right > 0 ? left > Long.MAX_VALUE - right
                : left < Long.MIN_VALUE - right) {
            throw new ArithmeticException("Longoverflow");
        }
        return left + right;
    }

    static final long  safeSubtract(long  left, long  right) {
        if (right > 0 ? left < Long.MIN_VALUE + right
                : left > Long.MAX_VALUE + right) {
            throw new ArithmeticException("Longoverflow");
        }
        return left - right;
    }

    static final long  safeMultiply(long  left, long  right) {
        if (right > 0 ? left > Long.MAX_VALUE/right
                || left < Long.MIN_VALUE/right
                : (right < -1 ? left > Long.MIN_VALUE/right
                || left < Long.MAX_VALUE/right
                : right == -1
                && left == Long.MIN_VALUE) ) {
            throw new ArithmeticException("Integer overflow");
        }
        return left * right;
    }

    static final long  safeDivide(long  left, long  right) {
        if ((left == Long.MIN_VALUE) && (right == -1)) {
            throw new ArithmeticException("Integer overflow");
        }
        return left / right;
    }

    static final long  safeNegate(long  a) {
        if (a == Long.MIN_VALUE) {
            throw new ArithmeticException("Integer overflow");
        }
        return -a;
    }
    static final long  safeAbs(long  a) {
        if (a == Integer.MIN_VALUE) {
            throw new ArithmeticException("Integer overflow");
        }
        return Math.abs(a);
    }



}
