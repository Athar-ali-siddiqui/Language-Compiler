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

//    private Set<Token> checked = new HashSet<>();
    private boolean isSemanticError = false;
    private int erroredTokenIndex = -1;
    private List<Token> list = new ArrayList<>();

    
//    Semantic Variables
    private Semantic_Analyzer SE = new Semantic_Analyzer();
    private int scope = 0;
    private List<Integer> scopeStack = new ArrayList<>();    
    private String curClass = "";
    
//    List<String[]> String 
    public Syntax_Analyzer(String path ,ArrayList<Token> tokens) {
        scopeStack.add(0);
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
//            System.out.println("PARSED = "+checked);
            
            return true;
        }
        else {
            
//            System.out.println("PARSED = "+checked);
//            System.out.println("error wala word = "+invalidToken.value + " ,line no ="+ invalidToken.line);
//            System.out.println("INDEX == "+ index);
            
//            Token errored = (Token)checked.toArray()[checked.size()-1];
            
//            System.out.println("Unexpected Token:\'" + errored.value+"\' "+"at line "+errored.line );
            Token errored = tokens.get(erroredTokenIndex);
            if(!isSemanticError){
                
                System.out.println("Unexpected Token:\'" + errored.value+"\' "+"at line "+errored.line );
            }
            else System.out.println(" (line# "+errored.line +")");
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
            List<Token> tempForList = new ArrayList<>(list);
            List<Integer> tempForScopeStack = new ArrayList<>(scopeStack);
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

//                        checked.add(tokens.get(index));

                        if(cp.equals(";") || cp.equals("{")){
                            if(! checkSemantic() ) {
                                isSemanticError = true;
                                return false;
                            }
                        }
                        else if(!cp.equals("}"))
                            list.add(tokens.get(index));
                        
                        checkScope(cp);
                        
                        index++;   
//                        System.out.println("NEXT Terminal value ="+tokens.get(index).value);               
//                        System.out.println("%% checked = " + checked);
                    }
                    else{
//                        System.out.println("INVALID TERMINAL = " +tokens.get(index).value);
//                        checked.add(tokens.get(index));
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
//                System.out.println("(BACKED TO INITIAL POSITION");
                index = prev;
                list = new ArrayList<>(tempForList);
                scopeStack = new ArrayList<>(tempForScopeStack);
            }
        }
        return false;
    }
    private void createScope(String cp){
//        System.out.println("\nclass part = " + cp +"     ( at line "+tokens.get(index).line+")");
        scope++;
        scopeStack.add(scope);
//        System.out.println("(+) Creating Scope ");
//        System.out.println("scope no : "+scope + " , scopeStack = "+scopeStack);
    }
    private void destroyScope(String cp){
//        System.out.println("\nclass part = " + cp +"     ( at line "+tokens.get(index).line+")");
        if(scopeStack.size() == 1) {
            curClass = "";
//            System.out.println("(%-)curClass = "+ curClass +"     ( at line "+tokens.get(index).line+")");
            return;
        }
        scopeStack.remove(scopeStack.size() - 1);
//        System.out.println("(-) Destroying Scope");
//        System.out.println("scopeStack = "+ scopeStack);
    }
    
    private void checkScope(String cp){
        
        if( cp.equals("(") && (list.get(0).type.equals("FUNC") ) )
            createScope(cp);

        else if(cp.equals(")") && tokens.get(index+1).type.equals("{") && ( list.get(0).type.equals("IF") || list.get(0).type.equals("WHILE") ))
            createScope(cp);
        else if( cp.equals("}") ){
            destroyScope(cp);
            
        }
   
    }
    private boolean checkSemantic(){
//        System.out.println("(@) list = "+list+"");
        
        boolean check = true; 
        String zero = list.get(0).type , one = list.get(1).type;
        
        if(zero.equals("CLASS") || one.equals("CLASS")) check = checkClass();
        else if(zero.equals("FUNC")) check = checkFunction();
        else if(zero.equals("AM")) check = checkVariable();
        else if((zero.equals("DT") || zero.equals("ID")) && one.equals("ID"))
            check = checkVariable();
        else if(zero.equals("IF") || zero.equals("WHILE"))
            check = checkROPExp();
        

        list.clear();
//        System.out.println("curClass = "+curClass);
        return check;
    }
    private boolean checkROPExp(){
        List<String> allTypes = new ArrayList<>();
        int s = 2;
        for(int i = 2 ; i < list.size() - 1 ;i++){
//            List<Token> list = new ArrayList<>();
            
            if(this.list.get(i).type.equals("ROP")){
                String type = checkExp(s, i);
                s = i+1;
                allTypes.add(type);
            }
//            else
//                list.add(this.list.get(i));
            
        }
        System.out.println("(!@#%#$&$*%) list = "+list);
        System.out.println("list[s] = "+list.get(s).value);
        System.out.println("list[n-1] = "+list.get(list.size()- 1).value);
        String type = checkExp(s, list.size()-1);
        System.out.println("Type = "+type);
//                s = i+1;
                allTypes.add(type);
        System.out.println("(!@#$%^&*)AllTYPES OF ROP ="+allTypes);
        String ft = allTypes.get(0);
        for(int i = 1 ; i < allTypes.size() ; i++){
            ft = SE.compatibility(DTtoCP(ft), DTtoCP(allTypes.get(i)), "+");
            System.out.println("ft = "+ft);
            if(ft == null){
                System.out.println("ERROR : Compatibilit issue in IF/WHILE ");
                return false;
            }
        }
        return true;
    }
    private boolean checkVariable(){
        String am = null ;
        
        boolean isStatic = false , isConst = false , isAbst = false;
        
        int i = 0;
        if(list.get(i).type.equals("AM")) am = list.get(i++).value;
        
        if(list.get(i).type.equals("STATIC")){
            isStatic = true;i++;
        }
        if(list.get(i).type.equals("CONST")){
            isConst = true;i++;
        }
        else if(list.get(i).type.equals("ABSTRACT")){
            isAbst = true;i++;
        }
        
        String dt = list.get(i++).value , id = list.get(i++).value ,arr = "";
//      Datatype agr ID hai tou Id ka look up
        if(list.get(0).type.equals("ID") && SE.lookUpMT(dt) == null){
            System.out.print("ERROR: '"+dt+"' is not declared scope");
            return false;
        }
//        Incase array hai tou [] yeh wala part nikal rahe hain
//        int i = 2;
        while(i < list.size() && !list.get(i).type.equals(";") && !list.get(i).type.equals("=")){
            arr += list.get(i++).value;
        }
//       [] yeh wala part append kar rahe hain DATATYPE me
        
//        System.out.println("arr = "+arr);
        int sc =scopeStack.get(scopeStack.size() -1);
//        System.out.println("(&)scope ="+sc);
//        Agr scope zero hai tou matlab global varible hai yeh
        
//        System.out.println("(&)i = "+i);
//        System.out.println("(&)list = "+list);
        
        if(i < list.size() ){
            String expAns = checkExp(i+1, list.size());
//            System.out.println("");
            System.out.println("(&)expAns = '"+expAns+"'");
            System.out.println("(&)dt = '"+dt+"'");
            if(arr.length() > 0){
                System.out.println("ArrExp = "+ expAns);
                String types[] = expAns.split(",");
                for(String s : types){
                    if((dt.equals("int") && s.equals("float")) || dt.equals("float") && s.equals("int")){
                        
                    }
                    else if(!dt.equals(s)){
                        MainTableElement temp = SE.lookUpMT(s);
                        if(temp != null){
                            if(!temp.childOf.contains(dt)){
                                System.out.println("ERROR: Type-mismatch in Array");
                                return false;
                            }
                            
                        }
                        else {
                            System.out.print("ERROR: Type-mismatch in Array");
                            return false;
                        }
                        
                    }
                }
                
            }
            else if(expAns == null){
//                if(i+2 == list.size()) System.out.print("ERROR: Type-mismatch in expression");
                return false;
            }
            else if(  !dt.equals(expAns)  && !( ( dt.equals("int") && expAns.equals("float") ) || ( dt.equals("float") && expAns.equals("int") )) ){
                if(i+2 == list.size()) System.out.print("ERROR: Type-mismatch in expression");
                return false;
            }
            
        }
        if(arr.length() > 0) dt += arr;
        if(am == null){
            if(sc == 0){
                    if(SE.lookUpGT(id)!= null){
                        System.out.print("ERROR: '"+id+"' is already declared in scope");
                        return false;
                    }
                    SE.insertIntoGT(id, id, dt);
                }
                else{
                    String key = sc +":"+id;
                    if(SE.lookUpFT(key)!= null){
                        System.out.print("ERROR: '"+id+"' is already declared in scope");
                        return false;
                    }
                    SE.insertIntoFT(key, id, dt, sc);
                }
        }
        else{
            if(SE.lookUpCDT(curClass,id)!= null){
                System.out.print("ERROR: '"+id+"' is already declared in scope");
                return false;
            }
            SE.insertIntoCDT(curClass, id, id, dt, am, isStatic, isConst, isAbst);
        }
        
        return true;
    }
    
    private boolean checkFunction(){
        String am = null , id ="" , rt = null ;
        StringBuilder para = new StringBuilder();
        
        boolean isStatic = false , isConst = false , isAbst = false;
        
        int i = 1;
        if(list.get(i).type.equals("AM")) am = list.get(i++).value;
        
        if(list.get(i).type.equals("STATIC")){
            isStatic = true;i++;
        }
        if(list.get(i).type.equals("CONST")){
            isConst = true;i++;
        }
        else if(list.get(i).type.equals("ABSTRACT")){
            isAbst = true;i++;
        }
        id = list.get(i).value;
        i+=2;
        while( !list.get(i).type.equals(")") ){
            if( list.get(i).type.equals("DT") )
                para.append(list.get(i++).value+",");
            else if( list.get(i).type.equals("ID") ){
                
                String temp = list.get(i).value;
                if(SE.lookUpMT(temp) == null){
                    System.out.print("ERROR: '"+temp+"' was not declared in this scope");
                    return false;
                }
                para.append(temp+",");
                i++;
            }
            i++;
        }
        if(para.length() > 0 )para.deleteCharAt(para.length()-1);
        i++;
        if(list.size() > i+1)rt = list.get(++i).value;
//      GLobal Functions
        if(am == null){
            String key = id+":"+ para.toString() ;
            if(SE.lookUpGT(key) != null){
                System.out.print("ERROR: '"+key+"' is already declared");
                  return false;
            }
            SE.insertIntoGT(key, id , para.toString() +"->"+rt);
        }
//        Constructor
        else if(rt == null){
            String key = id+":"+para.toString() ;
            if(list.get(2).value.equals(curClass)){
                if(SE.lookUpCDT(curClass, key ) != null){
                    System.out.print("ERROR: '"+key + "' is already declared");
                    return false;
                }
                
                SE.insertIntoCDT(curClass, key ,id , para.toString() +"->ctr" ,am,isStatic , isConst , isAbst);
            }
            else{
                System.out.print("ERROR: No return statement in function");
                return false;
            }
        }
//        Class Functions
        else{
            
            String key = id+":"+para.toString();
            if(!list.get(2).value.equals(curClass)){
                if(SE.lookUpCDT(curClass, key ) != null){
                    System.out.print("ERROR: '"+key + "' is already declared");
                    return false;
                } 
                SE.insertIntoCDT(curClass, key ,id , para.toString()+"->"+rt ,am,isStatic , isConst , isAbst);
            }
            else{
                System.out.print("ERROR: Return type specification for constructor invalid");
                return false;
            }
        }
        return true;
    }
    
    private boolean checkClass(){
        String name = "", type ="CLASS", tm = "";
        List<String> childs = new ArrayList<>();
        
        int n = list.size() , i = 0;
        if(!list.get(i).type.equals("CLASS")) tm = list.get(i++).value;
        i++;
        name = list.get(i++).value ;

        if(SE.lookUpMT(name) != null) {
            System.out.print("ERROR: Class '"+name +"' is already declared ");
            return false;
        }
        curClass = name;
        for(i = i + 1 ; i < n ;i++){
            Token parent = list.get(i);
            if(parent.type.equals("ID")){
                if(SE.lookUpMT(parent.value) == null) {
                    System.out.print("ERROR: Parent Class '"+parent.value +"' not found ");
                    return false;
                }
                childs.add(parent.value);
            }
        }
        return SE.insertIntoMT(name, type, tm, childs);
    }
    
    
    private String checkExp(int i,int end){
        List<String> exp = new ArrayList<>();
        
        String pr ="";
        String first = "" , opr = "" ;
        for(;i < end ;i++){
            String cp = list.get(i).type;
            String vp = list.get(i).value;
            pr += list.get(i).value;
//            System.out.println(cp +"  ");
            if(cp.equals("[")){
                
                int dim = 1;
                
                if(list.get(i+1).type.equals("[")) 
                    dim++;
                

                Set<String> allTypes = new HashSet<>();
                
                int j = i;
                for(; j < end ;j++){
                    if(list.get(j).type.equals(",") && !list.get(j).type.equals("]")){
                        String top = ""; 
                        for(int c = i ; c < j+1 ;c++){
                            top += list.get(c).value;
                        }
                        System.out.println("Top = "+top);
                        allTypes.add(checkExp(i, j+1));
                        i = j+1;
                    }
                    else if(list.get(j).type.equals("[")) i = j+1;
//                    arrDec += list.get(i).value;
                }
                allTypes.add(checkExp(i, j-1));
                System.out.println("(&&)ALLTYPES ="+allTypes);
                i = j;
                String ret = "";
                for(String s : allTypes) ret += s+",";
                
                return ret;
 
            }
            
            else if(cp.equals("IC") || cp.equals("FC") || cp.equals("SC") || cp.equals("CC")){

                exp.add(cp);
            }
            else if(vp.equals("+") || vp.equals("-") || vp.equals("/") || vp.equals("*") || vp.equals("(") || vp.equals(")")){
                exp.add(vp);
            }
            else if(cp.equals("ID")){
                    if(i+1 < list.size() && list.get(i+1).type.equals("(")){
                        int j = i+2;
                        vp += ":";
                        String type = lookUpFunc(vp);
//                        System.out.println("(****)TYPE ="+type.split("->")[1]);
                        if(list.get(j).type.equals(")")){

                            type = type.split("->")[1];
                            System.out.println("(*%***)TYPE ="+type);
                            exp.add(type);
                            
                        }
                        else{
                            List<String> paraTypes = new ArrayList<>();
                            i += 2;
                            for(; j < end ;j++){
                                if(list.get(j).type.equals(")") ){
                                    String intype = checkExp(i, j);
                                    paraTypes.add(intype);
                                    i = j;
                                    break;
                                }
                                if(list.get(j).type.equals(",") ){
                                    String intype = checkExp(i, j);
                                    paraTypes.add(intype);
                                    i = j;
                                }
                                
                            }
                            System.out.println("(>>>)ParaTypes = "+paraTypes);
                            for(String paraType : paraTypes) vp += paraType;
                            
                            type = lookUpFunc(vp);
                            if(type == null ){
                                System.out.println("ERROR: not found");
                                return null;
                            }
                            
                        }
                        i = j+1;
                    }
                    if(i+1 < list.size() && list.get(i+1).type.equals("[")){
                        String typeofID = lookUpVar(vp);
                        int dim = 1;
                        
                        
                        if(typeofID == null){
                            System.out.println("ERROR: '"+vp+"' not found ");
                            return null;
                        }
                        
                        if(typeofID.length()-3 > 0 && typeofID.charAt(typeofID.length()-3) == ']') dim++;
//                        System.out.println("(!!!!)typeofARRay= "+typeofID);
//                        System.out.println("(!!!!)dim = "+dim);
                        int j = i + 2;
                        int innerArrayCount = 0;
                        while(j < list.size() ){
                            if(list.get(j).type.equals("]") && innerArrayCount == 0) break;
                            
                            if(list.get(j).type.equals("["))innerArrayCount++;
                            else if(list.get(j).type.equals("]"))innerArrayCount--;
                            j++;
                        };
//                        System.out.println("$LIST = "+list);
//                        System.out.println("$j = "+j);
                        if(i+2 == j){
                            System.out.println("ERROR: Index value of array must be Integer ");
                            return null;
                        }
                        String innerType = checkExp(i+2, j);
                        dim--;
//                        System.out.println("Inner type of [] = "+innerType);
                        if(innerType == null){
//                            System.out.print("ERROR: Array ki dimension me masla hai kuch");
                            return null;
                        }
                        else if(!innerType.equals("int")){
                            System.out.println("ERROR: Index value of array must be Integer ");
                            return null;
                        }
                        i = j;
//                        System.out.println("$LIST = "+list);
//                        System.out.println("$i = "+i);
                        
                        if(i+1 < list.size() && list.get(i+1).type.equals("[")){
                            dim--;
//                            System.out.println("TRUE IN ");
                            i++;
                            j = i;

                            while(j+1 < list.size() && !list.get(++j).type.equals("]"));
//                            System.out.println("$LIST = "+list);
//                            System.out.println("$j = "+j);
                            if(i+1 == j){
                                System.out.println("ERRO8R: Index value of array must be Integer ");
                                return null;
                            }

                            innerType = checkExp(i+1, j);
//                            System.out.println("Inner type of [] = "+innerType);
                            if(innerType == null){
                                 return null;
                            }else if(!innerType.equals("int")){
                                System.out.println("ERROR: Index value of array must be Integer ");
                                return null;
                            }
                            i = j;
                        }
//                        System.out.println("DIM = "+dim);
                        if(dim != 0) {
                            System.out.print("ERROR: Array ki dimension me masla hai kuch");
                            return null;
                        }
                        String ty = typeofID.split("]")[0];
//                        System.out.println("typeofARRay= "+ty.substring(0, ty.length()-1));
                        exp.add(DTtoCP(ty.substring(0, ty.length()-1)));    
                    
                }
                else{
                    String typeofID = lookUpVar(vp);
                    System.out.println("t5ypeofID = "+typeofID);

    //                Agr variable lookup me nahi mila tou
                    if(typeofID == null){
                        System.out.print("ERROR: '"+vp+"' not found");
                        return null;
                    }
    //                Agr look up me mil gaya tou compatibity check karo             
                    else{
                        exp.add(typeofID);
                    }
                
                }
            }

        }
//        System.out.println("~"+pr);
        
        return CPtoDT(checkCompatibility(exp));

    }
    
    private String checkCompatibility(List<String> exp){
        System.out.println("\nEXP = "+exp);
        Stack<String> stk = new Stack<>();
        List<String> post = new ArrayList<>();
        
        for(String str : exp){
            if(str.equals("(")) stk.add("(");
            else if(str.equals(")")){
                while(!stk.empty() && !stk.peek().equals("("))
                    post.add(stk.pop());
                stk.pop();
            }
            else if(prec(str) == -1){
                post.add(str);
            }
            else{
                while(!stk.empty() && prec(str) <= prec(stk.peek()) )
                    post.add(stk.pop());
                
                stk.add(str);
            }
        }
        while(!stk.empty()) 
            post.add(stk.pop());
        
        System.out.println("POSTFIX = "+post+"\n");
        for(String str : post){
            if(str.equals("*") || str.equals("-") || str.equals("+") || str.equals("^") || str.equals("/") ){
                String s = !stk.empty()? stk.pop() : null;
                String f = !stk.empty()? stk.pop() : null;
//                System.out.println("fisrt = "+f);
//                System.out.println("second = "+s);
                String temp = SE.compatibility(f, s, str);
//                System.out.println("temp = "+temp);
                if(temp == null){
                    System.out.print("E1RROR: Type-mismatch in expression ");
                    return null;
                }
                else stk.add(temp);
            }
            else {
                stk.add(str);
            }
        }
//        System.out.println("Stack = "+stk);
        return stk.isEmpty()? null :stk.peek();
    }
    private int prec(String s){
        if(s.equals("^")) return 3;
        else if(s.equals("*") || s.equals("/")) return 2;
        else if(s.equals("-") || s.equals("+")) return 1;
        
        return -1;
    }
    private String CPtoDT(String first){
        if(first == null) return null;
        if(first.equals("IC")) return "int";
        if(first.equals("SC")) return "string";
        if(first.equals("CC")) return "char";
        if(first.equals("FC")) return "float";
        return first;
    } 
    private String DTtoCP(String first){
        if(first.equals("int")) return "IC";
        if(first.equals("string")) return "SC";
        if(first.equals("char")) return "CC";
        if(first.equals("float")) return "FC";
        return first;
    }
    private String lookUpVar(String id){
        String ans = null;
        for(int i = scopeStack.size() -1 ; i >= 1 ;i--){
            String key = scopeStack.get(i) +":"+id;
            
            FunctionTableElement temp = SE.lookUpFT(key);
            if(temp != null) {
                return DTtoCP(temp.getType());
                
            }
        }
        if(curClass.length() > 0){
            ClassTableElement temp = SE.lookUpCDT(curClass,id);
            if(temp != null) {
                return DTtoCP(temp.getType());
                
            }
        }
        
        GlobalTableElement temp = SE.lookUpGT(id);
        if(temp != null) {
            return DTtoCP(temp.getType());
                
        }
        return null;
    }
    private String lookUpFunc(String id){
        String ans = null;
        
        if(curClass.length() > 0){
            ClassTableElement temp = SE.lookUpCDT(curClass,id);
            if(temp != null) {
                return DTtoCP(temp.getType());
                
            }
        }
        
        GlobalTableElement temp = SE.lookUpGT(id);
        if(temp != null) {
            return DTtoCP(temp.getType());
                
        }
        return null;
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
