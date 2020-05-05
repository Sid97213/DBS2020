import java.io.*;
import java.util.*;
import java.util.Map;

class Relations{
	String relation; String fd;
	Relations(String relation,String fd){
		this.relation=relation;
		this.fd=fd;
	}

	public static List<String> candidate_key(String relation,String fd)
	{
		List<String> fd_set = new ArrayList<>();
		List<String> fd_set1 = new ArrayList<>();
		StringTokenizer st1 = new StringTokenizer(fd, ",{}"); 
    	while (st1.hasMoreTokens()) {
       		fd_set.add(st1.nextToken());
   		}

    	List<Integer> storeIndex = new ArrayList<>();			   //ensuring all right side attributes are single attributes   
        for (String s : fd_set) {											
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
       					storeIndex.add(fd_set.indexOf(s));
       				}
       			}
       		}
       	}
       	for (String s: fd_set1){						          //add decomposed relations to fd_set
       		fd_set.add(s);
      	}
       	for(int i: storeIndex){						              //remove the original relation that was decomposed
       		fd_set.remove(i);
       	}

       	for(String s: fd_set){						              //removing redundant fd 
       		char[] sch = s.toCharArray();
       		if((sch[0]==s.charAt(s.length()-1))&&(s.charAt(1)=='-')){
       			fd_set.remove(s);
       		}
       	}

       	int num_of_attr = (relation.length()-1)/2;				  //finding essential elements of the key
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

        List<String> store = new ArrayList<>();
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

        return store;
    }
    public static Set<Character> stringToCharacterSet(String s){
        Set<Character> set = new HashSet<>();
        for (char c : s.toCharArray()) {
            set.add(c);
        }
       return set;
    }
    public static Map<Character,Integer> closure(Map<Character,Integer> map,List<String> fd_set)    //function to find closure of attr
    {
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

    public static void nf3_to_bcnf(Relations r){
        List<String> fd_set = new ArrayList<>();
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
                    nf3_to_bcnf(r1); 
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
            for(String s2: super_keys){
                if(s1.equals(s2)){
                    flag=1;
                    break;
                }
            }
        }
        if((flag==1)||(fd_set.isEmpty())) return true;
        else return false;
    }
    
}

class DBS{
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
		
        //System.out.println("The decomposed bcnf relations are: ");
        //r1.nf3_to_bcnf(r1);
        
	}
}
