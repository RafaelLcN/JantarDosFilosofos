import java.util.concurrent.Semaphore;

public class jantarFilosofos {

    private static final int N = 5;

    static class Filosofo extends Thread {
        private final int id;
        private final Semaphore garfoEsquerdo;
        private final Semaphore garfoDireito;
        private final Semaphore mutex;

        public Filosofo(int id, Semaphore garfoEsquerdo, Semaphore garfoDireito, Semaphore mutex) {
            this.id = id;
            this.garfoEsquerdo = garfoEsquerdo;
            this.garfoDireito = garfoDireito;
            this.mutex = mutex;
        }

        @Override
        public void run() {
            try {
                while (!Thread.currentThread().isInterrupted()) {
                    pensar();
                    pegarGarfos();
                    comer();
                    liberarGarfos();
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }

        private void pensar() throws InterruptedException {
            System.out.println("Filósofo " + id + " está pensando");
            Thread.sleep((long) (Math.random() * 1000));
        }

        private void pegarGarfos() throws InterruptedException {
            System.out.println("Filósofo " + id + " está com fome");

            mutex.acquire();
            try {
                garfoEsquerdo.acquire();
                garfoDireito.acquire();
            } finally {
                mutex.release();
            }
        }

        private void comer() throws InterruptedException {
            System.out.println("Filósofo " + id + " está comendo");
            Thread.sleep((long) (Math.random() * 1000));
        }

        private void liberarGarfos() {
            garfoDireito.release();
            garfoEsquerdo.release();
            System.out.println("Filósofo " + id + " liberou os garfos");
        }
    }

    public static void main(String[] args) {
        Semaphore[] garfos = new Semaphore[N];

        for (int i = 0; i < N; i++) {
            garfos[i] = new Semaphore(1, true);
        }

        Semaphore mutex = new Semaphore(1, true);

        Filosofo[] filosofos = new Filosofo[N];

        for (int i = 0; i < N; i++) {
            Semaphore esquerdo = garfos[i];
            Semaphore direito = garfos[(i + 1) % N];
            filosofos[i] = new Filosofo(i, esquerdo, direito, mutex);
            filosofos[i].start();
        }
    }
}