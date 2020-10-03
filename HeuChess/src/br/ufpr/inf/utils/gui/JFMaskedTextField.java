package br.net.sercomtel.eti.util.gui;

import java.awt.Toolkit;
import java.text.ParseException;

import javax.swing.JFormattedTextField;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.MaskFormatter;
import javax.swing.text.PlainDocument;

/**
 * Classe criada para facilitar a especificacao e uso de padroes de entrada de dados
 * em aplicacoes Graficas. Esta classe e filha de JFormattedTextField e implementa novos
 * metodos para customizar o seu uso. Tambem utiliza internamente a classe MaskFormmatter
 * para especificar mascaras de entrada mais especificas.
 * @see <code>JTextField</code>
 * @see <code>JFormattedTextField</code>  
 * @see <code>MaskFormmatter</code>
 * @author Alexandre Romolo Moreira Feitosa
 * @serial 1.0 
 */
public class JFMaskedTextField extends JFormattedTextField {
	
	private static final long serialVersionUID = 1L;
	
	static public final byte ENTRADA_SOMENTE_LETRAS         = 1;
	static public final byte ENTRADA_SOMENTE_NUMEROS        = 2;
	static public final byte ENTRADA_SOMENTE_LETRAS_NUMEROS = ENTRADA_SOMENTE_LETRAS + ENTRADA_SOMENTE_NUMEROS;
	static public final byte ENTRADA_QUALQUER_CARACTER      = 4;
	
        static public final byte CONVERTE_ENTRADA_MAIUSCULAS    = 1;
	static public final byte CONVERTE_ENTRADA_MINUSCULAS    = 2;
	static public final byte NAO_CONVERTE_ENTRADA           = 3;
	
        private int quantidadeMaximaCaracteres;
	private int tipoEntrada;
	private int tipoModificadorEntrada;	
        
        private String caracteresValidosEntrada;
        private String caracteresInvalidosEntrada;
	
        private MaskFormatter maskFormatter;
	
	/*
	 Character  Description
 
 	 # Any valid number, uses Character.isDigit. 
 	 ' Escape character, used to escape any of the special formatting characters. 
 	 U Any character (Character.isLetter). All lowercase letters are mapped to upper case. 
 	 L Any character (Character.isLetter). All upper case letters are mapped to lower case. 
 	 A Any character or number (Character.isLetter or Character.isDigit) 
 	 ? Any character (Character.isLetter). 
 	 * Anything. 
 	 H Any hex character (0-9, a-f or A-F). 
        */
	
        public JFMaskedTextField(){		
            this(ENTRADA_QUALQUER_CARACTER, NAO_CONVERTE_ENTRADA);
        }
        
	public JFMaskedTextField(byte tipoEntrada, byte tipoModificadorEntrada){		
            setTipoEntrada(tipoEntrada);
            setModificadorEntrada(tipoModificadorEntrada);		
	}
	
	public JFMaskedTextField(byte tipoEntrada, byte tipoModificadorEntrada, int quantidadeMaximaCaracteres){
            this(tipoEntrada,tipoModificadorEntrada);			  	
            setQuantidadeMaximaCaracteres(quantidadeMaximaCaracteres);
	}
	
	public JFMaskedTextField(byte tipoEntrada, byte tipoModificadorEntrada, int quantidadeMaximaCaracteres, int quantidadeColunas){
            this(tipoEntrada,tipoModificadorEntrada,quantidadeMaximaCaracteres);
            setQuantidadeColunas(quantidadeColunas);            
	}
	
	public JFMaskedTextField(String mascara) throws ParseException{
            setMascaraEntrada(mascara);            
	}
	
	public JFMaskedTextField(String mascara, char caracterMarcador) throws ParseException{
            this(mascara);
            setCaracterMarcadorEntrada(caracterMarcador);			
	}
	
	public JFMaskedTextField(String mascara, char caracterMarcador, int quantidadeColunas) throws ParseException{	
            this(mascara,caracterMarcador);
            setQuantidadeColunas(quantidadeColunas);				
	}
	
        public void setQuantidadeMaximaCaracteres(int quantidade){
            if (quantidade > 0)	
		this.quantidadeMaximaCaracteres = quantidade;
            else
		throw new IllegalArgumentException("Quantidade Máxima de caracteres não pode ser igual ou menor que zero!");
	}	
        
        public int getQuantidadeMaximaCaracteres(){
            return quantidadeMaximaCaracteres;
        }
        
        public void setTipoEntrada(byte tipoEntrada){
            if (tipoEntrada == ENTRADA_SOMENTE_LETRAS || 
		tipoEntrada == ENTRADA_SOMENTE_NUMEROS || 
		tipoEntrada == ENTRADA_SOMENTE_LETRAS_NUMEROS || 
		tipoEntrada == ENTRADA_QUALQUER_CARACTER)
		this.tipoEntrada = tipoEntrada;
            else
		throw new IllegalArgumentException("Tipo de Entrada definido é inválido!");			 
	}
        
        public int getTipoEntrada(){
            return tipoEntrada;
        }
        
        public void setModificadorEntrada(byte tipoModificadorEntrada){
            if (tipoModificadorEntrada == CONVERTE_ENTRADA_MAIUSCULAS ||
		tipoModificadorEntrada == CONVERTE_ENTRADA_MINUSCULAS ||
		tipoModificadorEntrada == NAO_CONVERTE_ENTRADA)
		this.tipoModificadorEntrada = tipoModificadorEntrada;
            else
		throw new IllegalArgumentException("Tipo de Modificador de Entrada definido é inválido!");
	}
        
        public int getModificadorEntrada(){
            return tipoModificadorEntrada;
        }
        
        public void setQuantidadeColunas(int quantidade){
            if (quantidade < 0)
                throw new IllegalArgumentException("Quantidade de Colunas deve ser maior ou igual a zero!");
            
            setColumns(quantidade);    
        }

        public int getQuantidadeColunas(){
            return getColumns();
        }
   	
        public void setCaracteresValidosEntrada(String caracteres){
            if (maskFormatter != null){
                maskFormatter.setValidCharacters(caracteres);
                maskFormatter.install(this);                
            }
            caracteresValidosEntrada = caracteres;                
	}
        
        public String getCaracteresValidosEntrada(){
            if (maskFormatter != null){
                return maskFormatter.getValidCharacters();                
            }else
                return caracteresValidosEntrada;
	}
        
        public void setCaracteresInvalidosEntrada(String caracteres){
            if (maskFormatter != null){
                maskFormatter.setInvalidCharacters(caracteres);
		maskFormatter.install(this);	
            }
            caracteresInvalidosEntrada = caracteres;
	}
        
        public String getCaracteresInvalidosEntrada(){
            if (maskFormatter != null){
                return maskFormatter.getInvalidCharacters();		
            }else
		return caracteresValidosEntrada;
	}
        
        public void setFinalConteraCaracteresMascara(boolean contem){
            if (maskFormatter != null){
		maskFormatter.setValueContainsLiteralCharacters(contem);
		maskFormatter.install(this);		
            }else
                throw new IllegalArgumentException("O Componente não possui nenhuma máscara Definida!");		
	}
        
        public boolean getFinalConteraCaracteresMascara(){
            if (maskFormatter != null){
		return maskFormatter.getValueContainsLiteralCharacters();		
            }else
                throw new IllegalArgumentException("O Componente não possui nenhuma máscara Definida!");						
        }
        
        public void setPerrmiteEntradaValoresInvalidos(boolean permite){
            if (maskFormatter != null){
		maskFormatter.setAllowsInvalid(permite);
		maskFormatter.install(this);		
            }else
                throw new IllegalArgumentException("O Componente não possui nenhuma máscara Definida!");				
	}
        
        public boolean getPerrmiteEntradaValoresInvalidos(){
            if (maskFormatter != null){
		return maskFormatter.getAllowsInvalid();		
            }else
                throw new IllegalArgumentException("O Componente não possui nenhuma máscara Definida!");		
	}
        
        public void setCaracterMarcadorEntrada(char caracter){
            if (maskFormatter != null){
		maskFormatter.setPlaceholderCharacter(caracter);
		maskFormatter.install(this);		
            }else
                throw new IllegalArgumentException("O Componente não possui nenhuma máscara Definida!");		
	}
        
        public char getCaracterMarcadorEntrada(){
            if (maskFormatter != null){
		return maskFormatter.getPlaceholderCharacter();		
            }else
		throw new IllegalArgumentException("O Componente não possui nenhuma máscara Definida!");
	}
        
        public void setValorInicial(String texto){
            if (maskFormatter != null){
		maskFormatter.setPlaceholder(texto);
		maskFormatter.install(this);		
            }else
                throw new IllegalArgumentException("O Componente não possui nenhuma máscara Definida!");		
	}	
        
        public String getValorInicial(String texto){
            if (maskFormatter != null){
		return maskFormatter.getPlaceholder();		
            }else
                throw new IllegalArgumentException("O Componente não possui nenhuma máscara Definida!");		
	}	
        
        protected Document createDefaultModel() {
		return new JFMaskedTextFieldDocument();		
	}
        
        private class JFMaskedTextFieldDocument extends PlainDocument {
		
            private static final long serialVersionUID = JFMaskedTextField.serialVersionUID;
		
            public void insertString(int offs, String str, AttributeSet a) throws BadLocationException {
			
                if (JFMaskedTextField.this == null) 
                    return;
			
                if (str == null) {
                    return;
                }
			
                char[] upper = str.toCharArray();
		int offsAux  = offs;			 
			
		for (int i = 0; i < upper.length; i++, offsAux++) {
                    
                    ////////////////////////////////////////////////
                    // validação de cada Caractere a ser inserido //
                    ////////////////////////////////////////////////
                    
                    if (maskFormatter == null){
                        
                        ////////////////////////////////////////////////////
                        // verifica se o tamanho máximo já foi preenchido //
                        ////////////////////////////////////////////////////
                        
                        if (quantidadeMaximaCaracteres != 0){
                            if (getLength() == quantidadeMaximaCaracteres){						
                                Toolkit.getDefaultToolkit().beep();
                                return;	
                            }
                        }
                        
                        //////////////////////////////////////
                        // Verifica se é um Caracter Válido //
                        //////////////////////////////////////
                        
                        if (caracteresValidosEntrada != null){
                            boolean achou = false;
                            for (int cont = 0; cont < caracteresValidosEntrada.length(); cont++){
                                if (caracteresValidosEntrada.charAt(cont) == upper[i]){
                                    achou = true;
                                    break;
                                }
                            }
                            if (!achou){
                                Toolkit.getDefaultToolkit().beep();
                                return;
                            }
                        }
                        
                        ////////////////////////////////////////
                        // Verifica se é um Caracter Inválido //
                        //////////////////////////////////////// 
                        
                        if (caracteresInvalidosEntrada != null){
                            for (int cont = 0; cont < caracteresInvalidosEntrada.length(); cont++){
                                if (caracteresInvalidosEntrada.charAt(cont) == upper[i]){
                                    Toolkit.getDefaultToolkit().beep();
                                    return;
                                }
                            }
                        }
                    }
                    
                    ////////////////////////////////////////
                    // Verifica tipos de entradas válidas //
                    ////////////////////////////////////////
                    
                    if (tipoEntrada == ENTRADA_SOMENTE_NUMEROS){
			if (Character.isDigit(upper[i]) == false){
                            Toolkit.getDefaultToolkit().beep();
                            return;
			}
                    }else
                        if (tipoEntrada == ENTRADA_SOMENTE_LETRAS){
                            if (Character.isLetter(upper[i]) == false){
                                Toolkit.getDefaultToolkit().beep();
				return;
                            }
			}else
                            if (tipoEntrada == ENTRADA_SOMENTE_LETRAS_NUMEROS){
                                if (Character.isLetterOrDigit(upper[i]) == false){
                                    Toolkit.getDefaultToolkit().beep();
                                    return;
				}
                            }
				
                    if (tipoEntrada != ENTRADA_SOMENTE_NUMEROS){
                        
                        ///////////////////////////////////////
                        // verifica modificadores de entrada //
                        ///////////////////////////////////////
                        
			if (tipoModificadorEntrada == CONVERTE_ENTRADA_MAIUSCULAS){
                            upper[i] = Character.toUpperCase(upper[i]);	
                        }else
                            if (tipoModificadorEntrada == CONVERTE_ENTRADA_MINUSCULAS){
                                upper[i] = Character.toLowerCase(upper[i]);
                            }
                    }
				
                    //////////////////////////////////////////////////////
                    // insere o caractere após as validações de entrada //
                    //////////////////////////////////////////////////////
                    
                    super.insertString(offsAux, Character.toString(upper[i]), a);				
                }
	    }
	}
        
        public void setMascaraEntrada(MaskFormatter novaMascara){
            if (novaMascara == null)
		throw new IllegalArgumentException("O valor de MaskFormatter passado é igual a null!");
		
            maskFormatter = novaMascara;
            maskFormatter.install(this);            
            caracteresValidosEntrada   = null;
            caracteresInvalidosEntrada = null;            
	}
        
        public void setMascaraEntrada(String mascara) throws ParseException{
            if (mascara == null)
		throw new IllegalArgumentException("O valor de MaskFormatter passado é igual a null!");
	
            maskFormatter = new MaskFormatter(mascara);			
            maskFormatter.setAllowsInvalid(false);
            maskFormatter.install(this);				
            caracteresValidosEntrada   = null;
            caracteresInvalidosEntrada = null;            
        }
        
        public MaskFormatter getMascaraEntrada(){
            return maskFormatter;
	}
        
        public void removeMascaraEntrada(){
            if (maskFormatter != null){
                maskFormatter.uninstall();
                caracteresValidosEntrada = maskFormatter.getValidCharacters();
                caracteresInvalidosEntrada = maskFormatter.getInvalidCharacters();
                maskFormatter = null;                
            }
        }
}