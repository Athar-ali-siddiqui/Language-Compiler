/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package main;

import java.util.*;

/**
 *
 * @author Ather Ali Siddiqui
 */
public class Semantic_Analyzer {
    private Map <String, FunctionTableElement> function_table = new HashMap<>();
    private Map <String, GlobalTableElement> global_table = new HashMap<>();
    private Map <String, MainTableElement> main_table = new HashMap<>();
    private List<Integer> scopeStack = new ArrayList<>();
    private String curr_class_name;
    private int scope;
    
    
    public MainTableElement lookUpMT(String name){
        
        return main_table.get(name);
        
    }
    
    public boolean insertIntoMT(String name , String type , String tm , List<String> childs){
        MainTableElement temp = new MainTableElement(name, type, tm, childs);
        main_table.put(name, temp);
//        showMT();

        return true;
    }
    public FunctionTableElement lookUpFT(String key){
        return function_table.get(key);
    }
    
    public boolean insertIntoFT(String key , String name , String type,int scope ){
        FunctionTableElement temp = new FunctionTableElement(name, type,scope);
        function_table.put(key, temp);
//        showFT();

        return true;
    }
    public ClassTableElement lookUpCDT(String className , String propName){
        Map <String, ClassTableElement> temp = main_table.get(className).cdt;
        
        return temp.get(propName);
        
    }
    public boolean insertIntoCDT(String className , String key ,String name, String type, String am ,boolean sta,boolean fin , boolean abs ){
        Map <String, ClassTableElement> map = main_table.get(className).cdt;
        ClassTableElement temp = new ClassTableElement(name , type,am , sta , fin ,abs);
        map.put(name, null);
        map.put(key, temp);
//        showCDT(className);
        return true;
    }
    public GlobalTableElement lookUpGT(String name){
        return global_table.get(name);
    }
    public boolean insertIntoGT(String key ,String name , String type ){
        GlobalTableElement temp = new GlobalTableElement(name, type);
        global_table.put(key, temp);
        if(!name.equals(key))global_table.put(name, null);
        showGT();
        return true;
    }
    public String compatibility(String left, String right, String op)
    {
        List<String> types = new ArrayList();
        types.add("SC");types.add("CC");types.add("IC");types.add("FC");
        if(left == null || right == null) return null;
        else if( !types.contains(left) || !types.contains(right)  ) return null;
        else if ("SC".equals(left) && "SC".equals(right))
        {
            if ("+".equals(op)) { return "SC"; }
            else if ("==".equals(op) || "!=".equals(op)) { return "boolean"; }
        }
        else if (!"SC".equals(left) && !"SC".equals(right))  // else if ((left == "float" || left == "int" || left == "char") && (right == "float" || right == "int" || right == "char"))
        {
            if ("<".equals(op) || ">".equals(op) || "==".equals(op) || "<=".equals(op) || ">=".equals(op) || "!=".equals(op)) { return "boolean"; }
            else if ("IC".equals(left) && "IC".equals(right)) { return "IC"; }
            else if (!"CC".equals(right) && !"CC".equals(left)) { return "FC"; }
        }
        return null;
    }
    private void showFT(){
        System.out.println("\nFunctionTable ==> ");
        for(String key : function_table.keySet()){
            System.out.println(key+" >>> "+function_table.get(key));
        }
    }
    private void showGT(){
        System.out.println("\nGlobalTable ==> ");
        for(String key : global_table.keySet()){
            System.out.println(key+" >>> "+global_table.get(key));
        }
    }
    private void showMT(){
        System.out.println("\nMainTable ==> ");
        for(String key : main_table.keySet()){
            System.out.println(key+" >>> "+main_table.get(key));
        }
    }
    private void showCDT(String className){
        System.out.println("\nClassTable of {"+className+"} ==> ");
        for(String key : main_table.get(className).cdt.keySet()){
            System.out.println(key+" >>> "+main_table.get(className).cdt.get(key));
        }
    }
    
}
