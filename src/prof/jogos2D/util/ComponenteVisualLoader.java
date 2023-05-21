package prof.jogos2D.util;


import java.util.HashMap;
import prof.jogos2D.image.ComponenteVisual;

/**
 * Esta classe existe para simplificar o carregamento dos elementos visuais. 
 * Permite carregar todos os elementos no in�cio da aplica��o onde se sabe quais os componentes a carregar
 * e depois apenas cham�-los usando um nome �nico.
 * 
 * @author F. S�rgio Barbosa
 */
public class ComponenteVisualLoader {
	
	// guarda os componentes carregados com refer�ncia ao nome
	private static HashMap<String,ComponenteVisual> mapper = new HashMap<String,ComponenteVisual>();
		
	
	/** armazena um componente, dando um nome de identifica��o 
	 * @param name nome pelo qual vai ser conhecido o componente
	 * @param cv o componente a ser guardado
	 */
	public static void store( String name, ComponenteVisual cv ){
		mapper.put( name, cv );
	}
	
	
	/** retorna um componente igual ao associado ao nome
	 * @param name nome do componente armazenado
	 * @return o componente visual com o nome usado
	 */
	public static ComponenteVisual getCompVisual( String name ){
		ComponenteVisual cv = mapper.get( name );
		if( cv == null )
			return cv;
		
		return cv.clone();
	}
}
