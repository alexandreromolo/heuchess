/*
 * ColorList.java
 *
 * Created on 16 de Setembro de 2006, 18:49
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package br.ufpr.inf.heuchess.telas.editorheuristica;

import java.awt.Color;
import java.util.Vector;

/**
 *
 * @author Alexandre Rômolo Moreira Feitosa - alexandreromolo@hotmail.com
 */
public class ColorList {
    
    private Vector colorList = new Vector();
    private int    corAtual  = 0;
    
    public ColorList() {
        
        colorList.add(Color.lightGray);
        colorList.add(Color.pink);
        colorList.add(Color.yellow);
        colorList.add(Color.darkGray);
        colorList.add(Color.orange);
        colorList.add(Color.black);        
        colorList.add(Color.green);
        colorList.add(Color.white);
        colorList.add(Color.magenta);
        colorList.add(Color.cyan);
        colorList.add(Color.gray);        
        colorList.add(Color.blue); 
        
        colorList.add(new Color(0xF0F8FF)); // AliceBlue 
        colorList.add(new Color(0xFAEBD7)); // AntiqueWhite  
        colorList.add(new Color(0x00FFFF)); // Aqua  
        colorList.add(new Color(0x7FFFD4)); // Aquamarine  
        colorList.add(new Color(0xF0FFFF)); // Azure   
        colorList.add(new Color(0xF5F5DC)); // Beige   
        colorList.add(new Color(0xFFE4C4)); // Bisque   
        //colorList.add(new Color(0x000000)); // Black   
        colorList.add(new Color(0xFFEBCD)); // BlanchedAlmond   
        //colorList.add(new Color(0x0000FF)); // Blue   
        colorList.add(new Color(0x8A2BE2)); // BlueViolet   
        colorList.add(new Color(0xA52A2A)); // Brown   
        colorList.add(new Color(0xDEB887)); // BurlyWood   
        colorList.add(new Color(0x5F9EA0)); // CadetBlue   
        colorList.add(new Color(0x7FFF00)); // Chartreuse   
        colorList.add(new Color(0xD2691E)); // Chocolate   
        colorList.add(new Color(0xFF7F50)); // Coral   
        colorList.add(new Color(0x6495ED)); // CornflowerBlue   
        colorList.add(new Color(0xFFF8DC)); // Cornsilk   
        colorList.add(new Color(0xDC143C)); // Crimson   
        //colorList.add(new Color(0x00FFFF)); // Cyan   
        colorList.add(new Color(0x00008B)); // DarkBlue   
        colorList.add(new Color(0x008B8B)); // DarkCyan   
        colorList.add(new Color(0xB8860B)); // DarkGoldenRod   
        //colorList.add(new Color(0xA9A9A9)); // DarkGray   
        colorList.add(new Color(0x006400)); // DarkGreen   
        colorList.add(new Color(0xBDB76B)); // DarkKhaki   
        colorList.add(new Color(0x8B008B)); // DarkMagenta   
        colorList.add(new Color(0x556B2F)); // DarkOliveGreen   
        colorList.add(new Color(0xFF8C00)); // Darkorange   
        colorList.add(new Color(0x9932CC)); // DarkOrchid   
        colorList.add(new Color(0x8B0000)); // DarkRed   
        colorList.add(new Color(0xE9967A)); // DarkSalmon   
        colorList.add(new Color(0x8FBC8F)); // DarkSeaGreen   
        colorList.add(new Color(0x483D8B)); // DarkSlateBlue   
        colorList.add(new Color(0x2F4F4F)); // DarkSlateGray   
        colorList.add(new Color(0x00CED1)); // DarkTurquoise   
        colorList.add(new Color(0x9400D3)); // DarkViolet   
        colorList.add(new Color(0xFF1493)); // DeepPink   
        colorList.add(new Color(0x00BFFF)); // DeepSkyBlue   
        colorList.add(new Color(0x696969)); // DimGray   
        colorList.add(new Color(0x1E90FF)); // DodgerBlue   
        colorList.add(new Color(0xD19275)); // Feldspar   
        colorList.add(new Color(0xB22222)); // FireBrick   
        colorList.add(new Color(0xFFFAF0)); // FloralWhite   
        colorList.add(new Color(0x228B22)); // ForestGreen   
        colorList.add(new Color(0xFF00FF)); // Fuchsia   
        colorList.add(new Color(0xDCDCDC)); // Gainsboro   
        colorList.add(new Color(0xF8F8FF)); // GhostWhite   
        colorList.add(new Color(0xFFD700)); // Gold   
        colorList.add(new Color(0xDAA520)); // GoldenRod   
        //colorList.add(new Color(0x808080)); // Gray   
        //colorList.add(new Color(0x008000)); // Green   
        colorList.add(new Color(0xADFF2F)); // GreenYellow   
        colorList.add(new Color(0xF0FFF0)); // HoneyDew   
        colorList.add(new Color(0xFF69B4)); // HotPink   
        colorList.add(new Color(0xCD5C5C)); // IndianRed   
        colorList.add(new Color(0x4B0082)); // Indigo   
        colorList.add(new Color(0xFFFFF0)); // Ivory   
        colorList.add(new Color(0xF0E68C)); // Khaki   
        colorList.add(new Color(0xE6E6FA)); // Lavender   
        colorList.add(new Color(0xFFF0F5)); // LavenderBlush   
        colorList.add(new Color(0x7CFC00)); // LawnGreen   
        colorList.add(new Color(0xFFFACD)); // LemonChiffon   
        colorList.add(new Color(0xADD8E6)); // LightBlue   
        colorList.add(new Color(0xF08080)); // LightCoral   
        colorList.add(new Color(0xE0FFFF)); // LightCyan   
        colorList.add(new Color(0xFAFAD2)); // LightGoldenRodYellow   
        //colorList.add(new Color(0xD3D3D3)); // LightGrey   
        colorList.add(new Color(0x90EE90)); // LightGreen   
        colorList.add(new Color(0xFFB6C1)); // LightPink   
        colorList.add(new Color(0xFFA07A)); // LightSalmon   
        colorList.add(new Color(0x20B2AA)); // LightSeaGreen   
        colorList.add(new Color(0x87CEFA)); // LightSkyBlue   
        colorList.add(new Color(0x8470FF)); // LightSlateBlue   
        colorList.add(new Color(0x778899)); // LightSlateGray   
        colorList.add(new Color(0xB0C4DE)); // LightSteelBlue   
        colorList.add(new Color(0xFFFFE0)); // LightYellow   
        colorList.add(new Color(0x00FF00)); // Lime   
        colorList.add(new Color(0x32CD32)); // LimeGreen   
        colorList.add(new Color(0xFAF0E6)); // Linen   
        //colorList.add(new Color(0xFF00FF)); // Magenta   
        colorList.add(new Color(0x800000)); // Maroon   
        colorList.add(new Color(0x66CDAA)); // MediumAquaMarine   
        colorList.add(new Color(0x0000CD)); // MediumBlue   
        colorList.add(new Color(0xBA55D3)); // MediumOrchid   
        colorList.add(new Color(0x9370D8)); // MediumPurple   
        colorList.add(new Color(0x3CB371)); // MediumSeaGreen   
        colorList.add(new Color(0x7B68EE)); // MediumSlateBlue   
        colorList.add(new Color(0x00FA9A)); // MediumSpringGreen   
        colorList.add(new Color(0x48D1CC)); // MediumTurquoise   
        colorList.add(new Color(0xC71585)); // MediumVioletRed   
        colorList.add(new Color(0x191970)); // MidnightBlue   
        colorList.add(new Color(0xF5FFFA)); // MintCream   
        colorList.add(new Color(0xFFE4E1)); // MistyRose   
        colorList.add(new Color(0xFFE4B5)); // Moccasin   
        colorList.add(new Color(0xFFDEAD)); // NavajoWhite   
        colorList.add(new Color(0x000080)); // Navy   
        colorList.add(new Color(0xFDF5E6)); // OldLace   
        colorList.add(new Color(0x808000)); // Olive   
        colorList.add(new Color(0x6B8E23)); // OliveDrab   
        //colorList.add(new Color(0xFFA500)); // Orange   
        colorList.add(new Color(0xFF4500)); // OrangeRed   
        colorList.add(new Color(0xDA70D6)); // Orchid   
        colorList.add(new Color(0xEEE8AA)); // PaleGoldenRod   
        colorList.add(new Color(0x98FB98)); // PaleGreen   
        colorList.add(new Color(0xAFEEEE)); // PaleTurquoise   
        colorList.add(new Color(0xD87093)); // PaleVioletRed   
        colorList.add(new Color(0xFFEFD5)); // PapayaWhip   
        colorList.add(new Color(0xFFDAB9)); // PeachPuff   
        colorList.add(new Color(0xCD853F)); // Peru   
        //colorList.add(new Color(0xFFC0CB)); // Pink   
        colorList.add(new Color(0xDDA0DD)); // Plum   
        colorList.add(new Color(0xB0E0E6)); // PowderBlue   
        colorList.add(new Color(0x800080)); // Purple   
        colorList.add(new Color(0xFF0000)); // Red   
        colorList.add(new Color(0xBC8F8F)); // RosyBrown   
        colorList.add(new Color(0x4169E1)); // RoyalBlue   
        colorList.add(new Color(0x8B4513)); // SaddleBrown   
        colorList.add(new Color(0xFA8072)); // Salmon   
        colorList.add(new Color(0xF4A460)); // SandyBrown   
        colorList.add(new Color(0x2E8B57)); // SeaGreen   
        colorList.add(new Color(0xFFF5EE)); // SeaShell   
        colorList.add(new Color(0xA0522D)); // Sienna   
        colorList.add(new Color(0xC0C0C0)); // Silver   
        colorList.add(new Color(0x87CEEB)); // SkyBlue   
        colorList.add(new Color(0x6A5ACD)); // SlateBlue   
        colorList.add(new Color(0x708090)); // SlateGray   
        colorList.add(new Color(0xFFFAFA)); // Snow   
        colorList.add(new Color(0x00FF7F)); // SpringGreen   
        colorList.add(new Color(0x4682B4)); // SteelBlue   
        colorList.add(new Color(0xD2B48C)); // Tan   
        colorList.add(new Color(0x008080)); // Teal   
        colorList.add(new Color(0xD8BFD8)); // Thistle   
        colorList.add(new Color(0xFF6347)); // Tomato   
        colorList.add(new Color(0x40E0D0)); // Turquoise   
        colorList.add(new Color(0xEE82EE)); // Violet   
        colorList.add(new Color(0xD02090)); // VioletRed   
        colorList.add(new Color(0xF5DEB3)); // Wheat   
        //colorList.add(new Color(0xFFFFFF)); // White   
        colorList.add(new Color(0xF5F5F5)); // WhiteSmoke   
        //colorList.add(new Color(0xFFFF00)); // Yellow   
        colorList.add(new Color(0x9ACD32)); // YellowGreen 
                
        /*
        Lista de Cores suportadas por web-browsers na ordem normal (Total de 143)
        =========================================================================
        #F0F8FF //AliceBlue 
        #FAEBD7 //AntiqueWhite  
        #00FFFF //Aqua  
        #7FFFD4 //Aquamarine  
        #F0FFFF //Azure   
        #F5F5DC //Beige   
        #FFE4C4 //Bisque   
        #000000 //Black   
        #FFEBCD //BlanchedAlmond   
        #0000FF //Blue   
        #8A2BE2 //BlueViolet   
        #A52A2A //Brown   
        #DEB887 //BurlyWood   
        #5F9EA0 //CadetBlue   
        #7FFF00 //Chartreuse   
        #D2691E //Chocolate   
        #FF7F50 //Coral   
        #6495ED //CornflowerBlue   
        #FFF8DC //Cornsilk   
        #DC143C //Crimson   
        #00FFFF //Cyan   
        #00008B //DarkBlue   
        #008B8B //DarkCyan   
        #B8860B //DarkGoldenRod   
        #A9A9A9 //DarkGray   
        #006400 //DarkGreen   
        #BDB76B //DarkKhaki   
        #8B008B //DarkMagenta   
        #556B2F //DarkOliveGreen   
        #FF8C00 //Darkorange   
        #9932CC //DarkOrchid   
        #8B0000 //DarkRed   
        #E9967A //DarkSalmon   
        #8FBC8F //DarkSeaGreen   
        #483D8B //DarkSlateBlue   
        #2F4F4F //DarkSlateGray   
        #00CED1 //DarkTurquoise   
        #9400D3 //DarkViolet   
        #FF1493 //DeepPink   
        #00BFFF //DeepSkyBlue   
        #696969 //DimGray   
        #1E90FF //DodgerBlue   
        #D19275 //Feldspar   
        #B22222 //FireBrick   
        #FFFAF0 //FloralWhite   
        #228B22 //ForestGreen   
        #FF00FF //Fuchsia   
        #DCDCDC //Gainsboro   
        #F8F8FF //GhostWhite   
        #FFD700 //Gold   
        #DAA520 //GoldenRod   
        #808080 //Gray   
        #008000 //Green   
        #ADFF2F //GreenYellow   
        #F0FFF0 //HoneyDew   
        #FF69B4 //HotPink   
        #CD5C5C //IndianRed   
        #4B0082 //Indigo   
        #FFFFF0 //Ivory   
        #F0E68C //Khaki   
        #E6E6FA //Lavender   
        #FFF0F5 //LavenderBlush   
        #7CFC00 //LawnGreen   
        #FFFACD //LemonChiffon   
        #ADD8E6 //LightBlue   
        #F08080 //LightCoral   
        #E0FFFF //LightCyan   
        #FAFAD2 //LightGoldenRodYellow   
        #D3D3D3 //LightGrey   
        #90EE90 //LightGreen   
        #FFB6C1 //LightPink   
        #FFA07A //LightSalmon   
        #20B2AA //LightSeaGreen   
        #87CEFA //LightSkyBlue   
        #8470FF //LightSlateBlue   
        #778899 //LightSlateGray   
        #B0C4DE //LightSteelBlue   
        #FFFFE0 //LightYellow   
        #00FF00 //Lime   
        #32CD32 //LimeGreen   
        #FAF0E6 //Linen   
        #FF00FF //Magenta   
        #800000 //Maroon   
        #66CDAA //MediumAquaMarine   
        #0000CD //MediumBlue   
        #BA55D3 //MediumOrchid   
        #9370D8 //MediumPurple   
        #3CB371 //MediumSeaGreen   
        #7B68EE //MediumSlateBlue   
        #00FA9A //MediumSpringGreen   
        #48D1CC //MediumTurquoise   
        #C71585 //MediumVioletRed   
        #191970 //MidnightBlue   
        #F5FFFA //MintCream   
        #FFE4E1 //MistyRose   
        #FFE4B5 //Moccasin   
        #FFDEAD //NavajoWhite   
        #000080 //Navy   
        #FDF5E6 //OldLace   
        #808000 //Olive   
        #6B8E23 //OliveDrab   
        #FFA500 //Orange   
        #FF4500 //OrangeRed   
        #DA70D6 //Orchid   
        #EEE8AA //PaleGoldenRod   
        #98FB98 //PaleGreen   
        #AFEEEE //PaleTurquoise   
        #D87093 //PaleVioletRed   
        #FFEFD5 //PapayaWhip   
        #FFDAB9 //PeachPuff   
        #CD853F //Peru   
        #FFC0CB //Pink   
        #DDA0DD //Plum   
        #B0E0E6 //PowderBlue   
        #800080 //Purple   
        #FF0000 //Red   
        #BC8F8F //RosyBrown   
        #4169E1 //RoyalBlue   
        #8B4513 //SaddleBrown   
        #FA8072 //Salmon   
        #F4A460 //SandyBrown   
        #2E8B57 //SeaGreen   
        #FFF5EE //SeaShell   
        #A0522D //Sienna   
        #C0C0C0 //Silver   
        #87CEEB //SkyBlue   
        #6A5ACD //SlateBlue   
        #708090 //SlateGray   
        #FFFAFA //Snow   
        #00FF7F //SpringGreen   
        #4682B4 //SteelBlue   
        #D2B48C //Tan   
        #008080 //Teal   
        #D8BFD8 //Thistle   
        #FF6347 //Tomato   
        #40E0D0 //Turquoise   
        #EE82EE //Violet   
        #D02090 //VioletRed   
        #F5DEB3 //Wheat   
        #FFFFFF //White   
        #F5F5F5 //WhiteSmoke   
        #FFFF00 //Yellow   
        #9ACD32 //YellowGreen 
        */
    }
    
    public Color nextColor(){
        corAtual++;
        if (corAtual >= colorList.size()){
            corAtual = 0;
        }
        return (Color) colorList.get(corAtual);
    }
    
    public Color previousColor(){
        corAtual--;
        if (corAtual < 0){
            corAtual = colorList.size()-1;
        }
        return (Color) colorList.get(corAtual);
    }    
}
