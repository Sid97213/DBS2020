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
        		if (sch[i]=='>'){
        			if(i+2 != s.length()){
        				for(int j=1; j<=(s.length()-i-1); j++){
        					String decomp = "";
        					decomp = decomp.copyValueOf(sch,0,i+1);
        					decomp = decomp + sch[i+j];
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
       		if((sch[0]==sch[sch.length-1])&&(sch[1]=='-')){
       			fd_set.remove(s);
       		}
       	}

       	int num_of_attr = (relation.length()-1)/2;				  //finding essential elements of the key
       	HashMap<Character, Integer> map = new HashMap<>();        //initializing map to 0 for all attr
       	char[] temp = relation.toCharArray();
       	for(int j=1; j<relation.length(); j=j+2){
       		map.put(temp[j],0);
       	}

       	for(String s: fd_set){                                    //marking all right side attributes
       		char[] temp1 = s.toCharArray();
       		for (Map.Entry<Character, Integer> entry : map.entrySet()){
       			if(entry.getKey()==temp1[s.length()-1]){
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
       		map.put(temp[i],0);   
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
                char[] sch = s.toCharArray();
                for(int i=0; sch[i]!='-'; i++){
                    left.add(sch[i]);
                }
                for(int j=s.length()-1; sch[j]!='>'; j--){
                    right.add(sch[j]);
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
                     map_new.put(temp[i],0);   
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
       				char[] sch = s.toCharArray();
       				if(key==sch[0]){
       					for(int i=0; sch[i]!='-'; i++){
       						if(map.get(sch[i])==0){
       							flag=0;
       						}	
       					}
                        if(flag==1){
                            map.put(sch[sch.length-1],1);
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
	}
}
