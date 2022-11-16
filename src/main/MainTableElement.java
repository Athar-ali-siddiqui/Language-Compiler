/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package main;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author Ather Ali Siddiqui
 */
public class MainTableElement {
    private String name , type , tm ;
    public List<String> childOf = new ArrayList<>();
    public Map <String, ClassTableElement> cdt = new HashMap<>();

    public MainTableElement(String name, String type, String tm, List<String> childOf) {
        this.name = name;
        this.type = type;
        this.tm = tm;
        this.childOf = childOf;
    }
    
    public void add(String key ,ClassTableElement value){
        cdt.put(key, value);
        
    }

    public String getTm() {
        return tm;
    }


    @Override
    public String toString() {
        return "MainTableElement{" + "name=" + name + ", type=" + type + ", tm=" + tm + ", childOf=" + childOf + '}';
    }
    
     
}
