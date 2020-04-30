import java.io.*;
import java.util.*;

class Relations{
	String relation; String fd;
	Relations(String relation,String fd){
		this.relation=relation;
		this.fd=fd;
	}

	public static String find_key(String relation,String fd)
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
        for (Map.Entry<Character, Integer> entry : map.entrySet()){
        	if(entry.getValue()==0){
        		essential_attr.add(entry.getKey());			
        	}
       	}

       	for(int i=1; i<relation.length(); i=i+2){                   //initializing all relation attr to 0
       		map.put(relation.charAt(i),0);   
       	}
		
       	for(Character c: essential_attr){                           //changing value of essential attr of key to 1 in map
       		map.put(c,1); 		
       	}

       	Map<Character,Integer> map_new = closure(map,fd_set);		//calling the closure function on the essential attr

        boolean ans=false;                          
        ans = complete(map_new);                                    //check whether the closure is the complete set

        String key="";
        if(ans==true){                                              //if closure of essential attr is complete, then return it as the key
            for(Character c: essential_attr){
                key=key+c;
            }
            return key;
        }
        else{                                                       //finding attr that are on both left and right sides of the FD set
            List<Character> left = new ArrayList<>();
            List<Character> right = new ArrayList<>();
            List<Character> common = new ArrayList<>();
            for(String s: fd_set){
                for(int i=0; s.charAt(i)!='-'; i++){
                    left.add(s.charAt(i));
                }
                for(int j=s.length()-1; s.charAt(j)!='>'; j--){
                    right.add(s.charAt(j));
                }
            }
            for(Character a: left){
                for(Character b: right){
                    if(a==b){
                        common.add(a);
                    }
                }
            }
            List<Character> curr = new ArrayList<>();               //if any common attr is already present in the prev closure, then we dont consider it for the key
            for(Character a: common){
                if(map_new.get(a)==1){
                    curr.add(a);
                } 
            }
            for(Character a: curr){
                common.remove(a);
            }
            for(Character a: common){                               //try adding each common element and checking whether the closure is complete or not
                essential_attr.add(a);
                for(int i=1; i<relation.length(); i=i+2){                   
                     map_new.put(relation.charAt(i),0);   
                }
                for(Character b: essential_attr){
                    map_new.put(b,1);
                }
                Map<Character,Integer> map1 = closure(map_new,fd_set);
                ans = complete(map1);
                if (ans){
                    for(Character c: essential_attr){
                        key=key+c;
                    }
                    return key;
                }
                essential_attr.remove(a);
            }
            System.out.println("Test case not working yet");
            return key;
        }
    }

    public static Map<Character,Integer> closure(Map<Character,Integer> map,List<String> fd_set)    //function to find closure of attr
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

        List<String> violates_bcnf = new ArrayList<>();
        String key = r.find_key(r.relation,r.fd);
        for(int i=0;i<key.length();i++){
            for(String s: fd_set){
                for(int j=s.length()-1; s.charAt(j)!='>'; j--){
                    if(s.charAt(j)==key.charAt(i)){
                        violates_bcnf.add(s);
                    }
                }
            }
            
        }

        for(String s: violates_bcnf){
            Set<Character> alpha = new HashSet<Character>();
            Set<Character> beta = new HashSet<Character>();
            for(int i=0; s.charAt(i)!='-'; i++){
                alpha.add(s.charAt(i));
            }                                       //System.out.println(alpha);
            
            for(int j=s.length()-1; s.charAt(j)!='>'; j--){
                beta.add(s.charAt(j));
            }                                       //System.out.println(beta); 

            System.out.println("The decomposed relations are: ");
            Set<Character> union = new HashSet<Character>(alpha); 
            union.addAll(beta);
            System.out.print("Relation 1: ");
            System.out.print(union);  System.out.print("\t");
            System.out.print("The key of this relation is: ");
            for(Character a: alpha){
                System.out.print(a);
            }System.out.println();

            Set<Character> difference = new HashSet<Character>(beta); 
            difference.removeAll(alpha); 
            Set<Character> difference1 = new HashSet<Character>(r_set); 
            difference1.removeAll(difference); 
            System.out.print("Relation 2: "); 
            System.out.print(difference1);    System.out.print("\t");
            Set<Character> key_of_r2 = new HashSet<Character>(); 
            for(Character a: difference1){
                for(int i=0; i<key.length(); i++){
                    for(Character c: alpha){
                        if(a==key.charAt(i)||(a==c)){
                            key_of_r2.add(a);
                        }
                    }
                }
            }
            System.out.print("The key of this relation is: ");
            for(Character b: key_of_r2){
                System.out.print(b);
            } System.out.println();
        }
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
		String key=r1.find_key(relation,fd);
		System.out.println("The key of the relation is: " + key);

        r1.nf3_to_bcnf(r1);
	}
}
