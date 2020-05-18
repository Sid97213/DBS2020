import java.io.*;
import java.util.*;
import java.util.Map;
import java.util.Dictionary;

class SuperKeys
{
    static Set<String> super_key = new HashSet<String>();
    void func(String[] arr, int n, Dictionary vis, String data[], String left[], String right[])
    {
        // int no_of_attributes = n;
        int num_fd = left.length;
        int ch = 1;
        for (int j = 0; j < n; j++)
        {
            if ((int)vis.get(arr[j]) == 0)
            {
                ch = 0;
                break;
            }
        }
        if (ch == 1) return;
        for (int i = 0; i < num_fd; i++)
        {
            int check = 1;
            for (int j = 0; j < n; j++)
            {
                if ((int)vis.get(arr[j]) == 0)
                {
                    check = 0;
                    break;
                }
            }
            if (check == 1) 
            {
                break;
            }
            check = 1;
            for (int j=0; j<left[i].length(); j++) {
                String tmp = Character.toString(left[i].charAt(j));
                if ((int)vis.get(tmp) == 0)
                    {
                        check = 0;
                        break;
                    }
            }
            if (check == 1)
            {
                int rec = 0;
                for (int j=0; j<right[i].length(); j++) {
                    String tmp = Character.toString(right[i].charAt(j));
                    if ((int)vis.get(tmp) == 0) rec = 1;
                            vis.put(tmp, 1);
                }
                if (rec == 1) func(arr, n, vis, data, left, right);
            }
        }
        int chh = 1;
        for (int j = 0; j < n; j++)
        {
            if ((int)vis.get(arr[j]) == 0)
            {
                chh = 0;
                break;
            }
        }
        if (chh == 1)
        {
            String tmp = "";
            for (int i = 0; i < data.length; i++)
            {   
                tmp += data[i];
            }
            super_key.add(tmp);
        }   
    }
}

class HighestNF
{
    static int hnf = 1;

    static boolean anagrams(String lhs, String key) 
    { 
        int n1 = lhs.length(); 
        int n2 = key.length(); 

        if (n1 != n2) 
            return false; 
  
        // Sorting both strings 
        char[] ch1 = lhs.toCharArray(); 
        char[] ch2 = key.toCharArray(); 
        Arrays.sort(ch1); 
        Arrays.sort(ch2); 
  
        // Comparing sorted strings 
        for (int i = 0; i < n1; i++) 
            if (ch1[i] != ch2[i]) 
                return false; 
  
        return true; 
    }

    void highest_nf(String attributes[], int n, String left[], String right[], List<String> arr)
    {
        int num_attr = n;
        Dictionary prime_attr = new Hashtable();
        for (int i = 0; i < num_attr; i++){
            prime_attr.put(attributes[i], 0);
        }
        for(String key : arr){
            for (int i=0; i<key.length(); i++) {
                prime_attr.put(Character.toString(key.charAt(i)),1);
            }
        }

        //check for 2nf
        // presence of partial dependency
        int pd=0;
        for (int i=0; i<right.length; i++) 
        {
            String rhs = right[i];
            for (int j=0; j<rhs.length(); j++) 
            {
                if ((int)prime_attr.get(Character.toString(rhs.charAt(j)))==0)          //only need to check if RHS is non-candidate
                {              
                    String lhs = left[i];
                    for(String key : arr)
                    {
                        Dictionary dic3 = new Hashtable();      //candidate keys
                        Dictionary dic4 = new Hashtable();      //lhs
                        for (int z = 0; z < num_attr; z++) 
                        {
                            dic3.put(attributes[z], 0); 
                            dic4.put(attributes[z], 0); 
                        }
                        for (int k=0; k<key.length(); k++) {
                           dic3.put(Character.toString(key.charAt(k)),1);
                        }
                        for (int k=0; k<lhs.length(); k++) {
                           dic4.put(Character.toString(lhs.charAt(k)),1);
                        }
                        int flag1 = 0;
                        int flag2 = 0;
                        for (int k = 0; k < num_attr; k++)
                        {
                            if ((int)dic3.get(attributes[k]) == 0 && (int)dic4.get(attributes[k]) == 1) flag1 = 1;
                            if ((int)dic3.get(attributes[k]) == 1 && (int)dic4.get(attributes[k]) == 0) flag2 = 1;
                        }

                        if (flag1 == 0 && flag2 == 1)
                        { 
                            pd = 1;
                            break;
                        }
                    }               
                }if (pd == 1) break; 
            }if (pd == 1) break;

        }
        if (pd== 1)
        {
            hnf=1;
            return;
        }
        hnf=2;

        //check for 3nf
        // for X->Y, either X is a super key or Y is a prime attribute
        for (int i=0; i<right.length; i++) {
            String lhs = left[i];
            int flag1=0;
            for(String key : SuperKeys.super_key)
            {
                if (anagrams(lhs, key))
                {
                    flag1 = 1;
                    break;
                }
            }
            if (flag1==1) continue;                 //first condition is true
            int flag2 = 0;
            String rhs = right[i]; 
            for (int j=0; j<rhs.length(); j++)
            {
                if ((int)prime_attr.get(Character.toString(rhs.charAt(j))) == 0)
                {
                    flag2 = 1;
                    break;
                }
            }
            if (flag2 == 1)                         //second condition false
            {
                // hnf = 2;
                break;
                // return;
            }
        }
        hnf=3;

        //check for bcnf
        // for every X->Y, X should be a super key
        for (int i = 0; i < right.length; i++)
        {
            String lhs = left[i];
            int flag1 = 0;
            for(String key : SuperKeys.super_key)
            {
                if (anagrams(lhs, key))
                {
                    flag1 = 1;
                    break;
                }
            }
            if (flag1 == 0)
            {
                hnf = 3;
                // break;
                return;
            }
        }
        hnf = -1;
        return;
    }
}


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

        for(String s: fd_set0){ 
            //System.out.println(s);                                  //removing redundant fd 
            char[] sch = s.toCharArray();
            if((sch[0]==s.charAt(s.length()-1))&&(s.charAt(1)=='-')){
                fd_set0.remove(s);
            }
        }
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
            for(int j=0; j<key_len; j++){                               //{AB->C,AD->G,AD->H,BD->E,BD->F,A->I,H->J}
                if((i & (1<<j))>0){
                    sub1=sub1+key.charAt(j);
                }
            }
            if(sub1!=""){
                key_subsets.add(sub1);
            }
        }
        Collections.sort(key_subsets, Comparator.comparing(String::length)); //sorting key_subsets by the length of the strings

        // System.out.println("Subsets:");
        // for(String d: key_subsets){
        //     System.out.println(d);
        // }
    
        //Making the map by marking the subset keys one by one
        Map<Character, Integer> map = new HashMap<>();
        
        for(String s: key_subsets){
            // System.out.println("Considering subset: "+s+" for relations" );
            String rel_schema="";
            Map<Character, Integer> ret_map= new HashMap<>();
            for(int j=1; j<r.relation.length(); j=j+2){
                map.put(r.relation.charAt(j), 0);
            }
            for(int i=0; i<s.length(); i++){
                map.put(s.charAt(i), 1);
                rel_schema=rel_schema+ s.charAt(i);
            }
            // System.out.println("Map being passed to closure:");
            // for(Map.Entry<Character, Integer> m: map.entrySet()){
            //     System.out.println(m);
            // }
            ret_map.putAll(closure(map, fd_set));
            // System.out.println("You can get to i.e., return of closure:");
            // for(Map.Entry<Character, Integer> m: ret_map.entrySet()){
            //     System.out.println(m);
            // }
            for(Map.Entry<Character,Integer> m: ret_map.entrySet()){
                char c;
                if(1==m.getValue()){
                    c=m.getKey();
                    String re=Arrays.toString(convertString(rels));
                    if(rel_schema.indexOf(c)==-1 && re.indexOf(c)==-1){                                     
                        rel_schema=rel_schema+c;
                    }
                }
            }
            rels.add(rel_schema);
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
                if(!rels.contains(schema1)){
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

class DBS{ 

    static void combinationUtil(String[] arr, int n, int r, int index, String data[], int i, String left[], String right[]) 
    {
        if (index == r) 
        {
            Dictionary vis = new Hashtable(); 
            for (int j = 0; j < n; j++)
            {
                vis.put(arr[j], 0);
            }
            for (int j = 0; j < r; j++) vis.put(data[j], 1); 
            SuperKeys c = new SuperKeys();
            c.func(arr, n, vis, data, left, right);
            return; 
        }
        if (i >= n) 
            return; 
        data[index] = (arr[i]); 
        combinationUtil(arr, n, r, index + 1, data, i + 1, left, right); 
        combinationUtil(arr, n, r, index, data, i + 1, left, right); 
    }

    static void Combinations(String[] arr, int n, int r, String left[], String right[]) 
    {
        String data[] = new String[r];
        combinationUtil(arr, n, r, 0, data, 0, left, right); 
    }

    public static void main(String[] args){
        Scanner sc = new Scanner(System.in);
        System.out.print("Enter the relation scheme: ");
        String relation = sc.nextLine();
        System.out.print("Enter the functional dependencies in the relation: ");
        String fd = sc.nextLine();

        Relations r1 = new Relations(relation,fd);
        List<String> arr =r1.candidate_key(relation,fd);
        System.out.println("The candidate keys of the relation are: ");
        for(String s: arr) {System.out.println(s);}


        //formatting input
        ArrayList attr = new ArrayList();
        StringTokenizer str = new StringTokenizer(relation, "{,}");
        while (str.hasMoreTokens()){
            attr.add(str.nextToken());      
        } 
        String[] attributes = new String[attr.size()];
        for (int i=0; i<attr.size(); i++) {
            attributes[i] = (String)attr.get(i);
        }

        StringTokenizer st1 = new StringTokenizer(fd, "{,}"); 
        ArrayList left = new ArrayList();
        ArrayList right = new ArrayList();
        while (st1.hasMoreTokens()){
          String st2 = st1.nextToken();
          StringTokenizer st3 = new StringTokenizer(st2, "->");
          left.add(st3.nextToken());
          right.add(st3.nextToken());
        } 

        int num_fd = left.size();
        String[] left_attr = new String[num_fd];
        String[] right_attr = new String[num_fd];
        for (int i=0; i<num_fd; i++) {
            left_attr[i]=(String)left.get(i);
            right_attr[i]=(String)right.get(i);
        }

        //computing super keys of the relation
        String temp = "";
        for (int i = 0; i < attr.size(); i++)
        {
            temp += attributes[i];
            Combinations(attributes, attr.size(), i + 1, left_attr, right_attr);
        }
        
        //for printing all super keys
        // System.out.println("Super Key: ");
        // for(String key : SuperKeys.super_key)
        // {
        //    System.out.println(key);
        // }

        //computing highest normal form
        HighestNF nf = new HighestNF();
        nf.highest_nf(attributes, attr.size(), left_attr, right_attr, arr);
        int n = nf.hnf;
        if (n==-1){
            System.out.println("Highest normal form: BCNF");
        }
        else
        System.out.println("Highest normal form: " + n);
        
        if(n==1){
            r1.nf2_normalisation(r1,arr.get(0));
        }
        else if(n==2){
            r1.nf3_normalisation(r1,arr.get(0));
        }
        else if(n==3){
            System.out.println("The decomposed bcnf relations are: ");
            r1.convert_to_bcnf(r1);
        }


        
        // r1.nf2_normalisation(r1, arr.get(0));
        //r1.nf3_normalisation(r1, arr.get(0));
    }
}

/*Test Cases for nf3_normalisation:
1. R: {A,B,C,D,E,F,G}
   FD: {A->CDE,B->FG,AB->CDEFG} 
2. R: {P,L,C,A}
   FD: {P->LCA,LC->AP,A->C}
*/

/*Test Cases for nf2_normalisation
1. R: {A,B,C,D,E,F}
   FD: {A->B,C->D,AC->EF}
2. R: {A,B,C,D,E,F}
   FD: {AB->C,A->D,B->EF}
*/