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
public class GlobalTableElement {
    private String name , type;
    private boolean isStatic , isFinal;

    public GlobalTableElement(String name, String type) {
        this.name = name;
        this.type = type;
    }

    @Override
    public String toString() {
        return "GlobalTableElement{" + "name=" + name + ", type=" + type + '}';
    }

    public GlobalTableElement(String name, String type, boolean isStatic, boolean isFinal) {
        this.name = name;
        this.type = type;
        this.isStatic = isStatic;
        this.isFinal = isFinal;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public boolean isIsStatic() {
        return isStatic;
    }

    public void setIsStatic(boolean isStatic) {
        this.isStatic = isStatic;
    }

    public boolean isIsFinal() {
        return isFinal;
    }

    public void setIsFinal(boolean isFinal) {
        this.isFinal = isFinal;
    }
    
    
}
