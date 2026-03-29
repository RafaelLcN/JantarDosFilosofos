import java.util.concurrent.Semaphore;

public class jantarFilosofos2 {

    private static final int N = 5;
    private static final Semaphore[] garfos = new Semaphore[N];

    static class Filosofo extends Thread {
        private final int id;
        private final Semaphore esquerdo;
        private final Semaphore direito;

        public Filosofo(int id, Semaphore esquerdo, Semaphore direito) {
            this.id = id;
            this.esquerdo = esquerdo;
            this.direito = direito;
        }

        @Override
        public void run() {
            try {
                while (true) {
                    pensar();
                    pegarGarfos();
                    comer();
                    soltarGarfos();
                }
            } catch (InterruptedException e) {
                System.out.println("Filósofo " + id + " interrompido.");
            }
        }

        private void pensar() throws InterruptedException {
            System.out.println("Filósofo " + id + " está pensando.");
            Thread.sleep((long) (Math.random() * 1000));
        }

        private void pegarGarfos() throws InterruptedException {
            System.out.println("Filósofo " + id + " está com fome.");

            
            esquerdo.acquire();
            System.out.println("Filósofo " + id + " pegou o garfo esquerdo.");

            
            direito.acquire();
            System.out.println("Filósofo " + id + " pegou o garfo direito.");
        }

        private void comer() throws InterruptedException {
            System.out.println("Filósofo " + id + " está comendo.");
            Thread.sleep((long) (Math.random() * 1000));
        }

        private void soltarGarfos() {
            esquerdo.release();
            direito.release();
            System.out.println("Filósofo " + id + " soltou os garfos.");
        }
    }

    public static void main(String[] args) {
        for (int i = 0; i < N; i++) {
            garfos[i] = new Semaphore(1);
        }

        for (int i = 0; i < N; i++) {
            Semaphore esquerdo = garfos[i];
            Semaphore direito = garfos[(i + 1) % N];

            new Filosofo(i, esquerdo, direito).start();
        }
    }
}