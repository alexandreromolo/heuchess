package br.ufpr.inf.utils.gui;

import java.awt.Toolkit;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.PlainDocument;

/**
 *
 * @author Alexandre Rômolo Moreira Feitosa - alexandreromolo@hotmail.com
 * Created on 18 de Setembro de 2006, 18:18
 */
public class DocumentMasked extends PlainDocument {
    
    public static final int ENTRANCE_ONLY_LETTERS  = 10;
    public static final int ENTRANCE_ONLY_NUMBERS  = 20;
    public static final int ENTRANCE_ONLY_LETTERS_NUMBERS = ENTRANCE_ONLY_LETTERS + ENTRANCE_ONLY_NUMBERS;
    public static final int ENTRANCE_ANY_CHARACTER = 40;
    
    public static final int ONLY_CAPITAL    = 1;
    public static final int ONLY_VERY_SMALL = 2;
    public static final int NORMAL          = 3;
    
    private int MaxCharacters;
    private int Entrance;
    private int Modifier;
    
    private char[] validCharacters;
    
    public DocumentMasked(int MaxCharacters){        
        validaMaxCharacters(MaxCharacters);
    }
     
    public DocumentMasked(int Entrance, int Modifier){        
        validaEntrance(Entrance);
        validaModifier(Modifier);        
    }
    
    public DocumentMasked(int Entrance, int Modifier, int MaxCharacters){        
        validaEntrance(Entrance);
        validaModifier(Modifier);
        validaMaxCharacters(MaxCharacters);
    }
    
    public DocumentMasked(char[] validCharacters){
        validaCaracteresValidos(validCharacters);
    }
    
    public DocumentMasked(char[] validCharacters, int Modifier){
        validaCaracteresValidos(validCharacters);
        validaModifier(Modifier);        
    }
     
    public DocumentMasked(char[] validCharacters, int Modifier, int MaxCharacters){
        validaCaracteresValidos(validCharacters);
        validaModifier(Modifier);
        validaMaxCharacters(MaxCharacters);
    }
    
    private void validaEntrance(int Entrance){
        if (Entrance == ENTRANCE_ONLY_LETTERS         ||
            Entrance == ENTRANCE_ONLY_NUMBERS         ||
            Entrance == ENTRANCE_ONLY_LETTERS_NUMBERS ||
            Entrance == ENTRANCE_ANY_CHARACTER){
            
            this.Entrance = Entrance;
        }else{
            throw new IllegalArgumentException("Entrance");
        }
    }
    
    private void validaModifier(int Modifier){
        if (Modifier == ONLY_CAPITAL    ||
            Modifier == ONLY_VERY_SMALL ||
            Modifier == NORMAL){
            
            this.Modifier = Modifier;
        }else{
            throw new IllegalArgumentException("Modifier");
        }
    }
    
    private void validaMaxCharacters(int MaxCharacters){
        
        if (MaxCharacters > 0){
            this.MaxCharacters = MaxCharacters;
        }else{
            throw new IllegalArgumentException("MaxCharacters");
        }
    }
    
    private void validaCaracteresValidos(char[] validCharacters){
        
        if (validCharacters != null && validCharacters.length > 0){
            this.validCharacters = validCharacters;
        }else{
            throw new IllegalArgumentException("ValidCharacters");
        }
    }
    
    @Override
    public void insertString(int offs, String str, AttributeSet a) throws BadLocationException {
        
        if (str == null) {
            return;
        }
        
        char[] upper = str.toCharArray();
        int offsAux  = offs;
        
        for (int i = 0; i < upper.length; i++, offsAux++) {
            
            ////////////////////////////////////////////////
            // validação de cada Caractere a ser inserido //
            ////////////////////////////////////////////////
            
            // verifica se o tamanho máximo já foi preenchido //
            
            if (MaxCharacters != 0){
                if (getLength() == MaxCharacters){
                    Toolkit.getDefaultToolkit().beep();
                    return;
                }
            }
            
            if (validCharacters != null){
                
                // verifica se o caracter faz parte dos vetor de caracteres válidos //
                
                boolean procura = false;
                for (int x = 0; x < validCharacters.length; x++){
                    if (upper[i] == validCharacters[x]){
                        procura = true;
                        break;
                    }
                }
                if (procura == false){
                    Toolkit.getDefaultToolkit().beep();
                    return;
                }
            }else{
                
                // verifica tipos de entradas válidas //
                
                if (Entrance == ENTRANCE_ONLY_NUMBERS){
                    if (Character.isDigit(upper[i]) == false){
                        Toolkit.getDefaultToolkit().beep();
                        return;
                    }
                }else
                    if (Entrance == ENTRANCE_ONLY_LETTERS){
                        if (Character.isLetter(upper[i]) == false){
                            Toolkit.getDefaultToolkit().beep();
                            return;
                        }
                    }else
                        if (Entrance == ENTRANCE_ONLY_LETTERS_NUMBERS){
                            if (Character.isLetterOrDigit(upper[i]) == false){
                                Toolkit.getDefaultToolkit().beep();
                                return;
                            }
                        }
            }   
            
            // verifica modificadores de entrada //
                
            if (Entrance != ENTRANCE_ONLY_NUMBERS){
                
                if (Modifier == ONLY_CAPITAL){
                    upper[i] = Character.toUpperCase(upper[i]);
                }else
                    if (Modifier == ONLY_VERY_SMALL){
                        upper[i] = Character.toLowerCase(upper[i]);
                    }
            }            
            
            // insere o caractere após as validações de entrada //
            
            super.insertString(offsAux, Character.toString(upper[i]), a);
        }        
    }
}
