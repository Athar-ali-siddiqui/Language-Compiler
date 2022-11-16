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
public class ClassTableElement {
    private String name , type , am;
    private boolean isStatic , isFinal , isAbstract;

    @Override
    public String toString() {
        return "ClassTableElement{" + "name=" + name + ", type=" + type + ", am=" + am + ", isStatic=" + isStatic + ", isFinal=" + isFinal + ", isAbstract=" + isAbstract + '}';
    }

    public ClassTableElement(String name, String type, String am, boolean isStatic, boolean isFinal, boolean isAbstract) {
        this.name = name;
        this.type = type;
        this.am = am;
        this.isStatic = isStatic;
        this.isFinal = isFinal;
        this.isAbstract = isAbstract;
    }

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }

    public String getAm() {
        return am;
    }

    public boolean isIsStatic() {
        return isStatic;
    }

    public boolean isIsFinal() {
        return isFinal;
    }

    public boolean isIsAbstract() {
        return isAbstract;
    }
    
}
