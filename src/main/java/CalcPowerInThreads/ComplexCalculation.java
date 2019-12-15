package CalcPowerInThreads;

import java.math.BigInteger;

public class ComplexCalculation {
    public BigInteger calculateResult(BigInteger base1, BigInteger power1, BigInteger base2, BigInteger power2) {
        BigInteger result;
        PowerCalculatingThread t1 = new PowerCalculatingThread(base1, power1);
        PowerCalculatingThread t2 = new PowerCalculatingThread(base2, power2);
        t1.start();
        t2.start();

        try {
            t1.join(2000);
            t2.join(2000);
        }catch(InterruptedException e){
            e.printStackTrace();
        }
        if(t1.isAlive()) t1.interrupt();
        if(t2.isAlive()) t2.interrupt();
        result = t1.getResult().add(t2.getResult());
        return result;
    }

    private static class PowerCalculatingThread extends Thread {
        private BigInteger result = BigInteger.ONE;
        private BigInteger base;
        private BigInteger power;

        public PowerCalculatingThread(BigInteger base, BigInteger power) {
            this.base = base;
            this.power = power;
        }

        @Override
        public void run() {
           /*
           Implement the calculation of result = base ^ power
           */
            for(BigInteger i = BigInteger.ZERO; i.compareTo(power) < 0; i = i.add(BigInteger.ONE)) {
                result = result.multiply(base);
                if(this.isInterrupted())
                    return;
            }
        }

        public BigInteger getResult() { return result; }
    }


    public static void main(String[] args) {
        ComplexCalculation calc = new ComplexCalculation();
        BigInteger result = calc.calculateResult(BigInteger.valueOf(2), BigInteger.valueOf(0), BigInteger.valueOf(2), BigInteger.valueOf(0));
        System.out.println(result);
    }
}
