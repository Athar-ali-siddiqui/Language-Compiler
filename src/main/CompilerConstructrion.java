/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package main;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

/**
 *
 * @author Ather Ali Siddiqui
 */
public class CompilerConstructrion {

    /**
     * @param args the command line arguments
     */
    static Map<String , String> keywords = new LinkedHashMap<>();
    
    
    static void setKeywords() {
        try {
            File myObj = new File("C:\\Users\\Ather Ali Siddiqui\\Documents\\NetBeansProjects\\Compiler-Constructrion\\src\\main\\keywords.txt");
            Scanner in = new Scanner(myObj);
            while (in.hasNextLine()) {
                String line = in.nextLine();
                if(!line.isEmpty()){
                    String[] t = line.split(" ");
                    keywords.put(t[0],t[1]);
                }  
            }
        } catch (FileNotFoundException e) {System.out.print(e.getMessage());}
    }
    public static void main(String[] args) {
        // TODO code application logic here
        setKeywords();
        
        String sourceCode = "C:\\Users\\Ather Ali Siddiqui\\Documents\\NetBeansProjects\\Compiler-Constructrion\\src\\main\\program.txt" ;
        String wordOut =  "C:\\Users\\Ather Ali Siddiqui\\Documents\\NetBeansProjects\\Compiler-Constructrion\\src\\main\\words.txt" ;
        String tokenOut ="C:\\Users\\Ather Ali Siddiqui\\Documents\\NetBeansProjects\\Compiler-Constructrion\\src\\main\\tokens.txt" ;
        Lexical_Analyzer lexi = new Lexical_Analyzer(keywords , sourceCode , wordOut , tokenOut);
        ArrayList<Token> tokens  = lexi.getTokens();
        
        String cfgPath = "C:\\Users\\Ather Ali Siddiqui\\Documents\\NetBeansProjects\\Compiler-Constructrion\\src\\main\\cfgs.txt" ;
        Syntax_Analyzer syn = new Syntax_Analyzer(cfgPath , tokens);
//        syn.tokens = tokens ;
        System.out.println(syn.checkSyntax()? "Syntax is Correct" : "Syntax Error");
    }
    
}
