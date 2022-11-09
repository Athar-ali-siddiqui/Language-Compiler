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

public class Syntax_Analyzer {
    public ArrayList<Token> tokens = new ArrayList<>();
    public int index = 0;
    private String cfgPath ;
    private Map<String , ArrayList<String[]> > cfgs = new LinkedHashMap<>();

    private Set<Token> checked = new HashSet<>();
    private int erroredTokenIndex = -1;

    
//    Semantic Variables
    private int scope = 0;
    private List<Integer> scopeStack = new ArrayList<>();    
    
//    List<String[]> String 
    public Syntax_Analyzer(String path ,ArrayList<Token> tokens) {
        this.cfgPath = path;
        this.tokens = tokens;
        this.setCfgs();
        
    }
    private void setCfgs() {
        try {
            File myObj = new File(this.cfgPath);
            Scanner in = new Scanner(myObj);
            while (in.hasNextLine()) {
                String line = in.nextLine().toUpperCase();
                if(!line.isEmpty()){
                    String[] t = line.split("->");
                    String key = t[0].trim();
//                    System.out.println("kekeekekek = "+key);
                    if(!key.isEmpty() && key.charAt(0) != '#'){
                        String[] values = t[1].trim().replaceAll("\\s+", " ").split(" ");
                        
//                        System.out.println(key +" -> " + Arrays.toString(values));

                        ArrayList<String[]> temp = cfgs.get(key);
                        if(temp != null ){
                            temp.add(values);
                        }
                        else{
                            temp = new ArrayList<>();
                            temp.add(values);
                            cfgs.put(key, temp);
                        }
                    }
                    
//                    cfgs.put(key,values);
                }  
            }
        } catch (FileNotFoundException e) {System.out.print(e.getMessage());}
//        printCfgs();
//        System.out.println("-----");
//        System.out.println(cfgs.get("<s-body>"));
//        System.out.println("cfgss = "+cfgs);
    }
    public boolean checkSyntax(){
        System.out.println("tokens.size() == "+ tokens.size());
        
        if (this.helper("<START>") && index == tokens.size() ) {
            System.out.println("INDEX == "+ index);
            System.out.println("PARSED = "+checked);
            System.out.println("I'm checking hashCode = "+checked.contains(0));
            return true;
        }
        else {
            
            System.out.println("PARSED = "+checked);
//            System.out.println("error wala word = "+invalidToken.value + " ,line no ="+ invalidToken.line);
            System.out.println("INDEX == "+ index);
            
//            Token errored = (Token)checked.toArray()[checked.size()-1];
            Token errored = tokens.get(erroredTokenIndex);
//            System.out.println("Unexpected Token:\'" + errored.value+"\' "+"at line "+errored.line );

            System.out.println("Unexpected Token:\'" + errored.value+"\' "+"at line "+errored.line );
            return false;
        }
    }
    private boolean helper(String curNT ){
        List<String[]> productionRules = cfgs.get(curNT);
//        System.out.print("~MAIN\n"+curNT + " -> ");
//        printCfg(productionRules);
//        System.out.println("------------");
        
        for(String[] pr : productionRules){
//             System.out.println("% "+curNT + " -> "+ Arrays.toString(pr));
            int prev = index;
            int j = 0;
            for( ; j < pr.length ;j++){
                
                String element = pr[j];
                
//                System.out.println("\nElement :"+ element +"' { of :"+curNT+"}");
                if(element.charAt(0) == '~') {
                    ++index;
                    return true;
                }
                if(element.charAt(0) == '<') {
//                    System.out.println("into => "+ element);
                    
                    if(!helper(element)) {
//                        System.out.println("@backing off");

                        break;
                        
                    }
                }
                else if(element.charAt(0) == 'E' && element.length() == 1) {

                    continue;
                }
                else {
//                    System.out.println("HERE IN TERMINAL");
//                    System.out.println("tokens.get(index).type ="+ tokens.get(index).type +"'");
                    String cp = tokens.get(index).type;
                    if(cp.equals(element)){
//                        if(invalidToken == tokens.get(index)) invalidToken = null;                        
//                        System.out.println("Matched Terminal ="+element);
//                        System.out.println("Matched Terminal value ="+tokens.get(index).value);

                        checked.add(tokens.get(index));
                        index++;                     
                        if(cp.equals("(")) createScope();
                        else if(cp.equals("}")) destroyScope();
//                        System.out.println("NEXT Terminal value ="+tokens.get(index).value);               
//                        System.out.println("%% checked = " + checked);
                    }
                    else{
//                        System.out.println("INVALID TERMINAL = " +tokens.get(index).value);
                        checked.add(tokens.get(index));
                        break;
                    }
                    erroredTokenIndex = Math.max(erroredTokenIndex , index);
                }
            }
            if( j == pr.length ){
//                System.out.println("Successfully parsed from here");
                return true;
            }
            else{
                index = prev;
            }
        }
        return false;
    }
    private void createScope(){
        scope++;
        scopeStack.add(scope);
        System.out.println("\n(+) Creating Scope ");
        System.out.println("scope no : "+scope + " , scopeStack = "+scopeStack);
    }
    private void destroyScope(){
        System.out.println("\n(-) Destroying Scope");
        scopeStack.remove(scopeStack.size() - 1);
        System.out.println("scopeStack = "+ scopeStack);
    }
    
    private void printCfgs(){
        for(String key : cfgs.keySet()){
            System.out.print(key +" -> " );
//            List<String> notFound = new ArrayList<>();
//            List<String> terminals = new ArrayList<>();
            for(String[] arr : cfgs.get(key)){
                System.out.print(Arrays.toString(arr) +" | ");
                
//                for(String ele : arr){
//                    if(ele.charAt(0) == '<'){
//                        if(!cfgs.containsKey(ele)) notFound.add(ele);
//                    }
//                    else{
//                        terminals.add(ele);
//                    }
//
//                }
                
            }
            System.out.println(" ");
        }
    }
    private void printCfg(List<String[]> productionRules){
        if(productionRules == null) return;
        for(String[] arr : productionRules){
            System.out.print(Arrays.toString(arr) +" | ");
        }
        System.out.println("");
    }
}
