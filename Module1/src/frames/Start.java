/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package frames;
import java.io.*;
import java.util.*;
import java.util.Map;
import java.util.Dictionary;
import java.util.List;

/**
 *
 * @author PARTH KRISHNA SHARMA
 */

class Relations{
    String relation; String fd;
    Relations(String relation,String fd){
        this.relation=relation;
        this.fd=fd;
    }

    public static List<String> candidate_key(String relation,String fd)
    {
        List<String> fd_set0 = new ArrayList<>();
        //List<String> fd_set1 = new ArrayList<>();
        StringTokenizer st1 = new StringTokenizer(fd, ",{}"); 
        while (st1.hasMoreTokens()) {
            fd_set0.add(st1.nextToken());
        }
        List<String> fd_set = get_fd_set(fd_set0);                 //find closure of fd_set

        int num_of_attr = (relation.length()-1)/2;                //finding essential elements of the key
        HashMap<Character, Integer> map = new HashMap<>();        //initializing map to 0 for all attr
        for(int j=1; j<relation.length(); j=j+2){
            map.put(relation.charAt(j),0);
        }

        for(String s: fd_set){                                    //marking all right side attributes
            for (Map.Entry<Character, Integer> entry : map.entrySet()){
                if(entry.getKey()==s.charAt(s.length()-1)){
                    map.put(entry.getKey(),1);
                }
            }   
        }       
        
        List<Character> essential_attr = new ArrayList<>();        //adding the essential attr of key to list
        List<Character> non_essential_attr = new ArrayList<>(); 
        for (Map.Entry<Character, Integer> entry : map.entrySet()){
            if(entry.getValue()==0){
                essential_attr.add(entry.getKey());         
            }
            else{
                non_essential_attr.add(entry.getKey());
            }
        }

        for(int i=1; i<relation.length(); i=i+2){                   //initializing all relation attr to 0
            map.put(relation.charAt(i),0);   
        }

        ArrayList<String> store = new ArrayList<>();
        HashSet<Character> list = new HashSet<Character>();
        HashMap<String,Integer> cand_key = new HashMap<>();
        for(Character c: essential_attr) { list.add(c);}
        int n = non_essential_attr.size();
        for(int i=0; i<=(1<<n); i++){
            for(int j=0; j<n; j++){
                if ((i & (1 << j)) != 0){
                    list.add(non_essential_attr.get(j));
                } 
            }
            for(Character c: list){ map.put(c,1);} 
            Map<Character,Integer> map_new = closure(map,fd_set);
            boolean ans=false;                          
            ans = complete(map_new);
            if(ans==true){ 
                String s = "";
                for(Character c: list) { s=s+c;} cand_key.put(s,0);
            }
            if((i==0)&&(ans==true)){
                String s = "";
                for(Character c: list) { s=s+c;} store.add(s);
                break;
            }
            else{
                list.retainAll(essential_attr);
                for(int k=1; k<relation.length(); k=k+2){                   //initializing all relation attr to 0
                    map.replace(relation.charAt(k),0);   
                }
            }
        }
        int max_len=0;
        int min_len=100;
        for (Map.Entry<String, Integer> entry : cand_key.entrySet()){
            String s = entry.getKey();
            if(s.length()<min_len){
                min_len=s.length();
            }
            if(s.length()>max_len){
                max_len=s.length();
            }
        }
        
        for (Map.Entry<String, Integer> entry : cand_key.entrySet()){
            while(entry.getValue()!=2){
                for (Map.Entry<String, Integer> entry1 : cand_key.entrySet()){
                    String s = entry1.getKey();
                    if((entry1.getValue()==0)&&(s.length()==min_len)){
                        cand_key.replace(s,2);
                        store.add(s);
                    }
                }
                min_len++;
                for (Map.Entry<String, Integer> entry1 : cand_key.entrySet()){
                    String s = entry1.getKey();
                    if(entry1.getValue()==0){
                        for(String s1: store){
                            if(stringToCharacterSet(s).containsAll(stringToCharacterSet(s1))){
                                cand_key.replace(s,2);
                            }
                        }
                    }
            
                }
            }
        }

        ArrayList<String> cand_key_set = new ArrayList<>();
        cand_key_set= removeDuplicates(store);
        return cand_key_set;
    }

    public static List<String> get_fd_set(List<String> fd_set0){
        List<String> fd_set1 = new ArrayList<>();
        List<String> storeString = new ArrayList<>();               //ensuring all right side attributes are single attributes   
        for (String s : fd_set0) {                                          
            for(int i=0; i<s.length(); i++){
                char[] sch = s.toCharArray();
                if (s.charAt(i)=='>'){
                    if(i+2 != s.length()){
                        for(int j=1; j<=(s.length()-i-1); j++){
                            String decomp = "";
                            decomp = decomp.copyValueOf(sch,0,i+1);
                            decomp = decomp + s.charAt(i+j);
                            fd_set1.add(decomp);
                        }
                        storeString.add(s);
                    }
                }
            }
        }
        for (String s: fd_set1){                                  //add decomposed relations to fd_set
            fd_set0.add(s); //System.out.println(s);
        }          
        for(String s1: storeString){                                 //remove the original relation that was decomposed
            //System.out.println(s1);
            fd_set0.remove(new String(s1));
        }

//        for(String s: fd_set0){ 
//            //System.out.println(s);                                  //removing redundant fd 
//            char[] sch = s.toCharArray();
//            if((sch[0]==s.charAt(s.length()-1))&&(s.charAt(1)=='-')){
//                fd_set0.remove(s);
//            }
//        }
        List<String> fd_set = loop_transitive_fd(fd_set0);
        return fd_set;
    }

    public static Set<Character> stringToCharacterSet(String s){
        Set<Character> set = new HashSet<>();
        for (char c : s.toCharArray()) {
            set.add(c);
        }
       return set;
    }

    public static <T> ArrayList<T> removeDuplicates(ArrayList<T> list) 
    { 
        ArrayList<T> newList = new ArrayList<T>(); 
        for (T element : list) { 
            if (!newList.contains(element)) { 
                newList.add(element); 
            } 
        } 
        return newList; 
    } 

    public static Map<Character,Integer> closure(Map<Character,Integer> map,List<String> fd_set0)    //function to find closure of attr
    {
        List<String> fd_set = new ArrayList<>();
        fd_set = get_fd_set(fd_set0);
        
        for(int j=0; j<3; j++){
        for(Character key: map.keySet()){
        int flag=1;                     
            if(map.get(key)==1){
                for(String s:fd_set){
                    if(key==s.charAt(0)){
                        if(s.charAt(1)=='-'){
                            map.put(s.charAt(s.length()-1),1);
                        }
                        else{
                            for(int i=0; s.charAt(i)!='-'; i++){
                               if(map.get(s.charAt(i))==0){
                                  flag=0;
                               }    
                            }
                            if(flag==1){
                             map.put(s.charAt(s.length()-1),1);
                            }
                        }
                    }
                }
            }
        }
       }
        return map;
    }  

    public static HashSet<Character> closure_calc (List<Character> attrs, List <String>f_set){
        HashSet<Character> x= new HashSet<Character>();
        for(int i=0; i<attrs.size(); i++){
            x.add(attrs.get(i));
        }
    
        HashSet<Character> xplus= new HashSet<Character>();
        xplus.addAll(x);
        
        HashSet<Character> new_xplus=new HashSet<Character>();
        int outer_change=1;
        while(outer_change==1){
            int change=1;
            HashSet<Character> outer_old_xplus= new HashSet<Character>();
            outer_old_xplus.addAll(xplus);
            while(change==1){
                HashSet<Character> old_xplus= new HashSet<Character>();
                old_xplus.addAll(xplus);
                for(String f: f_set){
                    HashSet <Character> s1= new HashSet<Character>();
                    for(int i=0; i<f.indexOf("-"); i++){
                        s1.add(f.charAt(i));
                    }
                    
                    if(isSubset(s1,xplus)){
                        for(int i=f.indexOf(">")+1; i<f.length(); i++){
                            xplus.add(f.charAt(i));
                        }
                    }
                    if(xplus.equals(old_xplus)){
                        change=0;
                    }
                }
                new_xplus.addAll(xplus);
            }
            if(outer_old_xplus.equals(new_xplus)){
                outer_change=0;
            }
        }
        return xplus;
    }
    
    public static <T> boolean isSubset(Set<T> setA, Set<T> setB) { //to check if a is the subset of the other
        return setB.containsAll(setA);
    }

    public static List<String> transitive_fd(String dependancy, List<String> fd_set, List <String> redun){ //the dependancy passed to this function must have a single attribute on the rhs
        char c;
        String r="";
        int index=dependancy.indexOf('>');
        c=dependancy.charAt(index+1);
        List<Character> attr=new ArrayList<>();
        List<String> fd_return=new ArrayList<>();
        HashSet<Character> ret= new HashSet<Character>();
        fd_return.addAll(fd_set);
        for(int i=0; i<dependancy.length(); i++){
            if(dependancy.charAt(i)=='>'){
                c=dependancy.charAt(i+1);
            }
            else if(i<(index-1)){
                r=r+dependancy.charAt(i);
            }
        }
        attr.add(c);
        ret= closure_calc(attr, fd_set);
        for(char ch: ret){
            String s1=r;
            s1=s1+"->"+ch;
            if(!redun.contains(s1)){
                fd_return.add(s1);
            }
        }
        return fd_return;
    }

    public static List <String> loop_transitive_fd(List <String> fd_set){
        List <String> fd_ret= new ArrayList<>();
        for(String s: fd_set){
            List <String> temp= new ArrayList<>();
            temp.addAll(transitive_fd(s, fd_set, fd_ret));
                for(String s1: temp){
                    if(!fd_ret.contains(s1)){
                        fd_ret.add(s1);
                    }
                }
        }
        return fd_ret;
    }

    public static Map<Character,Integer> closure_for_3nf(Map<Character,Integer> map,List<String> fd_set)    //being used only for 3nf xD
    {
        int flag=1;
        for(Character key: map.keySet()){                       
            if(map.get(key)==1){
                for(String s:fd_set){
                    if(key==s.charAt(0)){
                        for(int i=0; s.charAt(i)!='-'; i++){
                            if(map.get(s.charAt(i))==0){
                                flag=0;
                            }   
                        }
                        if(flag==1){
                            map.put(s.charAt(s.length()-1),1);
                        }
                    }
                }
            }
        }
        return map;
    }

    public static boolean equivalence(List <String>fd_set1, List <String> fd_set2, Relations r){
        boolean res=false;
        int flag=1;
        for(int i=1; i<r.relation.length(); i=i+2){
            HashMap<Character, Integer> map = new HashMap<>();      
            for(int j=1; j<r.relation.length(); j=j+2){
                if(j==i){   
                    map.put(r.relation.charAt(j),1);
                }
                else{
                    map.put(r.relation.charAt(j), 0);
                }
            }
            if(!closure_for_3nf(map, fd_set1).equals(closure_for_3nf(map, fd_set2))){
                flag=flag*0;
            }   
            else{
                flag=flag*1;
            }
        }
        if(flag==1){
            res=true;
        }
        return res;
    }

    public static List<String> minimal_cover(Relations r){
        List<String> fd_ret= new ArrayList<>();
        List<String> fd_set = new ArrayList<>();
       
        
        StringTokenizer st1 = new StringTokenizer(r.fd, ",{}"); 
        while (st1.hasMoreTokens()) {
            fd_set.add(st1.nextToken());
        }
        fd_ret.addAll(fd_set); //1
        Set<Character> r_set = new HashSet<Character>();
        StringTokenizer st2 = new StringTokenizer(r.relation, ",{}"); 
        while (st2.hasMoreTokens()) {
            Character c = (st2.nextToken()).charAt(0);
            r_set.add(c);
        }

        for(String s:fd_set){
            List<String> fd_set1 = new ArrayList<>();
            for(String s1:fd_set){
                if(!s.equals(s1)){
                    if(!fd_set1.contains(s1)){
                        fd_set1.add(s1);
                    }
                }
            }
            for (int i=0; (s.charAt(i)!='-'); i++){
                String d="";
                if((s.indexOf("-")==1)){
                    for(int j=0; j<(s.length()); j++){
                        d=d+s.charAt(j);
                        
                    }
                }
                else{                
                    for(int j=0; j<(s.length()); j++){
                        // if(s.indexOf("-")==1){
                        //     d=d+s.charAt(0);
                        // }
                        if(s.charAt(i)!=s.charAt(j)){
                            d=d+s.charAt(j);
                        }
                    }
                }
                if(!fd_set1.contains(d)){
                    fd_set1.add(d);      
                }          
                if(equivalence(fd_ret, fd_set1, r)){ //if(equivalence(fd_ret, fd_set1, r)){
                   for(String s2: fd_set1){
                        List<String> fd_set2 = new ArrayList<>();
                        for (String s3: fd_set1){
                            if(!s2.equals(s3)){
                                if(!fd_set2.contains(s3)){
                                    fd_set2.add(s3);
                                }
                            }
                        }
                        if(equivalence(fd_set1, fd_set2, r)){
                            fd_ret.removeAll(fd_ret);
                            fd_ret.addAll(fd_set2);
                        }
                        else{
                            fd_ret.removeAll(fd_ret);
                            fd_ret.addAll(fd_set1);
                        }
                    }
                }

                else{
                    for(String s2: fd_set){
                        List<String> fd_set2 = new ArrayList<>();
                        for (String s3: fd_set){
                            if(!s2.equals(s3)){
                                if(!fd_set2.contains(s3)){
                                    fd_set2.add(s3);
                                }
                            }
                        }
                        if(equivalence(fd_set, fd_set2, r)){
                            // fd_ret= fd_set2;
                            fd_ret.removeAll(fd_ret);
                            fd_ret.addAll(fd_set2);
                        }
                        else{
                            // fd_ret=fd_set;
                            fd_ret.removeAll(fd_ret);
                            fd_ret.addAll(fd_set);
                        }
                    }
                }
            }

        }
        //REMOVE THE FOLLOWING PART OF THIS FUNCTION IF CLOSURE WORKS PROPERLY
        List<String> to_check= new ArrayList<>();
        to_check.addAll(fd_ret);
        for(String red: to_check){
            List<String> fd_red= new ArrayList<>();
            for(String red1:to_check){
                if(!red1.equals(red)){
                    fd_red.add(red1);
                }
            }
            if(equivalence(fd_red, fd_ret, r)){
                fd_ret.removeAll(fd_ret);
                fd_ret.addAll(fd_red);
            }
            fd_red.removeAll(fd_red);
        }
        // int rk=1;
        // for(String debug: fd_ret){
        //     System.out.println(debug+" "+rk);
        //     rk++;
        // }
        return fd_ret;
    }

    public static boolean complete(Map<Character,Integer> map_new){
        boolean complete=true;                                             //checking whether the closure of essential attr is the entire set of attributes or not
        for(Integer key: map_new.values()){
            if(key==0){
                complete=false;
                break;
            }
        }
        return complete;
    }

    public static String[] convertString(List<String> arrayList){ //converts a List to a String
        String str[]=new String[arrayList.size()];
        Object[] objarr=arrayList.toArray();
        int i=0;
        for(Object Obj: objarr){
            str[i++]=(String)Obj;
        }
        return str;
    }

    public static void nf2_normalisation(Relations r, String  key){ //requires the primary key to be passed as an argument for it to work properly
        List<String> fd_set = new ArrayList<>();  
        List<String> rels= new ArrayList<>();                 
        StringTokenizer st1 = new StringTokenizer(r.fd, ",{}"); 
        while (st1.hasMoreTokens()) {
            fd_set.add(st1.nextToken());
        }
        // String rel_attributes=r.relation;
        int key_len=key.length();
        List <String> key_subsets= new ArrayList<>();
        
        for(int i=0; i< (1<<key_len); i++){                             //{A,B,C,D,E,F,G,H,I,J}
            String sub1="";
            for(int j=0; j<key_len; j++){                               //{AB->C,AD->GH,BD->EF,A->I,H->J}
                if((i & (1<<j))>0){
                    sub1=sub1+key.charAt(j);
                }
            }
            if(sub1!=""){
                key_subsets.add(sub1);
            }
        }
        Collections.sort(key_subsets, Comparator.comparing(String::length)); //sorting key_subsets by the length of the strings
        
        for(String s: key_subsets){
            // System.out.println("Considering subset: "+s+" for relations" );
            String rel_schema="";
            List<Character> attr_list= new ArrayList<>();
            
            for(int i=0; i<s.length(); i++){
                attr_list.add(s.charAt(i));
                rel_schema=rel_schema+ s.charAt(i);
            }
            // System.out.println("Set being passed to closure:");
            // System.out.println(attr_list);
            HashSet<Character> ret_set= new HashSet<Character>();
            ret_set=closure_calc(attr_list, fd_set);
            // System.out.println("You can get to i.e., return of closure:");
            // System.out.println(ret_set);

            for(Character c1: ret_set){
                String re=Arrays.toString(convertString(rels));
                if(rel_schema.indexOf(c1)==-1 && re.indexOf(c1)==-1){                                     
                    rel_schema=rel_schema+c1;
                }
            }
            if(rel_schema.length()>1){
                rels.add(rel_schema);
            }
        }
        System.out.println("Decomposed relations satisfying NF2:");

        for(String l: rels){
            String t1="";
            String f1="";
            List<String> keys=new ArrayList<>(); 
            t1=t1+'{';
            for(int i=0; i<l.length(); i++){
                if(i==l.length()-1){
                    t1=t1 + l.charAt(i);
                }
                else{
                t1=t1 + l.charAt(i)+',';
                }
            }
            t1=t1+'}';
            f1=f1+'{';
            int x=0;
            for(String lf: fd_set){
                if(x==fd_set.size()-1){
                    int present=1;
                    for(int li=0; li<lf.length(); li++){
                        if((t1.indexOf(lf.charAt(li))==-1) && lf.charAt(li)!='-' && lf.charAt(li)!='>'){
                            present=0;
                        }
                    }
                    if (present==1){
                        f1=f1+lf;
                    }
                }
                else{
                    int present=1;
                    for(int li=0; li<lf.length(); li++){
                        if((t1.indexOf(lf.charAt(li))==-1) && lf.charAt(li)!='-' && lf.charAt(li)!='>'){
                            present=0;
                        }
                    }
                    if (present==1){
                        f1=f1+lf+',';
                    }
                }
                x++;
            }
            f1=f1+'}';
            keys=candidate_key(t1, f1);
            System.out.println(t1 + " with its key being: " + keys.get(0));
        }
    }

    public static void nf3_normalisation(Relations r, String key){ //normalises the given relation to 3nf regardless of the nf it is in
        List<String> fd_set = new ArrayList<>();                   //requires the primary key to be passed as an argument for it to work properly
        List <String> rels= new ArrayList<>();                     // note that this function does not alter the original Relation schemas but rather just prints the decomposed schemas
        StringTokenizer st1 = new StringTokenizer(r.fd, ",{}"); 
        while (st1.hasMoreTokens()) {
            fd_set.add(st1.nextToken());
        }
        Set<Character> r_set = new HashSet<Character>();
        StringTokenizer st2 = new StringTokenizer(r.relation, ",{}"); 
        while (st2.hasMoreTokens()) {
            Character c = (st2.nextToken()).charAt(0);
            r_set.add(c);
        }
        List<String> fd_new= new ArrayList<>();
        fd_new.addAll(minimal_cover(r));
        String s1="";
        for (String str: fd_new){
            String schema1="";
            s1="";
            // System.out.println("Schema and s1 updated");
            // System.out.println("Now considering the relations:" + str);
            for (int i=0; i<str.indexOf("-") ;i++){
                s1=s1+str.charAt(i);
                schema1=schema1+s1;
                // System.out.println("Schema now:"+ schema1);
            }
            // System.out.println("S1:"+ s1);
            int flag_var=0;
            for(String str1: fd_new){
                if (str1.lastIndexOf(s1)!=-1 && str1.lastIndexOf(s1)<str1.indexOf("-")){
                    // System.out.println("matched in "+ str1);
                    flag_var=1;
                    for(int k=str1.lastIndexOf(s1)+3; k<str1.length(); k++){
                        if(schema1.lastIndexOf(str1.charAt(k))==-1){
                            schema1=schema1+str1.charAt(k);
                        }
                    }
                    // System.out.println("Schema now:"+schema1);
                }
            }
            // System.out.println("Schema to add:"+schema1);
            if(flag_var==1){
                if(!rels.contains(schema1) && schema1.length()>1){
                    rels.add(schema1);
                    // System.out.println(schema1+" added");
                }
            }
        }
    
        int contains_key=0;
        for(String l: rels){
            if(l.contains(key)){
                contains_key=1;
            }
        }

        if(contains_key==0){
            rels.add(key);
        }
        System.out.println("Decomposed relations satisfying NF3:");
        for(String l: rels){
            String t1="";
            String f1="";
            List<String> keys=new ArrayList<>(); 
            t1=t1+'{';
            for(int i=0; i<l.length(); i++){
                if(i==l.length()-1){
                    t1=t1 + l.charAt(i);
                }
                else{
                t1=t1 + l.charAt(i)+',';
                }
            }
            t1=t1+'}';
            f1=f1+'{';
            int x=0;
            for(String lf: fd_set){
                if(x==fd_set.size()-1){
                    int present=1;
                    for(int li=0; li<lf.length(); li++){
                        if((t1.indexOf(lf.charAt(li))==-1) && lf.charAt(li)!='-' && lf.charAt(li)!='>'){
                            present=0;
                        }
                    }
                    if (present==1){
                        f1=f1+lf;
                    }
                }
                else{
                    int present=1;
                    for(int li=0; li<lf.length(); li++){
                        if((t1.indexOf(lf.charAt(li))==-1) && lf.charAt(li)!='-' && lf.charAt(li)!='>'){
                            present=0;
                        }
                    }
                    if (present==1){
                        f1=f1+lf+',';
                    }
                }
                x++;
            }
            f1=f1+'}';
            // System.out.println(f1);
            keys=candidate_key(t1, f1);
            System.out.println(t1 + " with its key being: " + keys.get(0));
            // System.out.println(l);
        }
    }

    public static void convert_to_bcnf(Relations r){
        List<String> fd_set5 = new ArrayList<>();
        StringTokenizer st1 = new StringTokenizer(r.fd, ",{}"); 
        while (st1.hasMoreTokens()) {
            fd_set5.add(st1.nextToken());
        }
        List<String> fd_set = new ArrayList<>();
        fd_set = get_fd_set(fd_set5);
        
        Set<Character> r_set = new HashSet<Character>();
        StringTokenizer st2 = new StringTokenizer(r.relation, ",{}"); 
        while (st2.hasMoreTokens()) {
            Character c = (st2.nextToken()).charAt(0);
            r_set.add(c);
        }
        List<String> super_keys = r.candidate_key(r.relation,r.fd); 
        boolean is_bcnf = check_bcnf(super_keys,fd_set);

        if(!is_bcnf){
            List<String> violates_bcnf = new ArrayList<>();
            int flag=0;
            for(String s:fd_set){
                String s1="";
                for(int i=0; s.charAt(i)!='-'; i++){
                    s1=s1+s.charAt(i);
                }
                for(String s2: super_keys){
                    if(s1.equals(s2)){
                        flag=1;
                    }
                }
                if(flag==0){
                    violates_bcnf.add(s);
                }
                flag=0;
            }
            while(!is_bcnf){
                String s= violates_bcnf.get(0);
                Set<Character> alpha = new HashSet<Character>();
                Set<Character> beta = new HashSet<Character>();
                for(int i=0; s.charAt(i)!='-'; i++){
                    alpha.add(s.charAt(i));
                }                                       //System.out.println(alpha);
                for(int j=s.length()-1; s.charAt(j)!='>'; j--){
                    beta.add(s.charAt(j));
                }                                       //System.out.println(beta); 
                //System.out.println("The decomposed bcnf relations are: ");
                Set<Character> union = new HashSet<Character>(alpha); 
                union.addAll(beta);
                System.out.print("Relation : ");
                System.out.print(union);  System.out.print("\t");
                System.out.print("The key of this relation is: ");
                for(Character a: alpha){
                    System.out.print(a);
                }System.out.println();
                
                Set<Character> difference = new HashSet<Character>(beta); 
                difference.removeAll(alpha); 
                Set<Character> difference1 = new HashSet<Character>(r_set); 
                difference1.removeAll(difference); 
                Relations r1 = new Relations("",""); 
                r1 = convert_to_Relation(difference1,fd_set); 
                List<String> super_key_r = r1.candidate_key(r1.relation,r1.fd); 
                List<String> fd_set1 = new ArrayList<>();
                StringTokenizer st3 = new StringTokenizer(r1.fd, ",{}"); 
                while (st3.hasMoreTokens()) {
                    fd_set1.add(st3.nextToken());
                }
                if(check_bcnf(super_key_r,fd_set1)){
                    System.out.print("Relation : ");
                    System.out.print(difference1);    System.out.print("\t");
                    String s1=""; 
                    for(Character a: difference1){
                        s1=s1+a;
                    } 
                    String key_of_r2="";
                    for(String s2: super_keys){
                        if(stringToCharacterSet(s1).containsAll(stringToCharacterSet(s2))){
                            key_of_r2 = s2;
                        }
                    }
                    System.out.print("The key of this relation is: ");
                    System.out.println(key_of_r2);
                }
                else{
                    convert_to_bcnf(r1); 
                }
                is_bcnf=true;
            }
        }
    }

    public static Relations convert_to_Relation(Set<Character> difference1,List<String> fd_set){
        Relations r1;
        String s="{";
        for(Character a: difference1){
            s=s+a+',';
        }
        List<String> rem = new ArrayList<>();
        for(String s1: fd_set){
            for(int i=0; i<s1.length(); i++){
                String temp = Character.toString(s1.charAt(i)); 
                if(!s.contains(temp)){
                    if(!(s1.charAt(i)=='-')&&(!(s1.charAt(i)=='>'))){
                        rem.add(s1);
                    }
                }
            }
        }
        for(String s2: rem){
            fd_set.remove(s2);
        }
        String fd ="";
        for(String s2: fd_set){ fd=fd+s2+',';}
        r1 = new Relations(s,fd);
        return r1;
    }

    public static boolean check_bcnf(List<String> super_keys,List<String> fd_set){
        int flag=0;
        for(String s:fd_set){
            flag=0;
            String s1="";
            for(int i=0; s.charAt(i)!='-'; i++){
                s1=s1+s.charAt(i);
            }
            if(super_keys.contains(s1)){
                flag=1;
            }
            else{ 
                flag = 0;
                break;
            }
        }
        if((flag==1)||(fd_set.isEmpty())) return true;
        else return false;
    }
}

public class Start extends javax.swing.JFrame {

    static List<String> arr;
    static String relation;
    static String fd;
    /**
     * Creates new form Start
     */
    public Start() {
        initComponents();
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jTextField1 = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        jTextField2 = new javax.swing.JTextField();
        jButton2 = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jLabel1.setBackground(new java.awt.Color(0, 204, 255));
        jLabel1.setFont(new java.awt.Font("Dialog", 1, 24)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(255, 0, 0));
        jLabel1.setText("MODULE 1");

        jLabel2.setFont(new java.awt.Font("Dialog", 1, 18)); // NOI18N
        jLabel2.setText("Enter the relational attributes (Eg.  {A,B,C})");

        jTextField1.setBackground(new java.awt.Color(255, 255, 0));
        jTextField1.setFont(new java.awt.Font("Dialog", 0, 14)); // NOI18N
        jTextField1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextField1ActionPerformed(evt);
            }
        });

        jLabel3.setFont(new java.awt.Font("Dialog", 1, 18)); // NOI18N
        jLabel3.setText("Enter the functional dependencies (Eg.  {AB->C,C->B})");

        jTextField2.setBackground(new java.awt.Color(255, 255, 0));
        jTextField2.setFont(new java.awt.Font("Dialog", 0, 14)); // NOI18N
        jTextField2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextField2ActionPerformed(evt);
            }
        });

        jButton2.setText("Compute Candidate Keys");
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(187, 187, 187)
                        .addComponent(jLabel1))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(30, 30, 30)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel3)
                            .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 395, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, 435, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jTextField2, javax.swing.GroupLayout.PREFERRED_SIZE, 435, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(153, 153, 153)
                        .addComponent(jButton2)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(43, 43, 43)
                .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 54, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, 71, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jTextField2, javax.swing.GroupLayout.PREFERRED_SIZE, 71, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(28, 28, 28)
                .addComponent(jButton2, javax.swing.GroupLayout.PREFERRED_SIZE, 52, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(52, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jTextField1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextField1ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jTextField1ActionPerformed

    private void jTextField2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextField2ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jTextField2ActionPerformed

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        // TODO add your handling code here:
        relation = jTextField1.getText();
        fd = jTextField2.getText();
        Relations r1 = new Relations(relation,fd);
        arr =r1.candidate_key(relation,fd);
//        System.out.println("The candidate keys of the relation are: ");
//        for(String s: arr) {System.out.println(s);}
        Candidate_keys ck= new Candidate_keys();
        ck.setVisible(true);
        dispose();
    }//GEN-LAST:event_jButton2ActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(Start.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(Start.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(Start.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(Start.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new Start().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton2;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JTextField jTextField1;
    private javax.swing.JTextField jTextField2;
    // End of variables declaration//GEN-END:variables
}
