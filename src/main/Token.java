/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package main;

/**
 *
 * @author Ather Ali Siddiqui
 */
public class Token {
    String type,value;
    int line , id;
    public Token(int id ,String type, String value, int line) {
        this.type = type;
        this.value = value;
        this.line = line;
        this.id = id;
    }

    public Token(int id ,String value, int line) {
        this.value = value;
        this.line = line;
        this.id = id;
    }
    @Override
    public String toString() {
        if(type == null) return value;
        return "( " + id +" , "+ type + " , " + value + " , " + line + ')';
    }

    @Override
    public int hashCode() {
        return id; //To change body of generated methods, choose Tools | Templates.
    }
    
    
}