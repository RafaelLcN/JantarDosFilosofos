import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class JantarDosFilosofos {

    private static final int N = 5;

    private enum Estado {
        PENSANDO, FOME, COMENDO
    }

    private static class Mesa {
        private final Estado[] estados = new Estado[N];
        private final Condition[] condicoes = new Condition[N];
        private final long[] tickets = new long[N];

        private final ReentrantLock lock = new ReentrantLock(true);

        private long proximoTicket = 0;

        public Mesa() {
            for (int i = 0; i < N; i++) {
                estados[i] = Estado.PENSANDO;
                condicoes[i] = lock.newCondition();
                tickets[i] = Long.MAX_VALUE;
            }
        }

        private int esquerda(int i) {
            return (i + N - 1) % N;
        }

        private int direita(int i) {
            return (i + 1) % N;
        }

        private boolean temPrioridadeSobreVizinho(int i, int vizinho) {
            if (estados[vizinho] != Estado.FOME) {
                return true;
            }

            if (tickets[i] < tickets[vizinho]) {
                return true;
            }

            if (tickets[i] > tickets[vizinho]) {
                return false;
            }

            return i < vizinho;
        }

        private boolean podeComer(int i) {
            int esq = esquerda(i);
            int dir = direita(i);

            return estados[i] == Estado.FOME
                    && estados[esq] != Estado.COMENDO
                    && estados[dir] != Estado.COMENDO
                    && temPrioridadeSobreVizinho(i, esq)
                    && temPrioridadeSobreVizinho(i, dir);
        }

        private void testar(int i) {
            if (podeComer(i)) {
                estados[i] = Estado.COMENDO;
                condicoes[i].signal();
            }
        }

        public void pegarGarfos(int i) throws InterruptedException {
            lock.lock();
            try {
                estados[i] = Estado.FOME;
                tickets[i] = proximoTicket++;

                System.out.println("Filósofo " + i + " está com fome.");

                testar(i);

                while (estados[i] != Estado.COMENDO) {
                    condicoes[i].await();
                }

                System.out.println("Filósofo " + i + " pegou os garfos.");
            } finally {
                lock.unlock();
            }
        }

        public void soltarGarfos(int i) {
            lock.lock();
            try {
                estados[i] = Estado.PENSANDO;
                tickets[i] = Long.MAX_VALUE;

                System.out.println("Filósofo " + i + " soltou os garfos.");

                testar(esquerda(i));
                testar(direita(i));
            } finally {
                lock.unlock();
            }
        }
    }

    private static class Filosofo extends Thread {
        private final int id;
        private final Mesa mesa;

        public Filosofo(int id, Mesa mesa) {
            this.id = id;
            this.mesa = mesa;
        }

        @Override
        public void run() {
            try {
                while (true) {
                    pensar();
                    mesa.pegarGarfos(id);
                    comer();
                    mesa.soltarGarfos(id);
                }
            } catch (InterruptedException e) {
                System.out.println("Filósofo " + id + " foi interrompido.");
                Thread.currentThread().interrupt();
            }
        }

        private void pensar() throws InterruptedException {
            System.out.println("Filósofo " + id + " está pensando.");
            Thread.sleep((long) (Math.random() * 1000));
        }

        private void comer() throws InterruptedException {
            System.out.println("Filósofo " + id + " está comendo.");
            Thread.sleep((long) (Math.random() * 1000));
        }
    }

    public static void main(String[] args) {
        Mesa mesa = new Mesa();

        Filosofo[] filosofos = new Filosofo[N];

        for (int i = 0; i < N; i++) {
            filosofos[i] = new Filosofo(i, mesa);
            filosofos[i].start();
        }
    }
}
