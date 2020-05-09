import java.util.*;
import java.io.*;
import java.util.Map;

class MemoryExceededException extends Exception{
	MemoryExceededException(List<Integer> keys,String s){
		Demo obj = new Demo();
		System.out.println("Error: Memory Limit Exceeded!!"+"\n"+"The entered key exceeds data structure capacity of bucket "+s+" !!");
		Demo.Bucket_overflow(keys,s);
	}
}
class Demo{
	public static List<String> hash_fn(List<Integer> keys){
		List<String> hash_val = new ArrayList<>();
		for(Integer x: keys){
			int mod = x%10; 
			String s = Integer.toBinaryString(mod);
			while(s.length()!=4){
         		s='0'+s;
        	}
        	hash_val.add(s);
		}
		return hash_val;
    }
    public static String get_last_digits(String hash_val,int num){
        return (hash_val.substring(4-num,4));
    }
    public static int power(int x, int y){ 
        int result = x; 
        for (int i = 1; i < y; i++){
        result = result * x; 
        } 
        return result; 
    }
    public static int get_GD(int[] counti){
    	int[] pow = new int[2];
    	for(int i=0; i<2; i++){
    		if(0<=counti[i] && counti[i]<=2) {pow[i]=1;}
    		else if(3<=counti[i] && counti[i]<=4) {pow[i]=2;}
    		else if(5<=counti[i] && counti[i]<=8) {pow[i]=3;}
    		else if(9<=counti[i] && counti[i]<=16) {pow[i]=4;}
    		else{ System.out.println("Error: space exceeded!");}
    	}
		return(Math.max(pow[0],pow[1]));
    }
    
    public static int[] get_num(List<String> hash_val,String s1,String s2,int i){
    	int[] arr = new int[2];
    	for(String s: hash_val){
    		if(get_last_digits(s,i).equals(s1)){
    			arr[0]++;
    		}
    		else if(get_last_digits(s,i).equals(s2)){
    			arr[1]++;
    		}
    	}
    	return arr;
    }
    public static void Bucket_overflow(List<Integer> keys,String s){
    	List<String> hash_val = hash_fn(keys);
    	int[][] array = new int[keys.size()][2];
		for(int i=0; i<keys.size(); i++){
			array[i][0] = keys.get(i);
			array[i][1] = Integer.parseInt(hash_val.get(i));
		}
		System.out.print("Overflow bucket '"+s+"' contains keys: ");
		for(int i=0; i<keys.size(); i++){
			String s1 = Integer.toString(array[i][1]);
			while(s1.length()!=4){
         		s1='0'+s1;
        	}
			if(s1.equals(s)){
				System.out.print(array[i][0]+"  ");
			}
		}
		System.out.println();
    }
	public static List<String> Calculate(List<Integer> keys,String oper) throws MemoryExceededException{
		List<String> hash_val = hash_fn(keys);
		int[] counti = new int[2]; 
		counti[0]=0; counti[1]=0;
		for(String s: hash_val){
			if(get_last_digits(s,1).equals("0"))
				counti[0]++;
			else if(get_last_digits(s,1).equals("1")){
				counti[1]++;
			}
		}
		List<String> ans = new ArrayList<>();
		ans.clear();

			if(counti[0]>2){
				int[] arr1 = get_num(hash_val,"00","10",2);
				if(arr1[0]>2){
					int[] arr2 = get_num(hash_val,"000","100",3);
					if(arr2[0]>2){
						int[] arr3 = get_num(hash_val,"0000","1000",4);
						if(arr3[0]>2 || arr3[1]>2){
							if(arr3[0]>2)   throw new MemoryExceededException(keys,"0000");
							if(arr3[1]>2) 	throw new MemoryExceededException(keys,"1000");
						}
						else{
							if(arr3[0]<=2) { ans.add("0000");}
							if( arr3[1]<=2) { ans.add("1000");}
						}
					}
					else if(arr2[0]<=2) { ans.add("000"); }
					if(arr2[1]>2){
						int[] arr3 = get_num(hash_val,"0100","1100",4);
						if(arr3[0]>2 || arr3[1]>2){
							if(arr3[0]>2) throw new MemoryExceededException(keys,"0100");
							if(arr3[1]>2) throw new MemoryExceededException(keys,"1100");
						}
						else{
							if( arr3[0]<=2) { ans.add("0100");}
							if( arr3[1]<=2) { ans.add("1100");}
						}
					}
					else if( arr2[1]<=2) {  ans.add("100"); }
				}
				else if( arr1[0]<=2) {  ans.add("00"); }
				if(arr1[1]>2){
					int[] arr2 = get_num(hash_val,"010","110",3);
					if(arr2[0]>2){
						int[] arr3 = get_num(hash_val,"0010","1010",4);
						if(arr3[0]>2 || arr3[1]>2){ 
							if(arr3[0]>2) throw new MemoryExceededException(keys,"0010");
							if(arr3[1]>2) throw new MemoryExceededException(keys,"1010");
						}
						else{
							if(arr3[0]>0 && arr3[0]<=2) { ans.add("0010");}
							if(arr3[1]>0 && arr3[1]<=2) { ans.add("1010");}
						}
					}
					else if(arr2[0]>0 && arr2[0]<=2) {  ans.add("010"); }
					if(arr2[1]>2){
						int[] arr3 = get_num(hash_val,"0110","1110",4);
						if(arr3[0]>2 || arr3[1]>2){
							if(arr3[0]>2) throw new MemoryExceededException(keys,"0110");
							if(arr3[1]>2) throw new MemoryExceededException(keys,"1110");
						}
						else{
							if(arr3[0]>0 && arr3[0]<=2) { ans.add("0110");}
							if(arr3[1]>0 && arr3[1]<=2) { ans.add("1110");}
						}
					}
					else if(arr2[1]>0 && arr2[1]<=2) {  ans.add("110"); }
				}
				else if(arr1[0]>0 && arr1[0]<=2) {  ans.add("10"); }
				
			}
			else if(counti[0]>0 && counti[0]<=2){
				ans.add("0");
			}
			if(counti[1]>2){
				int[] arr1 = get_num(hash_val,"01","11",2);
				if(arr1[0]>2){
					int[] arr2 = get_num(hash_val,"001","101",3);
					if(arr2[0]>2){
						int[] arr3 = get_num(hash_val,"0001","1001",4);
						if(arr3[0]>2 || arr3[1]>2){
							if(arr3[0]>2) throw new MemoryExceededException(keys,"0001");
							if(arr3[1]>2) throw new MemoryExceededException(keys,"1001");
						}
						else{
							if(arr3[0]>0 && arr3[0]<=2) { ans.add("0001");}
							if(arr3[1]>0 && arr3[1]<=2) { ans.add("1001");}
						}
					}
					else if(arr2[0]>0 && arr2[0]<=2) {  ans.add("001"); }
					if(arr2[1]>2){
						int[] arr3 = get_num(hash_val,"0101","1101",4);
						if(arr3[0]>2 || arr3[1]>2){
						 	if(arr3[0]>2) throw new MemoryExceededException(keys,"0101");
							if(arr3[1]>2) throw new MemoryExceededException(keys,"1101");
						}
						else{
							if(arr3[0]>0 && arr3[0]<=2) { ans.add("0101");}
							if(arr3[1]>0 && arr3[1]<=2) { ans.add("1101");}
						}
					}
					else if(arr2[1]>0 && arr2[1]<=2) {  ans.add("101"); }
				}
				else if(arr1[0]>0 && arr1[0]<=2) {  ans.add("01"); }
				if(arr1[1]>2){
					int[] arr2 = get_num(hash_val,"011","111",3);
					if(arr2[0]>2){
						int[] arr3 = get_num(hash_val,"0011","1011",4);
						if(arr3[0]>2 || arr3[1]>2){
							if(arr3[0]>2) throw new MemoryExceededException(keys,"0011");
							if(arr3[1]>2) throw new MemoryExceededException(keys,"1011");
						}
						else{
							if(arr3[0]>0 && arr3[0]<=2) { ans.add("0011");}
							if(arr3[1]>0 && arr3[1]<=2) { ans.add("1011");}
						}
					}
					else if(arr2[0]>0 && arr2[0]<=2) {  ans.add("011"); }
					if(arr2[1]>2){
						int[] arr3 = get_num(hash_val,"0111","1111",4);
						if(arr3[0]>2 || arr3[1]>2){
							if(arr3[0]>2) throw new MemoryExceededException(keys,"0111"); 
							if(arr3[1]>2) throw new MemoryExceededException(keys,"1111"); 
						}
						else{
							if(arr3[0]>0 && arr3[0]<=2) { ans.add("0111");}
							if(arr3[1]>0 && arr3[1]<=2) { ans.add("1111");}
						}
					}
					else if(arr2[1]>0 && arr2[1]<=2) {  ans.add("111"); }
				}
				else if(arr1[1]>0 && arr1[1]<=2) {  ans.add("11"); }
			}
			else if(counti[1]>0 && counti[1]<=2){
				ans.add("1");
			}

		int[][] array = new int[keys.size()][2];
		for(int i=0; i<hash_val.size(); i++){
			array[i][0] = keys.get(i);
			array[i][1] = Integer.parseInt(hash_val.get(i));
		}
		int GD=0;
		for(String s: ans){
			if(s.length()>GD){
				GD=s.length();
			}
		}

		if(oper.equals("insert")||oper.equals("delete")){
			print_result(ans,array,GD);
		}
		return ans;
	}
	public static void print_result(List<String> ans,int[][] array,int GD){
		System.out.println("GD: "+GD);
		for(String s: ans){
			System.out.print("LD: "+s.length()+"  ");
			System.out.print("Bucket:"+s+": ");
			for(int i=0; i<array.length; i++){
				String s1 = Integer.toString(array[i][1]);
				while(s1.length()!=4){
         		s1='0'+s1;
        		}
				if(s.equals(get_last_digits(s1,s.length()))){
					System.out.print(array[i][0]+"  ");
				}
			}
			System.out.println();
		}
	}
	public static void search(List<Integer> keys,int x){
		int mod = x%10; 
		String s = Integer.toBinaryString(mod);
		while(s.length()!=4){
        	s='0'+s;
        }
		List<String> buckets = new ArrayList<>();
		try{
			buckets = Calculate(keys,"search");
		}
		catch(MemoryExceededException mee) { return;}

		for(String s1: buckets){
			if(s1.equals(get_last_digits(s,s1.length()))){
				System.out.println("Key "+x+" is present in bucket "+s1);
			}
			
		}
		
	}
	public static void main(String args[]){
		System.out.print("Enter the number of operations: ");
        Scanner sc = new Scanner(System.in);
        String input = sc.nextLine();
        int num = Integer.parseInt(input);
        System.out.println(num);
        List<Integer> store = new ArrayList<>();
        
        while(num!=0){
            System.out.print("Enter the operation: ");
            String oper = sc.nextLine();

            if(oper.equals("insert")){
                System.out.print("Enter the value of the key to be inserted: ");
                String key_string = sc.nextLine();
                int key = Integer.parseInt(key_string); 
                if(store.contains(key)){
                    System.out.println("Error: Duplicate key error!\n\tThis key is already present in the database!");
                }
                else{
                    store.add(key);
                    try{  Calculate(store,"insert"); }
                    catch(MemoryExceededException mee){
                    	return;
                    }
                }
            }
            else if(oper.equals("delete")){
                System.out.print("Enter the value of the key to be deleted: ");
                String key_string = sc.nextLine();
                int key = Integer.parseInt(key_string);  
                if(store.contains(key)){
                    store.remove(new Integer(key));
                    try{  Calculate(store,"delete"); }
                    catch(MemoryExceededException mee){
                    	return;
                    } 
                } 
                else{
                    System.out.println("Error: Missing key error!\n\tThe entered key does not exist in the database!");
                }   
            }
            else if(oper.equals("search")){
            	System.out.print("Enter the value of the key to be deleted: ");
            	String key_string = sc.nextLine();
            	int key = Integer.parseInt(key_string);
            	if(store.contains(key)){
            		search(store,key);
            	}
            	else{
            		System.out.println("Error: Missing key error!\n\tThe entered key does not exist in the database!");
            	}
            }
            else{
            	System.out.println("Invalid Operation!!");
            }
            num--;
        }
	}
}