package main;
import java.util.*;import java.io.*;import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;
/**
 * @author Athar Ali Siddiqui
 */
public class Lexical_Analyzer {
    private Map<String , String> keywords;
    private ArrayList<Token> tokens = new ArrayList<>();
    private String sourceCodePath = null , wordOutputPath = null , tokenOutputPath = null ;
    private int id = 0;
    public Lexical_Analyzer(Map<String , String> keywords ,String scp , String wop , String top) {
        this.sourceCodePath = scp;
        this.wordOutputPath = wop;
        this.tokenOutputPath = top;
        this.keywords = keywords;
        try {
            this.breakWord();
        } catch (FileNotFoundException ex) {
            Logger.getLogger(Lexical_Analyzer.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public ArrayList<Token> getTokens(){
        ArrayList<Token> temp = new ArrayList<>(this.tokens);
        temp.add(new Token(id++,"PROGRAM TERMINATOR" ,"~",-1));
//        System.out.println("a == " + temp);
        return temp;
    }
    
    
    private void writeOnFile(String path){  
        if(path != null || !path.isEmpty()){
            try {

            FileWriter fWriter = new FileWriter(path);

            StringBuilder sb = new StringBuilder();
            for(Token s : tokens)sb.append(s+"\n");           
            fWriter.write(sb.toString());
            fWriter.close();  
            }
            catch (IOException e) {System.out.print(e.getMessage());}
        }

    }
    
    private void breakWord( ) throws FileNotFoundException{
//        setKeywords();
        File myObj = new File(this.sourceCodePath);
        Scanner in = new Scanner(myObj);
        int line_no = 1;
        while (in.hasNextLine()) {
            ArrayList<String> list = new ArrayList<>();
            String line = in.nextLine() + " ";  
            StringBuilder temp = new StringBuilder("");
            
//            System.out.println("line = " + line);
            for(int i = 0 ; i < line.length() ;i++){
//                System.out.println(" ~i = "+i);
                char ch = line.charAt(i);
//                System.out.println(" ~char at top = "+ch);
//                System.out.println(" ~temp = "+temp);

//                Space ' ' ,Single Line Comment 
                if( ch == ' ' || ch == '#') {
                    if( !temp.equals(" ") ) list.add(temp.toString());
                    
                    temp = new StringBuilder();
//                Single Line Comment check
                    if(ch == '#')break;
                }
//                Multi line comment
                else if(ch == '$'){
                    if( temp.length() > 0 ) list.add(temp.toString());
                    temp = new StringBuilder();
                    temp.append("$");
                    boolean first_itr_check = false , check = true;
                    int j  = i+1;
                    do{
                        if(first_itr_check){
                            line = in.nextLine();
                            line += " ";   
                            j = 0;
                        }
                        for(; j < line.length() ;j++){
                            if(line.charAt(j) == '$') {
                                temp = new StringBuilder();
                                check = false;
                                break;
                            }
                            else temp.append(line.charAt(j));
                        }
                        first_itr_check = true;
                    }while(check && in.hasNextLine());
                    
                    if(check) list.add(temp.toString());
                    i = j;
                }
                else if(ch == '&' && line.charAt(i+1) == '&'){
                    if( temp.length() > 0 ) list.add(temp.toString());
                    temp = new StringBuilder();
                    list.add("&&");
                    i++;
                }
                else if(ch == '|' && line.charAt(i+1) == '|'){
                    if( temp.length() > 0 ) list.add(temp.toString());
                    temp = new StringBuilder();
                    list.add("||");
                    i++;
                }
                else if( ch =='\'' ){
                    
                    if( temp.length() > 0 ) list.add(temp.toString());
                        
                    temp = new StringBuilder().append("'");

                    if(line.charAt(i+1) == '\\'  ){   
                        if(line.charAt(i+3) == '\''){
                            if(line.charAt(i+2) == '\\')temp.append("\\\\'");
                            else if (line.charAt(i+2) == '\'')temp.append("\\\''");
                            else temp.append("\\"+line.charAt(i+2)+"'");
                            i+=3;
                        }
                        else{
                            temp.append("\\"+line.charAt(i+2));
                            i+=2;
                        }
                        
                    }
                    else if(line.charAt(i+2) == '\''){
                        temp.append(line.charAt(i+1)+"'");
                        i+=2;
                    }
                    else{
                        
                        temp.append(line.charAt(i+1));
                        i++;
                    }
//                    System.out.println("temp = "+temp);
                    list.add(temp.toString());
                    temp = new StringBuilder();
//                    System.out.println("temp = "+temp);
                }
//                integer and float
                else if(ch == '.' && isAlphaNumeric(line.charAt(i+1))){
                    StringBuilder temp2 = new StringBuilder();
                    i++;
//                    System.out.println("temp = "+temp);
//                    System.out.println(" is alpha Num = "+isAlphaNumerics(temp2.toString()));
//                    ~~~.~~~
//                       ^^^^ : saving right side of dot into temp2
                    for( ; i < line.length() ;i++){
                        ch = line.charAt(i);
                        if(isAlphaNumeric(ch)) temp2.append(ch);
                        else {i--;break;}
                    }
//                    System.out.println("temp2 = "+ temp2);
//                    System.out.println(" is alpha Num = "+isAlphaNumerics(temp2.toString()));
                    
//                    .IC,ID - .IC - .ID
                    if(temp.length() == 0){
                        if(isIdentifier(temp2.toString())){
                            list.add(".");list.add(temp2.toString());   
                        }
                        else list.add("."+temp2.toString());
                        temp = new StringBuilder();
                    }
//                    (ID/FCID/NUM).ID
//                              ^  ^ ^ : () - . - ID
                    else if(isIdentifier(temp2.toString())){
                        list.add(temp.toString());list.add(".");list.add(temp2.toString());
                    }
//                    NUM.(NUM/FCID)
//                      ^           : NUM.()
                    else if(( isAlphaNumerics(temp2.toString())) && isDigits(temp.toString()) ){
                        list.add(temp.toString()+"."+temp2.toString());
                    }
//                    (ID/FCID).(NUM/FCID)
//                           ^ ^            : () - .()
                    else if((isAlphaNumerics(temp2.toString())) && !isDigits(temp.toString()) ){
                        list.add(temp.toString());list.add("."+temp2.toString());
                    }

                    temp = new StringBuilder();
                }

//                checking punctuator working as word breaker
                else if(isPunctuator(ch)){
                    if( temp.length() > 0 ) list.add(temp.toString());
                    temp = new StringBuilder();
                    list.add(ch+"");
                }
                else if(isOperator(ch)){
                    if( temp.length() > 0 ) list.add(temp.toString());
                    temp = new StringBuilder();
                    if( i < line.length() - 1 && line.charAt(i+1) == '='){
                        list.add(ch+"=");
                        i++;
                    }
                    else
                        list.add(ch+"");
                }
//                checking String if { ch is " }
                else if ( ch == '"'){
                    if( temp.length() > 0 ) list.add(temp.toString());
                    temp = new StringBuilder().append('"');
                    i++;
                    for(; i < line.length() ;i++){
                        char c = line.charAt(i);
                        if(c == '"'){
                            temp.append(c);
                            break;
                        }
//              here we can check Invalid lexem when there is no second valid element after \
//              valid : \" ,\\ ,\n
//                              \n ko esi rehne dengy string me "\nsadas" .... else me handle hoga
                        else if(c == '\\'){ 
                            if(line.charAt(i+1) == '"' || line.charAt(i+1) == '\\' 
                                    || line.charAt(i+1) == 'n') {
                                    temp.append("\\"+line.charAt(++i));
                            }
//                            if \z occurs then only z will be append in string
                            else{
                                temp.append(line.charAt(++i));
                            }
                        }
                        else
                            temp.append(c);
                    }
                    list.add(temp.toString());
                    temp = new StringBuilder();
                }
                else {
                    temp.append(ch);
                }
//                System.out.println("list = "+list);
            } 
            for(String str : list){
                str = str.trim();
                if(str.equals("$") || str.equals(" ") || str.length() == 0)continue;
                tokens.add(new Token(id++,str.trim(),line_no));
            }
            line_no++;
        }
        writeOnFile(this.wordOutputPath);
        createToken();
    }
    private void createToken(){
        for(Token tok : tokens){
            String word = tok.value;
            
            if( keywords.get(word) != null){
                tok.type = keywords.get(word);
//                System.out.println("typye = "+ tok.type);
            }
//            identifier
            else if(isIdentifier(word))
                 tok.type ="ID";           
//            Integer
            else if(Pattern.matches("[+-]?[\\d]+", word))
                tok.type ="IC";           
//            Float
            else if(Pattern.matches("[+-]?[\\d]*[.][\\d]+", word))
                tok.type ="FC";          
//            char
            else if(isCharacter(word)){
                
                word = word.substring(1, word.length()-1);
                if(word.equals("\\\\")){
                    tok.value = "\\";
                }
                else if(word.equals("\\'")){
                    tok.value = "'";
                }
                else 
                    tok.value = word;
                tok.type ="CC";
            }
//            string
            else if(Pattern.matches("\"(.|\\s)*\"", word))
                tok.type ="SC";
            else tok.type ="INVALID";
            
        }
        writeOnFile(this.tokenOutputPath);
    }
    private boolean isIdentifier(String word){
        return Pattern.matches("[_]*[a-zA-Z]+[\\w]*", word);
    }
    private boolean isCharacter(String str){
//        if("'\\'".equals(str) || "'''".equals(str)) return false;
//        else 
//        if(Pattern.matches("'.'", str)) return true;
//        else if(str.length() > 3 && str.charAt(0) == '\'' && str.charAt(1) == '\\' && str.charAt(3) == '\''){
//            char esc[] = {'n','r','\\','\''};
//            for(char e : esc) if(e == str.charAt(2)) return true;
//        }
//        return false;
        return Pattern.matches("'[.\\.]'", str) || Pattern.matches("'.'", str);
    }
    private boolean isDigit(char ch){
        return Pattern.matches("[\\d]", ch+"");
    }
    private boolean isOperator(char ch){
        char[] opr = {'+','-','/','*','%','=','<','>','!'};
        for(char i : opr ) if(ch  == i)return true;
        return false;
    }
    private boolean isPunctuator(char ch){
        char[] punc = {'.',',',';','(',')','{' ,'}','[',']',':' };
        for(char i : punc ) if(ch  == i)return true;
        return false;
    }
    private boolean isAlphabet(char c){
        return Pattern.matches("[a-zA-Z]+", c + "");
    }
    private boolean isAlphabets(String str){
        return Pattern.matches("[a-zA-Z]+", str);
    }
    private boolean isDigits(String str){
        return Pattern.matches("[\\d]+", str);
    }
    private boolean isAlphaNumeric(char ch){
        return Pattern.matches("[\\w]", ch+"");
    }
    private boolean isAlphaNumerics(String str){

        return Pattern.matches("[\\w]+", str);
    }
}
