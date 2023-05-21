package prof.jogos2D.util;

import java.util.Random;

/** classe auxiliar que devolve alguns números aleatórios
 * Tendo esta classe, todas as partes do jogo usam o mesmo gerador
 * de números aleatórios e assim até se poderiam repetir jogos usando
 * a mesma semente na geração dos números (ainda não implementada)
 * 
 * @author F. Sérgio Barbosa
 */
public class RandomGenerator {

	public static Random random = new Random();
	
	public static void startGenerating( long seed ){
		random = new Random( seed );
	}

	/** devolve um número inteiro aleatório */
	public static int nextInt() {
		return random.nextInt();
	}

	/** devolve um número aleatório desde 0 até bound (exclusivo) */
	public static int nextInt(int bound) {
		return random.nextInt(bound);
	}

	/**
	 * retotna um número aleatório (distribuição uniforme) entre a gama ini (inclusivé)
	 * e end (exclusive)
	 * @param ini número minimo (inclusive) a sair
	 * @param end número máximo (exclusive) a sair
	 * @return
	 */
	public static int nextRange( int ini, int end ){
		return ini + random.nextInt( end - ini );
	}
	
	/** devolve um booleano aleatório */
	public static boolean nextBoolean() {
		return random.nextBoolean();
	}

	/** devolve um número float aleatório (entre 0 e 1)*/
	public static float nextFloat() {
		return random.nextFloat();
	}

	/** devolve um número double aleatório (entre 0 e 1)*/
	public static double nextDouble() {
		return random.nextDouble();
	}
}
